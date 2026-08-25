package mip.mva.sp.web.service;

import mip.mva.sp.comm.exception.PushException;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.T510VO;
import mip.mva.sp.comm.vo.T530VO;
import mip.mva.sp.comm.vo.T540VO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.web.service
 * @FileName    : WebService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 웹 검증 처리 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface WebService {

	/**
	 * 푸시 시작
	 * 
	 * @param t540 푸시 정보
	 * @return 푸시 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException, PushException
	 */
	T540VO pushStart(T540VO t540) throws SpException, PushException;

	/**
	 * QR-MPM 시작
	 * 
	 * @param t510 QR-MPM 정보
	 * @return QR-MPM 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	T510VO qrStart(T510VO t510) throws SpException;

	/**
	 * App to App 시작
	 * 
	 * @param t530 App to App 정보
	 * @return App to App 정보 + Base64로 인코딩된 M200 메시지
	 * @throws SpException
	 */
	T530VO app2AppStart(T530VO t530) throws SpException;

	/**
	 * 인증 완료
	 * 
	 * @param trxcode 거래코드
	 * @throws SpException
	 */
	void authCmpl(String trxcode) throws SpException;

	/**
	 * RSA 암호화
	 * 
	 * @param data 평문 데이터
	 * @param targetDid 복호화 대상 DID
	 * @param isOaep OAEP 적용여부
	 * @return 암호화 데이터
	 */
	String rsaEncrypt(String data, String targetDid, Boolean isOaep) throws SpException;

	/**
	 * RSA 복호화
	 * 
	 * @param data 암호화 데이터
	 * @param isOaep OAEP 적용여부
	 * @return 복호화 데이터
	 */
	String rsaDecrypt(String data, Boolean isOaep) throws SpException;

}
