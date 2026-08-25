package mip.mva.sp.comm.enums;

import java.util.Objects;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.enums
 * @FileName    : ModeEnum.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 모드 Enum
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public enum ModeEnum {

	INDIRECT("indirect"), // indirect
	DIRECT("direct"), // direct
	PROXY("proxy"), // proxy
	P2P("p2p"), // p2p
	;

	/** 모드 값 */
	private String val;

	/**
	 * 생성자
	 * 
	 * @param val 모드 값
	 */
	ModeEnum(String val) {
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	/**
	 * Enum 조회
	 * 
	 * @param val Enum Value
	 * @return ModeEnum
	 */
	public static ModeEnum getEnum(String val) {
		for (ModeEnum item : values()) {
			if (Objects.equals(item.getVal(), val.toLowerCase())) {
				return item;
			}
		}

		return null;
	}

}
