package com.dili.orders.domain;

public enum WeighingStatementState {

	UNPAID("待支付", 1), PAID("已支付", 2), REFUNDED("已退款", 3), FROZEN("已冻结", 3);

	private String name;
	private Integer value;

	private WeighingStatementState(String name, Integer value) {
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
