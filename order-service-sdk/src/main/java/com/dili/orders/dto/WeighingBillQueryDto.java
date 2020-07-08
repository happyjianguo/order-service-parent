package com.dili.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;

import com.dili.orders.domain.WeighingBill;
import com.dili.ss.domain.annotation.Operator;

public class WeighingBillQueryDto extends WeighingBill {

	/**
	 * 
	 */
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

}
