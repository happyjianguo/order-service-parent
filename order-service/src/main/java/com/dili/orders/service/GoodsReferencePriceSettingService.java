package com.dili.orders.service;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 * @author Seabert.Zhan
 */
public interface GoodsReferencePriceSettingService extends BaseService<GoodsReferencePriceSetting, Long> {

	/**
	 * 获取所有商品
	 *
	 * @param goodsReferencePriceSetting
	 * @return
	 */
	List<GoodsReferencePriceSetting> getAllGoods(GoodsReferencePriceSetting goodsReferencePriceSetting);

	/**
	 * 品类参考价设置详情
	 *
	 * @param goodsReferencePriceSetting
	 * @return
	 */
	BaseOutput<GoodsReferencePriceSetting> detail(GoodsReferencePriceSetting goodsReferencePriceSetting);

	/**
	 * 新增品类参考价
	 *
	 * @param goodsReferencePriceSetting
	 * @return
	 */
	BaseOutput<GoodsReferencePriceSetting> insertGoodsReferencePriceSetting(GoodsReferencePriceSetting goodsReferencePriceSetting);

	/**
	 * 修改品类参考价
	 *
	 * @param goodsReferencePriceSetting
	 * @return
	 */
	BaseOutput<GoodsReferencePriceSetting> updateGoodsReferencePriceSetting(GoodsReferencePriceSetting goodsReferencePriceSetting);
}