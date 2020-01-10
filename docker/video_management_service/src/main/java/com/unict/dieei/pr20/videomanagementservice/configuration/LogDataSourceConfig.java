package com.unict.dieei.pr20.videomanagementservice.configuration;

import com.unict.dieei.pr20.videomanagementservice.model.log.LogInfo;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.unict.dieei.pr20.videomanagementservice.repository.log",
        entityManagerFactoryRef = "logEntityManagerFactory",
        transactionManagerRef= "logTransactionManager"
)
public class LogDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.log")
    public DataSourceProperties logDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.log.configuration")
    public HikariDataSource logDataSource() {
        return logDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "logEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean logEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(logDataSource()).packages(LogInfo.class).build();
    }

    @Bean(name = "logTransactionManager")
    public PlatformTransactionManager logTransactionManager(
            final @Qualifier("logEntityManagerFactory") LocalContainerEntityManagerFactoryBean logEntityManagerFactory) {
        return new JpaTransactionManager(logEntityManagerFactory.getObject());
    }
}
