package com.dili.orders.dto;

import java.util.Date;

/**
 * 用于计算参考价
 * @author Tyler.Mao
 */
public class WeighingTransCalcDto {

	private int cransCount;
	private Date settlementDate;
	private Long tradeAmount;
	private String measureType;
	private Long goodsId;
	private int netWeight;
	private int unitWeight;
	private int unitAmount;
	private Long unitPrice;

	public int getCransCount() {
		return cransCount;
	}

	public void setCransCount(int cransCount) {
		this.cransCount = cransCount;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
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
}
