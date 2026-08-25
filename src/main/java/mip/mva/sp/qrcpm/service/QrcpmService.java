package mip.mva.sp.qrcpm.service;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.T520VO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.qrcpm.service
 * @FileName    : QrcpmService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : QR-CPM 인터페이스 검증 처리 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface QrcpmService {

	/**
	 * QR-CPM 시작
	 * 
	 * @param t520 QR-CPM 정보
	 * @return QR-CPM 정보
	 * @throws SpException
	 */
	T520VO start(T520VO t520) throws SpException;

}
