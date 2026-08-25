package mip.mva.sp.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.comm.util.HttpUtil;
import mip.mva.sp.config.vo.CaVO;
import mip.mva.sp.config.vo.ServiceVO;
import mip.mva.sp.config.vo.VerifyConfigVO;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.config
 * @FileName    : ConfigBean.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : 스프링 부트 커스텀 프로퍼티 설정 Class
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주          최초생성
 * </pre>
 */
@Component
@ConfigurationProperties("app")
public class ConfigBean implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigBean.class);

	public static final String TYPE = "mip";
	public static final String VERSION = "2.0.0";

	public static final String M120 = "120";
	public static final String M200 = "200";
	public static final String M310 = "310";
	public static final String M320 = "320";
	public static final String M400 = "400";
	public static final String M900 = "900";

	public static final String PROFILE_TYPE = "VERIFY";
	
	public static final String START_QRCPM = "start_qrcpm";
	public static final String START_NONCPM = "start_noncpm";
	public static final String WAIT_JOIN = "wait_join";
	public static final String JOIN = "join";
	public static final String WAIT_VERIFY = "wait_verify";
	public static final String VERIFY_HOLDER = "verify_holder";
	public static final String VERIFY_VERIFIER = "verify_verifier";
	public static final String ACK = "ack";
	public static final String WAIT_PROFILE = "wait_profile";
	public static final String PROFILE = "profile";
	public static final String VP = "vp";
	public static final String FINISH = "finish";
	public static final String ERROR = "error";

	public static final Integer CPM_TIMEOUT = 60;

	public static final Gson gson = new Gson();

	private final HttpUtil httpUtil;

	private String verifyFilePath;
	private VerifyConfigVO verifyConfig;

	/**
	 * 생성자
	 * 
	 * @param httpUtil HttpUtil
	 */
	public ConfigBean(HttpUtil httpUtil) {
		this.httpUtil = httpUtil;
	}

	/**
	 * 설정 완료 후 초기화 (verifyConfig JSON 로드)
	 *
	 * @throws SpException
	 */
	@Override
	public void afterPropertiesSet() throws SpException {
		// 검증정보 JSON 파일 로드 Start
		try {
			LOGGER.debug("verifyFilePath : {}", this.getVerifyFilePath());

			Path absolutePath = Paths.get(ResourceUtils.getFile(this.getVerifyFilePath()).getAbsolutePath());

			LOGGER.debug("absolutePath : {}", absolutePath);

			byte[] verifyFileByteArr = Files.readAllBytes(absolutePath);

			String verifyFileString = new String(verifyFileByteArr, StandardCharsets.UTF_8);

			LOGGER.debug("verifyFileString : {}", verifyFileString);

			verifyConfig = gson.fromJson(verifyFileString, VerifyConfigVO.class);

			LOGGER.debug("verifyConfig : {}", verifyConfig.toString());

			ArrayList<ServiceVO> serviceList = new ArrayList<>();
			ServiceVO service = null;
			Map<String, ServiceVO> services = verifyConfig.getServices();

			for (Map.Entry<String, ServiceVO> entry : services.entrySet()) {
				service = new ServiceVO();

				service.setSvcCode(entry.getValue().getSvcCode());
				service.setServiceName(entry.getValue().getServiceName());
				service.setPresentType(entry.getValue().getPresentType());

				serviceList.add(service);
			}

			verifyConfig.setServiceList(serviceList);

			if (verifyConfig.getPush() != null && verifyConfig.getPush().getCaListApiUse()) {
				verifyConfig.setCaList(this.getCaListFromApi());
			}
		} catch (SpException e) {
			throw e;
		} catch (IOException | JsonSyntaxException e) {
			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, e);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.UNKNOWN_ERROR, null, e);
		}
		// 검증정보 JSON 파일 로드 End
	}

	public String getVerifyFilePath() {
		return verifyFilePath;
	}

	public void setVerifyFilePath(String verifyFilePath) {
		this.verifyFilePath = verifyFilePath;
	}

	public VerifyConfigVO getVerifyConfig() {
		return verifyConfig;
	}

	public void setVerifyConfig(VerifyConfigVO verifyConfig) {
		this.verifyConfig = verifyConfig;
	}

	public ArrayList<CaVO> getCaListFromApi() throws SpException {
		String serverDomain = verifyConfig.getPush().getOpnPushServerList();

		Map<String, Object> paramMap = new HashMap<>();

		paramMap.put("apiType", TYPE);

		LOGGER.debug("paramMap : {}", gson.toJson(paramMap));

		String result = httpUtil.executeHttpPost(serverDomain, gson.toJson(paramMap));

		LOGGER.debug("result : {}", result);

		JsonObject resultObj = JsonParser.parseString(result).getAsJsonObject();

		boolean resultFlag = resultObj.get("result").getAsBoolean();

		ArrayList<CaVO> caList = null;

		if (resultFlag) {
			String caListStr = resultObj.get("caList").getAsJsonArray().toString();

			caList = gson.fromJson(caListStr, new TypeToken<ArrayList<CaVO>>() {}.getType());
		} else {
			String errmsg = resultObj.get("errmsg").getAsString();

			throw new SpException(MipErrorEnum.SP_CONFIG_ERROR, null, errmsg);
		}

		return caList;
	}

}
