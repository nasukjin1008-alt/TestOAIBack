package co.dzone.config;

import co.dzone.framework.datasource.DynamicRoutingDataSource;
import co.dzone.framework.util.ObjectUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    @Value("${mybatis.config-location}")
    private String CONFIG_LOCATION_PATH;

    @Value("${mybatis.mapper-locations}")
    private String MAPPER_LOCATIONS_PATH;

    @Bean(name = "defaultDataSource")
    @ConfigurationProperties(prefix = "spring.ecm.datasource.hikari")
    public HikariConfig defaultHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("defaultDataSource");
        return config;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = defaultHikariConfig();

        // 읽기/쓰기 분리 설정이 없으면 단일 DataSource 사용
        if (ObjectUtil.isEmpty(config.getJdbcUrl())) {
            return new HikariDataSource(defaultHikariConfig());
        }

        HikariDataSource defaultDs = new HikariDataSource(config);

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("default", defaultDs);

        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(defaultDs);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setConfigLocation(resolver.getResource(CONFIG_LOCATION_PATH));
        factoryBean.setMapperLocations(resolver.getResources(MAPPER_LOCATIONS_PATH));

        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
