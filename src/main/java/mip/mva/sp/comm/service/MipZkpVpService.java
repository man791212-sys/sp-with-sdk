package mip.mva.sp.comm.service;

import java.util.List;
import java.util.Map;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service
 * @FileName    : MipZkpVpService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 영지식 VP 검증 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface MipZkpVpService {

	/**
	 * Profile 요청
	 * 
	 * @param trxInfo 거래정보
	 * @return Base64로 인코딩된 Profile
	 * @throws SpException
	 */
	String getProfile(TrxInfoVO trxInfo) throws SpException;

	/**
	 * VP 검증
	 * 
	 * @param trxInfo 거래정보
	 * @param vp VP 정보
	 * @return 검증 결과
	 * @throws SpException
	 */
	Boolean verifyVp(TrxInfoVO trxInfo, VP vp) throws SpException;

	/**
	 * VP data 조회
	 * 
	 * @param vp VP
	 * @return 복호화된 VP data
	 * @throws SpException
	 */
	String getVPData(VP vp) throws SpException;

	/**
	 * Privacy 조회(영지식)
	 * 
	 * @param trxcode String
	 * @return Privacy 목록(영지식)
	 * @throws SpException
	 */
	List<Map<String, String>> getPrivacyZkp(String trxcode) throws SpException;

	/**
	 * ZkpSchemaName 조회
	 * 
	 * @param trxcode String
	 * @return ZkpSchemaName 목록
	 * @throws SpException
	 */
	List<String> getZkpSchemaName(String trxcode) throws SpException;

}
