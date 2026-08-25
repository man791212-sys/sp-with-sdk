package mip.mva.sp.comm.enums;

import java.util.Objects;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.enums
 * @FileName    : PresentTypeEnum.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 제공유형 Enum
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public enum PresentTypeEnum {

	DID_AUTH(0), // 인증
	DID_VP(1), // VP
	ZKP_VP(2), // 영지식
	;

	/** 제공유형 값 */
	private Integer val;

	/**
	 * 생성자
	 * 
	 * @param val 제공유형 값
	 */
	PresentTypeEnum(Integer val) {
		this.val = val;
	}

	public Integer getVal() {
		return val;
	}

	/**
	 * Enum 조회
	 * 
	 * @param val Enum Value
	 * @return PresentTypeEnum
	 */
	public static PresentTypeEnum getEnum(Integer val) {
		for (PresentTypeEnum item : values()) {
			if (Objects.equals(item.getVal(), val)) {
				return item;
			}
		}

		return null;
	}

}
