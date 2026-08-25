package mip.mva.sp.comm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

import com.google.gson.reflect.TypeToken;
import com.raonsecure.omnione.core.crypto.GDPCryptoHelperClient;
import com.raonsecure.omnione.core.data.did.DIDAssertionType;
import com.raonsecure.omnione.core.data.did.PublicKey;
import com.raonsecure.omnione.core.data.did.v2.DIDs;
import com.raonsecure.omnione.core.data.iw.PrivacyVcType;
import com.raonsecure.omnione.core.data.iw.Unprotected;
import com.raonsecure.omnione.core.data.iw.profile.EncryptKeyTypeEnum;
import com.raonsecure.omnione.core.data.iw.profile.Profile;
import com.raonsecure.omnione.core.data.iw.profile.result.VCVerifyProfileResult;
import com.raonsecure.omnione.core.data.iw.v2.VerifiablePresentation;
import com.raonsecure.omnione.core.data.rest.ResultJson;
import com.raonsecure.omnione.core.data.rest.ResultProfile;
import com.raonsecure.omnione.core.data.rest.ResultVcStatus;
import com.raonsecure.omnione.core.eoscommander.crypto.digest.Sha256;
import com.raonsecure.omnione.core.eoscommander.crypto.util.HexUtils;
import com.raonsecure.omnione.core.exception.IWException;
import com.raonsecure.omnione.core.key.IWDIDManager;
import com.raonsecure.omnione.core.key.IWKeyManagerInterface;
import com.raonsecure.omnione.core.key.IWKeyManagerInterface.OnUnLockListener;
import com.raonsecure.omnione.core.key.KeyManagerFactory;
import com.raonsecure.omnione.core.key.KeyManagerFactory.KeyManagerType;
import com.raonsecure.omnione.core.key.data.AESType;
import com.raonsecure.omnione.core.key.store.IWDIDFile;
import com.raonsecure.omnione.core.util.GDPLogger;
import com.raonsecure.omnione.core.util.GDPLogger.LogLevelType;
import com.raonsecure.omnione.core.util.http.HttpException;
import com.raonsecure.omnione.core.zkp.ZkpConstants;
import com.raonsecure.omnione.core.zkp.data.CredentialDefinition;
import com.raonsecure.omnione.core.zkp.data.Proof;
import com.raonsecure.omnione.core.zkp.data.ProofRequest;
import com.raonsecure.omnione.core.zkp.data.proofrequest.AttributeInfo;
import com.raonsecure.omnione.core.zkp.data.proofrequest.PredicateInfo;
import com.raonsecure.omnione.core.zkp.data.schema.CredentialSchema;
import com.raonsecure.omnione.core.zkp.enums.PredicateType;
import com.raonsecure.omnione.core.zkp.revoc.data.dto.Identifiers;
import com.raonsecure.omnione.core.zkp.revoc.data.dto.ProofVerifyParam;
import com.raonsecure.omnione.core.zkp.util.BigIntegerUtil;
import com.raonsecure.omnione.sdk_server_core.OmniOption;
import com.raonsecure.omnione.sdk_server_core.api.EosDataApi;
import com.raonsecure.omnione.sdk_server_core.api.ZKPApi;
import com.raonsecure.omnione.sdk_server_core.blockchain.common.BlockChainException;
import com.raonsecure.omnione.sdk_server_core.blockchain.common.ServerInfo;
import com.raonsecure.omnione.sdk_server_core.blockchain.common.StateDBResultDatas;
import com.raonsecure.omnione.sdk_server_core.blockchain.convert.DidMulTbl;
import com.raonsecure.omnione.sdk_server_core.blockchain.convert.VcStatusTbl;
import com.raonsecure.omnione.sdk_server_core.blockchain.convert.VcStatusTbl.VCStatusEnum;
import com.raonsecure.omnione.sdk_server_core.data.IWApiBaseData;
import com.raonsecure.omnione.sdk_server_core.data.VcResult;
import com.raonsecure.omnione.sdk_server_core.data.response.SDKResponse;
import com.raonsecure.omnione.sdk_verifier.VerifyApi;
import com.raonsecure.omnione.sdk_verifier.api.data.SpProfileParam;
import com.raonsecure.omnione.sdk_verifier.api.data.VcVerifyProfileParam;

import mip.mva.sp.comm.enums.CaInfoEnum;
import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.enums.PresentTypeEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.config.ConfigBean;
import mip.mva.sp.config.vo.BlockchainVO;
import mip.mva.sp.config.vo.DidWalletFileVO;
import mip.mva.sp.config.vo.ServiceVO;
import mip.mva.sp.config.vo.SpVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.comm.util
 * @FileName    : VerifyManager.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 검증 Manager
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@SuppressWarnings("unchecked")
@Component
public class VerifyManager implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyManager.class);

	private static final Map<String, Map<String, Object>> CAINFO_MAP = new HashMap<>();

	/** 블록체인 설정 */
	private final BlockchainVO blockchain;
	/** DID 지갑 설정 */
	private final DidWalletFileVO didWalletFile;
	/** SP 설정 */
	private final SpVO sp;
	/** 서비스 설정 */
	private final Map<String, ServiceVO> services;
	/** HttpUtil */
	private final HttpUtil httpUtil;

	/** 블록체인 서버정보 */
	private ServerInfo blockChainServerInfo;
	/** 키 매니저 */
	private IWKeyManagerInterface keyManager;
	/** DID 매니저 */
	private IWDIDManager didManager;
	/** DID Document */
	private DIDs didDoc;
	/** API Basedata */
	private IWApiBaseData iWApiBaseData;

	/**
	 * 생성자
	 * 
	 * @param configBean 설정정보
	 * @param httpUtil HttpUtil
	 */
	public VerifyManager(ConfigBean configBean, HttpUtil httpUtil) {
		this.blockchain = configBean.getVerifyConfig().getBlockchain();
		this.didWalletFile = configBean.getVerifyConfig().getDidWalletFile();
		this.sp = configBean.getVerifyConfig().getSp();
		this.services = configBean.getVerifyConfig().getServices();
		this.httpUtil = httpUtil;
	}

	/**
	 * 초기 설정
	 */
	@Override
	public void afterPropertiesSet() throws SpException {
		initBlockchainServer();
		initDidAndWallet();
		loadCaInfoMap();
	}

	/**
	 * Blockchain 초기 설정
	 *
	 * @throws SpException
	 */
	private void initBlockchainServer() throws SpException {
		try {
			if (ObjectUtils.isEmpty(blockchain)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "blockchain is empty");
			}

			String serverDomain = blockchain.getServerDomain();
			Integer connectTimeout = blockchain.getConnectTimeout();
			Integer readTimeout = blockchain.getReadTimeout();
			Boolean useCache = blockchain.getUseCache();
			Boolean sdkDetailLog = blockchain.getSdkDetailLog();

			if (ObjectUtils.isEmpty(serverDomain)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "serverDomain is empty");
			} else {
				blockChainServerInfo = new ServerInfo(serverDomain);
			}

			if (!ObjectUtils.isEmpty(connectTimeout)) {
				blockChainServerInfo.setConnectTimeout(connectTimeout);
			}

			if (!ObjectUtils.isEmpty(readTimeout)) {
				blockChainServerInfo.setReadTimeout(readTimeout);
			}

			if (Boolean.TRUE.equals(useCache)) {
				OmniOption.setUseCache(useCache);
			}

			if (Boolean.TRUE.equals(sdkDetailLog)) {
				OmniOption.setSdkDetailLog(true);

				GDPLogger.setLevel(LogLevelType.DEBUG);
			}
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}
	}

	/**
	 * DID & Wallet 초기 설정
	 *
	 * @throws SpException
	 */
	private void initDidAndWallet() throws SpException {
		try {
			if (ObjectUtils.isEmpty(didWalletFile)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "fileInfo is empty");
			}

			String keymanagerPassword = didWalletFile.getKeymanagerPassword();

			if (ObjectUtils.isEmpty(keymanagerPassword)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "keymanagerPassword is empty");
			}

			File keymanagerFile = ResourceUtils.getFile(didWalletFile.getKeymanagerPath());

			String keymanagerFileInfo = keymanagerFile.getAbsolutePath();

			keyManager = KeyManagerFactory.getKeyManager(KeyManagerType.DEFAULT, keymanagerFileInfo, keymanagerPassword.toCharArray());

			keyManager.unLock(keymanagerPassword.toCharArray(), new OnUnLockListener() {
				@Override
				public void onSuccess() {
					LOGGER.debug("[OMN] API keyManager onSuccess");
				}

				@Override
				public void onFail(int errCode) {
					LOGGER.error("[OMN] API keyManager onFail({})", errCode);
				}

				@Override
				public void onCancel() {
					LOGGER.error("[OMN] API keyManager onCancel");
				}
			});

			didManager = new IWDIDManager(didWalletFile.getDidFilePath());

			IWDIDFile iwdidWalletFile = new IWDIDFile(didWalletFile.getDidFilePath());

			didDoc = iwdidWalletFile.getDataFromDIDsV2();

			iWApiBaseData = new IWApiBaseData(blockChainServerInfo, keyManager, didWalletFile.getSignKeyId(), blockchain.getAccount());
		} catch (SpException e) {
			throw e;
		} catch (FileNotFoundException | IWException e) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}
	}

	/**
	 * CaInfo 초기 설정
	 */
	private void loadCaInfoMap() {
		try {
			String serverDomain = blockchain.getServerDomain().split(",")[0];

			Map<String, Object> paramMap = new HashMap<>();

			paramMap.put("scope", "omnione.ent");
			paramMap.put("code", "omnione.ent");
			paramMap.put("key_type", "sha256");
			paramMap.put("json", true);
			paramMap.put("table", "ca");
			paramMap.put("lower_bound", "");
			paramMap.put("upper_bound", "");
			paramMap.put("limit", 1000);

			LOGGER.debug("paramMap : {}", ConfigBean.gson.toJson(paramMap));

			String result = httpUtil.executeHttpPost(serverDomain + "/v1/chain/get_table_rows", ConfigBean.gson.toJson(paramMap));

			LOGGER.debug("result : {}", result);

			Map<String, Object> caInfoMap = ConfigBean.gson.fromJson(result, new TypeToken<Map<String, Object>>() {}.getType());
			List<Map<String, Object>> caInfoList = (List<Map<String, Object>>) caInfoMap.get("rows");

			if (!ObjectUtils.isEmpty(caInfoList)) {
				for (Map<String, Object> caInfo : caInfoList) {
					Double statusCode = (Double) caInfo.get("status_code");

					if (Objects.equals(statusCode, 0.0)) {
						String packageName = (String) caInfo.get("app_package_name");

						CAINFO_MAP.put(packageName, caInfo);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Profile 생성
	 *
	 * @param svcCode 서비스코드
	 * @return Profile 생성 결과
	 * @throws SpException
	 */
	public ResultProfile profile(String svcCode) throws SpException {
		ResultProfile resultProfile = null;

		try {
			if (ObjectUtils.isEmpty(services)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "service is empty");
			}

			ServiceVO service = services.get(svcCode);

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "service[" + svcCode + "] is empty");
			}

			String spName = service.getSpName();

			if (ObjectUtils.isEmpty(spName)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "spName is empty");
			}

			String serviceName = service.getServiceName();

			if (ObjectUtils.isEmpty(serviceName)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "serviceName is empty");
			}

			Integer encryptType = service.getEncryptType();

			if (ObjectUtils.isEmpty(encryptType)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "encryptType is empty");
			}

			Integer keyType = service.getKeyType();

			if (ObjectUtils.isEmpty(keyType)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "keyType is empty");
			}

			List<String> authType = service.getAuthType();

			String nonce = this.generateNonce();
			String type = ConfigBean.PROFILE_TYPE;

			Profile profile = new Profile();

			profile.setSpName(spName);
			profile.setName(serviceName);
			profile.setEncryptType(encryptType);
			profile.setKeyType(keyType);
			profile.setAuthType(authType);
			profile.setNonce(nonce);
			profile.setType(type);

			String callBackUrl = service.getCallBackUrl();

			if (!ObjectUtils.isEmpty(callBackUrl)) {
				profile.setCallBackUrl(callBackUrl);
			}

			SpProfileParam spProfileParam = new SpProfileParam(blockChainServerInfo, keyManager, didWalletFile.getSignKeyId(), svcCode, profile, didDoc.getId(), blockchain.getAccount());

			spProfileParam.setEncryptKeyId(didWalletFile.getEncryptKeyId());

			String spProfileJson = VerifyApi.makeSpProfile(spProfileParam);

			LOGGER.debug("spProfileJson : {}", spProfileJson);

			resultProfile = new ResultProfile();

			resultProfile.setResult(true);
			resultProfile.setProfileJson(spProfileJson);
		} catch (SpException e) {
			throw e;
		} catch (BlockChainException | HttpException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		}  catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return resultProfile;
	}

	/**
	 * VP 검증
	 *
	 * @param svcCode 서비스코드
	 * @param vCVerifyProfileResult 검증 요청문
	 * @return 검증 결과
	 * @throws SpException
	 */
	public VcResult verify(String svcCode, VCVerifyProfileResult vCVerifyProfileResult) throws SpException {
		VcResult vcResult = null;

		try {
			VcVerifyProfileParam vcVerifyProfileParam = new VcVerifyProfileParam(blockChainServerInfo, keyManager, didWalletFile.getSignKeyId(), blockchain.getAccount(), vCVerifyProfileResult, didWalletFile.getDidFilePath());

			vcVerifyProfileParam.setServiceCode(svcCode);
			vcVerifyProfileParam.setEncryptKeyId(didWalletFile.getEncryptKeyId());
			vcVerifyProfileParam.setCheckRequiredPrivacy(sp.getCheckRequiredPrivacy());
			vcVerifyProfileParam.setCheckVCExpirationDate(sp.getCheckVcExpirationDate());

			vcResult = VerifyApi.verify2(vcVerifyProfileParam, false);

			LOGGER.debug("vcResult : {}", vcResult.toJson());
		} catch (BlockChainException | HttpException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		}  catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return vcResult;
	}

	/**
	 * VP 재검증(부인방지)
	 *
	 * @param vCVerifyProfileResult 검증 요청문
	 * @return 검증 결과
	 * @throws SpException
	 */
	public VcResult checkUserProof(VCVerifyProfileResult vCVerifyProfileResult) throws SpException {
		VcResult vcResult = null;

		try {
			VcVerifyProfileParam vcVerifyProfileParam = new VcVerifyProfileParam(blockChainServerInfo, keyManager, didWalletFile.getSignKeyId(), blockchain.getAccount(), vCVerifyProfileResult, didWalletFile.getDidFilePath());

			vcVerifyProfileParam.setEncryptKeyId(didWalletFile.getEncryptKeyId());

			vcResult = VerifyApi.checkUserProof(vcVerifyProfileParam, false);

			LOGGER.debug("vcResult : {}", vcResult.toJson());
		} catch (BlockChainException | HttpException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		}  catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return vcResult;
	}

	/**
	 * Profile 생성(영지식)
	 *
	 * @param svcCode 서비스코드
	 * @return Profile 생성 결과
	 * @throws SpException
	 */
	public ResultProfile profileZkp(String svcCode) throws SpException {
		ResultProfile resultProfile = null;

		try {
			ServiceVO service = services.get(svcCode);

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "service does not exist");
			}

			String spName = service.getSpName();

			if (ObjectUtils.isEmpty(spName)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "spName is empty");
			}

			String serviceName = service.getServiceName();

			if (ObjectUtils.isEmpty(serviceName)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "serviceName is empty");
			}

			Integer encryptType = service.getEncryptType();

			if (ObjectUtils.isEmpty(encryptType)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "encryptType is empty");
			}

			Integer keyType = service.getKeyType();

			if (ObjectUtils.isEmpty(keyType)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "keyType is empty");
			}

			List<String> zkpSchemaName = service.getZkpSchemaName();

			if (ObjectUtils.isEmpty(zkpSchemaName)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "zkpSchemaName is empty");
			}

			String nonce = this.generateNonce();
			String type = ConfigBean.PROFILE_TYPE;

			Profile profile = new Profile();

			profile.setSpName(spName);
			profile.setName(serviceName);
			profile.setEncryptType(encryptType);
			profile.setKeyType(keyType);
			profile.setNonce(nonce);
			profile.setType(type);

			ProofRequest proofRequest = this.createProofRequest(service);

			profile.setProofRequest(proofRequest);

			SpProfileParam spProfileParam = new SpProfileParam(blockChainServerInfo, keyManager, didWalletFile.getSignKeyId(), null, profile, didDoc.getId(), blockchain.getAccount(), true);

			spProfileParam.setEncryptKeyId(didWalletFile.getEncryptKeyId());

			String zkpProfileJson = VerifyApi.makeZkpProfile(spProfileParam);

			LOGGER.debug("zkpProfileJson : {}", zkpProfileJson);

			resultProfile = new ResultProfile();

			resultProfile.setResult(true);
			resultProfile.setProfileJson(zkpProfileJson);
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return resultProfile;
	}

	/**
	 * VP 검증(영지식)
	 *
	 * @param svcCode 서비스코드
	 * @param vCVerifyProfileResult 검증 요청문
	 * @return 검증 결과
	 * @throws SpException
	 */
	public ResultJson verifyZkp(String svcCode, VCVerifyProfileResult vCVerifyProfileResult) throws SpException {
		ResultJson resultJson = null;

		try {
			AESType aESType = Objects.equals(vCVerifyProfileResult.getEncryptType(), 1) ? AESType.AES128 : AESType.AES256;
			EncryptKeyTypeEnum encryptKeyTypeEnum = EncryptKeyTypeEnum.getEnum(vCVerifyProfileResult.getKeyType());

			byte[] vpDataByte = null;

			if (Objects.equals(encryptKeyTypeEnum, EncryptKeyTypeEnum.ALGORITHM_RSA)) {
				vpDataByte = keyManager.rsaDecrypt(didWalletFile.getEncryptKeyId(), HexUtils.toBytes(vCVerifyProfileResult.getData()), aESType);
			} else if (Objects.equals(encryptKeyTypeEnum, EncryptKeyTypeEnum.ALGORITHM_RSA_OAEP_SHA256)) {
				vpDataByte = keyManager.rsaDecryptByOaep(didWalletFile.getEncryptKeyId(), HexUtils.toBytes(vCVerifyProfileResult.getData()), aESType);
			} else {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_KEY_TYPE);
			}

			String data = new String(vpDataByte, StandardCharsets.UTF_8);

			LOGGER.debug("data : {}", data);

			Proof proof = ConfigBean.gson.fromJson(data, Proof.class);

			BigInteger verifierNonce = new BigInteger(vCVerifyProfileResult.getZkpNonce());

			List<ProofVerifyParam> proofVerifyParams = new LinkedList<>();

			for (Identifiers identifiers : proof.getIdentifiers()) {
				String schemaId = identifiers.getSchemaId();

				CredentialSchema credentialSchema = (CredentialSchema) new ZKPApi().getCredentialSchema(iWApiBaseData, schemaId).getResultData();

				String credDefId = identifiers.getCredDefId();

				CredentialDefinition credentialDefinition = (CredentialDefinition) new ZKPApi().getCredentialDefinition(iWApiBaseData, credDefId).getResultData();

				ProofVerifyParam proofVerifyParam = new ProofVerifyParam.Builder().setSchema(credentialSchema).setCredentialDefinition(credentialDefinition).build();

				proofVerifyParams.add(proofVerifyParam);
			}

			ServiceVO service = services.get(svcCode);

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "service does not exist");
			}

			ProofRequest proofRequest = this.createProofRequest(service);

			SDKResponse sDKResponse = new ZKPApi().verifyProof(iWApiBaseData, proof, proofRequest, proofVerifyParams, verifierNonce);

			LOGGER.debug("sDKResponse : {}", sDKResponse.toJson());

			resultJson = new ResultJson();

			if (Boolean.TRUE.equals(sDKResponse.isSuccess())) {
				resultJson.setResult(true);
			} else {
				resultJson.setResult(false);
				resultJson.setErrorCode(sDKResponse.getResultCode());
				resultJson.setErrorMsg(sDKResponse.getResultMsg());
			}
		} catch (SpException e) {
			throw e;
		} catch (IWException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return resultJson;
	}

	/**
	 * ProofRequest 생성
	 *
	 * @param service 서비스 정보
	 * @return ProofRequest
	 * @throws SpException
	 */
	private ProofRequest createProofRequest(ServiceVO service) throws SpException {
		ProofRequest proofRequest = null;

		try {
			// restrictionList 생성 Start
			List<Map<String, String>> restrictionList = new ArrayList<>();

			List<String> zkpSchemaNameList = service.getZkpSchemaName();
			Map<String, String> restriction = null;

			for (String zkpSchemaName : zkpSchemaNameList) {
				restriction = new HashMap<>();

				restriction.put("schema_name", zkpSchemaName);

				restrictionList.add(restriction);
			}
			// restrictionList 생성 End

			// attributes 생성 Start
			LOGGER.debug("attrList : {}", service.getAttrList());

			List<String> attrList = service.getAttrList();

			Map<String, AttributeInfo> attributes = new HashMap<>();

			if (!ObjectUtils.isEmpty(attrList)) {
				for (String attr : attrList) {
					AttributeInfo attributeInfo = new AttributeInfo();

					attributeInfo.setName(attr);

					for (Map<String, String> r : restrictionList) {
						attributeInfo.addRestriction(r);
					}

					attributes.put("attribute_referent_" + (attrList.indexOf(attr) + 1), attributeInfo);
				}
			}
			// attributes 생성 End

			// predicates 생성 Start
			LOGGER.debug("predList : {}", service.getPredList());

			List<Map<String, Object>> predList = service.getPredList();

			Map<String, PredicateInfo> predicates = new HashMap<>();

			if (!ObjectUtils.isEmpty(predList)) {
				int index = 1;

				for (Map<String, Object> map : predList) {
					String key = map.keySet().iterator().next();
					Map<String, String> info = (Map<String, String>) map.get(key);
					String type = info.get("type");
					Integer value = Integer.parseInt(info.get("value"));

					PredicateInfo predicateInfo = new PredicateInfo();

					predicateInfo.setPType(PredicateType.valueOf(type));
					predicateInfo.setName(key);
					predicateInfo.setPValue(resolvePredicateValue(key, value));

					for (Map<String, String> restrictionValue : restrictionList) {
						predicateInfo.addRestriction(restrictionValue);
					}

					predicates.put("predicate_referent_" + index, predicateInfo);

					index++;
				}
			}
			// predicates 생성 End

			// proofRequest 생성 Start
			BigInteger nonce = new BigIntegerUtil().createRandomBigInteger(ZkpConstants.LARGE_NONCE);

			SDKResponse response = new ZKPApi().createProofRequest(null, attributes, predicates, nonce);

			LOGGER.debug("response : {}", response.toJson());

			proofRequest = (ProofRequest) response.getResultData();
			// proofRequest 생성 End
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return proofRequest;
	}

	/**
	 * Predicate 값 변환 (zkpbirth일 경우 연도 사용 변환)
	 *
	 * @param key predicate
	 * @param value 값
	 * @return 변환된 predicate 값
	 */
	private int resolvePredicateValue(String key, Integer value) {
		if (Objects.equals("zkpbirth", key)) {
			Calendar calendar = Calendar.getInstance();

			calendar.add(Calendar.YEAR, value);

			Date date = new Date(calendar.getTimeInMillis());

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

			return Integer.parseInt(simpleDateFormat.format(date));
		}

		return value;
	}

	/**
	 * nonce 생성
	 *
	 * @return nonce
	 * @throws SpException
	 */
	public String generateNonce() throws SpException {
		String nonce = "";

		try {
			nonce = Sha256.from(new GDPCryptoHelperClient().generateNonce()).toString();
		} catch (IWException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return nonce;
	}

	/**
	 * DID Assertion 생성
	 *
	 * @param nonce nonce
	 * @return DID Assertion JSON
	 * @throws SpException
	 */
	public String makeDIDAssertion(String nonce) throws SpException {
		String dIDAssertion = "";

		try {
			dIDAssertion = didManager.makeDIDAssertion2(DIDAssertionType.DEFAULT, didWalletFile.getSignKeyId(), HexUtils.toBytes(nonce), null, keyManager);
		} catch (IWException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return dIDAssertion;
	}

	/**
	 * VC 상태 조회
	 *
	 * @param vcId VC ID
	 * @return VC 상태 정보
	 * @throws SpException
	 */
	public ResultVcStatus getVCStatus(String vcId) throws SpException {
		ResultVcStatus resultVcStatus = new ResultVcStatus();

		try {
			resultVcStatus.setVcId(vcId);

			EosDataApi eosDataApi = new EosDataApi();

			StateDBResultDatas<VcStatusTbl> stateDBResultDatas = eosDataApi.getVCStatus(blockChainServerInfo, vcId);

			if (!stateDBResultDatas.getDataList().isEmpty()) {
				VcStatusTbl vcStatusTbl = stateDBResultDatas.getDataList().get(0);

				resultVcStatus.setVcStatus(vcStatusTbl.getStatusCodeEnum().toString());
				resultVcStatus.setMemo(vcStatusTbl.getMemo());
			} else {
				resultVcStatus.setVcStatus(VCStatusEnum.NOT_EXIST.toString());
			}

			resultVcStatus.setResult(true);
		} catch (BlockChainException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return resultVcStatus;
	}

	/**
	 * VP 복호화
	 *
	 * @param vCVerifyProfileResult 검증 요청문
	 * @return 복호화된 VP data
	 * @throws SpException
	 */
	public String getVPData(VCVerifyProfileResult vCVerifyProfileResult) throws SpException {
		String vpData = "";

		try {
			LOGGER.debug("encryptType : {}", vCVerifyProfileResult.getEncryptType());
			LOGGER.debug("keyType : {}", vCVerifyProfileResult.getKeyType());

			byte[] decVp = null;

			AESType aESType = Objects.equals(vCVerifyProfileResult.getEncryptType(), 1) ? AESType.AES128 : AESType.AES256;
			EncryptKeyTypeEnum encryptKeyTypeEnum = EncryptKeyTypeEnum.getEnum(vCVerifyProfileResult.getKeyType());

			if (Objects.equals(encryptKeyTypeEnum, EncryptKeyTypeEnum.ALGORITHM_RSA)) {
				decVp = keyManager.rsaDecrypt(didWalletFile.getEncryptKeyId(), HexUtils.toBytes(vCVerifyProfileResult.getData()), aESType);
			} else if (Objects.equals(encryptKeyTypeEnum, EncryptKeyTypeEnum.ALGORITHM_RSA_OAEP_SHA256)) {
				decVp = keyManager.rsaDecryptByOaep(didWalletFile.getEncryptKeyId(), HexUtils.toBytes(vCVerifyProfileResult.getData()), aESType);
			} else {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_KEY_TYPE);
			}

			vpData = new String(decVp, StandardCharsets.UTF_8);

			LOGGER.debug("vpData : {}", vpData);
		} catch (IWException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return vpData;
	}

	/**
	 * Privacy 추출
	 * 
	 * @param vpData - VP data 문자열(JSON)
	 * @return Privacy 목록
	 */
	public List<Unprotected> getPrivacy(String vpData) throws SpException {
		List<Unprotected> privacyList = null;

		try {
			VerifiablePresentation verifiablePresentation = new VerifiablePresentation();

			verifiablePresentation.fromJson(vpData);

			VcResult vcResult = new VcResult();

			vcResult.setVpComplete(verifiablePresentation);

			privacyList = vcResult.getPrivacyList();

			com.raonsecure.omnione.core.data.did.Proof proof = vcResult.getVpComplete().getProof();
			List<com.raonsecure.omnione.core.data.did.Proof> proofs = vcResult.getVpComplete().getProofs();

			String vpNonce = null;

			if (!ObjectUtils.isEmpty(proof)) {
				vpNonce = proof.getNonce();
			} else {
				vpNonce = proofs.get(0).getNonce();
			}

			int ciLength = 64;

			if (!ObjectUtils.isEmpty(vpNonce) && vpNonce.length() > ciLength) {
				String info = new String(HexUtils.toBytes(vpNonce.substring(ciLength)), StandardCharsets.UTF_8);

				LOGGER.debug("info : {}", info);

				String ci = "";

				if (!info.contains("&")) {
					ci = info;
				} else {
					ci = info.split("&")[0];
				}

				if (!ObjectUtils.isEmpty(ci)) {
					Unprotected privacyCi = new Unprotected();

					privacyCi.setType("ci");
					privacyCi.setValue(ci);

					privacyList.add(privacyCi);
				}

				String telno = "";

				if (info.contains("&")) {
					telno = info.split("&")[1];
				}

				if (!ObjectUtils.isEmpty(telno)) {
					Unprotected privacyTelno = new Unprotected();

					privacyTelno.setType("telno");
					privacyTelno.setValue(telno);

					privacyList.add(privacyTelno);
				}
			}

			LOGGER.debug("privacyList : {}", ConfigBean.gson.toJson(privacyList));
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return privacyList;
	}

	/**
	 * Privacy 조회 - with VC Type
	 * 
	 * @param vpData - VP data 문자열(JSON)
	 * @return Privacy 목록
	 */
	public List<Map<String, String>> getPrivacyWithVcType(String vpData) throws SpException {
		List<Map<String, String>> privacyList = new ArrayList<>();

		try {
			VerifiablePresentation verifiablePresentation = new VerifiablePresentation();

			verifiablePresentation.fromJson(vpData);

			VcResult vcResult = new VcResult();

			vcResult.setVpComplete(verifiablePresentation);

			/*
			 * //VC Type을 지정하여 privacy 목록을 생성하는 경우
			 * List<Unprotected> mdriverlicPrivacyList = vcResult.getPrivacyListByVcType("mdriverlic");
			 * 
			 * for (Unprotected mdriverlicPrivacy : mdriverlicPrivacyList) {
			 * 	   LOGGER.debug("{} : {}", mdriverlicPrivacy.getType(), mdriverlicPrivacy.getValue());
			 * }
			 */

			// VP의 모든 정보로 privacy 목록을 생성하는 경우
			List<PrivacyVcType> privacyVcTypeList = vcResult.getPrivacyListWithVcType();

			extractPrivacyInfo(privacyList, privacyVcTypeList);

			com.raonsecure.omnione.core.data.did.Proof proof = vcResult.getVpComplete().getProof();
			List<com.raonsecure.omnione.core.data.did.Proof> proofs = vcResult.getVpComplete().getProofs();

			String vpNonce = null;

			if (!ObjectUtils.isEmpty(proof)) {
				vpNonce = proof.getNonce();
			} else {
				vpNonce = proofs.get(0).getNonce();
			}

			int ciLength = 64;

			if (!ObjectUtils.isEmpty(vpNonce) && vpNonce.length() > ciLength) {
				extractNonceInfo(privacyList, vpNonce, ciLength);
			}

			LOGGER.debug("privacyList : {}", ConfigBean.gson.toJson(privacyList));
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return privacyList;
	}

	/**
	 * VP의 PrivacyVcType 목록으로부터 정보 추출하여 Map 형태로 리스트에 추가
	 *
	 * @param privacyList 추가할 Privacy 목록
	 * @param privacyVcTypeList VP의 PrivacyVcType 목록
	 */
	private void extractPrivacyInfo(List<Map<String, String>> privacyList, List<PrivacyVcType> privacyVcTypeList) {
		for (PrivacyVcType privacyVcType : privacyVcTypeList) {
			LOGGER.debug("vcType : {}, privacy : {}", privacyVcType.getVcType(), privacyVcType.getUnprotected());

			for (Unprotected privacy : privacyVcType.getUnprotected()) {
				Map<String, String> privacyMap = new HashMap<>();

				privacyMap.put("vcType", privacyVcType.getVcType());
				privacyMap.put("type", privacy.getType());
				privacyMap.put("value", privacy.getValue());

				privacyList.add(privacyMap);
			}
		}
	}

	/**
	 * vpNonce로부터 정보 추출(CI, 전화번호)하여 Map 형태로 리스트에 추가
	 *
	 * @param privacyList 추가할 Privacy 목록
	 * @param vpNonce VP nonce 값
	 * @param ciLength CI 길이
	 */
	private void extractNonceInfo(List<Map<String, String>> privacyList, String vpNonce, int ciLength) {
		String info = new String(HexUtils.toBytes(vpNonce.substring(ciLength)), StandardCharsets.UTF_8);

		LOGGER.debug("info : {}", info);

		String ci = "";

		if (!info.contains("&")) {
			ci = info;
		} else {
			ci = info.split("&")[0];
		}

		if (!ObjectUtils.isEmpty(ci)) {
			Map<String, String> privacyMap = new HashMap<>();

			privacyMap.put("type", "ci");
			privacyMap.put("value", ci);

			privacyList.add(privacyMap);
		}

		String telno = "";

		if (info.contains("&")) {
			telno = info.split("&")[1];
		}

		if (!ObjectUtils.isEmpty(telno)) {
			Map<String, String> privacyMap = new HashMap<>();

			privacyMap.put("type", "telno");
			privacyMap.put("value", telno);

			privacyList.add(privacyMap);
		}
	}

	/**
	 * Privacy 추출(영지식)
	 * 
	 * @param vpData - VP data 문자열(JSON)
	 * @return Privacy 목록
	 */
	public List<Map<String, String>> getPrivacyZkp(String svcCode, String vpData) throws SpException {
		List<Map<String, String>> privacyList = new ArrayList<>();

		try {
			ServiceVO service = services.get(svcCode);

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "service does not exist");
			}

			Integer presentType = service.getPresentType();

			if (!Objects.equals(PresentTypeEnum.ZKP_VP.getVal(), presentType)) {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_PRESENT_TYPE, null, "privacyZkp");
			}

			Proof proof = ConfigBean.gson.fromJson(vpData, Proof.class);

			ProofRequest proofRequest = this.createProofRequest(service);

			for (Entry<String, AttributeInfo> entry : proofRequest.getRequestedAttributes().entrySet()) {
				Map<String, String> privacy = new HashMap<>();

				privacy.put(entry.getValue().getName(), proof.getRequestedProof().getRevealedAttrs().get(entry.getKey()).get("raw"));

				privacyList.add(privacy);
			}

			for (Entry<String, PredicateInfo> entry : proofRequest.getRequestedPredicates().entrySet()) {
				Map<String, String> privacy = new HashMap<>();

				privacy.put(entry.getValue().getName(), "true");

				privacyList.add(privacy);
			}
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return privacyList;
	}

	/**
	 * ZkpSchemaName 조회
	 * 
	 * @param svcCode 서비스코드
	 * @param vCVerifyProfileResult 검증 요청문
	 * @return ZkpSchemaName 목록
	 * @throws SpException
	 */
	public List<String> getZkpSchemaName(String svcCode, VCVerifyProfileResult vCVerifyProfileResult) throws SpException {
		List<String> zkpSchemaNameList = new ArrayList<>();

		try {
			ServiceVO service = services.get(svcCode);

			if (ObjectUtils.isEmpty(service)) {
				throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, "service does not exist");
			}

			AESType aESType = Objects.equals(vCVerifyProfileResult.getEncryptType(), 1) ? AESType.AES128 : AESType.AES256;
			EncryptKeyTypeEnum encryptKeyTypeEnum = EncryptKeyTypeEnum.getEnum(vCVerifyProfileResult.getKeyType());

			byte[] vpDataByte = null;

			if (Objects.equals(encryptKeyTypeEnum, EncryptKeyTypeEnum.ALGORITHM_RSA)) {
				vpDataByte = keyManager.rsaDecrypt(didWalletFile.getEncryptKeyId(), HexUtils.toBytes(vCVerifyProfileResult.getData()), aESType);
			} else if (Objects.equals(encryptKeyTypeEnum, EncryptKeyTypeEnum.ALGORITHM_RSA_OAEP_SHA256)) {
				vpDataByte = keyManager.rsaDecryptByOaep(didWalletFile.getEncryptKeyId(), HexUtils.toBytes(vCVerifyProfileResult.getData()), aESType);
			} else {
				throw new SpException(MipErrorEnum.SP_UNSUPPORTED_VP_KEY_TYPE);
			}

			String data = new String(vpDataByte, StandardCharsets.UTF_8);

			LOGGER.debug("data : {}", data);

			Proof proof = ConfigBean.gson.fromJson(data, Proof.class);

			String schemaId = "";

			for (Identifiers identifiers : proof.getIdentifiers()) {
				schemaId = identifiers.getSchemaId();

				LOGGER.debug("schemaId : {}", schemaId);

				for (String zipSchemaName : service.getZkpSchemaName()) {
					if (schemaId.contains(":" + zipSchemaName + ":")) {
						zkpSchemaNameList.add(zipSchemaName);
					}
				}
			}

			if (zkpSchemaNameList.isEmpty()) {
				throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, "zkpSchemaName does not exist");
			}
		} catch (SpException e) {
			throw e;
		} catch (IWException e) {
			throw new SpException(MipErrorEnum.SP_SDK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return zkpSchemaNameList;
	}

	/**
	 * CA명 조회
	 * 
	 * @param vpData - VP data 문자열(JSON)
	 * @return CA명
	 * @throws SpException
	 */
	public String getCaName(String vpData) throws SpException {
		String caName = "";

		try {
			VerifiablePresentation verifiablePresentation = new VerifiablePresentation();

			verifiablePresentation.fromJson(vpData);

			VcResult vcResult = new VcResult();

			vcResult.setVpComplete(verifiablePresentation);

			Map<String, Object> caInfo = (Map<String, Object>) vcResult.getVpComplete().getUncommitted().get("caInfo");
			String packageName = (String) caInfo.get("packageName");

			// 토스뱅크의 경우 안드로이드와 아이폰의 패키지 경로가 달라 예외packageName처리
			if (Objects.equals("com.vivarepublica.cash", packageName)) {
				packageName = "viva.republica.toss";
			}

			caName = CaInfoEnum.getEnum(packageName).getCaName();

			LOGGER.debug("caName : {}", caName);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return caName;
	}

	/**
	 * RSA 암호화
	 *
	 * @param data 암호화할 데이터
	 * @param targetDid 대상 DID
	 * @param isOaep OAEP 적용 여부
	 * @return 암호화된 데이터
	 */
	public String rsaEncrypt(String data, String targetDid, Boolean isOaep) throws SpException {
		String encData;

		try {
			LOGGER.debug("data : {}", data);
			LOGGER.debug("isOaep : {}", isOaep);

			EosDataApi eosDataApi = new EosDataApi();

			StateDBResultDatas<DidMulTbl> dbResultDatas = eosDataApi.getDIDV2(blockChainServerInfo, targetDid);

			PublicKey publicKey = new PublicKey();

			publicKey.setPublicKeyBase58(dbResultDatas.getDataList().get(0).getDidDocument().getKeyAgreement().get(0).getPublicKeyBase58());

			byte[] encByteData = null;

			if (Boolean.TRUE.equals(isOaep)) {
				encByteData = keyManager.rsaEncryptByOaep(publicKey, data.getBytes(), AESType.AES256);
			} else {
				encByteData = keyManager.rsaEncrypt(publicKey, data.getBytes(), AESType.AES256);
			}

			encData = Base64Util.encode(encByteData);

			LOGGER.debug("encData : {}", encData);
		} catch (BlockChainException | IWException e) {
			throw new SpException(MipErrorEnum.SP_NETWORK_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return encData;
	}

	/**
	 * RSA 복호화
	 *
	 * @param data 복호화할 데이터
	 * @param isOaep OAEP 적용 여부
	 * @return 복호화된 데이터
	 */
	public String rsaDecrypt(String data, Boolean isOaep) throws SpException {
		String decData;

		try {
			LOGGER.debug("data : {}", data);
			LOGGER.debug("isOaep : {}", isOaep);

			byte[] decByteData = null;

			if (Boolean.TRUE.equals(isOaep)) {
				decByteData = keyManager.rsaDecryptByOaep(didWalletFile.getEncryptKeyId(), Base64Util.decodeToByte(data), AESType.AES256);
			} else {
				decByteData = keyManager.rsaDecrypt(didWalletFile.getEncryptKeyId(), Base64Util.decodeToByte(data), AESType.AES256);
			}

			decData = new String(decByteData, StandardCharsets.UTF_8);

			LOGGER.debug("decData : {}", decData);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}

		return decData;
	}

}
