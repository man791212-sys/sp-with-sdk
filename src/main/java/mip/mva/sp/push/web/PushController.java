package mip.mva.sp.push.web;

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
import mip.mva.sp.comm.vo.T540VO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.push.service.PushService;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.push.web
 * @FileName    : PushController.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 푸시 인터페이스 검증 처리 Controller
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@RestController
@RequestMapping("/push")
public class PushController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PushController.class);

	/** 푸시 Service */
	private final PushService pushService;

	/**
	 * 생성자
	 * 
	 * @param pushService 푸시 Service
	 */
	public PushController(PushService pushService) {
		this.pushService = pushService;
	}

	/**
	 * 푸시 시작
	 * 
	 * @param t540 푸시 정보
	 * @return {"result": true, data: 푸시 정보 + Base64로 인코딩된 M200 메시지}
	 * @throws SpException, PushException
	 */
	@PostMapping("/start")
	public MipApiDataVO start(@RequestBody T540VO t540) throws SpException, PushException {
		LOGGER.debug("Push 시작!");

		T540VO data = pushService.start(t540);

		MipApiDataVO mipApiData = new MipApiDataVO();

		mipApiData.setResult(true);
		mipApiData.setData(Base64Util.encode(ConfigBean.gson.toJson(data)));

		return mipApiData;
	}

}
