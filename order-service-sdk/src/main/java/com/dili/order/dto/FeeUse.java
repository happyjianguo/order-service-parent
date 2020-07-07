package com.dili.order.dto;

/**
 * 费用分买方费用（useFor=1）与卖方费用（useFor=2），且入账至商户收益账户；
 * 
 * @author jiang
 *
 */
public enum FeeUse {

	BUYER("买家", 1), SELLER("卖家", 2);

	private String name;
	private Integer value;

	private FeeUse(String name, Integer value) {
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
