package com.dili;

import com.dili.ss.dto.DTOScan;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 由MyBatis Generator工具自动生成
 */
@SpringBootApplication
@MapperScan(basePackages = { "com.dili.orders.mapper", "com.dili.ss.dao" })
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.orders", "com.dili.commons","com.dili.logger.sdk" })
@RestfulScan({ "com.dili.orders.rpc", "com.dili.uap.sdk.rpc", "com.dili.bpmc.sdk.rpc" })
@DTOScan(value = { "com.dili.ss", "com.dili.orders.domain" })
@EnableDiscoveryClient
@EnableFeignClients(basePackages = { "com.diligrp.message.sdk.rpc", "com.dili.orders.rpc", "com.dili.assets.sdk.rpc", "com.dili.rule.sdk.rpc", "com.dili.customer.sdk.rpc", "com.dili.logger.sdk.rpc" })
public class Application extends SpringBootServletInitializer {

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
