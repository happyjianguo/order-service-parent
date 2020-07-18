package com.dili.orders.dto;


import com.dili.ss.domain.BaseDomain;

import java.util.List;

/**
 * 流水相关dto
 * @author xuliang
 */
public class SerialDto extends BaseDomain {

    /** 流水号*/
    private String serialNo;
    /** 总的期初余额*/
    private Long startBalance;
    /** 总的期末余额*/
    private Long endBalance;
    /** 流水列表 */
    private List<SerialRecordDo> serialRecordList;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public Long getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(Long startBalance) {
        this.startBalance = startBalance;
    }

    public Long getEndBalance() {
        return endBalance;
    }

    public void setEndBalance(Long endBalance) {
        this.endBalance = endBalance;
    }

    public List<SerialRecordDo> getSerialRecordList() {
        return serialRecordList;
    }

    public void setSerialRecordList(List<SerialRecordDo> serialRecordList) {
        this.serialRecordList = serialRecordList;
    }
}
