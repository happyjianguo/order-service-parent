package com.dili.orders.mapper;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.domain.WeighingSettlementBillDaily;
import com.dili.orders.dto.ReferencePriceQueryDto;
import com.dili.ss.base.MyMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Tyler
 * @date 2020年8月17日09:42:56
 */
public interface ReferencePriceMapper extends MyMapper<WeighingReferencePrice> {

    /**
     * 根据商品id获取商品规则
     * @param goodsId
     * @param marketId
     * @return
     */
    GoodsReferencePriceSetting getGoodsRuleByGoodsId(@Param(value = "goodsId") Long goodsId, @Param(value = "marketId") Long marketId);

    /**
     * 根据商品信息查询参考价表中数据
     * @return
     */
    WeighingReferencePrice getReferencePriceByGoodsId(ReferencePriceQueryDto queryDto);


    /**
     * 更新参考价信息
     * @param referencePrice
     */
    void updateReferencePriceByGoods(WeighingReferencePrice referencePrice);

    /*--------------------------weighing_settlement_bill_daily----------------------*/

    /**
     * 根据商品查询交易单据
     * @return
     */
    WeighingSettlementBillDaily getTransDataByGoodsId(ReferencePriceQueryDto queryDto);

    /**
     * 更新参考价中间表信息
     */
    void updateDaily(WeighingSettlementBillDaily  daily);

    /**
     * 添加当日参考价数据(weighing_settlement_bill_daily)
     * @author miaoguoxin
     * @date 2020/9/25
     */
    void insertDaily(WeighingSettlementBillDaily  daily);
}
