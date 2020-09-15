package com.dili.orders.dto;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.WeighingBill;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WeighingBillPrintDto extends WeighingBill {

	/**
	 * 过磅员登录账号
	 */
	private String weighingOperatorUserName;
	/**
	 * 过磅员姓名
	 */
	private String weighingOperatorName;

	private Boolean reprint = false;

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

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getOperationTime() {
		return this.getModifiedTime() != null ? this.getModifiedTime() : this.getCreatedTime();
	}

	public Boolean getReprint() {
		return reprint;
	}

	public void setReprint(Boolean reprint) {
		this.reprint = reprint;
	}

	public String getReprintString() {
		if (this.reprint) {
			return "（补打）";
		}
		return "";
	}

}
