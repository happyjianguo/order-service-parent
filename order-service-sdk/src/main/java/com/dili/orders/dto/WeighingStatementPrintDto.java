package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;

public class WeighingStatementPrintDto extends WeighingStatement {

	/**
	 * 过磅员登录账号
	 */
	private String weighingOperatorUserName;
	/**
	 * 过磅员姓名
	 */
	private String weighingOperatorName;
	/**
	 * 结算员登录账号
	 */
	private String settlementOperatorUserName;
	/**
	 * 结算员姓名
	 */
	private String settlementOperatorName;

	public String getWeighingOperatorUserName() {
		return weighingOperatorUserName;
	}

	public void setWeighingOperatorUserName(String weighingOperatorUserName) {
		this.weighingOperatorUserName = weighingOperatorUserName;
	}

	public String getWeighingOperatorName() {
		return weighingOperatorName;
	}

	public void setWeighingOperatorName(String weighingOperatorName) {
		this.weighingOperatorName = weighingOperatorName;
	}

	public String getSettlementOperatorUserName() {
		return settlementOperatorUserName;
	}

	public void setSettlementOperatorUserName(String settlementOperatorUserName) {
		this.settlementOperatorUserName = settlementOperatorUserName;
	}

	public String getSettlementOperatorName() {
		return settlementOperatorName;
	}

	public void setSettlementOperatorName(String settlementOperatorName) {
		this.settlementOperatorName = settlementOperatorName;
	}

}
