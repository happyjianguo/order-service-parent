package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;

import java.math.BigDecimal;

public class WeighingCollectionStatementDto extends WeighingStatement {
    //结算单id
    private Long weighingStatementId;

    //结算单code
    private String weighingStatementCode;

    //过磅单id
    private Long weighingBillId;

    //过磅单code
    private String weighingBillCode;

    //商品名称
    private String goodsName;

    //净重
    private BigDecimal netWeight;

    //件数 unit_amount
    private BigDecimal unitAmount;

    //单价
    private BigDecimal unitPrice;

    //人工费
    private BigDecimal staffCharges;

    //包装费
    private BigDecimal packingCharges;

    //代收费
    private BigDecimal collectionCharges;

    //合计
    private BigDecimal totalAmount;

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
        return weighingBillId;
    }

    @Override
    public void setWeighingBillId(Long weighingBillId) {
        this.weighingBillId = weighingBillId;
    }

    public String getWeighingBillCode() {
        return weighingBillCode;
    }

    public void setWeighingBillCode(String weighingBillCode) {
        this.weighingBillCode = weighingBillCode;
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

    public BigDecimal getStaffCharges() {
        return staffCharges;
    }

    public void setStaffCharges(BigDecimal staffCharges) {
        this.staffCharges = staffCharges;
    }

    public BigDecimal getPackingCharges() {
        return packingCharges;
    }

    public void setPackingCharges(BigDecimal packingCharges) {
        this.packingCharges = packingCharges;
    }

    public BigDecimal getCollectionCharges() {
        return collectionCharges;
    }

    public void setCollectionCharges(BigDecimal collectionCharges) {
        this.collectionCharges = collectionCharges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
