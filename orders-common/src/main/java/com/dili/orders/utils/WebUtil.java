package com.dili.orders.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class WebUtil {

	/**
	 * 获取应用层ip
	 * 
	 * @param request
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {
		return request.getHeader("clientIp");
	}

}
