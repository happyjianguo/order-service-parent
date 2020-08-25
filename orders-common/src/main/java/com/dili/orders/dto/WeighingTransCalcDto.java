package com.dili.orders.dto;

import java.util.Date;

/**
 * 用于计算参考价
 * @author Tyler.Mao
 */
public class WeighingTransCalcDto {

	private int id;
	private Long tradeAmount;
	private String measureType;
	private Long goodsId;
	private Long marketId;
	private int netWeight;
	private int unitWeight;
	private int unitAmount;
	private Long unitPrice;
	private Long maxPrice;
	private Long minPrice;
	private Long maxTradeAmount;
	private Long minTradeAmount;
	private int maxTradeWeight;
	private int minTradeWeight;
	private int tradeCount;
	private int tradePriceCount;
	private Long totalTradeAmount;
	private int totalTradeWeight;
	private Date settlementTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public int getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(int netWeight) {
		this.netWeight = netWeight;
	}

	public int getUnitWeight() {
		return unitWeight;
	}

	public void setUnitWeight(int unitWeight) {
		this.unitWeight = unitWeight;
	}

	public int getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(int unitAmount) {
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

	public int getMaxTradeWeight() {
		return maxTradeWeight;
	}

	public void setMaxTradeWeight(int maxTradeWeight) {
		this.maxTradeWeight = maxTradeWeight;
	}

	public int getMinTradeWeight() {
		return minTradeWeight;
	}

	public void setMinTradeWeight(int minTradeWeight) {
		this.minTradeWeight = minTradeWeight;
	}

	public int getTradeCount() {
		return tradeCount;
	}

	public void setTradeCount(int tradeCount) {
		this.tradeCount = tradeCount;
	}

	public int getTradePriceCount() {
		return tradePriceCount;
	}

	public void setTradePriceCount(int tradePriceCount) {
		this.tradePriceCount = tradePriceCount;
	}

	public Long getTotalTradeAmount() {
		return totalTradeAmount;
	}

	public void setTotalTradeAmount(Long totalTradeAmount) {
		this.totalTradeAmount = totalTradeAmount;
	}

	public int getTotalTradeWeight() {
		return totalTradeWeight;
	}

	public void setTotalTradeWeight(int totalTradeWeight) {
		this.totalTradeWeight = totalTradeWeight;
	}

	public Date getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(Date settlementTime) {
		this.settlementTime = settlementTime;
	}
}
