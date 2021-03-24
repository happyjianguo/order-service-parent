package com.dili.orders.service.component;

public interface FeeCalculator<T> {

	Long calculate(T param);
}
