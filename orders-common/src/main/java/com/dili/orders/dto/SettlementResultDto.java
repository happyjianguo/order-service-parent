package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;

public class SettlementResultDto {

	private WeighingStatement statement;
	private Boolean priceApprove;

	public SettlementResultDto() {
		super();
	}

	public SettlementResultDto(WeighingStatement statement, Boolean priceApprove) {
		super();
		this.statement = statement;
		this.priceApprove = priceApprove;
	}

	public WeighingStatement getStatement() {
		return statement;
	}

	public void setStatement(WeighingStatement statement) {
		this.statement = statement;
	}

	public Boolean getPriceApprove() {
		return priceApprove;
	}

	public void setPriceApprove(Boolean priceApprove) {
		this.priceApprove = priceApprove;
	}
}
