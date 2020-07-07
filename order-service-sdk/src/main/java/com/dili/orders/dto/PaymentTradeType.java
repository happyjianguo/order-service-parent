package com.dili.orders.dto;

public enum PaymentTradeType {

	TRADE("即时交易", 20), PREAUTHORIZED("预授权", 21);

	private PaymentTradeType(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	private String name;
	private Integer value;

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

}
