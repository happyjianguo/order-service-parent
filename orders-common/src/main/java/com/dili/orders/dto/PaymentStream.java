package com.dili.orders.dto;

public class PaymentStream {

	/**
	 * 期初余额
	 */
	private Long balance;
	/**
	 * 操作金额-正/负值
	 */
	private Long amount;
	private Long type;
	private String typeName;

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
