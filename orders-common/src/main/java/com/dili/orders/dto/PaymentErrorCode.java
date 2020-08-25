package com.dili.orders.dto;

public enum PaymentErrorCode {

	UNKNOWN_EXCEPTION("500000", "系统未知异常"),
	INVALIDATION_ARGUMENTS_EXCEPTION("500001", "无效参数错误"), 
	PERMISSION_DENIED_EXCEPTION("500002", "操作不允许"), 
	CONCURRENT_MODIFICATION_EXCEPTION("500003", "数据并发修改 "),
	DATA_NOT_FOUND_EXCEPTION("500004", "对象不存在 "),
	DUNPLICATE_DATA_EXCEPTION("500005", "对象已存在"), 
	SERVICE_NOT_FOUND_EXCEPTION("501001", "服务不存在 "), 
	ACCESS_DENIED_EXCEPTION("501002", "访问未授权"),
	TRADE_NOT_SUPPORT_EXCEPTION("502001", "交易不支持"), 
	CHANEL_NOT_SUPPORT_EXCEPTION("502002", "不支持的支付渠道 "),
	TRADE_NOT_FOUND_EXCEPTION("502003", "交易不存在"), 
	INVALIDATE_STATE_EXCEPTION("502004", "无效的交易状态"),
	FUND_ACCOUNT_NOT_FOUND_EXCEPTION("503001", "资金账号不存在"), 
	INVALIDATE_FOUND_ACCOUNT_STATE_EXCEPTION("503002", "无效资金账号状态"), 
	ACCOUNT_PASSWORD_INCORRECT_EXCEPTION("503002", "账号密码不正确"),
	LACK_OF_BALANCE_EXCEPTION("503003", "账户余额不足"),
	INVALIDATE_FOUND_STATE_EXCEPTION("503002", "无效资金状态");

	private PaymentErrorCode(String code, String name) {
		this.code = code;
		this.name = name;
	}

	private String code;
	private String name;

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
