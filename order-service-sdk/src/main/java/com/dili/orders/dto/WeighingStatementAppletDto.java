package com.dili.orders.dto;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.domain.WeighingStatementState;

public class WeighingStatementAppletDto extends WeighingStatement {

	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 单价（分），元/斤
	 */
	private Long unitPrice;
	/**
	 * 件重(2位小数，转化需要除以100)，单位斤
	 */
	private Integer unitWeight;
	/**
	 * 净重(2位小数，转化需要除以100)
	 */
	private Integer netWeight;
	/**
	 * 件数
	 */
	private Integer unitAmount;
	/**
	 * 计量方式
	 */
	private String measureType;

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
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

	public Integer getUnitAmount() {
		return unitAmount;
	}

	public void setUnitAmount(Integer unitAmount) {
		this.unitAmount = unitAmount;
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public String getStateName() {
		if (this.getState() == null) {
			return null;
		}
		return WeighingStatementState.valueOf(this.getState()).getName();

	}
}
