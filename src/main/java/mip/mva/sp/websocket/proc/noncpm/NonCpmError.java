package mip.mva.sp.websocket.proc.noncpm;

import java.util.Objects;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import mip.mva.sp.comm.enums.ProxyErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.ProxyService;
import mip.mva.sp.comm.util.SpringUtil;
import mip.mva.sp.comm.vo.WsInfoVO;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.websocket.vo.MsgError;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.websocket.proc.noncpm
 * @FileName    : NonCpmError.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Non CPM 오류 메세지 처리 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class NonCpmError {

	private static final Logger LOGGER = LoggerFactory.getLogger(NonCpmError.class);

	/**
	 * error 메세지 처리
	 * 
	 * @param message 메세지
	 * @param session Websocket 세션
	 * @param wsInfo Websocket 정보
	 * @throws SpException
	 */
	public void procError(String message, Session session, WsInfoVO wsInfo) throws SpException {
		LOGGER.error("message : {}", message);

		try {
			MsgError msgError = ConfigBean.gson.fromJson(message, MsgError.class);

			String trxcode = msgError.getTrxcode();
			String errmsg = msgError.getErrmsg();

			if (ObjectUtils.isEmpty(trxcode)) {
				LOGGER.error("msg : {} - trxcode", ProxyErrorEnum.MISSING_MANDATORY_ITEM.getMsg());
			} else {
				if (!Objects.equals(trxcode, wsInfo.getTrxcode())) {
					LOGGER.error("msg : {} - {}", ProxyErrorEnum.TRXCODE_NOT_FOUND.getMsg(), trxcode);
				}
			}

			ProxyService proxyService = (ProxyService) SpringUtil.getBean(ProxyService.class);

			proxyService.sendError(trxcode, errmsg);
		} catch (SpException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		wsInfo.setStatus(ConfigBean.ERROR);
	}

}
