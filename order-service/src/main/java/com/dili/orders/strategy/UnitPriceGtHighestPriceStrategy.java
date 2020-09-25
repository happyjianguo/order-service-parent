package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 策略：若接收到的单据中单价大于最高单价
 *             // 更新最大价格、最大交易额、最大交易量、交易价格数
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class UnitPriceGtHighestPriceStrategy implements UpdateTransDataTempInfoStrategy{
    @Override
    public WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        transData.setMaxPrice(weighingSettlementBill.getUnitPrice());
        transData.setMaxTradeAmount(weighingSettlementBill.getTradeAmount());
        transData.setMaxTradeWeight(weighingSettlementBill.getNetWeight());
        transData.setTradePriceCount(transData.getTradePriceCount() + 1);
        return transData;
    }
}