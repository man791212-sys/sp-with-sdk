package mip.mva.sp.websocket.client.cpm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.WsInfoVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.client.cpm
 * @FileName    : CpmClient.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : CPM 클라이언트 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CpmClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpmClient.class);

	/**
	 * 웹소켓 시작
	 * 
	 * @throws SpException 
	 */
	public void start(WsInfoVO wsInfo) throws SpException {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();

		container.setDefaultMaxTextMessageBufferSize(3145728);
		container.setDefaultMaxBinaryMessageBufferSize(3145728);
		container.setDefaultMaxSessionIdleTimeout(60000L);

		StandardWebSocketClient client = new StandardWebSocketClient(container);

		try {
			String url = wsInfo.getConnUrl();

			LOGGER.debug("connecting to : {}", url);

			CompletableFuture<WebSocketSession> future = client.execute(new CpmClientMsgHandler(wsInfo), url);

			future.get(wsInfo.getTimeout(), TimeUnit.SECONDS);		
		} catch (TimeoutException e) {
			throw new SpException(MipErrorEnum.SP_NETWORK_ERROR, wsInfo.getTrxcode(), "웹소켓 연결 타임아웃");
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.SP_NETWORK_ERROR, wsInfo.getTrxcode(), e.getMessage());
		}
	}

}
