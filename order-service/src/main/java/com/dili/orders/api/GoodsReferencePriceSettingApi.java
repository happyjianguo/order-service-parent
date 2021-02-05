package com.dili.orders.api;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.dto.ReferencePriceSettingRequestDto;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Description: 品类参考价api类
 *
 * @date:    2020/8/21
 * @author:   Seabert.Zhan
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
    * 保存或者修改设置
    * @author miaoguoxin
    * @date 2021/2/1
    */
    @PostMapping("/saveOrEdit")
    public BaseOutput<?> saveOrEdit(@RequestBody ReferencePriceSettingRequestDto requestDto){
        goodsReferencePriceSettingService.saveOrEdit(requestDto);
        return BaseOutput.success();
    }
}
