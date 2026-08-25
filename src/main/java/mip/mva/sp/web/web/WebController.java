package mip.mva.sp.web.web;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mip.mva.sp.comm.exception.PushException;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.vo.MipApiDataVO;
import mip.mva.sp.comm.vo.T510VO;
import mip.mva.sp.comm.vo.T530VO;
import mip.mva.sp.comm.vo.T540VO;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.web.service.WebService;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.web.web
 * @FileName    : WebController.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Web 서비스 Controller
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@RestController
@RequestMapping("/web")
public class WebController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

	/** Web Service */
	private final WebService webService;

	/**
	 * 생성자
	 * 
	 * @param webService Web Service
	 */
	public WebController(WebService webService) {
		this.webService = webService;
	}

	/**
	 * 푸시 시작
	 *
	 * @param t540 푸시 정보
	 * @return {"result": true, data: 푸시 정보 + Base64로 인코딩된 M200 메시지}
	 * @throws SpException, PushException
	 */
	@PostMapping("/push/start")
	public MipApiDataVO pushStart(@RequestBody T540VO t540) throws SpException, PushException {
		LOGGER.debug("Push 시작!");

		T540VO data = webService.pushStart(t540);

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(data)));

		return mipApiData;
	}

	/**
	 * QR-MPM 시작
	 *
	 * @param t510 QR-MPM 정보
	 * @return {"result": true, data: QR-MPM 정보 + Base64로 인코딩된 M200 메시지}
	 * @throws SpException
	 */
	@PostMapping("/qr/start")
	public MipApiDataVO qrStart(@RequestBody T510VO t510) throws SpException {
		LOGGER.debug("QR-MPM 시작!");

		T510VO data = webService.qrStart(t510);

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(data)));

		return mipApiData;
	}

	/**
	 * App to App 시작
	 *
	 * @param t530 App to App 정보
	 * @return {"result": true, data: App to App 정보 + Base64로 인코딩된 M200 메시지}
	 * @throws SpException
	 */
	@PostMapping("/app2app/start")
	public MipApiDataVO app2AppStart(@RequestBody T530VO t530) throws SpException {
		LOGGER.debug("App to App 시작!");

		T530VO data = webService.app2AppStart(t530);

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(data)));

		return mipApiData;
	}

	/**
	 * 인증 완료
	 *
	 * @param trxInfo 거래정보
	 * @return {"result": true}
	 * @throws SpException
	 */
	@PostMapping("/auth/cmpl")
	public MipApiDataVO authCmpl(@RequestBody TrxInfoVO trxInfo) throws SpException {
		LOGGER.debug("인증 완료!");

		webService.authCmpl(trxInfo.getTrxcode());

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);

		return mipApiData;
	}

	/**
	 * RSA 암호화
	 * 
	 * @param {"data": 평문 데이터, "decryptTargetDid": 복호화 대상 DID}
	 * @return {"result": true, data: 암호화 데이터}
	 * @throws SpException
	 */
	@PostMapping("/rsa/encrypt")
	public MipApiDataVO rsaEncrypt(@RequestBody Map<String, String> data) throws SpException {
		LOGGER.debug("RSA 암호화!");

		String pranText = data.get("data");
		String decryptTargetDid = data.get("decryptTargetDid");
		Boolean isOaep = Objects.equals("true", data.get("isOaep"));

		String encryptData = webService.rsaEncrypt(pranText, decryptTargetDid, isOaep);

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);
		mipApiData.setData(encryptData);

		return mipApiData;
	}

	/**
	 * RSA 복호화
	 * 
	 * @param data 암호화 데이터
	 * @return {"result": true, data: 복호화 데이터}
	 * @throws SpException
	 */
	@PostMapping("/rsa/decrypt")
	public MipApiDataVO rsaDecrypt(@RequestBody Map<String, String> data) throws SpException {
		LOGGER.debug("RSA 복호화!");

		String encryptText = data.get("data");
		Boolean isOaep = Objects.equals("true", data.get("isOaep"));

		String decryptData = webService.rsaDecrypt(encryptText, isOaep);

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);
		mipApiData.setData(decryptData);

		return mipApiData;
	}

}
