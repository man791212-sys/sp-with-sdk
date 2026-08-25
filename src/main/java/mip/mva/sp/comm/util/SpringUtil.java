package mip.mva.sp.comm.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.util
 * @FileName    : SpringUtil.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : Spring Util
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Component
public class SpringUtil implements ApplicationContextAware {

	/** Context */
	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		setContext(applicationContext);
	}

	private static void setContext(ApplicationContext applicationContext) {
		SpringUtil.context = applicationContext;
	}

	/**
	 * Context 조회
	 *
	 * @return ApplicationContext 스프링 컨텍스트
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	/**
	 * Bean 조회 with Bean 명칭
	 *
	 * @param bean Bean 명칭
	 * @return Bean Object
	 */
	public static Object getBean(String bean) {
		return context.getBean(bean);
	}

	/**
	 * Bean 조회 with Bean 클래스 타입
	 *
	 * @param clazz Bean 클래스 타입
	 * @return Bean Object
	 */
	public static Object getBean(Class<?> clazz) {
		return context.getBean(clazz);
	}

	/**
	 * Bean 조회 with Bean 명칭 & Bean 클래스 타입
	 * @param bean Bean 명칭
	 * @param clazz Bean 클래스 타입
	 * @return Bean Object
	 */
	public static <T> T getBean(String bean, Class<T> type) {
		return context.getBean(bean, type);
	}

	/**
	 * Request 조회
	 *
	 * @return Request
	 */
	public static HttpServletRequest getServletRequest() {
		ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (ObjectUtils.isEmpty(servletRequestAttributes)) {
			throw new IllegalStateException("Request 정보를 가져올 수 없습니다.");
		}

		return servletRequestAttributes.getRequest();
	}

}
