package com.dili.orders.service.component.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.orders.service.component.FeeCalculator;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;

@Component
public class SellerPoundageCalculator extends BasePoundageCalculator<PoundageCalculateParam> implements FeeCalculator<PoundageCalculateParam> {

	@Autowired
	public SellerPoundageCalculator(ChargeRuleRpc chargeRuleRpc, BusinessChargeItemRpc businessChargeItemRpc) {
		super(chargeRuleRpc, businessChargeItemRpc);
	}

}
