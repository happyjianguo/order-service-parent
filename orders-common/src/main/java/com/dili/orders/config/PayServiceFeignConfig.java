package com.dili.orders.config;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dili.orders.constants.OrdersConstant;

import feign.RequestInterceptor;

/**
 * 支付服务的Feign配置 ps：不要在该类上面加诸如 @Configuration，否则会变成全局配置
 */
@Configuration
public class PayServiceFeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return template -> {
			String mchid = getHeaders(getHttpServletRequest()).get("mchid");
			if (StringUtils.isBlank(mchid)) {
				mchid = FirmIdHolder.getFirmId();
			}
			template.header("appid", OrdersConstant.PAYMENT_APP_ID);
			template.header("token", OrdersConstant.PAYMENT_TOKEN);
			template.header("mchid", mchid);
		};
	}

	private HttpServletRequest getHttpServletRequest() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Map<String, String> getHeaders(HttpServletRequest request) {
		Map<String, String> map = new LinkedHashMap<>();
		Enumeration<String> enumeration = request.getHeaderNames();
		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}
}
