package com.dili.orders.listener;

import com.alibaba.fastjson.JSONObject;
import com.dili.orders.config.WeighingBillMQConfig;
import com.dili.orders.service.ReferencePriceService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

/**
 * 参考价监听消息类
 * @author Tyler.Mao
 * @date 2020年8月20日10:10:11
 */
@Component
public class ReferencePriceListener {


    public static final String EXCHANGE_REFERENCE_PRICE_CHANGE = WeighingBillMQConfig.EXCHANGE_REFERENCE_PRICE_CHANGE;
    public static final String QUEUE_REFERENCE_PRICE_CHANGE = WeighingBillMQConfig.QUEUE_REFERENCE_PRICE_CHANGE;


    @Autowired
    ReferencePriceService referencePriceService;

    /**
     * 客户信息修改后，更新账户冗余信息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = QUEUE_REFERENCE_PRICE_CHANGE, autoDelete = "false"),
            exchange = @Exchange(value = EXCHANGE_REFERENCE_PRICE_CHANGE, type = ExchangeTypes.DIRECT)
    ))
    public void processCustomerInfo(Channel channel, Message message) {
        try {
            String data = new String(message.getBody(), "UTF-8");
            System.out.println(data);
            referencePriceService.calcReferencePrice(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
