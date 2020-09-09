package com.dili.orders.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.WeighingStatement;
import com.fasterxml.jackson.annotation.JsonFormat;

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

	/**
	 * 计量方式
	 */
	private String measureType;

	/**
	 * 单价或件价
	 */
	private Long unitPrice;

	/**
	 * 件数
	 */
	private Integer unitAmount;

	private Boolean reprint;

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

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public Long getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Long unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(Integer unitAmount) {
		this.unitAmount = unitAmount;
	}

}
