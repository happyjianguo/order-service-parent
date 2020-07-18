package com.dili.orders.dto;

/**
 * 资金动作枚举
 */
public enum ActionType {

    INCOME(1, "收入"),
    EXPENSE(2, "支出");
    private int code;
    private String name;

    private ActionType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ActionType getByCode(int code) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.getCode() == code) {
                return actionType;
            }
        }
        return null;
    }

    public static String getNameByCode(int code) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.getCode() == code) {
                return actionType.name;
            }
        }
        return null;
    }
}
