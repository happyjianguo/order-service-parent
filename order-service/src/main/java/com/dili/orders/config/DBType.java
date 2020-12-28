package com.dili.orders.config;

public enum DBType {

	READ("read"), WRITE("write");

	private String value;

	private DBType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
