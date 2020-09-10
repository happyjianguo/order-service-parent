package com.dili.orders.api;

import com.dili.orders.config.GenericGlobalExceptionResolver;
import com.dili.orders.service.ReferencePriceService;
import com.dili.ss.domain.BaseOutput;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencePriceApi.class);

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
            LOGGER.error("获取商品参考价参数不正确({0})({1})({2})：",goodsId,marketId,tradeType);
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
            LOGGER.error("获取商品参考价异常："+e.getMessage());
            return BaseOutput.failure("500", "获取失败");
        }
    }

}

