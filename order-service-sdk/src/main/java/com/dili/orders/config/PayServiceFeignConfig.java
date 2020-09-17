package com.dili.orders.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.dili.orders.constants.OrdersConstant;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import feign.RequestInterceptor;

/**
 * 支付服务的Feign配置 ps：不要在该类上面加诸如 @Configuration，否则会变成全局配置
 */
@Configuration
public class PayServiceFeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return template -> {
			template.header("appid", OrdersConstant.PAYMENT_APP_ID);
			template.header("token", OrdersConstant.PAYMENT_TOKEN);
			template.header("mchid", "8");
		};
	}
}
