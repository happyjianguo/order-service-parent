package com.dili.orders.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.WeighingBill;
import com.dili.ss.domain.annotation.Operator;

public class WeighingBillQueryDto extends WeighingBill {

	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = -789812465777179417L;
	/**
	 * 状态
	 */
	@Operator(Operator.IN)
	@Column(name = "`state`")
	private List<Integer> states;
	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`created_time`")
	private LocalDateTime createdStart;
	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`created_time`")
	private LocalDateTime createdEnd;
	@Transient
	private String goodsSplitStr;
	@Operator(Operator.IN)
	@Column(name = "'goods_name'")
	private List<String> goodsNames;
	@Transient
	private String wsSerialNo;
	@Transient
	private Long operatorId;
	@Transient
	private String unitPriceStart;
	@Transient
	private Long unitPriceStartValue;
	@Transient
	private String unitPriceEnd;
	@Transient
	private Long unitPriceEndValue;

	public List<Integer> getStates() {
		return states;
	}

	public void setStates(List<Integer> states) {
		this.states = states;
	}

	public LocalDateTime getCreatedStart() {
		return createdStart;
	}

	public void setCreatedStart(LocalDateTime createdStart) {
		this.createdStart = createdStart;
	}

	public LocalDateTime getCreatedEnd() {
		return createdEnd;
	}

	public void setCreatedEnd(LocalDateTime createdEnd) {
		this.createdEnd = createdEnd;
	}

	public String getGoodsSplitStr() {
		return goodsSplitStr;
	}

	public void setGoodsSplitStr(String goodsSplitStr) {
		this.goodsSplitStr = goodsSplitStr;
		if (StringUtils.isNotBlank(goodsSplitStr)) {
			this.goodsNames = Arrays.asList(goodsSplitStr.split("，"));
		}
	}

	@JSONField(serialize = false)
	public List<String> getGoodsNames() {
		return goodsNames;
	}

	public String getWsSerialNo() {
		return wsSerialNo;
	}

	public void setWsSerialNo(String wsSerialNo) {
		this.wsSerialNo = wsSerialNo;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public void setUnitPriceStart(String unitPriceStart) {
		this.unitPriceStart = unitPriceStart;
		if (unitPriceStart != null) {
			this.unitPriceStartValue = new BigDecimal(unitPriceStart).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue();
		}
	}

	public String getUnitPriceStart() {
		return unitPriceStart;
	}

	public String getUnitPriceEnd() {
		return unitPriceEnd;
	}

	public void setUnitPriceEnd(String unitPriceEnd) {
		this.unitPriceEnd = unitPriceEnd;
		if (unitPriceEnd != null) {
			this.unitPriceEndValue = new BigDecimal(unitPriceEnd).setScale(2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue();
		}
	}

	@JSONField(serialize = false)
	public Long getUnitPriceStartValue() {
		return unitPriceStartValue;
	}

	@JSONField(serialize = false)
	public Long getUnitPriceEndValue() {
		return unitPriceEndValue;
	}

}
