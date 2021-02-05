package com.dili.orders.domain;

public enum WeighingBillState {

	NO_SETTLEMENT("未结算", 1), FROZEN("已冻结", 2), INVALIDATED("已作废", 3), SETTLED("已结算", 4), REFUNDED("已撤销", 5), CLOSED("已关闭", 6), NOT_RECEIVABLE("未回款", 7);

	private String name;
	private Integer value;

	private WeighingBillState(String name, Integer value) {
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
