package com.dili.orders.domain;

public enum TradingBillType {

	WEIGHING("过磅交易", 1), FARMER("老农交易", 2), PROPRIETARY("自营交易", 3);

	private String name;
	private Integer value;

	private TradingBillType(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

}
