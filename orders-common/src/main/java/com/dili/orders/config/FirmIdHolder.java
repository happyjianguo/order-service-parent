package com.dili.orders.config;

import org.springframework.stereotype.Component;

public class FirmIdHolder {

	private static final ThreadLocal<String> FIRM_THREAD_LOCAL = new ThreadLocal<String>();

	public static String getFirmId() {
		return FIRM_THREAD_LOCAL.get();
	}

	public static void setFirmId(String firmId) {
		FIRM_THREAD_LOCAL.set(firmId);
	}

	public static void reset() {
		FIRM_THREAD_LOCAL.remove();
	}
}
