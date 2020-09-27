package com.dili.orders.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 接收称重票据（用于计算参考价）
 * @Auther: miaoguoxin
 * @Date: 2020/9/26 13:54
 */
public class WeighingSettlementDto implements Serializable {
    /**过磅单号*/
    private String serialNo;
    /**计量方式 {@link com.dili.orders.domain.MeasureType}*/
    private String measureType;
    /**市场ID*/
    private Long marketId;
    /**商品ID*/
    private Long goodsId;
    /**件数*/
    private Integer unitAmount;
    /**单价*/
    private Long unitPrice;
    /**件重（单位是 “件” 时需要）*/
    private Integer unitWeight;
    /**重量*/
    private Integer netWeight;
    /**更新时间*/
    private Date settlementTime;
    /**交易额*/
    private Long tradeAmount;
    /**交易类型*/
    private String tradeType;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getMeasureType() {
        return measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(Integer unitAmount) {
        this.unitAmount = unitAmount;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getUnitWeight() {
        return unitWeight;
    }

    public void setUnitWeight(Integer unitWeight) {
        this.unitWeight = unitWeight;
    }

    public Integer getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Integer netWeight) {
        this.netWeight = netWeight;
    }

    public Date getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }

    public Long getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(Long tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
