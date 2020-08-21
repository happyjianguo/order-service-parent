package com.dili.orders.mapper;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.ss.base.MyMapper;

import java.util.List;

/**
 * Description: 品类参考价api类
 *
 * @date:    2020/8/21
 * @author:   Seabert.Zhan
 */
public interface GoodsReferencePriceSettingMapper extends MyMapper<GoodsReferencePriceSetting> {

    /**
     * @Description 列表查询
     * @param goodsReferencePriceSetting: 
     * @return java.util.List<com.dili.orders.domain.GoodsReferencePriceSetting> 
     * @author  Seabert.Zhan
     * @datetime  2020/8/21 10:55
     */
    List<GoodsReferencePriceSetting> listByQueryParams(GoodsReferencePriceSetting goodsReferencePriceSetting);

    /**
     * @Description 获取详情
     * @param goodsReferencePriceSetting: 
     * @return com.dili.orders.domain.GoodsReferencePriceSetting 
     * @author  Seabert.Zhan
     * @datetime  2020/8/21 10:55
     */
    GoodsReferencePriceSetting selectDetailById(GoodsReferencePriceSetting goodsReferencePriceSetting);
}