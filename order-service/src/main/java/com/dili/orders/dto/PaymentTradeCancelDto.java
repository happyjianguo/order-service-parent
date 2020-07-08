package com.dili.orders.dto;

/**
 * 预授权撤销交易请求参数类
 * 
 * @author jiang
 *
 */
public class PaymentTradeCancelDto {

	/**
	 * 订单交易号
	 */
	private String tradeId;

	/**
	 * 卖家资金账号
	 */
	private Long accountId;

	/**
	 * 买家交易密码
	 */
	private String password;

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

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
