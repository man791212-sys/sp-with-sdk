package mip.mva.sp.comm.enums;

import java.util.Objects;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.enums
 * @FileName    : RequestTypeEnum.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 요청유형 Enum
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public enum RequestTypeEnum {

	CMD_310_REQ("mip", "1.1.0", "310", "profile"), // Profile 요청
	CMD_400_REQ("mip", "1.1.0", "400", "presentation"), // 검증 요청
	;

	/** 유형 */
	private String type;
	/** 버전 */
	private String version;
	/** Command */
	private String cmd;
	/** Request */
	private String request;

	/**
	 * 생성자
	 * 
	 * @param type    유형
	 * @param version 버전
	 * @param cmd     Command
	 * @param request Request
	 */
	RequestTypeEnum(String type, String version, String cmd, String request) {
		this.type = type;
		this.version = version;
		this.cmd = cmd;
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public String getCmd() {
		return cmd;
	}

	public String getRequest() {
		return request;
	}

	/**
	 * Enum 조회
	 * 
	 * @param cmd Command
	 * @return RequestTypeEnum
	 */
	public static RequestTypeEnum getEnum(String cmd) {
		for (RequestTypeEnum item : values()) {
			if (Objects.equals(item.getCmd(), cmd)) {
				return item;
			}
		}

		return null;
	}

}
