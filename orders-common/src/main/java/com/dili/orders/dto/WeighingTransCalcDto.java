package com.dili.orders.dto;

import java.util.Date;

/**
 * 用于计算参考价
 * @author Tyler.Mao
 */
public class WeighingTransCalcDto {

	private Long tradeAmount;
	private String measureType;
	private Long goodsId;
	private Long marketId;
	private Integer netWeight;
	private Integer unitWeight;
	private Integer unitAmount;
	private Long unitPrice;
	private Long maxPrice;
	private Long minPrice;
	private Long maxTradeAmount;
	private Long minTradeAmount;
	private Integer maxTradeWeight;
	private Integer minTradeWeight;
	private Integer tradeCount;
	private Integer tradePriceCount;
	private Long totalTradeAmount;
	private Integer totalTradeWeight;
	private Date settlementTime;
	private String tradeType;

	public Long getTradeAmount() {
		return tradeAmount;
	}

	public void setTradeAmount(Long tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
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

	public Integer getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(Integer netWeight) {
		this.netWeight = netWeight;
	}

	public Integer getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(Integer unitWeight) {
		this.unitWeight = unitWeight;
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
}
