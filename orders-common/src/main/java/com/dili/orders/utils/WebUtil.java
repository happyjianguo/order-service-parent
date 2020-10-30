package com.dili.orders.utils;

import javax.servlet.http.HttpServletRequest;

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
