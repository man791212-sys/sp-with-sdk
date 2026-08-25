package mip.mva.sp.config.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.config.vo
 * @FileName    : VerifyConfigVO.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 검증 설정 VO
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
public class VerifyConfigVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 블록체인 설정 */
	private BlockchainVO blockchain;
	/** DID 파일 설정 */
	private DidWalletFileVO didWalletFile;
	/** SP 설정 */
	private SpVO sp;
	/** 서비스 설정 */
	private HashMap<String, ServiceVO> services;
	/** 푸시 설정 */
	private PushVO push;
	/** DB 설정 */
	private DbVO db;

	/** 서비스 목록 */
	private ArrayList<ServiceVO> serviceList;
	/** CA 목록 */
	private ArrayList<CaVO> caList;

	public BlockchainVO getBlockchain() {
		return blockchain;
	}

	public void setBlockchain(BlockchainVO blockchain) {
		this.blockchain = blockchain;
	}

	public DidWalletFileVO getDidWalletFile() {
		return didWalletFile;
	}

	public void setDidWalletFile(DidWalletFileVO didWalletFile) {
		this.didWalletFile = didWalletFile;
	}

	public SpVO getSp() {
		return sp;
	}

	public void setSp(SpVO sp) {
		this.sp = sp;
	}

	public HashMap<String, ServiceVO> getServices() {
		return services;
	}

	public void setServices(HashMap<String, ServiceVO> services) {
		this.services = services;
	}

	public PushVO getPush() {
		return push;
	}

	public void setPush(PushVO push) {
		this.push = push;
	}

	public ArrayList<ServiceVO> getServiceList() {
		return serviceList;
	}

	public void setServiceList(ArrayList<ServiceVO> serviceList) {
		this.serviceList = serviceList;
	}

	public ArrayList<CaVO> getCaList() {
		return caList;
	}

	public void setCaList(ArrayList<CaVO> caList) {
		this.caList = caList;
	}

	public DbVO getDb() {
		return db;
	}

	public void setDb(DbVO db) {
		this.db = db;
	}

	public CaVO getCa(String appCode) {
		CaVO result = null;

		for (CaVO ca : caList) {
			if (Objects.equals(appCode, ca.getAppCode())) {
				result = ca;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

}
