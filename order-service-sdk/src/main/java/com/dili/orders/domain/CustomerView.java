package com.dili.orders.domain;


import com.dili.customer.sdk.domain.dto.CustomerExtendDto;

public class CustomerView extends CustomerExtendDto {
    /**
     * 客户身份类型Json
     */
    private String subType;

    private String subTypeTranslate;

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getSubTypeTranslate() {
        return subTypeTranslate;
    }

    public void setSubTypeTranslate(String subTypeTranslate) {
        this.subTypeTranslate = subTypeTranslate;
    }
}
