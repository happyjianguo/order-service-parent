package com.dili.orders.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class WeighingBillUpdateDto {

	@Id
	@Column(name = "`id`")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 过磅单号
	 */
	@Column(name = "`serial_no`")
	private String serialNo;

	/**
	 * 计量方式
	 */
	@Column(name = "`measure_type`")
	private String measureType;

	/**
	 * 交易类型
	 */
	@Column(name = "`trade_type`")
	private String tradeType;

	/**
	 * 商品id
	 */
	@Column(name = "`goods_id`")
	private Long goodsId;

	/**
	 * 商品名称
	 */
	@Column(name = "`goods_name`")
	private String goodsName;

	/**
	 * 商品产地id
	 */
	@Column(name = "`goods_origin_city_id`")
	private Long goodsOriginCityId;

	/**
	 * 商品产地名称
	 */
	@Column(name = "`goods_origin_city_name`")
	private String goodsOriginCityName;

	/**
	 * 件数
	 */
	@Column(name = "`unit_amount`")
	private Integer unitAmount;

	/**
	 * 单价（分）
	 */
	@Column(name = "`unit_price`")
	private Long unitPrice;

	/**
	 * 件重(2位小数，转化需要除以100)
	 */
	@Column(name = "`unit_weight`")
	private Integer unitWeight;

	/**
	 * 取重(2位小数，转化需要除以100)
	 */
	@Column(name = "`fetched_weight`")
	private Integer fetchedWeight;

	/**
	 * 取重时间
	 */
	@Column(name = "`fetch_weight_time`")
	private LocalDateTime fetchWeightTime;

	/**
	 * 毛重(2位小数，转化需要除以100)
	 */
	@Column(name = "`rough_weight`")
	private Integer roughWeight;

	/**
	 * 净重(2位小数，转化需要除以100)
	 */
	@Column(name = "`net_weight`")
	private Integer netWeight;

	/**
	 * 车牌号
	 */
	@Column(name = "`plate_number`")
	private String plateNumber;

	/**
	 * 皮重(2位小数，转化需要除以100)
	 */
	@Column(name = "`tare_weight`")
	private Integer tareWeight;

	/**
	 * 除杂比例（百分比，转换需除以100）
	 */
	@Column(name = "`subtraction_rate`")
	private Integer subtractionRate;

	/**
	 * 除杂重量(2位小数，转化需要除以100)
	 */
	@Column(name = "`subtraction_weight`")
	private Integer subtractionWeight;

	/**
	 * 估计净重(2位小数，转化需要除以100)
	 */
	@Column(name = "`estimated_net_weight`")
	private Integer estimatedNetWeight;

	/**
	 * 冻结金额(分)
	 */
	@Column(name = "`frozen_amount`")
	private Long frozenAmount;

	/**
	 * 皮重单据号
	 */
	@Column(name = "`tare_bill_number`")
	private String tareBillNumber;

	/**
	 * 修改人id
	 */
	@Column(name = "`modifier_id`")
	private Long modifierId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Long getGoodsOriginCityId() {
		return goodsOriginCityId;
	}

	public void setGoodsOriginCityId(Long goodsOriginCityId) {
		this.goodsOriginCityId = goodsOriginCityId;
	}

	public String getGoodsOriginCityName() {
		return goodsOriginCityName;
	}

	public void setGoodsOriginCityName(String goodsOriginCityName) {
		this.goodsOriginCityName = goodsOriginCityName;
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

	public Integer getFetchedWeight() {
		return fetchedWeight;
	}

	public void setFetchedWeight(Integer fetchedWeight) {
		this.fetchedWeight = fetchedWeight;
	}

	public LocalDateTime getFetchWeightTime() {
		return fetchWeightTime;
	}

	public void setFetchWeightTime(LocalDateTime fetchWeightTime) {
		this.fetchWeightTime = fetchWeightTime;
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

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public Integer getTareWeight() {
		return tareWeight;
	}

	public void setTareWeight(Integer tareWeight) {
		this.tareWeight = tareWeight;
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

	public Integer getEstimatedNetWeight() {
		return estimatedNetWeight;
	}

	public void setEstimatedNetWeight(Integer estimatedNetWeight) {
		this.estimatedNetWeight = estimatedNetWeight;
	}

	public Long getFrozenAmount() {
		return frozenAmount;
	}

	public void setFrozenAmount(Long frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	public String getTareBillNumber() {
		return tareBillNumber;
	}

	public void setTareBillNumber(String tareBillNumber) {
		this.tareBillNumber = tareBillNumber;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}
}
