package mip.mva.sp.comm.service.impl;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.raonsecure.omnione.core.data.iw.profile.EncryptKeyTypeEnum;
import com.raonsecure.omnione.core.data.iw.profile.EncryptTypeEnum;

import mip.mva.sp.comm.enums.AuthTypeEnum;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.PresentTypeEnum;
import mip.mva.sp.comm.enums.TrxStsCodeEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.MipDidVpService;
import mip.mva.sp.comm.service.MipZkpVpService;
import mip.mva.sp.comm.service.ProxyService;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.util.VerifyManager;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.config.vo.ServiceVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service.impl
 * @FileName    : ProxyServiceImpl.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Proxy 검증 ServiceImpl
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Service("proxyService")
public class ProxyServiceImpl implements ProxyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServiceImpl.class);

	/** 설정정보 */
	private final ConfigBean configBean;

	/** 검증 Manager */
	private final VerifyManager verifyManager;

	/** 거래정보 Service */
	private final TrxInfoService trxInfoService;

	/** VP 검증 Service */
	private final MipDidVpService mipDidVpService;

	/** 영지식 VP 검증 Service */
	private final MipZkpVpService mipZkpVpService;

	/**
	 * 생성자
	 * 
	 * @param configBean      설정정보
	 * @param verifyManager   검증 Manager
	 * @param trxInfoService  거래정보 Service
	 * @param mipDidVpService VP 검증 Service
	 * @param mipZkpVpService 영지식 VP 검증 Service
	 */
	public ProxyServiceImpl(ConfigBean configBean, VerifyManager verifyManager, TrxInfoService trxInfoService, MipDidVpService mipDidVpService, MipZkpVpService mipZkpVpService) {
		this.configBean = configBean;
		this.verifyManager = verifyManager;
		this.trxInfoService = trxInfoService;
		this.mipDidVpService = mipDidVpService;
		this.mipZkpVpService = mipZkpVpService;
	}

	/**
	 * DID Assertion 생성
	 * 
	 * @param nonce Nonce
	 * @return DID Assertion
	 * @throws SpException
	 */
	@Override
	public String makeDIDAssertion(String nonce) throws SpException {
		LOGGER.debug("nonce : {}", nonce);

		return verifyManager.makeDIDAssertion(nonce);
	}

	/**
	 * Profile 요청
	 * 
	 * @param trxcode 거래코드
	 * @return Base64로 인코딩된 Profile
	 * @throws SpException
	 */
	@Override
	public String getProfile(String trxcode) throws SpException {
		LOGGER.debug("trxcode : {}", trxcode);

		String profile = "";

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

			ServiceVO service = configBean.getVerifyConfig().getServices().get(trxInfo.getSvcCode());

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, trxcode, "service does not exist");
			}

			Integer presentType = service.getPresentType();

			if (Objects.equals(PresentTypeEnum.DID_VP.getVal(), presentType)) {
				profile = mipDidVpService.getProfile(trxInfo);
			} else if (Objects.equals(PresentTypeEnum.ZKP_VP.getVal(), presentType)) {
				profile = mipZkpVpService.getProfile(trxInfo);
			} else {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE, trxcode);
			}
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, trxcode, e);
		}

		return profile;
	}

	/**
	 * VP 검증
	 * 
	 * @param trxcode 거래코드
	 * @param vp      VP 정보
	 * @return 검증 결과
	 * @throws SpException
	 */
	@Override
	public Boolean verifyVp(String trxcode, VP vp) throws SpException {
		LOGGER.debug("trxcode : {}, vp : {}", trxcode, ConfigBean.gson.toJson(vp));

		Boolean result = false;

		try {
			TrxInfoVO trxInfo = trxInfoService.getTrxInfo(trxcode);

			if (ObjectUtils.isEmpty(trxInfo)) {
				throw new SpException(MipErrorEnum.SP_TRXINFO_NOT_FOUND, trxcode);
			}

			if (!Objects.equals(TrxStsCodeEnum.PROFILE_REQ.getVal(), trxInfo.getTrxStsCode())) {
				throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode, "stsCode != 0002");
			}

			if (Objects.equals("Y", trxInfo.getVpVerifyResult())) {
				throw new SpException(MipErrorEnum.SP_MSG_SEQ_ERROR, trxcode, "verifyResult == Y");
			}

			this.validateVp(trxInfo, vp);

			Integer presentType = vp.getPresentType();

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
	 * @param trxcode 거래코드
	 * @param errmsg  오류 메세지
	 * @throws SpException
	 */
	@Override
	public void sendError(String trxcode, String errmsg) throws SpException {
		TrxInfoVO trxInfo = new TrxInfoVO();

		trxInfo.setTrxcode(trxcode);
		trxInfo.setTrxStsCode(TrxStsCodeEnum.VERIFY_ERR.getVal());
		trxInfo.setErrorCn(errmsg);

		trxInfoService.modifyTrxInfo(trxInfo);
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

		ServiceVO service = configBean.getVerifyConfig().getServices().get(trxInfo.getSvcCode());

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
