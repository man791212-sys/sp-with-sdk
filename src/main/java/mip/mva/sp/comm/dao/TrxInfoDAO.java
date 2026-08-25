package mip.mva.sp.comm.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.dao.DataAccessException;

import mip.mva.sp.comm.vo.TrxInfoVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.dao
 * @FileName    : TrxInfoDAO.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 거래정보 DAO
 *
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Mapper
public interface TrxInfoDAO {

	/**
	 * 거래정보 조회
	 *
	 * @param trxcode 거래코드
	 * @return 거래정보
	 * @throws DataAccessException
	 */
	TrxInfoVO selectTrxInfo(String trxcode) throws DataAccessException;

	/**
	 * 거래정보 등록
	 *
	 * @param trxInfo 거래정보
	 * @throws DataAccessException
	 */
	void insertTrxInfo(TrxInfoVO trxInfo) throws DataAccessException;

	/**
	 * 거래정보 수정
	 *
	 * @param trxInfo 거래정보
	 * @throws DataAccessException
	 */
	void updateTrxInfo(TrxInfoVO trxInfo) throws DataAccessException;

	/**
	 * 거래정보 삭제
	 *
	 * @param trxcode 거래코드
	 * @throws DataAccessException
	 */
	void deleteTrxInfo(String trxcode) throws DataAccessException;

}
