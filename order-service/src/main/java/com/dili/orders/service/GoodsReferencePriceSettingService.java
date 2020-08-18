package com.dili.orders.service;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.ss.base.BaseService;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
public interface GoodsReferencePriceSettingService extends BaseService<GoodsReferencePriceSetting, Long> {

	/**
	 * 获取所有商品
	 * 
	 * @param goodsReferencePriceSetting
	 * @return
	 */
	List<GoodsReferencePriceSetting> getAllGoods(GoodsReferencePriceSetting goodsReferencePriceSetting);
}