package com.dili.orders.service.referenceprice;

import cn.hutool.core.util.NumberUtil;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingSettlementBillDaily;
import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.udojava.evalex.Expression;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 专门用于计算中间价的各种数量
 * @Auther: miaoguoxin
 * @Date: 2020/9/16 12:17
 */
@Component
public class ReferencePriceCalculator {

    /**重量都是公斤，需要转换成斤*/
    private static final Integer KG_TO_JIN_RATE = 2;
    /**重量换算单位，传过来都是化成了整数  如：100.24，传过来就是10024*/
    private static final Integer WEIGHT_CONVERT_RATE = 100;
    /**计算单价公式*/
    private static final String CAL_UNIT_PRICE_FORMULA = "unitPrice/(unitWeight*rate)";
    /**计算交易额公式*/
    private static final String CAL_TRADE_AMOUNT_FORMULA = "(unitPrice/unitWeight)*netWeight*rate";
    /**计算总平均价公式*/
    private static final String CAL_TOTAL_AVG_PRICE_FORMULA = "totalTradeAmount/(totalTradeWeight*rate)";
    /**计算中间价公式*/
    private static final String CAL_MIDDLE_PRICE_FORMULA = "(totalTradeAmount-maxAmount-minAmount)/((totalTradeWeight-maxWeight-minWeight)*rate)";


    /**
    * 计算单价
    * @author miaoguoxin
    * @date 2020/9/25
    */
    public Long getUnitPrice(WeighingSettlementBillTemp billTemp) {
        if (MeasureType.PIECE.getValue().equals(billTemp.getMeasureType())) {
            //  元/件转元/斤
            BigDecimal unitPrice = new Expression(CAL_UNIT_PRICE_FORMULA)
                    .with("unitPrice", BigDecimal.valueOf(billTemp.getUnitPrice()))
                    .with("unitWeight", getRealWeight(billTemp.getUnitWeight()))
                    .with("rate", BigDecimal.valueOf(KG_TO_JIN_RATE))
                    .eval();
            return unitPrice.setScale(2, RoundingMode.DOWN).longValue();
        }
        return billTemp.getUnitPrice();
    }

    /**
     * 计算交易额
     * @author miaoguoxin
     * @date 2020/9/16
     */
    public Long getTradeAmount(WeighingSettlementBillTemp billTemp) {
        if (MeasureType.PIECE.getValue().equals(billTemp.getMeasureType())) {
            // 将元/件的单据转换为元/斤
            BigDecimal tradeAmount = new Expression(CAL_TRADE_AMOUNT_FORMULA)
                    .with("unitPrice", BigDecimal.valueOf(billTemp.getUnitPrice()))
                    .with("unitWeight", getRealWeight(billTemp.getUnitWeight()))
                    .with("netWeight", getRealWeight(billTemp.getNetWeight()))
                    .with("rate", BigDecimal.valueOf(KG_TO_JIN_RATE))
                    .eval();
            return tradeAmount.setScale(2, RoundingMode.DOWN).longValue();
        }
        return billTemp.getTradeAmount();
    }

    /**
     * 计算总平均价
     * @author miaoguoxin
     * @date 2020/9/16
     */
    public Long getTotalAvgPrice(Long totalTradeAmount, Integer totalTradeWeight) {
        BigDecimal result = new Expression(CAL_TOTAL_AVG_PRICE_FORMULA)
                .with("totalTradeAmount", BigDecimal.valueOf(totalTradeAmount))
                .with("totalTradeWeight", getRealWeight(totalTradeWeight))
                .with("rate", BigDecimal.valueOf(KG_TO_JIN_RATE))
                .eval();
        return result.setScale(2, RoundingMode.DOWN).longValue();
    }

    /**
     *  计算参考价（去除最高价和最低价之后）
     * @author miaoguoxin
     * @date 2020/9/16
     */
    public Long getReferenceAvgPrice(WeighingSettlementBillDaily  transData) {
        //当价格数不超过2时，是算不出中间值的，故取平均值即可
        if (transData.getTradePriceCount() <= 2) {
            return this.getTotalAvgPrice(transData.getTotalTradeAmount(), transData.getTotalTradeWeight());
        }
        BigDecimal result = new Expression(CAL_MIDDLE_PRICE_FORMULA)
                .with("totalTradeAmount", BigDecimal.valueOf(transData.getTotalTradeAmount()))
                .with("maxAmount", BigDecimal.valueOf(transData.getMaxTradeAmount()))
                .with("minAmount", BigDecimal.valueOf(transData.getMinTradeAmount()))
                .with("totalTradeWeight", getRealWeight(transData.getTotalTradeWeight()))
                .with("maxWeight", getRealWeight(transData.getMaxTradeWeight()))
                .with("minWeight", getRealWeight(transData.getMinTradeWeight()))
                .with("rate", BigDecimal.valueOf(KG_TO_JIN_RATE))
                .eval();
        return result.setScale(2, RoundingMode.DOWN).longValue();
    }


    /**
     * 转换真正的重量
     * @author miaoguoxin
     * @date 2020/9/16
     */
    private static BigDecimal getRealWeight(Integer weight) {
        return NumberUtil.div(weight, WEIGHT_CONVERT_RATE);
    }

}
