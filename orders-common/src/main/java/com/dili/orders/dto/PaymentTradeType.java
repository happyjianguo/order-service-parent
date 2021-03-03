package com.dili.orders.dto;

public enum PaymentTradeType {

	PAY("缴费", 12), TRADE("即时交易", 20), PREAUTHORIZED("预授权", 21), TRANSFER("转账", 23);

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
