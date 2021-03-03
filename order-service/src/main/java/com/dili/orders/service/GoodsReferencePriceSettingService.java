package com.dili.orders.service;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.dto.ReferencePriceSettingRequestDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

import java.util.List;

/**
 * Description: 品类参考价接口定义
 *
 * @date:    2020/8/21
 * @author:   Seabert.Zhan
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

	/**
	* 保存/修改设置(v2)
	* @author miaoguoxin
	* @date 2021/2/1
	*/
	void saveOrEdit(ReferencePriceSettingRequestDto requestDto);
}
