package com.dili.orders.dto;

/**
 * 交易类型枚举
 */
public enum TradeType {

	DEPOSIT(10,"账户充值"),
    WITHDRAW(11,"账户提现"),
    FEE(12,"缴费"),
    AUTH_FEE(13,"预授权缴费"),
    PRE_DEPOSIT(14,"预存款"),
    EBANK_RECHARGE(15,"网银充值(充值失败后的补偿，是个特殊操作)"),
    DIRECT_TRADE(20,"即时交易"),
    AUTH_TRADE(21,"预授权交易"),
    VOUCH_TRADE(22,"担保交易"),
    TRANSFER(23,"账户转账"),
    BANK_DEPOSIT(30,"银行圈存"),
    BANK_WITHDRAW(31,"银行圈提"),
    CANCEL(40,"交易撤销"),
    REFUND(41,"交易退款"),
    CORRECT(42,"交易冲正");
	
    private int code;
    private String name;

    private TradeType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static TradeType getByCode(int code) {
        for (TradeType tradeType : TradeType.values()) {
            if (tradeType.getCode() == code) {
                return tradeType;
            }
        }
        return null;
    }

    public static String getNameByCode(int code) {
        for (TradeType tradeType : TradeType.values()) {
            if (tradeType.getCode() == code) {
                return tradeType.name;
            }
        }
        return null;
    }
}
