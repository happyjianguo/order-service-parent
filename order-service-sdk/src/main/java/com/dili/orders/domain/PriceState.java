package com.dili.orders.domain;

public enum PriceState {

	APPROVING("待审核", 1), ACCEPTED("已通过", 2), REJECTED("已拒绝", 3);

	private String name;
	private Integer value;

	private PriceState(String name, Integer value) {
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
