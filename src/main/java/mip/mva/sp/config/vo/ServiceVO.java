package mip.mva.sp.config.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.config.vo
 * @FileName    : ServiceVO.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 서비스 설정 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class ServiceVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** SP명 */
	private String spName;
	/** 서비스명 */
	private String serviceName;
	/** 서비스코드 */
	private String svcCode;
	/** 제공유형 */
	private Integer presentType;
	/** 암호화유형 */
	private Integer encryptType;
	/** 키유형 */
	private Integer keyType;
	/** 인증유형 */
	private ArrayList<String> authType;
	/** 영지식 스키마명 */
	private ArrayList<String> zkpSchemaName;
	/**
	 * 값 자체를 제출하는 영지식 증명 항목 리스트. ["zkpaddr","zkpsex","zkpasort"] 과 같이 JSON 배열로 정의함
	 * zkpaddr: 주소(동 까지), zkpsex: 성별, zkpasort: 면허종별
	 */
	private ArrayList<String> attrList;
	/**
	 * 검증을 위한 조건을 제시하여 조건에 맞음을 검증하는 영지식 증명 항목 리스트
	 * [{"zkpbirth":{"type":"LE","value":"19"}}] 과 같이 JSON 배열로 정의해야 함 현재는
	 * zkpbirth(생년월일) 밖에 없음
	 */
	private ArrayList<Map<String, Object>> predList;

	/** 콜백URL */
	private String callBackUrl;

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getSvcCode() {
		return svcCode;
	}

	public void setSvcCode(String svcCode) {
		this.svcCode = svcCode;
	}

	public Integer getPresentType() {
		return presentType;
	}

	public void setPresentType(Integer presentType) {
		this.presentType = presentType;
	}

	public Integer getEncryptType() {
		return encryptType;
	}

	public void setEncryptType(Integer encryptType) {
		this.encryptType = encryptType;
	}

	public Integer getKeyType() {
		return keyType;
	}

	public void setKeyType(Integer keyType) {
		this.keyType = keyType;
	}

	public ArrayList<String> getAuthType() {
		return authType;
	}

	public void setAuthType(ArrayList<String> authType) {
		this.authType = authType;
	}

	public ArrayList<String> getZkpSchemaName() {
		return zkpSchemaName;
	}

	public void setZkpSchemaName(ArrayList<String> zkpSchemaName) {
		this.zkpSchemaName = zkpSchemaName;
	}

	public ArrayList<String> getAttrList() {
		return attrList;
	}

	public void setAttrList(ArrayList<String> attrList) {
		this.attrList = attrList;
	}

	public ArrayList<Map<String, Object>> getPredList() {
		return predList;
	}

	public void setPredList(ArrayList<Map<String, Object>> predList) {
		this.predList = predList;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

}
