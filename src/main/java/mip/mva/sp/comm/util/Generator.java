package mip.mva.sp.comm.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.util.ObjectUtils;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.util
 * @FileName    : Generator.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 난수 생성 Util
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class Generator {

	/** SecureRandom 인스턴스 - 멀티스레드 환경에서 안전(thread-safe) */
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	/** 트랜잭션 코드 포맷터(24자리, thread-safe) */
	private static final DateTimeFormatter TRX_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS", Locale.KOREA);

	/** Nonce 포맷터(24자리, thread-safe) */
	private static final DateTimeFormatter NONCE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSS", Locale.KOREA);

	/**
	 * 생성자- 인스턴스화 방지
	 */
	private Generator() {
        // utility class, non-instantiable
    }

	/**
	 * 거래코드 생성 - 현재시간 yyyyMMddHHmmssSSS + 시큐어난수 (8자리)
	 * 
	 * @return 거래코드
	 */
	public static String genTrxcode() {
		String first = LocalDateTime.now().format(TRX_FORMATTER);
		String second = secRandom(4); // 4자리 생성하고 hex code로 표현되므로 8개 자리가 나옴
		
		return first + second;
	}

	/**
	 * Nonce 생성
	 *
	 * @return Nonce
	 */
	public static String genNonce() {
		String first = LocalDateTime.now().format(NONCE_FORMATTER);
		String second = secRandom(11); // 16진수 11개 자릿수 -> 스트링 -> 바이트배열 22바이트 + 18바이트 = 40바이트
		
		return first + second;
	}

	/**
	 * 난수 생성
	 *
	 * @param genNum 생성할 키의 길이
	 * @return 난수
	 */
	public static String secRandom(int genNum) {
		byte[] bytes = new byte[genNum];

		SECURE_RANDOM.nextBytes(bytes);

		return bytesToHexString(bytes);
	}

	/** Base Hex Chars */
	private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

	/**
	 * Byte Array to Hex String
	 *
	 * @param bytes Byte Array
	 * @return Hex String
	 */
	public static String bytesToHexString(byte[] bytes) {
		if (ObjectUtils.isEmpty(bytes)) {
			return null;
		}

		char[] hexChars = new char[bytes.length * 2];

		for (int i = 0; i < bytes.length; i++) {
			int value = bytes[i] & 0xff;

			hexChars[i * 2] = HEX_CHARS[value >>> 4];
			hexChars[i * 2 + 1] = HEX_CHARS[value & 0x0f];
		}

		return new String(hexChars);
	}

}
