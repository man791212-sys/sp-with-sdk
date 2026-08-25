package mip.mva.sp.websocket.proc.cpm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import mip.mva.sp.comm.vo.WsInfoVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.proc.cpm
 * @FileName    : CpmDefaultProc.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Default 메세지 처리 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CpmDefaultProc {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpmDefaultProc.class);

	/**
	 * default 메세지 처리
	 * 
	 * @param message 메세지
	 * @param session Websocket 세션
	 * @param wsInfo Websocket 정보
	 */
	public void procDefault(String message, WebSocketSession session, WsInfoVO wsInfo) {
		// default 처리를 위해 있는 class 로서 현재로서는 아무것도 하지 않고 들어온 메세지는 discard 된다.
		LOGGER.debug("message : {}", message);
	}

}
