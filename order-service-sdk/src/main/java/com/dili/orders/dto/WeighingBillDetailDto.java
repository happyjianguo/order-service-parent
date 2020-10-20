package com.dili.orders.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingOperationType;
import com.dili.orders.domain.WeighingStatement;
import com.dili.ss.util.MoneyUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WeighingBillDetailDto extends WeighingBill {

	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 8261542760333829553L;

	private WeighingStatement statement;
	private List<WeighingBillOperationRecord> records;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime weighingTime;

	public WeighingStatement getStatement() {
		return statement;
	}

	public void setStatement(WeighingStatement statement) {
		this.statement = statement;
	}

	public List<WeighingBillOperationRecord> getRecords() {
		return records;
	}

	public void setRecords(List<WeighingBillOperationRecord> records) {
		this.records = records;
	}

	public String getUnitWeightPrice() {
		if (this.getMeasureType().equals(MeasureType.PIECE.getValue())) {
			return null;
		}
		return MoneyUtils.centToYuan(this.getUnitPrice() * 2);
	}

	public String getUnitPiecePrice() {
		if (this.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			return null;
		}
		return MoneyUtils.centToYuan(this.getUnitPrice());
	}

	public LocalDateTime getWeighingTime() {
		return weighingTime;
	}

	public void setWeighingTime(LocalDateTime weighingTime) {
		this.weighingTime = weighingTime;
	}

}
