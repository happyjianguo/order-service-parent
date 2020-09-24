package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 策略：若小于最大值 大于最小值 更新交易价格数
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class BetweenLowAndHighestStrategy implements UpdateTransDataTempInfoStrategy {
    @Override
    public WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        System.out.println("------策略：BetweenLowAndHighestStrategy");
        // 更新最大价格、最大交易额、最大交易量、交易价格数
        transData.setMaxPrice(weighingSettlementBill.getUnitPrice());
        transData.setMaxTradeAmount(weighingSettlementBill.getTradeAmount());
        transData.setMaxTradeWeight(weighingSettlementBill.getNetWeight());
        transData.setTradePriceCount(transData.getTradePriceCount() + 1);
        return transData;
    }
}