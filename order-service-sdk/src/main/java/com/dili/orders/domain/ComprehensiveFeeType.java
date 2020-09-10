package com.dili.orders.domain;

public enum ComprehensiveFeeType {
    TESTING_CHARGE("检测收费", 1),QUERY_CHARGE("查询收费", 2);

    private String name;
    private Integer value;

    private ComprehensiveFeeType(String name, Integer value) {
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
