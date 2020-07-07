package com.dili.orders.dto;

import java.util.List;

import javax.persistence.Column;

import com.dili.orders.domain.WeighingBill;
import com.dili.ss.domain.annotation.Operator;

public class WeighingBillQueryDto extends WeighingBill {

	/**
	 * 状态
	 */
	@Operator(Operator.IN)
	@Column(name = "`state`")
	private List<Integer> states;

	public List<Integer> getStates() {
		return states;
	}

	public void setStates(List<Integer> states) {
		this.states = states;
	}

}
