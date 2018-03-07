package xyz.nesting.payment.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSource;

import xyz.nesting.common.db.MyBatisDao;

@Configuration
@MapperScan(basePackages = DruidDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "sqlSessionFactory", annotationClass=MyBatisDao.class)
public class DruidDataSourceConfig {

    static final String PACKAGE = "xyz.nesting.payment";
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";

    @Value("${mysql.druid.datasource.url}")
    private String url;

    @Value("${mysql.druid.datasource.username}")
    private String user;

    @Value("${mysql.druid.datasource.password}")
    private String password;

    @Value("${mysql.druid.datasource.driverClassName}")
    private String driverClass;

    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource)
            throws Exception {
    	
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(DruidDataSourceConfig.MAPPER_LOCATION));
        
        sessionFactory.setTypeAliasesPackage("xyz.nesting.payment.model;xyz.nesting.payment.protocol;com.tencent.protocol.pay_protocol;xyz.nesting.common.protocol");
        
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
        return sessionFactory.getObject();
    }
    
}