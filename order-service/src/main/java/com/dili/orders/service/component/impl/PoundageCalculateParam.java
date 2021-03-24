package com.dili.orders.service.component.impl;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PoundageCalculateParam {

	private String businessType;
	private WeighingBill weighingBill;
	private WeighingStatement weighingStatement;

}