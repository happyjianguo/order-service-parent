package com.dili.orders.service;

import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
public interface ReferencePriceService extends BaseService<WeighingReferencePrice, Long> {

    /**
     * 获取参考价逻辑
     * @param goodsId
     * @return Long
     */
    Long getReferencePriceByGoodsId(Long goodsId, Long marketId, Long tradeType);

    /**
     * 根据商品计算参考价规则
     * @param jsonStr
     */
    void calculateReferencePrice(WeighingSettlementBillTemp billTemp);
}
