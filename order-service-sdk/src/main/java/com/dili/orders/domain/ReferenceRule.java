package com.dili.orders.domain;

/**
 * 品类参考价设置 参考价规则(1.规则1 2.规则2 3.规则3 4.无)
 * @author Seabert.Zhan
 */
public enum ReferenceRule {
    UNSETTLED(1, "规则1：去掉最高、最低价的交易总额/交易量"),
    SETTLED(2, "规则2：交易总额/交易量"),
    RESCINDED(3, "规则3：固定价格"),
    CLOSED(4, "无");

    private String name;
    private Integer code;

    ReferenceRule(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ReferenceRule getEnabledState(Integer code) {
        for (ReferenceRule anEnum : ReferenceRule.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
