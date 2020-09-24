package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @ClassName UpdateTransDataTempInfoStrategyContext
 * @Description: 更新中间参考价上下文
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public class UpdateTransDataTempInfoStrategyContext {
    /**
     * 选用的策略
     */
    private UpdateTransDataTempInfoStrategy strategy;

    /**
     * 构造方法用来选用对应的策略
     *
     * @param transData
     * @param weighingSettlementBill
     */
    public UpdateTransDataTempInfoStrategyContext(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        //如果本次交易的数据中的单价大于原来商品的最大单价则需要更新商品的最大单价
        if (weighingSettlementBill.getUnitPrice() > transData.getMaxPrice()) {
            // 若接收到的单据中单价大于最高单价
            this.strategy = new UnitPriceGtHighestPriceStrategy();
        } else if (weighingSettlementBill.getUnitPrice() < transData.getMinPrice()) {
            // 若接收到的单据中单价小于最低价格
            // 更新最小价格、最小交易额、最小交易量、交易价格数
            this.strategy = new UnitPriceLtLowPriceStrategy();
        } else if (transData.getTradePriceCount() == 1 && transData.getMaxPrice().equals(weighingSettlementBill.getUnitPrice())) {
            // 若表中只有一个价格 且与传入的价格相同 累加最大最小交易额与最大最小交易量
            this.strategy = new OneAndEqPriceStrategy();
        } else if (transData.getMaxPrice().equals(weighingSettlementBill.getUnitPrice())) {
            // 若等于最大价格 则更新最大交易额、最大交易量
            this.strategy = new EqHighestPriceStrategy();
        } else if (transData.getMinPrice().equals(weighingSettlementBill.getUnitPrice())) {
            // 若等于最小价格 则更新最小交易额、最小交易量
            this.strategy = new EqLowPriceStrategy();
        } else {
            // 若小于最大值 大于最小值 更新交易价格数
            this.strategy = new BetweenLowAndHighestStrategy();
        }
    }


    /**
     * 执行对应的策略
     *
     * @param transData
     * @param weighingSettlementBill
     * @return
     */
    public WeighingTransCalcDto executeStrategy(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill) {
        return strategy.setData(transData, weighingSettlementBill);
    }

}