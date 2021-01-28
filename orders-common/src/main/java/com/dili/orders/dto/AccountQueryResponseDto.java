package com.dili.orders.dto;

public class AccountQueryResponseDto {

	// 资金账号ID
	private Integer fundAccountId;
	// 账号id
	private Integer accountId;

	public Integer getFundAccountId() {
		return fundAccountId;
	}

	public void setFundAccountId(Integer fundAccountId) {
		this.fundAccountId = fundAccountId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

}
