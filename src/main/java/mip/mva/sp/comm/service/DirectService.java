package mip.mva.sp.comm.service;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.M200VO;
import mip.mva.sp.comm.vo.M310VO;
import mip.mva.sp.comm.vo.M320VO;
import mip.mva.sp.comm.vo.M400VO;
import mip.mva.sp.comm.vo.M900VO;
import mip.mva.sp.comm.vo.VP;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service
 * @FileName    : DirectService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Direct 검증 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface DirectService {

	/**
	 * M200 요청
	 * 
	 * @param ifType 인터페이스유형
	 * @param mode 모드
	 * @param svcCode 서비스코드
	 * @param includeProfile profile 포함 여부
	 * @return M200 메세지
	 * @throws SpException
	 */
	M200VO getM200(String ifType, String mode, String svcCode, Boolean includeProfile) throws SpException;

	/**
	 * Profile 요청
	 * 
	 * @param m310 M310 메세지
	 * @return M310 메세지 + Profile
	 * @throws SpException
	 */
	M310VO getProfile(M310VO m310) throws SpException;

	/**
	 * BI 이미지 요청
	 * 
	 * @param m320 M320 메세지
	 * @return Base64로 인코딩된 Image
	 * @throws SpException
	 */
	String getImage(M320VO m320) throws SpException;

	/**
	 * VP 검증
	 * 
	 * @param m400 M400메세지
	 * @return 검증 결과
	 * @throws SpException
	 */
	Boolean verifyVp(M400VO m400) throws SpException;

	/**
	 * 오류 전송
	 * 
	 * @param m900 M900 메세지
	 * @throws SpException
	 */
	void sendError(M900VO m900) throws SpException;

	/**
	 * VP 재검증 - 부인방지
	 * 
	 * @param vp VP 정보
	 * @return 검증 결과
	 * @throws SpException
	 */
	Boolean reVerifyVP(VP vp) throws SpException;

	/**
	 * VP data 조회
	 * 
	 * @param vp VP
	 * @throws SpException
	 */
	String getVPData(VP vp) throws SpException;

}
