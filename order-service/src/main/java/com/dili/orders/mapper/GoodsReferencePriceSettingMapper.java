package com.dili.orders.mapper;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.ss.base.MyMapper;

import java.util.List;

public interface GoodsReferencePriceSettingMapper extends MyMapper<GoodsReferencePriceSetting> {

    List<GoodsReferencePriceSetting> listByQueryParams(GoodsReferencePriceSetting goodsReferencePriceSetting);
}