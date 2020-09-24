package com.dili.orders.service;

import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.ss.base.BaseService;

/**
 * Description: 参考价计算接口定义
 *
 * @date:    2020/8/21
 * @author:   Tyler
 */
public interface ReferencePriceService extends BaseService<WeighingReferencePrice, Long> {

    /**
     * 获取参考价逻辑
     * @param goodsId
     * @param marketId
     * @param tradeType
     * @return Long
     */
    Long getReferencePriceByGoodsId(Long goodsId, Long marketId, String tradeType);

    /**
     * 根据商品计算参考价规则
     * @param billTemp
     */
    void calculateReferencePrice(WeighingSettlementBillTemp billTemp);
}
