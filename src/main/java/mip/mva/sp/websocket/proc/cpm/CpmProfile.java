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
import mip.mva.sp.comm.util.SpringUtil;
import mip.mva.sp.comm.vo.WsInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.websocket.vo.MsgError;
import mip.mva.sp.websocket.vo.MsgProfile;
import mip.mva.sp.websocket.vo.MsgWaitProfile;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.proc.cpm
 * @FileName    : CpmProfile.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : CPM Profile 메세지 처리 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CpmProfile {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpmProfile.class);

	/**
	 * wait_profile 메세지 처리
	 * 
	 * @param message 메세지
	 * @param session Websocket 세션
	 * @param wsInfo Websocket 정보
	 * @throws SpException
	 */
	public void procWaitProfile(String message, WebSocketSession session, WsInfoVO wsInfo) throws SpException {
		LOGGER.debug("message : {}", message);

		String trxcode = null;
		String sendMsg = null;

		try {
			MsgWaitProfile msgWaitProfile = ConfigBean.gson.fromJson(message, MsgWaitProfile.class);

			trxcode = msgWaitProfile.getTrxcode();

			if (ObjectUtils.isEmpty(trxcode)) {
				throw new SpException(ProxyErrorEnum.MISSING_MANDATORY_ITEM, null, "trxcode");
			} else {
				if (!Objects.equals(trxcode, wsInfo.getTrxcode())) {
					throw new SpException(ProxyErrorEnum.TRXCODE_NOT_FOUND, trxcode);
				}
			}

			ConfigBean configBean = (ConfigBean) SpringUtil.getBean(ConfigBean.class);

			ProxyService proxyService = (ProxyService) SpringUtil.getBean(ProxyService.class);

			String profile = proxyService.getProfile(trxcode);
			String image = configBean.getVerifyConfig().getSp().getBiImageBase64();
			Boolean ci = configBean.getVerifyConfig().getSp().getIsCi();
			Boolean telno = configBean.getVerifyConfig().getSp().getIsTelno();

			MsgProfile msgProfile = new MsgProfile(trxcode, profile, image, ci, telno);

			sendMsg = ConfigBean.gson.toJson(msgProfile);
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
			wsInfo.setStatus(ConfigBean.WAIT_PROFILE);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);

			throw new SpException(ProxyErrorEnum.UNKNOWN_ERROR, trxcode, "sendString");
		}
	}

}
