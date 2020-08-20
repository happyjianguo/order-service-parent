package com.dili.orders.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbit mq 配置类
 */
@Configuration
public class WeighingBillMQConfig {

    //中间价
    public static final String EXCHANGE_REFERENCE_PRICE_CHANGE = "exchange_reference_price_change";
    public static final String QUEUE_REFERENCE_PRICE_CHANGE = "queue_reference_price_change";
    public static final String ROUTING_REFERENCE_PRICE_CHANGE = "routing_reference_price_change";

    /**
     * queue
     *
     * @return
     */
    @Bean(name = "weighingBillQueue")
    public Queue queue() {
        return new Queue(QUEUE_REFERENCE_PRICE_CHANGE);
    }

    /**
     * direct
     *
     * @return
     */
    @Bean(name = "weighingBillDirectExchange")
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_REFERENCE_PRICE_CHANGE);
    }

    /**
     * binding
     *
     * @return
     */
    @Bean(name = "weighingBillBinding")
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(ROUTING_REFERENCE_PRICE_CHANGE);
    }
}
