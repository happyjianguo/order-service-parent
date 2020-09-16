package com.dili.orders.listener;

import com.alibaba.fastjson.JSON;
import com.dili.orders.config.WeighingBillMQConfig;
import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.service.ReferencePriceService;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 参考价监听消息类
 * @author Tyler.Mao
 * @date 2020年8月20日10:10:11
 */
//@Component
public class ReferencePriceListener {


    public static final String EXCHANGE_REFERENCE_PRICE_CHANGE = WeighingBillMQConfig.EXCHANGE_REFERENCE_PRICE_CHANGE;
    public static final String QUEUE_REFERENCE_PRICE_CHANGE = WeighingBillMQConfig.QUEUE_REFERENCE_PRICE_CHANGE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencePriceListener.class);

    @Autowired
    private ReferencePriceService referencePriceService;

    /**
     * 手动确认状态
     */
    enum Action {
        ACCEPT,  // 处理成功
        RETRY,   // 可以重试的错误
    }

    long tag = 0;

    /**
     * 客户信息修改后，更新账户冗余信息
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = QUEUE_REFERENCE_PRICE_CHANGE, autoDelete = "false"),
            exchange = @Exchange(value = EXCHANGE_REFERENCE_PRICE_CHANGE, type = ExchangeTypes.DIRECT)
    ), ackMode = "MANUAL")
    public void processCustomerInfo(Channel channel, Message message) {
        String data = new String(message.getBody(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(data)) {
            rejectMsg(channel, message.getMessageProperties().getDeliveryTag());
            return;
        }
        LOGGER.info("接收MQ消息，开始计算参考价：{}", data);

        WeighingSettlementBillTemp weighingSettlementBill;
        try {
            weighingSettlementBill = JSON.parseObject(data, WeighingSettlementBillTemp.class);
        } catch (Exception e) {
            LOGGER.error("deserialize json failed", e);
            rejectMsg(channel, message.getMessageProperties().getDeliveryTag());
            return;
        }

        try {
            referencePriceService.calculateReferencePrice(weighingSettlementBill);
            ackMsg(channel, message.getMessageProperties().getDeliveryTag());
        } catch (Exception e) {
            LOGGER.error("业务处理失败，开始重新投递", e);
            Boolean redelivered = message.getMessageProperties().getRedelivered();
            if (redelivered) {
                // 消息已重复处理失败, 扔掉消息
                rejectMsg(channel, message.getMessageProperties().getDeliveryTag());
            } else {
                nackMsg(channel, message.getMessageProperties().getDeliveryTag());
            }
        }
    }

    private static void rejectMsg(Channel channel, long deliveryTag) {
        try {
            channel.basicReject(deliveryTag, false);
        } catch (IOException e) {
            LOGGER.error("reject message failed", e);
        }
    }

    private static void nackMsg(Channel channel, long deliveryTag) {
        try {
            channel.basicNack(deliveryTag, false, true);
        } catch (IOException e) {
            LOGGER.error("nack message failed", e);
        }
    }

    private static void ackMsg(Channel channel, long deliveryTag) {
        try {
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            LOGGER.error("ack message failed", e);
        }
    }
}
