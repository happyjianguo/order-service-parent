package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 策略：若接收到的单据中单价小于最低价格
 *             // 更新最小价格、最小交易额、最小交易量、交易价格数
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class UnitPriceLtLowPriceStrategy implements UpdateTransDataTempInfoStrategy{
    @Override
    public WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        System.out.println("------策略：UnitPriceLtLowPriceStrategy");
        transData.setMinPrice(weighingSettlementBill.getUnitPrice());
        transData.setMinTradeAmount(weighingSettlementBill.getTradeAmount());
        transData.setMinTradeWeight(weighingSettlementBill.getNetWeight());
        transData.setTradePriceCount(transData.getTradePriceCount() + 1);
        return transData;
    }
}