package com.dili.orders.domain;

public enum MeasureType {

	WEIGHT("计重", "1"), PIECE("计件", "2");

	private MeasureType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	private String name;
	private String value;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
