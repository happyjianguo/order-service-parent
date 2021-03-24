package com.dili.orders.service.component.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.component.SerialNumberGenerator;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;

@Component
public class WeighingBillSerialNoGenerator implements SerialNumberGenerator {

	@Autowired
	protected UidRpc uidRpc;

	@Override
	public String generate() {
		BaseOutput<String> output = this.uidRpc.bizNumber(OrdersConstant.WEIGHING_BILL_SERIAL_NO_GENERATE_RULE_CODE);
		if (output == null) {
			throw new AppException("调用过磅单号生成服务无响应");
		}
		if (!output.isSuccess()) {
			throw new AppException(output.getMessage());
		}
		return output.getData();
	}

}
