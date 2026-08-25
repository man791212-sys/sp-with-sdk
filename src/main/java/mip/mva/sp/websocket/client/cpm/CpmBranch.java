package mip.mva.sp.websocket.client.cpm;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.ProxyErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.WsInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.websocket.proc.cpm.CpmDefaultProc;
import mip.mva.sp.websocket.proc.cpm.CpmError;
import mip.mva.sp.websocket.proc.cpm.CpmProfile;
import mip.mva.sp.websocket.proc.cpm.CpmVerify;
import mip.mva.sp.websocket.proc.cpm.CpmVp;
import mip.mva.sp.websocket.vo.MsgError;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.client.cpm
 * @FileName    : CpmBranch.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : CPM 메세지 분기 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CpmBranch {

	private static final Logger LOGGER = LoggerFactory.getLogger(CpmBranch.class);

	private static final CpmVerify CPM_VERIFY = new CpmVerify();
	private static final CpmProfile CPM_PROFILE = new CpmProfile();
	private static final CpmVp CPM_VP = new CpmVp();
	private static final CpmError CPM_ERROR = new CpmError();
	private static final CpmDefaultProc CPM_DEFAULT_PROC = new CpmDefaultProc();

	/**
	 * 메세지 분기 처리
	 * 
	 * <pre>
	 * 패킷 헤더를 검사하여 각 패킷 처리 메서드로 분기한다.
	 * 연결을 끊는 것은 중계서버가 담당한다.
	 * 오류 메시지 생성 위치와 연결 끊기에 대해서는 다음과 같이 정의한다.
	 * 
	 * | 오류 메시지 생성 위치 | 오류 전파                      | 연결 끊기 |
	 * |-----------------------|--------------------------------|-----------|
	 * | 신분증앱              | 신분증앱 => 중계서버 => SP서버 | 중계서버  |
	 * | SP서버                | SP서버 => 중계서버 => 신분증앱 | 중계서버  |
	 * | 중계서버              | 중계서버 => 신분증앱, SP서버   | 중계서버  |
	 * 
	 * 중계서버는 오류를 수신하거나 생성 시 신분증앱과 SP서버 양쪽으로 모두 전파 후 양쪽 연결을 모두 끊는다.
	 * 그러므로 SP서버는 오류 메시지 송수신 후 별도로 연결을 끊을 필요가 없다.
	 * </pre>
	 * 
	 * @param message 메세지
	 * @param session 웹소켓 세션
	 * @param wsInfo 웹소켓 정보
	 * @throws SpException
	 */
	public void packetChoose(String message, WebSocketSession session, WsInfoVO wsInfo) throws SpException {
		LOGGER.debug("...............................packetChoose start..................................");
		LOGGER.debug("message: {}", message);

		String msg = "";

		try {
			JsonObject jsonObject = ConfigBean.gson.fromJson(message, JsonObject.class);

			msg = (jsonObject != null && jsonObject.has("msg")) ? jsonObject.get("msg").getAsString() : "";
		} catch (JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_UNEXPECTED_MSG_FORMAT, null, e);
		}

		if (ObjectUtils.isEmpty(msg)) {
			// message에 msg 항목이 없는 경우 에러 메시지를 전송하고 종료
			String trxcode = wsInfo.getTrxcode();

			trxcode = (trxcode != null) ? trxcode : "";

			MsgError msgError = new MsgError(trxcode, ProxyErrorEnum.MISSING_MANDATORY_ITEM.getCode(), ProxyErrorEnum.MISSING_MANDATORY_ITEM.getMsg());

			String sendMsg = ConfigBean.gson.toJson(msgError);

			LOGGER.error("Response Error Message: {}", sendMsg);

			try {
				session.sendMessage(new TextMessage(sendMsg));
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			switch (msg) {
				// DID Auth 요청 처리
				case ConfigBean.WAIT_VERIFY:
					LOGGER.debug("...............................received wait_verify.................................");

					CPM_VERIFY.procWaitVerify(message, session, wsInfo);

					break;
				// 프로파일 처리
				case ConfigBean.WAIT_PROFILE:
					LOGGER.debug("...............................received wait_profile................................");

					CPM_PROFILE.procWaitProfile(message, session, wsInfo);

					break;
				// VP 검증 처리
				case ConfigBean.VP:
					LOGGER.debug("...............................received vp..........................................");

					CPM_VP.procVp(message, session, wsInfo);

					break;
				// Error 처리
				case ConfigBean.ERROR:
					LOGGER.debug("...............................received error.......................................");

					CPM_ERROR.procError(message, session, wsInfo);

					break;
				default:
					LOGGER.debug("...............................received others......................................");

					CPM_DEFAULT_PROC.procDefault(message, session, wsInfo);

					break;
			}
		}
	}

}
