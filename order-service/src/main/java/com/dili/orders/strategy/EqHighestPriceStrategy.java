package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 策略：若等于最大价格 则更新最大交易额、最大交易量
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class EqHighestPriceStrategy implements UpdateTransDataTempInfoStrategy{
    @Override
    public WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        transData.setMaxTradeAmount(transData.getMaxTradeAmount() + weighingSettlementBill.getTradeAmount());
        transData.setMaxTradeWeight(transData.getMaxTradeWeight() + weighingSettlementBill.getNetWeight());
        return transData;
    }
}