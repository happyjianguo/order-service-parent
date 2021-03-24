package com.dili.orders.service.component.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;

@Component
public class FarmerSellerPoundageCalculator extends SellerPoundageCalculator {

	@Autowired
	public FarmerSellerPoundageCalculator(ChargeRuleRpc chargeRuleRpc, BusinessChargeItemRpc businessChargeItemRpc) {
		super(chargeRuleRpc, businessChargeItemRpc);
	}

	@Override
	public Long calculate(PoundageCalculateParam param) {
		return 0L;
	}

}
