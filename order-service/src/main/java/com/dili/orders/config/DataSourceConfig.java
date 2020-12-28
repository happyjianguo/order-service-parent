package com.dili.orders.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	@Bean(name = "selectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().build();
    }
 
    @Bean(name = "updateDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().build();
    }
}
