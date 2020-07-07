package com.dili.orders.rpc;

import java.util.List;

import com.dili.orders.dto.AccountRequestDto;
import com.dili.orders.dto.FeeDto;

/**
 * 确认预授权交易请求参数类
 * 
 * @author jiang
 *
 */
public class PaymentTradeConfirmDto extends AccountRequestDto {

	/**
	 * 交易订单号
	 */
	private String tradeId;

	/**
	 * 付款方账号
	 */
	private Long accountId;

	/**
	 * 交易金额
	 */
	private Long amount;

	/**
	 * 账户交易密码
	 */
	private String password;

	/**
	 * 买家、卖家手续费
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

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
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

}
