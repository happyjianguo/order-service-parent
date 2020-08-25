package com.dili.orders.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/6/18 14:38
 * @Description: 卡账户响应Dto
 */
public class UserAccountCardResponseDto implements Serializable {
	/** */
	private static final long serialVersionUID = 9154422843880027453L;
	/** 市场id */
	private Long firmId;
	/** 账号id */
	private Long accountId;
	/** 父账号id */
	private Long parentAccountId;
	/** 卡交易类型: 1-买家 2-卖家 */
	private Integer accountType;
	/** 资金账号ID */
	private Long fundAccountId;
	/** 客户id */
	private Long customerId;
	/** 客户名称 */
	private String customerName;
	/** 客户编号 */
	private String customerCode;
	/** 客户市场类型(冗余customer_market) */
	private String customerMarketType;
	/** 客户证件类型 */
	private String customerCertificateType;
	/** 客户身份号 */
	private String customerCertificateNumber;
	/** 客户电话 */
	private String customerContactsPhone;
	/** 使用权限(充值、提现、交费等) {@link com.dili.account.type.UsePermissionType} */
	private List<String> permissionList;
	/** 卡ID */
	private Long cardId;
	/** 卡号 */
	private String cardNo;
	/** 卡账户用途 {@link com.dili.account.type.AccountUsageType} */
	private List<String> usageType;
	/** 卡类型-主/副/临时/联营 {@link com.dili.account.type.CardType} */
	private Integer cardType;
	/** 卡片状态 {@link com.dili.account.type.CardStatus} */
	private Integer cardState;
	/** 账户状态 {@link com.dili.account.type.AccountStatus} */
	private Integer accountState;
	/** 账户是否禁用 {@link com.dili.account.type.DisableState} */
	private Integer disabledState;
	/** 开卡时间 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime cardCreateTime;
	/** 创建人名字 */
	private String creator;
	/** 创建人id */
	private Long creatorId;

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Integer getAccountState() {
		return accountState;
	}

	public void setAccountState(Integer accountState) {
		this.accountState = accountState;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getFirmId() {
		return firmId;
	}

	public void setFirmId(Long firmId) {
		this.firmId = firmId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getParentAccountId() {
		return parentAccountId;
	}

	public void setParentAccountId(Long parentAccountId) {
		this.parentAccountId = parentAccountId;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public Long getFundAccountId() {
		return fundAccountId;
	}

	public void setFundAccountId(Long fundAccountId) {
		this.fundAccountId = fundAccountId;
	}

	public Long getCardId() {
		return cardId;
	}

	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public List<String> getUsageType() {
		return usageType;
	}

	public void setUsageType(List<String> usageType) {
		this.usageType = usageType;
	}

	public List<String> getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(List<String> permissionList) {
		this.permissionList = permissionList;
	}

	public Integer getCardType() {
		return cardType;
	}

	public void setCardType(Integer cardType) {
		this.cardType = cardType;
	}

	public Integer getCardState() {
		return cardState;
	}

	public void setCardState(Integer cardState) {
		this.cardState = cardState;
	}

	public LocalDateTime getCardCreateTime() {
		return cardCreateTime;
	}

	public void setCardCreateTime(LocalDateTime cardCreateTime) {
		this.cardCreateTime = cardCreateTime;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getCustomerCertificateType() {
		return customerCertificateType;
	}

	public void setCustomerCertificateType(String customerCertificateType) {
		this.customerCertificateType = customerCertificateType;
	}

	public String getCustomerCertificateNumber() {
		return customerCertificateNumber;
	}

	public void setCustomerCertificateNumber(String customerCertificateNumber) {
		this.customerCertificateNumber = customerCertificateNumber;
	}

	public String getCustomerContactsPhone() {
		return customerContactsPhone;
	}

	public void setCustomerContactsPhone(String customerContactsPhone) {
		this.customerContactsPhone = customerContactsPhone;
	}

	public Integer getDisabledState() {
		return disabledState;
	}

	public void setDisabledState(Integer disabledState) {
		this.disabledState = disabledState;
	}

	public String getCustomerMarketType() {
		return customerMarketType;
	}

	public void setCustomerMarketType(String customerMarketType) {
		this.customerMarketType = customerMarketType;
	}

}
