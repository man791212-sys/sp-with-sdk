package mip.mva.sp.comm.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.util
 * @FileName    : HttpUtil.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Http Call Util
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Component
public class HttpUtil {

	private final RestTemplate restTemplate;

	/**
	 * 생성자 (RestTemplate 초기화)
	 */
	public HttpUtil() {
		this.restTemplate = new RestTemplate();
	}

	/**
	 * Http Call(POST) 실행
	 *
	 * @param url URL
	 * @param param 요청 파라미터
	 * @return 응답
	 * @throws SpException
	 */
	public String executeHttpPost(String url, String param) throws SpException {
		try {
			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.valueOf("application/json; charset=UTF-8"));

			HttpEntity<String> entity = new HttpEntity<>(param, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

			return response.getBody();
		} catch (RestClientException e) {
			throw new SpException(MipErrorEnum.SP_NETWORK_ERROR, null, e);
		}
	}

}
