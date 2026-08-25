package mip.mva.sp.websocket.proc.cpm;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import mip.mva.sp.comm.enums.ProxyErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.ProxyService;
import mip.mva.sp.comm.util.Base64Util;
import mip.mva.sp.comm.util.SpringUtil;
import mip.mva.sp.comm.vo.WsInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.websocket.vo.MsgError;
import mip.mva.sp.websocket.vo.MsgVerifyVerifier;
import mip.mva.sp.websocket.vo.MsgWaitVerify;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.proc.cpm
 * @FileName    : CpmVerify.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : CPM 검증자 검증 메세지 처리 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CpmVerify {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpmVerify.class);

	/**
	 * wait_verify 메세지 처리
	 * 
	 * @param message 메세지
	 * @param session Websocket 세션
	 * @param wsInfo Websocket 정보
	 * @throws SpException
	 */
	public void procWaitVerify(String message, WebSocketSession session, WsInfoVO wsInfo) throws SpException {
		LOGGER.debug("message : {}", message);

		String trxcode = null;
		String sendMsg = null;

		try {
			MsgWaitVerify msgWaitVerify = ConfigBean.gson.fromJson(message, MsgWaitVerify.class);

			trxcode = msgWaitVerify.getTrxcode();

			if (ObjectUtils.isEmpty(trxcode)) {
				throw new SpException(ProxyErrorEnum.MISSING_MANDATORY_ITEM, null, "trxcode");
			} else {
				if (!Objects.equals(trxcode, wsInfo.getTrxcode())) {
					throw new SpException(ProxyErrorEnum.TRXCODE_NOT_FOUND, null);
				}
			}

			String nonce = msgWaitVerify.getNonce();

			if (ObjectUtils.isEmpty(nonce)) {
				throw new SpException(ProxyErrorEnum.MISSING_MANDATORY_ITEM, null, "nonce");
			}

			// DID Assertion 생성
			ProxyService proxyService = (ProxyService) SpringUtil.getBean(ProxyService.class);

			String didAssertion = Base64Util.encode(proxyService.makeDIDAssertion(nonce));

			// Verify 응답 메시지 생성
			MsgVerifyVerifier msgVerifyVerifier = new MsgVerifyVerifier(trxcode, didAssertion);

			sendMsg = ConfigBean.gson.toJson(msgVerifyVerifier);
		} catch (SpException e) {
			MsgError msgError = new MsgError(wsInfo.getTrxcode(), e.getErrcode(), e.getErrmsg());

			sendMsg = ConfigBean.gson.toJson(msgError);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			MsgError msgError = new MsgError(wsInfo.getTrxcode(), ProxyErrorEnum.UNKNOWN_ERROR.getCode(), ProxyErrorEnum.UNKNOWN_ERROR.getMsg());

			sendMsg = ConfigBean.gson.toJson(msgError);
		}

		LOGGER.debug("sendMsg : {}", sendMsg);

		try {
			session.sendMessage(new TextMessage(sendMsg));

			wsInfo.setResult(sendMsg);
			wsInfo.setStatus(ConfigBean.WAIT_VERIFY);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);

			throw new SpException(ProxyErrorEnum.UNKNOWN_ERROR, trxcode, "sendString");
		}
	}

}
