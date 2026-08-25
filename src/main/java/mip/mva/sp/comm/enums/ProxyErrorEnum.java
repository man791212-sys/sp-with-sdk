package mip.mva.sp.comm.enums;

import java.util.Objects;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.enums
 * @FileName    : ProxyErrorEnum.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Proxy 오류 Enum
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public enum ProxyErrorEnum {

	PACKET_ERROR(1001, "packet error"), // 패킷 오류
	MISSING_MANDATORY_ITEM(1002, "missing mandatory item"), // 필수 항목 누락
	INVALID_DATA(1010, "invalid data"), // 잘못된 데이터
	INVALID_MSG(1011, "invalid msg"), // 잘못된 메시지
	UNSUPPORTED_MESSAGE_VERSION(1013, "unsupported message version"), // 지원되지 않는 메시지 버전
	TRXCODE_NOT_FOUND(1014, "trxcode not found"), // 트랜잭션 코드 없음
	DID_AUTH_FAILED(1031, "DID auth failed"), // DID 인증 실패
	SEQUENCE_ERROR(2001, "sequence error"), // 시퀀스 오류
	TIMEOUT_ERROR(2002, "timeout error"), // 타임아웃 오류
	UNEXPECTED_DISCONNECTION(2003, "unexpected disconnection"), // 예상치 못한 연결 끊김
	MISMATCHING_NONCE(2004, "mismatching nonce"), // 일치하지 않는 nonce
	MISMATCHING_AUTH_TYPE(2005, "mismatching auth type"), // 일치하지 않는 인증 유형
	CLOSE_REQUESTED_BY_HOLDER(9001, "Close requested by holder"), // 소유자에 의해 종료 요청됨
	CLOSE_REQUESTED_BY_VERIFIER(9002, "Close requested by verifier"), // 검증자에 의해 종료 요청됨
	UNKNOWN_ERROR(9999, "unknown error") // 알 수 없는 오류
	;

	/** Proxy 오류코드 */
	private Integer code;
	/** Proxy 오류메세지 */
	private String msg;

	/**
	 * 생성자
	 * 
	 * @param code 오류코드
	 * @param msg 오류메세지
	 */
	ProxyErrorEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	/**
	 * Enum 조회
	 * 
	 * @param code Enum Code
	 * @return ProxyErrorEnum
	 */
	public static ProxyErrorEnum getEnum(Integer code) {
		for (ProxyErrorEnum item : values()) {
			if (Objects.equals(item.getCode(), code)) {
				return item;
			}
		}

		return null;
	}

}
