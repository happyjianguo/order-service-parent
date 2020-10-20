package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatementState;

public class WeighingStatementAppletStateCountDto {

	/**
	 * 状态
	 */
	private Integer state;
	/**
	 * 冻结总金额
	 */
	private Long totalFrozenAmount;
	/**
	 * 交易总金额
	 */
	private Long totalTradeAmount;
	/**
	 * 总数
	 */
	private Long stateCount;

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getTotalFrozenAmount() {
		return totalFrozenAmount;
	}

	public void setTotalFrozenAmount(Long totalFrozenAmount) {
		this.totalFrozenAmount = totalFrozenAmount;
	}

	public Long getTotalTradeAmount() {
		return totalTradeAmount;
	}

	public void setTotalTradeAmount(Long totalTradeAmount) {
		this.totalTradeAmount = totalTradeAmount;
	}

	public Long getStateCount() {
		return stateCount;
	}

	public void setStateCount(Long stateCount) {
		this.stateCount = stateCount;
	}

	public String getStateName() {
		if (this.state == null) {
			return null;
		}
		return WeighingStatementState.valueOf(this.state).getName();
	}

}
