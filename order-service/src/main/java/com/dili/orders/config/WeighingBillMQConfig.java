package com.dili.orders.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbit mq 配置类
 * @author Tyler.Mao
 * @date 2020年8月20日11:18:42
 */
@Configuration
public class WeighingBillMQConfig {

    //账户流水
    public static final String EXCHANGE_REFERENCE_PRICE_CHANGE = "exchange_reference_price_change";
    public static final String QUEUE_REFERENCE_PRICE_CHANGE = "queue_reference_price_change";
    public static final String ROUTING_REFERENCE_PRICE_CHANGE = "routing_reference_price_change";

    /**
     * queue
     *
     * @return
     */
    @Bean
    public Queue priceMessageQueue() {
        return new Queue(QUEUE_REFERENCE_PRICE_CHANGE);
    }

    /**
     * direct
     *
     * @return
     */
    @Bean
    public DirectExchange priceDirectExchange() {
        return new DirectExchange(EXCHANGE_REFERENCE_PRICE_CHANGE);
    }

    /**
     * binding
     *
     * @return
     */
    @Bean
    public Binding priceBinding() {
        return BindingBuilder.bind(priceMessageQueue()).to(priceDirectExchange()).with(ROUTING_REFERENCE_PRICE_CHANGE);
    }
}
