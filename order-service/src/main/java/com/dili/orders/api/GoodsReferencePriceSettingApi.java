package com.dili.orders.api;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/goodsReferencePriceSetting")
public class GoodsReferencePriceSettingApi {

    @Autowired
    GoodsReferencePriceSettingService goodsReferencePriceSettingService;

    /**
     * 获取所有商品数据
     *
     * @param goodsReferencePriceSetting
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/getAllGoods", method = {RequestMethod.POST})
    public List<GoodsReferencePriceSetting> getAllGoods(@RequestBody GoodsReferencePriceSetting goodsReferencePriceSetting) {
        return goodsReferencePriceSettingService.getAllGoods(goodsReferencePriceSetting);
    }
}