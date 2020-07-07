package com.dili.orders.config;

import com.dili.orders.constants.OrdersConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

/**
 * 支付服务的Feign配置
 * ps：不要在该类上面加诸如 @Configuration，否则会变成全局配置
 */
public class PayServiceFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("appid", OrdersConstant.PAYMENT_APP_ID);
            template.header("token", OrdersConstant.PAYMENT_TOKEN);
        };
    }
}
