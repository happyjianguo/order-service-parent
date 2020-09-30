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

	public String getUnitWeightPrice() {
		if (this.getMeasureType().equals(MeasureType.PIECE.getValue())) {
			return null;
		}
		return MoneyUtils.centToYuan(this.getUnitPrice());
	}

	public String getUnitPiecePrice() {
		if (this.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			return null;
		}
		return MoneyUtils.centToYuan(this.getUnitPrice());
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getWeighingOperationTime() {
		return super.getModifiedTime() != null ? super.getModifiedTime() : super.getCreatedTime();
	}

}
