package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 策略：若等于最小价格 则更新最小交易额、最小交易量
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class EqLowPriceStrategy implements UpdateTransDataTempInfoStrategy{
    @Override
    public WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        System.out.println("------策略：EqLowPriceStrategy");
        transData.setMinTradeAmount(transData.getMinTradeAmount() + weighingSettlementBill.getTradeAmount());
        transData.setMinTradeWeight(transData.getMinTradeWeight() + weighingSettlementBill.getNetWeight());
        return transData;
    }
}