package com.dili.orders.domain;


import com.dili.customer.sdk.domain.dto.CustomerExtendDto;

public class CustomerView extends CustomerExtendDto {
    /**
     * 客户身份类型Json
     */
    private String subType;

    /**
     * 客户身份类型翻译
     */
    private String subTypeTranslate;

    /** 持卡人姓名 */
    private String holdName;

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

    public String getHoldName() {
        return holdName;
    }

    public void setHoldName(String holdName) {
        this.holdName = holdName;
    }
}
