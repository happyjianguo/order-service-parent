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
	/** 检测数值 */
	private String latestPdResult;
	/** 检测结果描述 */
	private String detectStateDesc;

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

	public String getConvertUnitPrice() {
		Long actualPrice = null;
		if (this.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			actualPrice = super.getUnitPrice() * 2;
		} else {
			// 转换为斤的价格，保留到分，四舍五入
			actualPrice = new BigDecimal(super.getUnitPrice() * 2).divide(new BigDecimal(super.getUnitWeight()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue();
		}
		return MoneyUtils.centToYuan(actualPrice);
	}

	public String getUnitWeightPrice() {
		if (this.getMeasureType().equals(MeasureType.PIECE.getValue())) {
			return null;
		}
		return MoneyUtils.centToYuan(super.getUnitPrice() * 2);
	}

	public String getUnitPiecePrice() {
		if (this.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			return null;
		}
		return MoneyUtils.centToYuan(super.getUnitPrice());
	}

	public LocalDateTime getWeighingTime() {
		return weighingTime;
	}

	public void setWeighingTime(LocalDateTime weighingTime) {
		this.weighingTime = weighingTime;
	}

	public String getLatestPdResult() {
		return latestPdResult;
	}

	public void setLatestPdResult(String latestPdResult) {
		this.latestPdResult = latestPdResult;
	}

	public String getDetectStateDesc() {
		return detectStateDesc;
	}

	public void setDetectStateDesc(String detectStateDesc) {
		this.detectStateDesc = detectStateDesc;
	}

}
