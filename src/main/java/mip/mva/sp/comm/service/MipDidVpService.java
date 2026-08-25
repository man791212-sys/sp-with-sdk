package mip.mva.sp.comm.service;

import java.util.List;
import java.util.Map;

import com.raonsecure.omnione.core.data.iw.Unprotected;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.TrxInfoVO;
import mip.mva.sp.comm.vo.VP;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service
 * @FileName    : MipDidVpService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : VP 검증 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface MipDidVpService {

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
	 * @param vp      VP 정보
	 * @return 검증 성공 여부
	 * @throws SpException
	 */
	Boolean verifyVp(TrxInfoVO trxInfo, VP vp) throws SpException;

	/**
	 * VP 재검증(부인방지)
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
	 * @return 복호화된 VP data
	 * @throws SpException
	 */
	String getVPData(VP vp) throws SpException;

	/**
	 * Privacy 조회
	 * 
	 * @param trxcode 거래코드
	 * @return Privacy 목록
	 * @throws SpException
	 */
	List<Unprotected> getPrivacy(String trxcode) throws SpException;

	/**
	 * Privacy 조회 - with VC Type
	 * 
	 * @param trxcode 거래코드
	 * @return Privacy 목록
	 * @throws SpException
	 */
	List<Map<String, String>> getPrivacyWithVcType(String trxcode) throws SpException;

	/**
	 * 이미지 변환(Hex String to byte Array)
	 * 
	 * @param imageData String
	 * @return 변환된 이미지 데이터
	 * @throws SpException
	 */
	byte[] transImageHexToByte(String imageData) throws SpException;

	/**
	 * CA명 조회
	 * 
	 * @param trxcode 거래코드
	 * @return CA명
	 * @throws SpException
	 */
	String getCaName(String trxcode) throws SpException;

}
