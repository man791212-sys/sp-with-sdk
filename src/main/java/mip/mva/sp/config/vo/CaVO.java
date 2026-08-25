package mip.mva.sp.config.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.config.vo
 * @FileName    : CaVO.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : CA 설정 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class CaVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 앱 코드 */
	private String appCode;
	/** 앱 이름 */
	private String appName;
	/** 앱 아이콘 */
	private String appIcon;
	/** 앱 링크(URL Scheme) 안드로이드 */
	private String appLinkAos;
	/** 앱 링크(URL Scheme) 아이폰 */
	private String appLinkIos;
	/** 앱 링크(Intent-packcage) 안드로이드 */
	private String appLink2Aos;
	/** 앱 링크(Universal Link) 아이폰 */
	private String appLink2Ios;
	/** CA DID */
	private String caDid;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppLinkAos() {
		return appLinkAos;
	}

	public void setAppLinkAos(String appLinkAos) {
		this.appLinkAos = appLinkAos;
	}

	public String getAppLinkIos() {
		return appLinkIos;
	}

	public void setAppLinkIos(String appLinkIos) {
		this.appLinkIos = appLinkIos;
	}

	public String getAppLink2Aos() {
		return appLink2Aos;
	}

	public void setAppLink2Aos(String appLink2Aos) {
		this.appLink2Aos = appLink2Aos;
	}

	public String getAppLink2Ios() {
		return appLink2Ios;
	}

	public void setAppLink2Ios(String appLink2Ios) {
		this.appLink2Ios = appLink2Ios;
	}

	public String getCaDid() {
		return caDid;
	}

	public void setCaDid(String caDid) {
		this.caDid = caDid;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

}
