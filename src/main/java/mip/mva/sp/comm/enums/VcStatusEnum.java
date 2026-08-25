package mip.mva.sp.comm.enums;

import java.util.Objects;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.enums
 * @FileName    : VcStatusEnum.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : VC 상태 Enum
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public enum VcStatusEnum {

	ACTIVE("ACTIVE"), // 활성화
	PAUSE("PAUSE"), // 일시정지
	NEED_RENEW("NEED_RENEW"), // 갱신필요
	REMOVE("REMOVE"), // 제거
	NOT_EXIST("NOT_EXIST"), // 존재하지 않음
	;

	/** VC 상태 값 */
	private String val;

	/**
	 * 생성자
	 * 
	 * @param val VC 상태 값
	 */
	VcStatusEnum(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	/**
	 * Enum 조회
	 * 
	 * @param val Enum Value
	 * @return VcStatusEnum
	 */
	public static VcStatusEnum getEnum(String val) {
		for (VcStatusEnum item : values()) {
			if (Objects.equals(item.getVal(), val.toUpperCase())) {
				return item;
			}
		}

		return null;
	}

}
