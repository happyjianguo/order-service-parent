package com.dili.orders.constants;

public class OrdersConstant {

    /**
     * 支付系统appid
     */
    public static final String PAYMENT_APP_ID = "100101";

    /**
     * 支付系统token
     */
    public static final String PAYMENT_TOKEN = "abcd1234";

    /**
     * 支付系统账户余额查询请求参数
     */
    public static final String PAYMENT_FUND_SERVICE_QUERY = "payment.fund.service:query";

    /**
     * 创建支付订单请求参数
     */
    public static final String PAYMENT_TRADE_SERVICE_PREPARE = "payment.trade.service:prepare";

    /**
     * 确认预授权交易请求参数,即时交易也是这个参数
     */
    public static final String PAYMENT_TRADE_SERVICE_COMMIT = "payment.trade.service:commit";

    /**
     * 确认预授权交易
     */
    public static final String PAYMENT_TRADE_SERVICE_CONFIRM = "payment.trade.service:confirm";

    /**
     * 撤销预授权交易请求参数
     */
    public static final String PAYMENT_TRADE_SERVICE_CANCEL = "payment.trade.service:cancel";

    /**
     * 缴费
     */
    public static final String PAYMENT_TRADE_SERVICE_PAY = "payment.trade.service:commit4";

    /**
     * 撤销交易
     */
    public static final String PAYMENT_TRADE_SERVICE_CANCEL_PAY = "payment.trade.service:cancel2";

}
