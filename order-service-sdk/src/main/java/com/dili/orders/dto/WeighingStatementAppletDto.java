package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.domain.WeighingStatementState;

public class WeighingStatementAppletDto extends WeighingStatement {

	public String getStateName() {
		if (this.getState() == null) {
			return null;
		}
		return WeighingStatementState.valueOf(this.getState()).getName();

	}
}
