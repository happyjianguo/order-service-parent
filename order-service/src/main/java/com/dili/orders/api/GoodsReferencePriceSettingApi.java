package com.dili.orders.api;

import com.alibaba.fastjson.JSON;
import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.dto.ReferencePriceSettingRequestDto;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import com.dili.ss.domain.BaseOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Description: 品类参考价api类
 *
 * @date: 2020/8/21
 * @author: Seabert.Zhan
 */
@RestController
@RequestMapping("/api/goodsReferencePriceSetting")
public class GoodsReferencePriceSettingApi {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoodsReferencePriceSettingApi.class);
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
     * 保存或者修改设置
     * @author miaoguoxin
     * @date 2021/2/1
     */
    @PostMapping("/saveOrEdit")
    public BaseOutput<?> saveOrEdit(@RequestBody ReferencePriceSettingRequestDto requestDto) {
        LOGGER.info("编辑中间价设置请求参数:{}", JSON.toJSONString(requestDto));
        goodsReferencePriceSettingService.saveOrEdit(requestDto);
        return BaseOutput.success();
    }
}
