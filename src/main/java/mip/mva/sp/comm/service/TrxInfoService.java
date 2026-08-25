package mip.mva.sp.comm.service;

import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.vo.TrxInfoVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service
 * @FileName    : TrxInfoService.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 거래정보 Service
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public interface TrxInfoService {

	/**
	 * 거래정보 조회
	 * 
	 * @param trxcode 거래코드
	 * @return 거래정보
	 * @throws SpException
	 */
	TrxInfoVO getTrxInfo(String trxcode) throws SpException;

	/**
	 * 거래정보 등록
	 * 
	 * @param trxInfo 거래정보
	 * @throws SpException
	 */
	void registTrxInfo(TrxInfoVO trxInfo) throws SpException;

	/**
	 * 거래정보 수정
	 * 
	 * @param trxInfo 거래정보
	 * @throws SpException
	 */
	void modifyTrxInfo(TrxInfoVO trxInfo) throws SpException;

	/**
	 * 거래정보 삭제
	 * 
	 * @param trxcode 거래코드
	 * @throws SpException
	 */
	void removeTrxInfo(String trxcode) throws SpException;

}
