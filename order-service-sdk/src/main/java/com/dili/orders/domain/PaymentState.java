package com.dili.orders.domain;

public enum PaymentState {

	UNRECEIVED("未回款", 1), RECEIVED("已回款", 2);

	private String name;
	private Integer value;

	private PaymentState(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

	public static PaymentState valueOf(Integer value) {
		for (PaymentState state : PaymentState.values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		throw new IllegalArgumentException("未知的回款状态");
	}

}
