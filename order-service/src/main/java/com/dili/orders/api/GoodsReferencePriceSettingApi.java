package com.dili.orders.api;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import com.dili.ss.domain.BaseOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 * @author Seabert.Zhan
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

    /**
     * 查询品类参考价设置详情
     *
     * @param goodsReferencePriceSetting
     * @return
     */
    @RequestMapping(value = "/detail")
    public BaseOutput<GoodsReferencePriceSetting> detail(@RequestBody GoodsReferencePriceSetting goodsReferencePriceSetting) {
        return goodsReferencePriceSettingService.detail(goodsReferencePriceSetting);
    }

    /**
     * 新增品类参考价
     *
     * @param goodsReferencePriceSetting
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    public BaseOutput<GoodsReferencePriceSetting> insert(@RequestBody GoodsReferencePriceSetting goodsReferencePriceSetting) {
        try {
            if (goodsReferencePriceSetting.getCreatedTime() == null) {
                goodsReferencePriceSetting.setCreatedTime(LocalDateTime.now());
            }
            goodsReferencePriceSettingService.insertGoodsReferencePriceSetting(goodsReferencePriceSetting);
            return BaseOutput.successData(goodsReferencePriceSetting);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("新增失败" + e.getMessage());
        }
    }

    /**
     * 修改品类参考价
     *
     * @param goodsReferencePriceSetting
     * @return BaseOutput
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public BaseOutput<GoodsReferencePriceSetting> update(@RequestBody GoodsReferencePriceSetting goodsReferencePriceSetting) {
        try {
            goodsReferencePriceSettingService.updateGoodsReferencePriceSetting(goodsReferencePriceSetting);
            return BaseOutput.successData(goodsReferencePriceSetting);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("修改失败" + e.getMessage());
        }
    }
}