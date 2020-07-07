package com.dili.orders.dto;

/**
 * 支付系统费用项，这个值是由业务系统确定
 * 
 * @author jiang
 *
 */
public enum FeeType {
	BUYER_POUNDAGE("买家手续费", 1), SELLER_POUNDAGE("卖家手续费", 2);

	private String name;
	private Integer value;

	private FeeType(String name, Integer value) {
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
