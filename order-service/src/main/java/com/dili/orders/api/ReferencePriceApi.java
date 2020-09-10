package com.dili.orders.api;

import com.dili.orders.service.ReferencePriceService;
import com.dili.ss.domain.BaseOutput;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 * @author Tyler
 * @date 2020年8月17日10:18:00
 */
@CrossOrigin
@RestController
@RequestMapping("/api/refrencePrice")
public class ReferencePriceApi {

    @Autowired
    ReferencePriceService referencePriceService;

    @Autowired
    private AmqpTemplate rabbitMQTemplate;

    /**
     * 根据商品ID获取参考价
     *
     * @param goodsId   商品编号
     * @param marketId  市场编号
     * @param tradeType 交易类型
     * @return BaseOutput<Object>
     */
    @RequestMapping(value = "/getReferencePriceByGoodsId", method = {RequestMethod.POST})
    BaseOutput<Object> getReferencePriceByGoodsId(Long goodsId, Long marketId, String tradeType) {
        if (Objects.isNull(goodsId) || Objects.isNull(marketId) || StringUtils.isBlank(tradeType)) {
            return BaseOutput.failure("请传入正确的参数");
        }
        try {
            Long referencePrice = referencePriceService.getReferencePriceByGoodsId(goodsId, marketId, tradeType);
            if (referencePrice == null || referencePrice == 0) {
                return BaseOutput.successData(0);
            }
            Double price = Double.valueOf(referencePrice.toString()) / 100;
            return BaseOutput.successData(price);
        } catch (Exception e) {
            return BaseOutput.failure("500", e.getMessage());
        }
    }


}

