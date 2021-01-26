package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;

import java.math.BigDecimal;

public class WeighingCollectionStatementDto extends WeighingStatement {
    //结算单id
    private Long weighingStatementId;

    //结算单code
    private String weighingStatementCode;

    //过磅单id
    private Long WeighingBillId;

    //过磅单code
    private String WeighingBillCode;

    //商品名称
    private String goodsName;

    //净重
    private BigDecimal netWeight;

    //件数 unit_amount
    private BigDecimal unitAmount;

    //单价
    private BigDecimal unitPrice;

    public Long getWeighingStatementId() {
        return weighingStatementId;
    }

    public void setWeighingStatementId(Long weighingStatementId) {
        this.weighingStatementId = weighingStatementId;
    }

    public String getWeighingStatementCode() {
        return weighingStatementCode;
    }

    public void setWeighingStatementCode(String weighingStatementCode) {
        this.weighingStatementCode = weighingStatementCode;
    }

    @Override
    public Long getWeighingBillId() {
        return WeighingBillId;
    }

    @Override
    public void setWeighingBillId(Long weighingBillId) {
        WeighingBillId = weighingBillId;
    }

    public String getWeighingBillCode() {
        return WeighingBillCode;
    }

    public void setWeighingBillCode(String weighingBillCode) {
        WeighingBillCode = weighingBillCode;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(BigDecimal netWeight) {
        this.netWeight = netWeight;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(BigDecimal unitAmount) {
        this.unitAmount = unitAmount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

}
