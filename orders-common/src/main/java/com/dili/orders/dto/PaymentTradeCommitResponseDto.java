package com.dili.orders.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class PaymentTradeCommitResponseDto {

	/**
	 * 账户id
	 */
	private Long accountId;
	/**
	 * 期初余额
	 */
	private Long balance;
	/**
	 * 操作金额-为0
	 */
	private Long amount;

	/**
	 * 期初冻结金额
	 */
	private Long frozenBalance;

	/**
	 * 冻结/解冻金额-当前有值
	 */
	private Long frozenAmount;
	/**
	 * 发生时间
	 */
	/**
	 * 发生时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime when;

	private List<PaymentStream> streams;

	/**
	 * 如果是即时交易，这个字段是卖方账户交易信息
	 */
	private PaymentTradeCommitResponseDto relation;

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Long getBalance() {
		return balance;
	}

	public void setBalance(Long balance) {
		this.balance = balance;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getFrozenBalance() {
		return frozenBalance;
	}

	public void setFrozenBalance(Long frozenBalance) {
		this.frozenBalance = frozenBalance;
	}

	public Long getFrozenAmount() {
		return frozenAmount;
	}

	public void setFrozenAmount(Long frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	public LocalDateTime getWhen() {
		return when;
	}

	public void setWhen(LocalDateTime when) {
		this.when = when;
	}

	public List<PaymentStream> getStreams() {
		return streams;
	}

	public void setStreams(List<PaymentStream> streams) {
		this.streams = streams;
	}

	public PaymentTradeCommitResponseDto getRelation() {
		return relation;
	}

	public void setRelation(PaymentTradeCommitResponseDto relation) {
		this.relation = relation;
	}

}
