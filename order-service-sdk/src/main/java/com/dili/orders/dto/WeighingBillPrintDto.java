package com.dili.orders.dto;

import com.dili.orders.domain.WeighingBill;

public class WeighingBillPrintDto extends WeighingBill {

	/**
	 * 过磅员登录账号
	 */
	private String weighingOperatorUserName;
	/**
	 * 过磅员姓名
	 */
	private String weighingOperatorName;

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

}
