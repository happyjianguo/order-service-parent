package com.dili.orders.domain;

public enum ComprehensiveFeeState {
    NO_SETTLEMEN("未结算", 1),SETTLED("已结算", 2), WITHDRAWN("已撤销", 3), CLOSED("已关闭", 4);

    private String name;
    private Integer value;

    private ComprehensiveFeeState(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
