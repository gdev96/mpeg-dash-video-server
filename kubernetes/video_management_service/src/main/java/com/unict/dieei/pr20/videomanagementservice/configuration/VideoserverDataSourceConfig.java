package com.unict.dieei.pr20.videomanagementservice.configuration;

import com.unict.dieei.pr20.videomanagementservice.model.videoserver.User;
import com.unict.dieei.pr20.videomanagementservice.model.videoserver.Video;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.unict.dieei.pr20.videomanagementservice.repository.videoserver",
        entityManagerFactoryRef = "videoserverEntityManagerFactory",
        transactionManagerRef = "videoserverTransactionManager"
)
public class VideoserverDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.videoserver")
    public DataSourceProperties videoserverDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.videoserver.configuration")
    public HikariDataSource videoserverDataSource() {
        return videoserverDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "videoserverEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean videoserverEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(videoserverDataSource()).packages(Video.class, User.class).build();
    }

    @Primary
    @Bean(name = "videoserverTransactionManager")
    public PlatformTransactionManager videoserverTransactionManager(
            final @Qualifier("videoserverEntityManagerFactory") LocalContainerEntityManagerFactoryBean videoserverEntityManagerFactory) {
        return new JpaTransactionManager(videoserverEntityManagerFactory.getObject());
    }
}
