package com.dili.orders.domain;

public enum WeighingOperationType {

    WEIGH("过磅", 1), FREEZE("冻结", 2), INVALIDATE("作废", 3), SETTLE("结算", 4), WITHDRAW("撤销", 5), COLLECTION("回款", 6);

    private String name;
    private Integer value;

    private WeighingOperationType(String name, Integer value) {
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
