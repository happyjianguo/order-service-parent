package com.dili.orders.api;

import com.dili.orders.service.ReferencePriceService;
import com.dili.ss.domain.BaseOutput;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;


/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 * @author Tyler
 * @date 2020年8月17日10:18:00
 */
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
     * @param goodsId
     * @return BaseOutput<Object>
     */
    @RequestMapping(value = "/getReferencePriceByGoodsId/{goodsId}/{marketId}", method = {RequestMethod.GET})
    BaseOutput<Object> getReferencePriceByGoodsId(@PathVariable(value = "goodsId") Long goodsId,@PathVariable(value = "marketId") Long marketId) {
        if (Objects.isNull(goodsId) || Objects.isNull(marketId)) {
            return BaseOutput.failure("请传入正确的商品ID与市场ID");
        }
        try {
            Long referencePrice = referencePriceService.getReferencePriceByGoodsId(goodsId,marketId);
            if (referencePrice == null || referencePrice == 0) {
                return BaseOutput.successData(0);
            }
            Double price = Double.valueOf(referencePrice.toString()) / 100;
            return BaseOutput.successData(price);
        } catch (Exception e) {
            return BaseOutput.failure("500",e.getMessage());
        }
    }

}
