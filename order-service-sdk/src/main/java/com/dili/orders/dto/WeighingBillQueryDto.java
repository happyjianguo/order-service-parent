package com.dili.orders.dto;

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

}
