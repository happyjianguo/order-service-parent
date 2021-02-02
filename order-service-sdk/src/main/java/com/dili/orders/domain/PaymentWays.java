package com.dili.orders.domain;

public enum PaymentWays {
    TOBEREVIEWED(1, "自付"),
    REFUSED(2, "代付");

    private String name;
    private Integer code;

    PaymentWays(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
