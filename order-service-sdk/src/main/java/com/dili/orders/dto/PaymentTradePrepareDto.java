package com.dili.orders.dto;

/**
 * 创建支付订单请求参数类
 * 
 * @author jiang
 *
 */
public class PaymentTradePrepareDto {

	/**
	 * 交易类型，10-充值 11-提现 12-缴费 13-预授权缴费 14-预存款 20-即时交易 21-预授权交易 22-担保交易 23-账户转账
	 * 40-撤销交易 41-交易退款 42-交易冲正
	 */
	private Integer type;

	/**
	 * 收款方账号，卖家
	 */
	private Long accountId;

	/**
	 * 业务账号ID
	 */
	private Long businessId;

	/**
	 * 操作金额-分
	 */
	private Long amount;

	/**
	 * 外部流水号
	 */
	private String serialNo;

	/**
	 * 账务周期号
	 */
	private String cycleNo;

	/**
	 * 交易备注
	 */
	private String description;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getCycleNo() {
		return cycleNo;
	}

	public void setCycleNo(String cycleNo) {
		this.cycleNo = cycleNo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
