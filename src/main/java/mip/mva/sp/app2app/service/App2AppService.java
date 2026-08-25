package mip.mva.sp.app2app.service;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.T530VO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.app2app.service
 * @FileName    : App2AppService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : App to App 인터페이스 검증 처리 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface App2AppService {

	/**
	 * App to App 시작
	 * 
	 * @param t530 App to App 정보
	 * @return App to App 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	T530VO start(T530VO t530) throws SpException;

}
