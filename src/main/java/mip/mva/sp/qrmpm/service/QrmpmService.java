package mip.mva.sp.qrmpm.service;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.T510VO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.qrmpm.service
 * @FileName    : QrmpmService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : QR-MPM 인터페이스 검증 처리 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface QrmpmService {

	/**
	 * QR-MPM 시작
	 * 
	 * @param t510 QR-MPM 정보
	 * @return QR-MPM 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	T510VO start(T510VO t510) throws SpException;

}
