package com.dili.orders.dto;

public class PaymentTradeCommitResponseDto {

	/**
	 * 账户id
	 */
	private Long accountId;
	/**
	 * 期初余额
	 */
	private Long balance;
	/**
	 * 操作金额-为0
	 */
	private Long amount;
	/**
	 * 发生时间
	 */
	private String when;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

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

	public String getWhen() {
		return when;
	}

	public void setWhen(String when) {
		this.when = when;
	}

}
