package com.dili.orders.config;

public class ClientIpHolder {

	private static final ThreadLocal<String> IP_THREAD_LOCAL = new ThreadLocal<String>();

	public static String getIp() {
		return IP_THREAD_LOCAL.get();
	}

	public static void setIp(String ip) {
		IP_THREAD_LOCAL.set(ip);
	}

	public static void reset() {
		IP_THREAD_LOCAL.remove();
	}
}
