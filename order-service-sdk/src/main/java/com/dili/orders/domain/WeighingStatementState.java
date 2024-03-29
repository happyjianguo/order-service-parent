package com.dili.orders.domain;

public enum WeighingStatementState {

	UNPAID("未结算", 1), PAID("已结算", 2), REFUNDED("已撤销", 3), FROZEN("已冻结", 4), INVALIDATED("已作废", 5), CLOSED("已关闭", 6);

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

	public static WeighingStatementState valueOf(Integer value) {
		for (WeighingStatementState state : WeighingStatementState.values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		throw new IllegalArgumentException("未知结算单状态");
	}

}
