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
public class RabbitMQConfig {

    //账户流水
    public static final String EXCHANGE_ACCOUNT_SERIAL = "exchange_account_serial";
    public static final String QUEUE_ACCOUNT_SERIAL = "queue_account_serial";
    public static final String ROUTING_ACCOUNT_SERIAL = "routing_account_serial";

    /**
     * queue
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_ACCOUNT_SERIAL);
    }

    /**
     * direct
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_ACCOUNT_SERIAL);
    }

    /**
     * binding
     * @return
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(ROUTING_ACCOUNT_SERIAL);
    }
}
