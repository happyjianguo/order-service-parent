package com.dili.orders.config;

import com.dili.orders.constants.OrdersConstant;
import feign.RequestInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 支付服务的Feign配置 ps：不要在该类上面加诸如 @Configuration，否则会变成全局配置
 */
@Configuration
public class FeignHeaderConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return template -> {
			Map<String, String> headerMap = getHeaders(getHttpServletRequest());
			String mchid = headerMap.get("mchid");
			String ip = headerMap.get("clientIp");
			if (StringUtils.isBlank(mchid)) {
				mchid = FirmIdHolder.getFirmId();
			}
			if (StringUtils.isBlank(ip)) {
				ip = ClientIpHolder.getIp();
			}
			template.header("appid", OrdersConstant.PAYMENT_APP_ID);
			template.header("token", OrdersConstant.PAYMENT_TOKEN);
			template.header("mchid", mchid);
			template.header("clientIp", ip);

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
