package com.dili.orders.dto;

import java.time.LocalDateTime;

import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingStatement;
import com.dili.ss.util.MoneyUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WeighingBillListPageDto extends WeighingBill {

	/**
	 *
	 */
	@Transient
	private static final long serialVersionUID = -4231427191843086522L;

	private WeighingStatement statement;
	private WeighingBillOperationRecord operationRecord;
	private String tradeTypeName;
	private Long unitWeightPrice;
	private Long unitPiecePrice;
	/**检测数值*/
	private String latestPdResult;
	/**检测结果描述*/
	private String detectStateDesc;

	public String getDetectStateDesc() {
		return detectStateDesc;
	}

	public void setDetectStateDesc(String detectStateDesc) {
		this.detectStateDesc = detectStateDesc;
	}

	public String getLatestPdResult() {
		return latestPdResult;
	}

	public void setLatestPdResult(String latestPdResult) {
		this.latestPdResult = latestPdResult;
	}

	public WeighingStatement getStatement() {
		return statement;
	}

	public void setStatement(WeighingStatement statement) {
		this.statement = statement;
	}

	public WeighingBillOperationRecord getOperationRecord() {
		return operationRecord;
	}

	public void setOperationRecord(WeighingBillOperationRecord operationRecord) {
		this.operationRecord = operationRecord;
	}

	public Long getUnitWeightPrice() {
		return unitWeightPrice;
	}

	public void setUnitWeightPrice(Long unitWeightPrice) {
		this.unitWeightPrice = unitWeightPrice;
	}

	public Long getUnitPiecePrice() {
		return unitPiecePrice;
	}

	public void setUnitPiecePrice(Long unitPiecePrice) {
		this.unitPiecePrice = unitPiecePrice;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getWeighingOperationTime() {
		return super.getModifiedTime() != null ? super.getModifiedTime() : super.getCreatedTime();
	}

	public String getTradeTypeName() {
		return tradeTypeName;
	}

	public void setTradeTypeName(String tradeTypeName) {
		this.tradeTypeName = tradeTypeName;
	}

}
