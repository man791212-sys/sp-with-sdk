package mip.mva.sp.comm.util;

import java.nio.charset.StandardCharsets;

import com.google.common.io.BaseEncoding;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.util
 * @FileName    : Base64Util.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Base64 Util
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class Base64Util {

	/**
	 * 생성자- 인스턴스화 방지
	 */
	private Base64Util() {
        // utility class, non-instantiable
    }

	/**
	 * String to Base64 String
	 *
	 * @param text String
	 * @return Base64 String
	 */
	public static String encode(String text) {
		return BaseEncoding.base64Url().encode(text.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Byte to Base64 String
	 *
	 * @param data Byte 배열
	 * @return Base64 String
	 */
	public static String encode(byte[] data) {
		return BaseEncoding.base64Url().encode(data);
	}

	/**
	 * Base64 String to String
	 *
	 * @param text Base64 String
	 * @return String
	 */
	public static String decode(String text) {
		CharSequence textCS = text;

		return new String(BaseEncoding.base64Url().decode(textCS), StandardCharsets.UTF_8);
	}

	/**
	 * Base64 String to byte
	 *
	 * @param text Base64 인코딩된 문자열
	 * @return byte[]
	 */
	public static byte[] decodeToByte(String text) {
		CharSequence textCS = text;

		return BaseEncoding.base64Url().decode(textCS);
	}

}
