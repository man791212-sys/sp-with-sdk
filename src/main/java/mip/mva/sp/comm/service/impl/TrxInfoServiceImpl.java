package mip.mva.sp.comm.service.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import mip.mva.sp.comm.dao.TrxInfoDAO;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.service.TrxInfoService;
import mip.mva.sp.comm.vo.TrxInfoVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.service.impl
 * @FileName    : TrxInfoServiceImpl.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 거래정보 ServiceImpl
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Service("trxInfoService")
public class TrxInfoServiceImpl implements TrxInfoService {

	/** 거래정보 DAO */
	private final TrxInfoDAO trxInfoDAO;

	/**
	 * 생성자
	 * 
	 * @param trxInfoDAO 거래정보 DAO
	 */
	public TrxInfoServiceImpl(TrxInfoDAO trxInfoDAO) {
		this.trxInfoDAO = trxInfoDAO;
	}

	/**
	 * 거래정보 조회
	 * 
	 * @param trxcode 거래코드
	 * @return 거래정보
	 * @throws SpException
	 */
	@Override
	public TrxInfoVO getTrxInfo(String trxcode) throws SpException {
		TrxInfoVO trxInfo = null;

		try {
			trxInfo = trxInfoDAO.selectTrxInfo(trxcode);

			if (ObjectUtils.isEmpty(trxInfo)) {
				throw new SpException(MipErrorEnum.SP_TRXCODE_NOT_FOUND, trxcode);
			}
		} catch (SpException e) {
			throw e;
		} catch (DataAccessException e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, trxcode, e);
		}

		return trxInfo;
	}

	/**
	 * 거래정보 등록
	 * 
	 * @param trxInfo 거래정보
	 * @throws SpException
	 */
	@Override
	public void registTrxInfo(TrxInfoVO trxInfo) throws SpException {
		try {
			trxInfoDAO.insertTrxInfo(trxInfo);
		} catch (DataAccessException e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, trxInfo.getTrxcode(), e);
		}
	}

	/**
	 * 거래정보 수정
	 * 
	 * @param trxInfo 거래정보
	 * @throws SpException
	 */
	@Override
	public void modifyTrxInfo(TrxInfoVO trxInfo) throws SpException {
		try {
			trxInfoDAO.updateTrxInfo(trxInfo);
		} catch (DataAccessException e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, trxInfo.getTrxcode(), e);
		}
	}

	/**
	 * 거래정보 삭제
	 * 
	 * @param trxcode 거래코드
	 * @throws SpException
	 */
	@Override
	public void removeTrxInfo(String trxcode) throws SpException {
		try {
			trxInfoDAO.deleteTrxInfo(trxcode);
		} catch (DataAccessException e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, trxcode, e);
		}
	}

}
