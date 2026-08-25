package mip.mva.sp.comm.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.raonsecure.omnione.core.data.iw.profile.EncryptKeyTypeEnum;
import com.raonsecure.omnione.core.data.iw.profile.EncryptTypeEnum;

import mip.mva.sp.comm.enums.AuthTypeEnum;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.PresentTypeEnum;
import mip.mva.sp.comm.enums.RequestTypeEnum;
import mip.mva.sp.comm.enums.TrxStsCodeEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.DirectService;
import mip.mva.sp.comm.service.MipDidVpService;
import mip.mva.sp.comm.service.MipZkpVpService;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.util.Generator;
import mip.mva.sp.comm.vo.M200VO;
import mip.mva.sp.comm.vo.M310VO;
import mip.mva.sp.comm.vo.M320VO;
import mip.mva.sp.comm.vo.M400VO;
import mip.mva.sp.comm.vo.M900VO;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.config.vo.ServiceVO;
import mip.mva.sp.config.vo.SpVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service.impl
 * @FileName    : DirectServiceImpl.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Direct 검증 ServiceImpl
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Service("directService")
public class DirectServiceImpl implements DirectService {

	/** SP 설정 */
	private final SpVO sp;
	/** 서비스 설정 */
	private final Map<String, ServiceVO> services;

	/** 거래정보 Service */
	private final TrxInfoService trxInfoService;
	/** VP 검증 Service */
	private final MipDidVpService mipDidVpService;
	/** 영지식 VP 검증 Service */
	private final MipZkpVpService mipZkpVpService;

	/** self proxy for transactional call */
	private final DirectService self;

	/**
	 * 생성자
	 * 
	 * @param configBean 설정정보
	 * @param trxInfoService 거래정보 Service
	 * @param mipDidVpService VP 검증 Service
	 * @param mipZkpVpService 영지식 VP 검증 Service
	 */
	public DirectServiceImpl(ConfigBean configBean, TrxInfoService trxInfoService, MipDidVpService mipDidVpService, MipZkpVpService mipZkpVpService, @Lazy DirectService directServiceProxy) {
		this.sp = configBean.getVerifyConfig().getSp();
		this.services = configBean.getVerifyConfig().getServices();

		this.trxInfoService = trxInfoService;
		this.mipDidVpService = mipDidVpService;
		this.mipZkpVpService = mipZkpVpService;
		
		this.self = directServiceProxy;
	}

	/**
	 * M200 요청
	 * 
	 * @param ifType 인터페이스유형
	 * @param mode 모드
	 * @param svcCode 서비스코드
	 * @param includeProfile profile 포함 여부
	 * @return M200 메세지
	 * @throws SpException
	 */
	@Override
	public M200VO getM200(String ifType, String mode, String svcCode, Boolean includeProfile) throws SpException {
		M200VO m200 = null;

		try {
			String trxcode = Generator.genTrxcode();
			String image = sp.getBiImageUrl();
			Boolean ci = sp.getIsCi();
			Boolean telno = sp.getIsTelno();
			String host = sp.getServerDomain();

			TrxInfoVO trxInfo = new TrxInfoVO();

			trxInfo.setIfType(ifType);
			trxInfo.setSvcCode(svcCode);
			trxInfo.setTrxcode(trxcode);
			trxInfo.setMode(mode);

			trxInfoService.registTrxInfo(trxInfo);

			String profile = "";

			if (Boolean.TRUE.equals(includeProfile)) {
				M310VO m310 = new M310VO();

				m310.setTrxcode(trxcode);
				m310.setRequest(RequestTypeEnum.CMD_310_REQ.getRequest());

				profile = self.getProfile(m310).getProfile();
			}

			m200 = new M200VO();

			m200.setTrxcode(trxcode);
			m200.setMode(mode);
			m200.setProfile(profile);
			m200.setImage(image);
			m200.setCi(ci);
			m200.setTelno(telno);
			m200.setHost(host);
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return m200;
	}

	/**
	 * Profile 요청
	 * 
	 * @param m310 M310 메세지
	 * @return M310 메세지 + Profile
	 * @throws SpException
	 */
	@Transactional
	@Override
	public M310VO getProfile(M310VO m310) throws SpException {
		this.validateM310(m310);

		String trxcode = m310.getTrxcode();

		try {
			TrxInfoVO trxInfo = trxInfoService.getTrxInfo(trxcode);

			// profile 요청은 시작(0001) 상태에서 해야 한다.
			// 0001 상태가 아닌 것은 이미 profile 요청이 됐거나, verify 된 상태일 수 있다.
			if (!Objects.equals(TrxStsCodeEnum.SERCIVE_REQ.getVal(), trxInfo.getTrxStsCode())) {
				throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode, "stsCode != 0001");
			}

			// 이미 verify 된 trx
			if (Objects.equals("Y", trxInfo.getVpVerifyResult())) {
				throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode, "verifyResult == Y");
			}

			ServiceVO service = services.get(trxInfo.getSvcCode());

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, trxcode, "service does not exist");
			}

			Integer presentType = service.getPresentType();

			if (Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType)) {
				m310.setProfile(mipDidVpService.getProfile(trxInfo));
			} else if (Objects.equals(PresentTypeEnum.ZKP_VP.getVal(), presentType)) {
				m310.setProfile(mipZkpVpService.getProfile(trxInfo));
			} else {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE, trxcode);
			}
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e);
		}

		return m310;
	}

	/**
	 * BI 이미지 요청
	 * 
	 * @param m320 M320 메세지
	 * @return Base64로 인코딩된 Image Data
	 * @throws SpException
	 */
	@Override
	public String getImage(M320VO m320) throws SpException {
		String image = "";

		String trxcode = m320.getTrxcode();

		try {
			image = sp.getBiImageBase64();
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, trxcode, e);
		}

		return image;
	}

	/**
	 * VP 검증
	 * 
	 * @param m400 M400메세지
	 * @return 검증 결과
	 * @throws SpException
	 */
	@Transactional
	@Override
	public Boolean verifyVp(M400VO m400) throws SpException {
		Boolean result = false;

		this.validateM400(m400);

		String trxcode = m400.getTrxcode();

		try {
			TrxInfoVO trxInfo = trxInfoService.getTrxInfo(trxcode);

			if (!Objects.equals(TrxStsCodeEnum.PROFILE_REQ.getVal(), trxInfo.getTrxStsCode())) {
				throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode, "stsCode != 0002");
			}

			if (Objects.equals("Y", trxInfo.getVpVerifyResult())) {
				throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode, "verifyResult == Y");
			}

			VP vp = m400.getVp();

			this.validateVp(trxInfo, vp);

			ServiceVO service = services.get(trxInfo.getSvcCode());

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, trxcode, "service does not exist");
			}

			Integer presentType = service.getPresentType();

			if (Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType)) {
				result = mipDidVpService.verifyVp(trxInfo, vp);
			} else if (Objects.equals(PresentTypeEnum.ZKP_VP.getVal(), presentType)) {
				result = mipZkpVpService.verifyVp(trxInfo, vp);
			} else {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE, trxcode);
			}
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e);
		}

		return result;
	}

	/**
	 * 오류 전송
	 * 
	 * @param m900 M900 메세지
	 * @throws SpException
	 */
	@Override
	public void sendError(M900VO m900) throws SpException {
		TrxInfoVO trxInfo = new TrxInfoVO();

		trxInfo.setTrxcode(m900.getTrxcode());
		trxInfo.setTrxStsCode(TrxStsCodeEnum.VERIFY_ERR.getVal());
		trxInfo.setErrorCn(m900.getErrmsg());

		trxInfoService.modifyTrxInfo(trxInfo);
	}

	/**
	 * VP 재검증 - 부인방지
	 * 
	 * @param vp VP 정보
	 * @return 검증 결과
	 * @throws SpException
	 */
	@Override
	public Boolean reVerifyVP(VP vp) throws SpException {
		Boolean result = false;

		Integer presentType = vp.getPresentType();

		if (Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType)) {
			result = mipDidVpService.reVerifyVP(vp);
		} else {
			throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE);
		}

		return result;
	}

	/**
	 * VP data 조회
	 * 
	 * @param vp VP
	 * @return 복호화된 VP data
	 * @throws SpException
	 */
	@Override
	public String getVPData(VP vp) throws SpException {
		String vpData = "";

		Integer presentType = vp.getPresentType();

		if (Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType)) {
			vpData = mipDidVpService.getVPData(vp);
		} else if (Objects.equals(PresentTypeEnum.ZKP_VP.getVal(), presentType)) {
			vpData = mipZkpVpService.getVPData(vp);
		} else {
			throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE);
		}

		return vpData;
	}

	/**
	 * M310 메세지 확인
	 * 
	 * @param m310 M310 메세지
	 * @throws SpException
	 */
	private void validateM310(M310VO m310) throws SpException {
		String type = m310.getType();
		String version = m310.getVersion();
		String cmd = m310.getCmd();
		String request = m310.getRequest();
		String trxcode = m310.getTrxcode();

		if (ObjectUtils.isEmpty(type)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m310.type");
		}

		if (ObjectUtils.isEmpty(version)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m310.version");
		}

		if (ObjectUtils.isEmpty(cmd)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m310.cmd");
		}

		if (ObjectUtils.isEmpty(request)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m310.request");
		}

		if (ObjectUtils.isEmpty(trxcode)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m310.trxcode");
		}

		if (!Objects.equals(RequestTypeEnum.CMD_310_REQ.getType(), type)) {
			throw new SpException(MipErrorEnum.SP_INVALID_TYPE, trxcode, "m310.type");
		}

		if (!Objects.equals(RequestTypeEnum.CMD_310_REQ.getCmd(), cmd)) {
			throw new SpException(MipErrorEnum.SP_INVALID_DATA, trxcode, "m310.cmd");
		}

		if (!Objects.equals(RequestTypeEnum.CMD_310_REQ.getRequest(), request)) {
			throw new SpException(MipErrorEnum.SP_INVALID_DATA, trxcode, "m310.request");
		}
	}

	/**
	 * M400 메세지 확인
	 * 
	 * @param m400 M400 메세지
	 * @throws SpException
	 */
	private void validateM400(M400VO m400) throws SpException {
		String type = m400.getType();
		String version = m400.getVersion();
		String cmd = m400.getCmd();
		String request = m400.getRequest();
		String trxcode = m400.getTrxcode();
		VP vp = m400.getVp();

		if (ObjectUtils.isEmpty(type)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m400.type");
		}

		if (ObjectUtils.isEmpty(version)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m400.version");
		}

		if (ObjectUtils.isEmpty(cmd)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m400.cmd");
		}

		if (ObjectUtils.isEmpty(request)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m400.request");
		}

		if (ObjectUtils.isEmpty(trxcode)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "trxcode");
		}

		if (ObjectUtils.isEmpty(vp)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "m400.vp");
		}

		if (!Objects.equals(RequestTypeEnum.CMD_400_REQ.getType(), type)) {
			throw new SpException(MipErrorEnum.SP_INVALID_TYPE, trxcode, "m400.type");
		}

		if (!Objects.equals(RequestTypeEnum.CMD_400_REQ.getCmd(), cmd)) {
			throw new SpException(MipErrorEnum.SP_INVALID_DATA, trxcode, "m400.cmd");
		}

		if (!Objects.equals(RequestTypeEnum.CMD_400_REQ.getRequest(), request)) {
			throw new SpException(MipErrorEnum.SP_INVALID_DATA, trxcode, "m400.request");
		}
	}

	/**
	 * VP 정보 확인
	 * 
	 * @param trxInfo 거래정보
	 * @param vp VP 정보
	 * @throws SpException
	 */
	private void validateVp(TrxInfoVO trxInfo, VP vp) throws SpException {
		String trxcode = trxInfo.getTrxcode();

		Integer presentType = vp.getPresentType();
		Integer encryptType = vp.getEncryptType();
		Integer keyType = vp.getKeyType();
		String data = vp.getData();
		String type = vp.getType();
		String nonce = Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType) ? vp.getNonce() : vp.getZkpNonce();

		if (ObjectUtils.isEmpty(presentType)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.presentType");
		}

		if (ObjectUtils.isEmpty(encryptType)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.encryptType");
		}

		if (ObjectUtils.isEmpty(keyType)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.keyType");
		}

		if (ObjectUtils.isEmpty(data)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.data");
		}

		if (ObjectUtils.isEmpty(nonce)) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.nonce");
		}

		if (ObjectUtils.isEmpty(PresentTypeEnum.getEnum(presentType))) {
			throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE, trxcode, "vp.presentType");
		}

		if (ObjectUtils.isEmpty(EncryptTypeEnum.getEnum(encryptType))) {
			throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_ENC_TYPE, trxcode, "vp.encryptType");
		}

		if (ObjectUtils.isEmpty(EncryptKeyTypeEnum.getEnum(keyType))) {
			throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_KEY_TYPE, trxcode, "vp.keyType");
		}

		if (!Objects.equals("VERIFY", type)) {
			throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_TYPE, trxcode, "vp.type");
		}

		ServiceVO service = services.get(trxInfo.getSvcCode());

		if (ObjectUtils.isEmpty(service)) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, trxcode, "service does not exist");
		}

		if (Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType)) {
			validateVpAuthType(trxcode, vp, service);
		}
	}

	/**
	 * VP 인증유형 확인
	 * 
	 * @param trxcode 거래코드
	 * @param vp VP 정보
	 * @param service 서비스 정보
	 * @throws SpException
	 */
	private void validateVpAuthType(String trxcode, VP vp, ServiceVO service) throws SpException {
		if (ObjectUtils.isEmpty(vp.getDid())) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.did");
		}

		if (ObjectUtils.isEmpty(vp.getAuthType())) {
			throw new SpException(MipErrorEnum.SP_MISSING_MANDATORY_ITEM, trxcode, "vp.authType");
		}

		for (String authTypeString : vp.getAuthType()) {
			if (ObjectUtils.isEmpty(AuthTypeEnum.getEnum(authTypeString.toLowerCase()))) {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_AUTH_TYPE, trxcode, "vp.authType");
			}
		}

		// authType validation
		/*
		 * ※ 일반인증: profile의 authType=null ※ 안심인증: profile의 authType='["pin", "face"]' ※
		 * pin, bio, face는 각각 대소문자 구별하지 않음 1) 일반인증으로 profile을 내린 경우 VP 검증 시
		 * M400.vp.authType 에 "pin" or "bio"를 포함하는지 확인 2) 안심인증으로 profile을 내린 경우 VP 검증 시
		 * M400.vp.authType 에 ["pin", "face"] 두 가지 모두를 포함하는지 확인
		 */
		if (Boolean.FALSE.equals(this.isAuthTypeValid(vp.getAuthType(), service.getAuthType()))) {
			throw new SpException(MipErrorEnum.SP_MISMATCHING_AUTH_TYPE, trxcode, "vp.authType");
		}
	}

	/**
	 * 인증유형 일치 여부
	 * 
	 * @param vpAuthType VP 인증유형
	 * @param authType 서비스 인증유형
	 * @return 인증유형 일치 여부
	 */
	private Boolean isAuthTypeValid(List<String> vpAuthType, List<String> authType) {
		// 일반인증
		if (ObjectUtils.isEmpty(authType)) {
			return vpAuthType.stream()
					.map(AuthTypeEnum::getEnum)
					.anyMatch(e -> Objects.equals(AuthTypeEnum.PIN, e) || Objects.equals(AuthTypeEnum.BIO, e));
		}

		// 안심인증
		boolean hasPin = vpAuthType.stream()
				.map(AuthTypeEnum::getEnum)
				.anyMatch(e -> Objects.equals(AuthTypeEnum.PIN, e));
		boolean hasFace = vpAuthType.stream()
				.map(AuthTypeEnum::getEnum)
				.anyMatch(e -> Objects.equals(AuthTypeEnum.FACE, e));

		return hasPin && hasFace;
	}

}
