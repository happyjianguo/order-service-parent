package com.dili.orders.dto;

public class AccountQueryDto {

	// 是否排除禁用状态 1：排除 0：不排除 默认1
	private Integer excludeUnusualState;

	// 主账户ID
	private Long parentAccountId;
	// 市场id
	private Long firmId;

	public Integer getExcludeUnusualState() {
		return excludeUnusualState;
	}

	public void setExcludeUnusualState(Integer excludeUnusualState) {
		this.excludeUnusualState = excludeUnusualState;
	}

	public Long getParentAccountId() {
		return parentAccountId;
	}

	public void setParentAccountId(Long parentAccountId) {
		this.parentAccountId = parentAccountId;
	}

	public Long getFirmId() {
		return firmId;
	}

	public void setFirmId(Long firmId) {
		this.firmId = firmId;
	}
}
