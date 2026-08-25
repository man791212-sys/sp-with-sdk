package mip.mva.sp.comm.enums;

import java.util.Objects;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.enums
 * @FileName    : TrxStsCodeEnum.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 거래 상태 코드 Enum
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public enum TrxStsCodeEnum {

	SERCIVE_REQ("0001"), // 서비스 요청
	PROFILE_REQ("0002"), // Profile 요청
	VERIFY_REQ("0003"), // 검증 요청
	VERIFY_COM("0004"), // 검증 완료
	VERIFY_ERR("0005"), // 검증 오류
	VP_CHK("0006"), // VP 확인
	;

	/** 거래상태코드 값 */
	private String val;

	/**
	 * 생성자
	 * 
	 * @param val 거래상태코드 값
	 */
	TrxStsCodeEnum(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	/**
	 * Enum 조회
	 * 
	 * @param val Enum Value
	 * @return TrxStsCodeEnum
	 */
	public static TrxStsCodeEnum getEnum(String val) {
		for (TrxStsCodeEnum item : values()) {
			if (Objects.equals(item.getVal(), val)) {
				return item;
			}
		}

		return null;
	}

}
