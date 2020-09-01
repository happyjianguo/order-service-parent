package com.dili.orders.dto;

/**
 * 业务类型
 */
public enum MyBusinessType {
    KCJM("kcjm", "空车进门"),
    JMSF("jmsf", "进门收费"),
    ZCPAY("ZC_PAY", "转场缴费"),
    LCPAY("LC_PAY", "离场缴费");

    private String name;
    private String code;

    MyBusinessType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static MyBusinessType getEnabledState(String code) {
        for (MyBusinessType anEnum : MyBusinessType.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
