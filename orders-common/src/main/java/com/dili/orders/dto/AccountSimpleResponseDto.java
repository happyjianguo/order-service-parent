package com.dili.orders.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 账户信息（包含余额）
 *
 * @Auther: miaoguoxin
 * @Date: 2020/7/7 15:21
 */
public class AccountSimpleResponseDto implements Serializable {
	/**
	 * 账户资金信息
	 */
	private BalanceResponseDto accountFund;
	/**
	 * 账户信息
	 */
	private UserAccountCardResponseDto accountInfo;

	private Integer buyerRegionTag;

	private List<String> customerCharacterTypes;

	public Integer getBuyerRegionTag() {
		return buyerRegionTag;
	}

	public void setBuyerRegionTag(Integer buyerRegionTag) {
		this.buyerRegionTag = buyerRegionTag;
	}

	public AccountSimpleResponseDto(BalanceResponseDto accountFund, UserAccountCardResponseDto accountInfo) {
		this.accountFund = accountFund;
		this.accountInfo = accountInfo;
	}

	public AccountSimpleResponseDto() {
	}

	public BalanceResponseDto getAccountFund() {
		return accountFund;
	}

	public void setAccountFund(BalanceResponseDto accountFund) {
		this.accountFund = accountFund;
	}

	public UserAccountCardResponseDto getAccountInfo() {
		return accountInfo;
	}

	public void setAccountInfo(UserAccountCardResponseDto accountInfo) {
		this.accountInfo = accountInfo;
	}

	public List<String> getCustomerCharacterTypes() {
		return customerCharacterTypes;
	}

	public void setCustomerCharacterTypes(List<String> customerCharacterTypes) {
		this.customerCharacterTypes = customerCharacterTypes;
	}
}
