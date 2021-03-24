package com.dili.orders.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;

public class DomainUtil {

	// 获取单价，如果按件计价需要转换为按公斤斤计价
	public static Long getConvertDoubleUnitPrice(WeighingBill weighingBill) {
		Long actualPrice = null;
		if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			actualPrice = weighingBill.getUnitPrice() * 2;
		} else {
			// 转换为斤的价格，保留到分，四舍五入
			actualPrice = new BigDecimal(weighingBill.getUnitPrice() * 2).divide(new BigDecimal(weighingBill.getUnitWeight()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue();
		}
		return actualPrice;
	}
}
