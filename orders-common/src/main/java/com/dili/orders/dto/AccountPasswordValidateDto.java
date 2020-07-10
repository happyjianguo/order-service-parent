package com.dili.orders.dto;

/**
 * 资金账户密码校验请求参数
 * 
 * @author jiang
 *
 */
public class AccountPasswordValidateDto {

	/**
	 * 资金账户id
	 */
	private Long accountId;
	/**
	 * 密码
	 */
	private String password;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
