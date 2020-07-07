package com.dili.order.dto;

/**
 * 确认预授权交易请求参数类
 * 
 * @author jiang
 *
 */
public class PaymentPreauthorizedTradeCommitDto {

	/**
	 * 交易号
	 */
	private String tradeId;

	/**
	 * 收款方账号
	 */
	private Long accountId;

	/**
	 * 支付渠道，1-账户渠道 2-现金渠道 3-POS渠道 4-网银渠道，交易过磅只能是1->账户渠道
	 */
	private final Integer channelId = 1;

	/**
	 * 账户密码
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

	public Integer getChannelId() {
		return channelId;
	}

}
