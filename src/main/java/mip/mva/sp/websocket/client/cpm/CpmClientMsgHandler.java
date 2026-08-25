package mip.mva.sp.websocket.client.cpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.WsInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.websocket.vo.MsgJoin;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.client.cpm
 * @FileName    : CpmClientMsgHandler.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : CPM 클라이언트 메세지 핸들러 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CpmClientMsgHandler extends TextWebSocketHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpmClientMsgHandler.class);

	private final WsInfoVO wsInfo;
	
	private static final CpmBranch CPM_BRANCH = new CpmBranch();

	/**
	 * 생성자
	 * 
	 * @param wsInfo 웹소켓 정보
	 */
	public CpmClientMsgHandler(WsInfoVO wsInfo) {
		this.wsInfo = wsInfo;
	}

	/**
	 * 웹소켓 연결
	 * 
	 * @param session 웹소켓 세션
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		LOGGER.debug("connected: {}", session.getId());

		try {
			MsgJoin msgJoin = new MsgJoin(wsInfo.getTrxcode());

			String jsonString = ConfigBean.gson.toJson(msgJoin);

			LOGGER.debug("send: {}", jsonString);

			session.sendMessage(new TextMessage(jsonString));
		} catch (Throwable t) {
			LOGGER.error(t.getMessage(), t);
		}
	}

	/**
	 * 메시지 수신
	 * 
	 * @param session 웹소켓 세션
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		String payload = message.getPayload();

		LOGGER.debug("received: {}", payload);

		if ((session != null) && (session.isOpen())) {
			try {
				CPM_BRANCH.packetChoose(payload, session, wsInfo);
			} catch (SpException e) {
				LOGGER.error("시스템 IO 에러", e);
			}
		} else {
			LOGGER.error("존재하지 않는 세션이거나 종료된 세션입니다.");
		}
	}

	/**
	 * 웹소켓 종료
	 * 
	 * @param session 웹소켓 세션
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		LOGGER.debug("connection closed: {} - {}", status.getCode(), status.getReason());
	}

	/**
	 * 웹소켓 오류
	 * 
	 * @param session 웹소켓 세션
	 */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) {
		LOGGER.error("connection error: {}", exception.getMessage());
	}

}
