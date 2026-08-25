package mip.mva.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp
 * @FileName    : SpApplication.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Spring Boot Initializer
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@SpringBootApplication
public class SpApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(SpApplication.class);
	}

}
