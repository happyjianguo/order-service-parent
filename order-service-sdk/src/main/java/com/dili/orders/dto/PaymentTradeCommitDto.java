package com.dili.orders.dto;

import java.util.List;

/**
 * 即时交易请求参数
 * 
 * @author jiang
 *
 */
public class PaymentTradeCommitDto {

	/**
	 * 交易订单号
	 */
	private String tradeId;

	/**
	 * 买家资金账号
	 */
	private Long accountId;

	/**
	 * 业务账户，就是卡务的卡账户
	 */
	private Long businessId;

	/**
	 * 交易过磅渠道只能是1-账户
	 */
	private final Integer channelId = 1;

	/**
	 * 买家资金账户交易密码
	 */
	private String password;

	/**
	 * 买卖家手续费
	 */
	private List<FeeDto> fees;

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

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<FeeDto> getFees() {
		return fees;
	}

	public void setFees(List<FeeDto> fees) {
		this.fees = fees;
	}

	public Integer getChannelId() {
		return channelId;
	}

}
