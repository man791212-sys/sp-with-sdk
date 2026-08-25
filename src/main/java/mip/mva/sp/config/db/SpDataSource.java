package mip.mva.sp.config.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ObjectUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import mip.mva.sp.comm.enums.MipErrorEnum;
import mip.mva.sp.comm.exception.SpException;
import mip.mva.sp.config.ConfigBean;

/**
 * @Project     : 모바일 운전면허증 서비스 구축 사업
 * @PackageName : mip.mva.sp.config.db
 * @FileName    : SpDataSource.java
 * @Author      : 민기주
 * @Date        : 2026. 4. 9.
 * @Description : DataSource 관리 Controller
 * 
 * <pre>
 * ==================================================
 * DATE            AUTHOR           NOTE
 * ==================================================
 * 2026. 4. 9.     민기주           최초생성
 * </pre>
 */
@Configuration
public class SpDataSource {

	/** HikariCP 설정 */
	private static final String  POOL_NAME            = "SpHikariPool";
	private static final int     MAX_POOL_SIZE        = 10;
	private static final int     MIN_IDLE             = 5;
	private static final long    CONNECTION_TIMEOUT   = 30000L;
	private static final long    IDLE_TIMEOUT         = 600000L;
	private static final long    MAX_LIFETIME         = 1800000L;
	private static final long    KEEPALIVE_TIME       = 60000L;

	private final ApplicationContext applicationContext;

	/** 설정정보 */
	private final ConfigBean configBean;

	/**
	 * 생성자
	 * 
	 * @param applicationContext ApplicationContext
	 * @param configBean 설정정보
	 */
	SpDataSource(ApplicationContext applicationContext, ConfigBean configBean) {
		this.applicationContext = applicationContext;
		this.configBean = configBean;
	}

	/**
	 * DataSource 생성 및 Bean 등록
	 *
	 * @return DataSource
	 * @throws SpException
	 */
	@Bean
	DataSource dataSource() throws SpException {
		HikariConfig hikariConfig = new HikariConfig();

		hikariConfig.setPoolName(POOL_NAME);
		hikariConfig.setMaximumPoolSize(MAX_POOL_SIZE);
		hikariConfig.setMinimumIdle(MIN_IDLE);
		hikariConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
		hikariConfig.setIdleTimeout(IDLE_TIMEOUT);
		hikariConfig.setMaxLifetime(MAX_LIFETIME);
		hikariConfig.setKeepaliveTime(KEEPALIVE_TIME);

		HikariDataSource hikariDataSource = null;

		try {
			if (ObjectUtils.isEmpty(configBean.getVerifyConfig().getDb())) {
				initH2MemoryDatabase(hikariConfig);
			} else {
				hikariConfig.setDriverClassName(configBean.getVerifyConfig().getDb().getDriverClassName());
				hikariConfig.setJdbcUrl(configBean.getVerifyConfig().getDb().getUrl());
				hikariConfig.setUsername(configBean.getVerifyConfig().getDb().getUsername());
				hikariConfig.setPassword(configBean.getVerifyConfig().getDb().getPassword());
			}

			hikariDataSource = new HikariDataSource(hikariConfig);
		} catch (SpException e) {
			throw e;
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, null, e);
		}

		return hikariDataSource;
	}

	/**
	 * H2 메모리 데이터베이스 초기화
	 *
	 * @param hikariConfig HikariConfig
	 * @throws SpException
	 */
	private void initH2MemoryDatabase(HikariConfig hikariConfig) throws SpException {
		hikariConfig.setDriverClassName("org.h2.Driver");
		hikariConfig.setJdbcUrl("jdbc:h2:mem:public;DB_CLOSE_DELAY=-1");
		hikariConfig.setUsername("sa");
		hikariConfig.setPassword("");

		String sql = "";

		sql += "CREATE TABLE tb_trx_info (";
		sql += "    trxcode CHARACTER VARYING(50) NOT NULL,";
		sql += "    if_type CHARACTER VARYING(50) NOT NULL,";
		sql += "    svc_code CHARACTER VARYING(50) NOT NULL,";
		sql += "    mode CHARACTER VARYING(50) NOT NULL,";
		sql += "    nonce CHARACTER VARYING(100) DEFAULT NULL,";
		sql += "    vp_verify_result CHARACTER VARYING(1) DEFAULT 'N' NOT NULL,";
		sql += "    vp CHARACTER LARGE OBJECT DEFAULT NULL,";
		sql += "    trx_sts_code CHARACTER VARYING(4) DEFAULT '0001' NOT NULL,";
		sql += "    profile_send_dt TIMESTAMP DEFAULT NULL,";
		sql += "    img_send_dt TIMESTAMP DEFAULT NULL,";
		sql += "    vp_recept_dt TIMESTAMP DEFAULT NULL,";
		sql += "    error_cn CHARACTER VARYING(4000) DEFAULT NULL,";
		sql += "    reg_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,";
		sql += "    udt_dt TIMESTAMP DEFAULT NULL,";
		sql += "    PRIMARY KEY (trxcode)";
		sql += ");";

		try (
			HikariDataSource hikariDataSourceTemp = new HikariDataSource(hikariConfig);
			Connection connection = hikariDataSourceTemp.getConnection();
			PreparedStatement ps = connection.prepareStatement(sql)
		) {
			ps.execute();
		} catch (SQLException e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, null, e);
		}
	}

	/**
	 * DataSourceTransactionManager 생성 및 Bean 등록
	 *
	 * @return DataSourceTransactionManager
	 * @throws SpException
	 */
	@Bean
	DataSourceTransactionManager transactionManager() throws SpException {
		DataSourceTransactionManager dataSourceTransactionManager = null;

		try {
			dataSourceTransactionManager = new DataSourceTransactionManager(dataSource());
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, null, e);
		}

		return dataSourceTransactionManager;
	}

	/**
	 * SqlSessionFactory 생성 및 Bean 등록
	 *
	 * @param dataSource DataSource
	 * @return SqlSessionFactory
	 * @throws SpException
	 */
	@Bean
	SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws SpException {
		SqlSessionFactory sqlSessionFactory = null;

		try {
			SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

			sqlSessionFactoryBean.setDataSource(dataSource);
			sqlSessionFactoryBean.setTypeAliasesPackage("mip.mva.sp.**.vo");

			if (ObjectUtils.isEmpty(configBean.getVerifyConfig().getDb())) {
				sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/h2/*.xml"));
			} else {
				sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:/mapper/" + configBean.getVerifyConfig().getDb().getProvider() + "/*.xml"));
			}

			org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();

			configuration.setMapUnderscoreToCamelCase(true);
			configuration.setJdbcTypeForNull(JdbcType.VARCHAR);

			sqlSessionFactoryBean.setConfiguration(configuration);

			sqlSessionFactory = sqlSessionFactoryBean.getObject();
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, null, e);
		}

		return sqlSessionFactory;
	}

	/**
	 * SqlSessionTemplate 생성 및 Bean 등록
	 *
	 * @param sqlSessionFactory SqlSessionFactory
	 * @return SqlSessionTemplate
	 * @throws SpException
	 */
	@Bean
	SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws SpException {
		SqlSessionTemplate sqlSessionTemplate = null;

		try {
			sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		} catch (Exception e) {
			throw new SpException(MipErrorEnum.SP_DB_ERROR, null, e);
		}

		return sqlSessionTemplate;
	}

}
