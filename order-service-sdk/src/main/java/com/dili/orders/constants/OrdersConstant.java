package com.dili.orders.constants;

public class OrdersConstant {

	public static final String SYSTEM_CODE = "ORDERS";

	/**
	 * 支付系统appid
	 */
	public static final String PAYMENT_APP_ID = "1040";

	/**
	 * 支付系统token
	 */
	public static final String PAYMENT_TOKEN = "abcd1040";

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

	/**
	 * 寿光市场编码
	 */
	public static final String SHOUGUANG_FIRM_CODE = "sg";

	/**
	 * 取重开关数据字典编码，用户查询数据字典，以确定取重按钮是否可用
	 */
	public static final String FETCH_WEIGHT_SWITCH_DD_CODE = "fetch_weight_switch";

	/**
	 * 买家手续费计费规则业务类型配置编码，用于在计费规则系统查询买家手续费的计费规则
	 */
	public static final String WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE = "WEIGHING_BILL_BUYER_POUNDAGE";

	/**
	 * 买家手续费计费规则业务类型配置编码，用于在计费规则系统查询买家手续费的计费规则
	 */
	public static final String WEIGHING_BILL_SELLER_POUNDAGE_BUSINESS_TYPE = "WEIGHING_BILL_SELLER_POUNDAGE";

	/**
	 * 价格审批流程定义key
	 */
	public static final String PRICE_APPROVE_PROCESS_DEFINITION_KEY = "weighingBillPriceApprove";

	/**
	 * 交易过磅单号生成规则配置编码
	 */
	public static final String WEIGHING_BILL_SERIAL_NO_GENERATE_RULE_CODE = "sg_weighing_bill";

	/**
	 * 交易过结算单号生成规则配置编码
	 */
	public static final String WEIGHING_STATEMENT_SERIAL_NO_GENERATE_RULE_CODE = "sg_weighing_statement";

	/**
	 * 交易过磅模块前缀
	 */
	public static final String WEIGHING_MODULE_PREFIX = "GB_";

	/**
	 * 赊销支付协议号
	 */
	public static final Integer CREDIT_PAY_PROTOCAL_ID = 9527;

}
