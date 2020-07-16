package com.dili.orders.dto;

import java.util.List;

import javax.persistence.Transient;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingStatement;

public class WeighingBillDetailDto extends WeighingBill {

	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 8261542760333829553L;

	private WeighingStatement statement;
	private List<WeighingBillOperationRecord> records;

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

}
