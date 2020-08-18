package com.dili.orders.mapper;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.dto.WeighingTransCalcDto;
import com.dili.ss.base.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * @author Tyler
 * @date 2020年8月17日09:42:56
 */
public interface ReferencePriceMapper extends MyMapper<WeighingReferencePrice> {

    /**
     * 根据商品id获取商品规则
     * @param goodsId
     * @return
     */
    GoodsReferencePriceSetting getGoodsRuleByGoodsId(Long goodsId);

    /**
     * 根据商品信息查询参考价表中数据
     * @param goodsId
     * @return
     */
    WeighingReferencePrice getReferencePriceByGoodsId(Long goodsId);

    /**
     * 根据商品查询交易单据
     * @param map
     * @return
     */
    List<WeighingTransCalcDto> getTransDataByGoodsId(Map<String,Object> map);

    /**
     * 根据商品ID查询商品是否存在
     * @param goodsId
     * @return
     */
    int getReferencePriceCountByGoodsIdIsExists(Long goodsId);

    /**
     * 更新参考价信息
     * @param referencePrice
     */
    void updateReferencePriceByGoods(WeighingReferencePrice referencePrice);

}