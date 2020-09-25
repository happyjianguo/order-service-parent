package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 策略：若表中只有一个价格 且与传入的价格相同 累加最大最小交易额与最大最小交易量
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class OneAndEqPriceStrategy implements UpdateTransDataTempInfoStrategy{
    @Override
    public WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        transData.setMaxTradeAmount(transData.getMaxTradeAmount() + weighingSettlementBill.getTradeAmount());
        transData.setMaxTradeWeight(transData.getMaxTradeWeight() + weighingSettlementBill.getNetWeight());
        transData.setMinTradeAmount(transData.getMinTradeAmount() + weighingSettlementBill.getTradeAmount());
        transData.setMinTradeWeight(transData.getMinTradeWeight() + weighingSettlementBill.getNetWeight());
        return transData;
    }
}