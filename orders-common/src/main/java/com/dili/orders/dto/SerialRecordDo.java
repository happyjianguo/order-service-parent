package com.dili.orders.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 业务端账户流水
 *
 * @author bob
 */
public class SerialRecordDo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 客户类型
     */
    private String customerType;
    /**  */
    private Long id;
    /**
     * 流水号
     */
    private String serialNo;
    /**
     * 操作类型
     */
    private Integer type;
    /**
     * 账户ID
     */
    private Long accountId;
    /**
     * 关联卡号
     */
    private String cardNo;
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 客户编号
     */
    private String customerNo;
    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 资金动作-收入,支出
     */
    private Integer action;
    /**
     * 期初余额-分
     */
    private Long startBalance;
    /**
     * 操作金额-分
     */
    private Long amount;
    /**
     * 期末余额-分
     */
    private Long endBalance;
    /**
     * 交易类型-充值、提现、消费、转账、其他
     */
    private Integer tradeType;
    /**
     * 交易渠道-现金、POS、网银
     */
    private Integer tradeChannel = 1;
    /**
     * 交易流水号
     */
    private String tradeNo;
    /**
     * 资金项目
     */
    private Integer fundItem;
    /**
     * 资金项目名称
     */
    private String fundItemName;
    /**
     * 操作员ID
     */
    private Long operatorId;
    /**
     * 操作员工号
     */
    private String operatorNo;
    /**
     * 操作员名称
     */
    private String operatorName;
    /**
     * 操作时间-与支付系统保持一致
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;
    /**
     * 备注
     */
    private String notes;
    /**
     * 商户ID
     */
    private Long firmId;

    /**
     * 持卡人姓名
     */
    private String holdName;

    /**
     * SerialRecordEntity constructor
     */
    public SerialRecordDo() {
        super();
    }

    /**
     * setter for
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * getter for
     */
    public Long getId() {
        return id;
    }

    /**
     * setter for 流水号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * getter for 流水号
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * @return
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * setter for 账户ID
     */
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /**
     * getter for 账户ID
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * setter for 关联卡号
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * getter for 关联卡号
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * setter for 客户ID
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * getter for 客户ID
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * setter for 客户编号
     */
    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    /**
     * getter for 客户编号
     */
    public String getCustomerNo() {
        return customerNo;
    }

    /**
     * setter for 客户姓名
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * getter for 客户姓名
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * setter for 资金动作-收入,支出
     */
    public void setAction(Integer action) {
        this.action = action;
    }

    /**
     * getter for 资金动作-收入,支出
     */
    public Integer getAction() {
        return action;
    }

    /**
     * setter for 期初余额-分
     */
    public void setStartBalance(Long startBalance) {
        this.startBalance = startBalance;
    }

    /**
     * getter for 期初余额-分
     */
    public Long getStartBalance() {
        return startBalance;
    }

    /**
     * setter for 操作金额-分
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    /**
     * getter for 操作金额-分
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * setter for 期末余额-分
     */
    public void setEndBalance(Long endBalance) {
        this.endBalance = endBalance;
    }

    /**
     * getter for 期末余额-分
     */
    public Long getEndBalance() {
        return endBalance;
    }

    /**
     * setter for 交易类型-充值、提现、消费、转账、其他
     */
    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    /**
     * getter for 交易类型-充值、提现、消费、转账、其他
     */
    public Integer getTradeType() {
        return tradeType;
    }

    /**
     * setter for 交易渠道-现金、POS、网银
     */
    public void setTradeChannel(Integer tradeChannel) {
        this.tradeChannel = tradeChannel;
    }

    /**
     * getter for 交易渠道-现金、POS、网银
     */
    public Integer getTradeChannel() {
        return tradeChannel;
    }

    /**
     * setter for 交易流水号
     */
    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    /**
     * getter for 交易流水号
     */
    public String getTradeNo() {
        return tradeNo;
    }

    /**
     * setter for 资金项目
     */
    public void setFundItem(Integer fundItem) {
        this.fundItem = fundItem;
    }

    /**
     * getter for 资金项目
     */
    public Integer getFundItem() {
        return fundItem;
    }

    /**
     * setter for 资金项目名称
     */
    public void setFundItemName(String fundItemName) {
        this.fundItemName = fundItemName;
    }

    /**
     * getter for 资金项目名称
     */
    public String getFundItemName() {
        return fundItemName;
    }

    /**
     * setter for 操作员ID
     */
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * getter for 操作员ID
     */
    public Long getOperatorId() {
        return operatorId;
    }

    /**
     * setter for 操作员工号
     */
    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    /**
     * getter for 操作员工号
     */
    public String getOperatorNo() {
        return operatorNo;
    }

    /**
     * setter for 操作员名称
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * getter for 操作员名称
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * setter for 操作时间-与支付系统保持一致
     */
    public void setOperateTime(LocalDateTime operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * getter for 操作时间-与支付系统保持一致
     */
    public LocalDateTime getOperateTime() {
        return operateTime;
    }

    /**
     * setter for 备注
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * getter for 备注
     */
    public String getNotes() {
        return notes;
    }

    /**
     * setter for 商户ID
     */
    public void setFirmId(Long firmId) {
        this.firmId = firmId;
    }

    /**
     * getter for 商户ID
     */
    public Long getFirmId() {
        return firmId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getHoldName() {
        return holdName;
    }

    public void setHoldName(String holdName) {
        this.holdName = holdName;
    }

    @Override
    public String toString() {
        return "SerialRecordDo{" +
                "customerType='" + customerType + '\'' +
                ", id=" + id +
                ", serialNo='" + serialNo + '\'' +
                ", type=" + type +
                ", accountId=" + accountId +
                ", cardNo='" + cardNo + '\'' +
                ", customerId=" + customerId +
                ", customerNo='" + customerNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", action=" + action +
                ", startBalance=" + startBalance +
                ", amount=" + amount +
                ", endBalance=" + endBalance +
                ", tradeType=" + tradeType +
                ", tradeChannel=" + tradeChannel +
                ", tradeNo='" + tradeNo + '\'' +
                ", fundItem=" + fundItem +
                ", fundItemName='" + fundItemName + '\'' +
                ", operatorId=" + operatorId +
                ", operatorNo='" + operatorNo + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", operateTime=" + operateTime +
                ", notes='" + notes + '\'' +
                ", firmId=" + firmId +
                ", holdName='" + holdName + '\'' +
                '}';
    }
}
