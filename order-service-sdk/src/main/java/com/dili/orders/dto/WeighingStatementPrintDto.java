package com.dili.orders.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.WeighingStatement;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class WeighingStatementPrintDto extends WeighingStatement {

	/**
	 * 过磅员登录账号
	 */
	private String weighingOperatorUserName;
	/**
	 * 过磅员姓名
	 */
	private String weighingOperatorName;
	/**
	 * 结算员登录账号
	 */
	private String settlementOperatorUserName;
	/**
	 * 结算员姓名
	 */
	private String settlementOperatorName;

	/**
	 * 计量方式
	 */
	private String measureType;

	/**
	 * 单价或件价
	 */
	private Long unitPrice;

	/**
	 * 皮重(2位小数，转化需要除以100)
	 */
	private Integer tareWeight;

	/**
	 * 毛重(2位小数，转化需要除以100)
	 */
	private Integer roughWeight;

	/**
	 * 净重(2位小数，转化需要除以100)
	 */
	private Integer netWeight;

	/**
	 * 买方余额
	 */
	private Long buyerBalance;

	/**
	 * 是否补打
	 */
	@JsonIgnore
	@JSONField(serialize = false)
	private Boolean reprint = false;

	/**
	 * 件重
	 */
	private Integer unitWeight;

	/**
	 * 件数
	 */
	private Integer unitAmount;

	/**
	 * 买家编号
	 */
	private String buyerCode;

	/**
	 * 卖家编号
	 */
	private String sellerCode;

	/**
	 * 品类名称
	 */
	private String goodsName;

	/**
	 * 车牌号
	 */
	private String plateNumber;
	/**
	 * 交易类型编码
	 */
	private String tradeType;

	/**
	 * 除杂比例
	 */
	private Integer subtractionRate;

	/**
	 * 除杂重量(2位小数，转化需要除以100)
	 */
	private Integer subtractionWeight;

	/**
	 * 包装箱名称
	 */
	private String packingTypeName;

	public String getWeighingOperatorUserName() {
		return weighingOperatorUserName;
	}

	public void setWeighingOperatorUserName(String weighingOperatorUserName) {
		this.weighingOperatorUserName = weighingOperatorUserName;
	}

	public String getWeighingOperatorName() {
		return weighingOperatorName;
	}

	public void setWeighingOperatorName(String weighingOperatorName) {
		this.weighingOperatorName = weighingOperatorName;
	}

	public String getSettlementOperatorUserName() {
		return settlementOperatorUserName;
	}

	public void setSettlementOperatorUserName(String settlementOperatorUserName) {
		this.settlementOperatorUserName = settlementOperatorUserName;
	}

	public String getSettlementOperatorName() {
		return settlementOperatorName;
	}

	public void setSettlementOperatorName(String settlementOperatorName) {
		this.settlementOperatorName = settlementOperatorName;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getOperationTime() {
		return this.getModifiedTime() != null ? this.getModifiedTime() : this.getCreatedTime();
	}

	public Boolean getReprint() {
		return reprint;
	}

	public void setReprint(Boolean reprint) {
		this.reprint = reprint;
	}

	public String getReprintString() {
		if (this.reprint) {
			return "（补打）";
		}
		return "";
	}

	public String getMeasureType() {
		return measureType;
	}

	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	public Long getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Long unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getTareWeight() {
		return tareWeight;
	}

	public void setTareWeight(Integer tareWeight) {
		this.tareWeight = tareWeight;
	}

	public Integer getRoughWeight() {
		return roughWeight;
	}

	public void setRoughWeight(Integer roughWeight) {
		this.roughWeight = roughWeight;
	}

	public Integer getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(Integer netWeight) {
		this.netWeight = netWeight;
	}

	public Long getBuyerBalance() {
		return buyerBalance;
	}

	public void setBuyerBalance(Long buyerBalance) {
		this.buyerBalance = buyerBalance;
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

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public Integer getSubtractionRate() {
		return subtractionRate;
	}

	public void setSubtractionRate(Integer subtractionRate) {
		this.subtractionRate = subtractionRate;
	}

	public Integer getSubtractionWeight() {
		return subtractionWeight;
	}

	public void setSubtractionWeight(Integer subtractionWeight) {
		this.subtractionWeight = subtractionWeight;
	}

	public String getPackingTypeName() {
		return packingTypeName;
	}

	public void setPackingTypeName(String packingTypeName) {
		this.packingTypeName = packingTypeName;
	}

}
