package mip.mva.sp.comm.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.vo.M900VO;
import mip.mva.sp.comm.vo.MipApiDataVO;
import mip.mva.sp.config.ConfigBean;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.exception
 * @FileName    : SpExceptionHandler.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : SP Exception Controller Advice
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@RestControllerAdvice
public class SpExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpExceptionHandler.class);

	/**
	 * SP Exception 핸들러
	 * 
	 * @param e SP Exception
	 * @return MipApiDataVO
	 */
	@ExceptionHandler(SpException.class)
	public MipApiDataVO handleSpException(SpException e) {
		MipApiDataVO mipApiData = new MipApiDataVO();

		M900VO m900 = new M900VO();

		m900.setType(ConfigBean.TYPE);
		m900.setVersion(ConfigBean.VERSION);
		m900.setCmd(ConfigBean.M900);
		m900.setErrcode(e.getErrcode());
		m900.setErrmsg(e.getErrmsg());
		m900.setTrxcode(e.getTrxcode());

		mipApiData.setResult(false);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(m900)));

		LOGGER.error("code[{}], msg[{}], trxcode[{}]", e.getErrcode(), e.getErrmsg(), e.getTrxcode(), e);

		return mipApiData;
	}

	/**
	 * PUSH Exception 핸들러
	 * 
	 * @param e PUSH Exception
	 * @return MipApiDataVO
	 */
	@ExceptionHandler(PushException.class)
	public MipApiDataVO handlePushException(PushException e) {
		MipApiDataVO mipApiData = new MipApiDataVO();

		M900VO m900 = new M900VO();

		m900.setType(ConfigBean.TYPE);
		m900.setVersion(ConfigBean.VERSION);
		m900.setCmd(ConfigBean.M900);
		m900.setErrcode(e.getErrcode());
		m900.setErrmsg(e.getErrmsg());
		m900.setTrxcode(e.getTrxcode());

		mipApiData.setResult(false);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(m900)));

		LOGGER.error("code[{}], msg[{}], trxcode[{}]", e.getErrcode(), e.getErrmsg(), e.getTrxcode(), e);

		return mipApiData;
	}

	/**
	 * Exception 핸들러
	 * 
	 * @param e Exception
	 * @return MipApiDataVO
	 */
	@ExceptionHandler(Exception.class)
	public MipApiDataVO handleException(Exception e) {
		MipApiDataVO mipApiData = new MipApiDataVO();

		M900VO m900 = new M900VO();

		m900.setType(ConfigBean.TYPE);
		m900.setVersion(ConfigBean.VERSION);
		m900.setCmd(ConfigBean.M900);
		m900.setErrcode(MipErrorEnum.UNKNOWN_ERROR.getCode());
		m900.setErrmsg(MipErrorEnum.UNKNOWN_ERROR.getMsg());

		mipApiData.setResult(false);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(m900)));

		LOGGER.error("{} : {}", MipErrorEnum.UNKNOWN_ERROR.getCode(), e.getMessage(), e);

		return mipApiData;
	}

}
