package mip.mva.sp.comm.exception;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.exception
 * @FileName    : PushException.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : PUSH Exception
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class PushException extends Exception {

	private static final long serialVersionUID = 1L;

	/** 오류코드 */
	private final Integer errcode;
	/** 오류메세지 */
	private final String errmsg;
	/** 거래코드 */
	private final String trxcode;

	/**
	 * 생성자
	 * 
	 * @param errcode 오류코드
	 * @param errmsg 오류메세지
	 * @param trxcode 거래코드
	 */
	public PushException(Integer errcode, String errmsg, String trxcode) {
		super();

		this.errcode = errcode;
		this.errmsg = errmsg;
		this.trxcode = trxcode;
	}

	public String getTrxcode() {
		return trxcode;
	}

	public Integer getErrcode() {
		return errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

}
