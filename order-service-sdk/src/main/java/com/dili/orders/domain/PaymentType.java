package com.dili.orders.domain;

public enum PaymentType {

	CARD("园区卡", 1), CREDIT("赊销", 2);

	private String name;
	private Integer value;

	private PaymentType(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

	public static PaymentType valueOf(Integer value) {
		for (PaymentType type : PaymentType.values()) {
			if (type.getValue().equals(value)) {
				return type;
			}
		}
		throw new IllegalArgumentException("未知的支付类型");
	}

}
