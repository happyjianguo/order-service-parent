package com.dili.orders.domain;

import com.dili.ss.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用于计算参考价
 * @author Tyler.Mao
 */
public class WeighingSettlementBillDaily implements Serializable {
    /**主键*/
    private Long id;
    /**商品id*/
    private Long goodsId;
    /**市场id*/
    private Long marketId;
    /**交易类型*/
    private String tradeType;
    /**单价（元/斤）*/
    private Long unitPrice;
    /**最大单价*/
    private Long maxPrice;
    /**最小单价*/
    private Long minPrice;
    /**单次最大累计交易金额（即maxPrice相同时进行累加）,该值只和maxPrice有关*/
    private Long maxTradeAmount;
    /**单次最小累计交易金额（即minPrice相同时进行累加）,该值只和minPrice有关*/
    private Long minTradeAmount;
    /**单次最大累计交易重量（即maxPrice相同时进行累加）,该值只和maxPrice有关*/
    private Integer maxTradeWeight;
    /**单次最小累计交易重量（即minPrice相同时进行累加）,该值只和minPrice有关*/
    private Integer minTradeWeight;
    /**交易总次数*/
    private Integer tradeCount;
    /**交易单价数量（即当有多个相同最大值或最小值时，只计算一次）*/
    private Integer tradePriceCount;
    /**总的交易额*/
    private Long totalTradeAmount;
    /**总的交易量*/
    private Integer totalTradeWeight;
    private Date settlementTime;
    /**交易日 pattern:yyyy-MM-dd  ex:2020-10-10*/
    private String settlementDay;

    /**
     * 初始化数据
     * @author miaoguoxin
     * @date 2020/9/25
     */
    public static WeighingSettlementBillDaily create(WeighingSettlementBillTemp temp, Long unitPrice) {
        WeighingSettlementBillDaily transData = new WeighingSettlementBillDaily();
        transData.setGoodsId(temp.getGoodsId());
        transData.setMarketId(temp.getMarketId());
        transData.setTradeType(temp.getTradeType());
        transData.setMinTradeAmount(temp.getTradeAmount());
        transData.setMaxTradeAmount(temp.getTradeAmount());
        transData.setMinTradeWeight(temp.getNetWeight());
        transData.setMaxTradeWeight(temp.getNetWeight());
        transData.setMinPrice(unitPrice);
        transData.setMaxPrice(unitPrice);
        transData.setTotalTradeAmount(temp.getTradeAmount());
        transData.setTotalTradeWeight(temp.getNetWeight());
        transData.setTradePriceCount(1);
        transData.setTradeCount(1);
        Date now = new Date();
        transData.setSettlementTime(now);
        transData.setSettlementDay(DateUtils.format(now, "yyyy-MM-dd"));
        return transData;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }


    public Long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxTradeAmount() {
        return maxTradeAmount;
    }

    public void setMaxTradeAmount(Long maxTradeAmount) {
        this.maxTradeAmount = maxTradeAmount;
    }

    public Long getMinTradeAmount() {
        return minTradeAmount;
    }

    public void setMinTradeAmount(Long minTradeAmount) {
        this.minTradeAmount = minTradeAmount;
    }

    public Integer getMaxTradeWeight() {
        return maxTradeWeight;
    }

    public void setMaxTradeWeight(Integer maxTradeWeight) {
        this.maxTradeWeight = maxTradeWeight;
    }

    public Integer getMinTradeWeight() {
        return minTradeWeight;
    }

    public void setMinTradeWeight(Integer minTradeWeight) {
        this.minTradeWeight = minTradeWeight;
    }

    public Integer getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(Integer tradeCount) {
        this.tradeCount = tradeCount;
    }

    public Integer getTradePriceCount() {
        return tradePriceCount;
    }

    public void setTradePriceCount(Integer tradePriceCount) {
        this.tradePriceCount = tradePriceCount;
    }

    public Long getTotalTradeAmount() {
        return totalTradeAmount;
    }

    public void setTotalTradeAmount(Long totalTradeAmount) {
        this.totalTradeAmount = totalTradeAmount;
    }

    public Integer getTotalTradeWeight() {
        return totalTradeWeight;
    }

    public void setTotalTradeWeight(Integer totalTradeWeight) {
        this.totalTradeWeight = totalTradeWeight;
    }

    public Date getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getSettlementDay() {
        return settlementDay;
    }

    public void setSettlementDay(String settlementDay) {
        this.settlementDay = settlementDay;
    }
}
