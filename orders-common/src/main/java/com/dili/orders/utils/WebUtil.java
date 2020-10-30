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
	 *
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public boolean strIsEmpty(String str) {
		return StringUtils.isEmpty(str);
	}

	public boolean listIsEmpty(List<Object> listObj) {
		if (listObj != null && listObj.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<String[]> getCrumbsByString(String crumbs) {
		if (StringUtils.isEmpty(crumbs)) {
			return null;
		}
		String[] sp = crumbs.split(",");
		List<String[]> list = new ArrayList<String[]>();
		for (String val : sp) {
			String[] url = val.split(":");
			list.add(url);
		}
		return list;
	}

	public int[] pageSplit(int curr, int total, int count) {
		int start = Math.max(1, curr - count / 2);
		int end = Math.min(total, start + count - 1);
		start = Math.max(1, end - count + 1);
		return createSplit(start, end);
	}

	private int[] createSplit(int start, int end) {
		int size = end + 1 - start;
		int[] pages = new int[size];
		for (int i = 0, j = start; i < size; i++, j++) {
			pages[i] = j;
		}
		return pages;
	}

	/**
	 * 获取上一页地址
	 * 
	 * @param req
	 * @return
	 */
	public static String fetchReferer(HttpServletRequest req) {
		String url = req.getHeader("referer");
		if (url == null) {
			url = "/";
		}
		StringBuffer sb = new StringBuffer();
		String msg = "(?<=[&?]{1})msg=(.*?[&?$])";
		String success = "(?<=[&?]{1})success=(.*?[&?$])";
		url = url.replaceAll(msg, "");
		url = url.replaceAll(success, "");
		sb.append(url);
		return sb.toString();
	}

	/* 获取当前时间 */
	public static Timestamp getCurrentTimestamp() {
		long time = System.currentTimeMillis();
		return new Timestamp(time);
	}

	public static String getRemoteIP(HttpServletRequest request) {
		String ip = null;
		if ((request.getHeader("x-forwarded-for") != null) && (!"unknown".equalsIgnoreCase(request.getHeader("x-forwarded-for")))) {
			ip = request.getHeader("x-forwarded-for");
		} else {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getClientIP(HttpServletRequest request) {
		return request.getHeader("clientIp");
	}

}
