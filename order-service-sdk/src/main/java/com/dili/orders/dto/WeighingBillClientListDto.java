package com.dili.orders.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WeighingBillClientListDto extends WeighingBill {

	private WeighingStatement statement;
	// 过磅员id
	private Long weighingOperatorId;
	// 过磅员姓名
	private String weighingOperatorName;
	// 过磅时间
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime weighingOperationTime;
	// 交易类型名称
	private String tradeTypeName;

	public WeighingStatement getStatement() {
		return statement;
	}

	public void setStatement(WeighingStatement statement) {
		this.statement = statement;
	}

	public Long getWeighingOperatorId() {
		return weighingOperatorId;
	}

	public void setWeighingOperatorId(Long weighingOperatorId) {
		this.weighingOperatorId = weighingOperatorId;
	}

	public String getWeighingOperatorName() {
		return weighingOperatorName;
	}

	public void setWeighingOperatorName(String weighingOperatorName) {
		this.weighingOperatorName = weighingOperatorName;
	}

	public LocalDateTime getWeighingOperationTime() {
		return weighingOperationTime;
	}

	public void setWeighingOperationTime(LocalDateTime weighingOperationTime) {
		this.weighingOperationTime = weighingOperationTime;
	}

	public String getTradeTypeName() {
		return tradeTypeName;
	}

	public void setTradeTypeName(String tradeTypeName) {
		this.tradeTypeName = tradeTypeName;
	}

}
