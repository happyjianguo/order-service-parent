package com.dili.orders.service.component.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;

@Component
public class FarmerBuyerPoundageCalculator extends BuyerPoundageCalculator {

	@Autowired
	public FarmerBuyerPoundageCalculator(ChargeRuleRpc chargeRuleRpc, BusinessChargeItemRpc businessChargeItemRpc) {
		super(chargeRuleRpc, businessChargeItemRpc);
	}

	@Override
	public Long calculate(PoundageCalculateParam param) {
		return 0L;
	}

}
