package com.dili.orders.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.persistence.Transient;

import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingStatement;
import com.dili.ss.util.MoneyUtils;

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

	@Override
	public Integer getUnitWeight() {
		return super.getUnitWeight() == null ? null : new BigDecimal(super.getUnitWeight()).divide(new BigDecimal(2), 2, RoundingMode.HALF_UP).intValue();
	}

}
