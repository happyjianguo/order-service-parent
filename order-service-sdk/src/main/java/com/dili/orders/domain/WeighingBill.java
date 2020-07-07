package com.dili.orders.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-06-23 11:33:35.
 */
@Table(name = "`weighing_bill`")
public class WeighingBill extends BaseDomain {
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
	 * 买方id
	 */
	@Column(name = "`buyer_id`")
	private Long buyerId;

	/**
	 * 买方卡号
	 */
	@Column(name = "`buyer_card_no`")
	private String buyerCardNo;

	/**
	 * 买方卡账户
	 */
	@Column(name = "`buyer_card_account`")
	private Long buyerCardAccount;

	/**
	 * 买方支付账号
	 */
	@Column(name = "`buyer_account`")
	private Long buyerAccount;

	/**
	 * 买方姓名
	 */
	@Column(name = "`buyer_name`")
	private String buyerName;

	/**
	 * 卖方id
	 */
	@Column(name = "`seller_id`")
	private Long sellerId;

	/**
	 * 卖方卡账户
	 */
	@Column(name = "`seller_card_account`")
	private Long sellerCardAccount;

	/**
	 * 卖方卡号
	 */
	@Column(name = "`seller_card_no`")
	private String sellerCardNo;

	/**
	 * 卖方支付账号
	 */
	@Column(name = "`seller_account`")
	private Long sellerAccount;

	/**
	 * 卖方姓名
	 */
	@Column(name = "`seller_name`")
	private String sellerName;

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
	 * 状态
	 */
	@Column(name = "`state`")
	private Integer state;

	/**
	 * 创建时间
	 */
	@Column(name = "`created_time`")
	private LocalDateTime createdTime;

	/**
	 * 修改时间
	 */
	@Column(name = "`modified_time`")
	private LocalDateTime modifiedTime;

	/**
	 * 结算时间
	 */
	@Column(name = "`settlement_time`")
	private LocalDateTime settlementTime;

	/**
	 * 创建人id
	 */
	@Column(name = "`creator_id`")
	private Long creatorId;

	/**
	 * 修改人id
	 */
	@Column(name = "`modifier_id`")
	private Long modifierId;

	/**
	 * @return id
	 */
	@FieldDef(label = "id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取过磅单号
	 *
	 * @return serial_no - 过磅单号
	 */
	@FieldDef(label = "过磅单号", maxLength = 20)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSerialNo() {
		return serialNo;
	}

	/**
	 * 设置过磅单号
	 *
	 * @param serialNo 过磅单号
	 */
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	/**
	 * 获取计量方式
	 *
	 * @return measure_type - 计量方式
	 */
	@FieldDef(label = "计量方式", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getMeasureType() {
		return measureType;
	}

	/**
	 * 设置计量方式
	 *
	 * @param measureType 计量方式
	 */
	public void setMeasureType(String measureType) {
		this.measureType = measureType;
	}

	/**
	 * 获取交易类型
	 *
	 * @return trade_type - 交易类型
	 */
	@FieldDef(label = "交易类型", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getTradeType() {
		return tradeType;
	}

	/**
	 * 设置交易类型
	 *
	 * @param tradeType 交易类型
	 */
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	/**
	 * 获取买方id
	 *
	 * @return buyer_id - 买方id
	 */
	@FieldDef(label = "买方id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getBuyerId() {
		return buyerId;
	}

	/**
	 * 设置买方id
	 *
	 * @param buyerId 买方id
	 */
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	/**
	 * 获取买方卡号
	 *
	 * @return buyer_card_no - 买方卡号
	 */
	@FieldDef(label = "买方卡号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getBuyerCardNo() {
		return buyerCardNo;
	}

	/**
	 * 设置买方卡账户
	 *
	 * @param buyerCardAccount 买方卡号
	 */
	public void setBuyerCardAccount(Long buyerCardAccount) {
		this.buyerCardAccount = buyerCardAccount;
	}

	/**
	 * 获取买方卡账户
	 *
	 * @return buyer_card_account - 买方卡号
	 */
	@FieldDef(label = "买方卡号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public Long getBuyerCardAccount() {
		return buyerCardAccount;
	}

	/**
	 * 设置买方卡号
	 *
	 * @param buyerCardNo 买方卡号
	 */
	public void setBuyerCardNo(String buyerCardNo) {
		this.buyerCardNo = buyerCardNo;
	}

	/**
	 * 获取买方支付账号
	 *
	 * @return buyer_account - 买方支付账号
	 */
	@FieldDef(label = "买方支付账号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public Long getBuyerAccount() {
		return buyerAccount;
	}

	/**
	 * 设置买方支付账号
	 *
	 * @param buyerAccount 买方支付账号
	 */
	public void setBuyerAccount(Long buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	/**
	 * 获取买方姓名
	 *
	 * @return buyer_name - 买方姓名
	 */
	@FieldDef(label = "买方姓名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * 设置买方姓名
	 *
	 * @param buyerName 买方姓名
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * 获取卖方id
	 *
	 * @return seller_id - 卖方id
	 */
	@FieldDef(label = "卖方id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getSellerId() {
		return sellerId;
	}

	/**
	 * 设置卖方id
	 *
	 * @param sellerId 卖方id
	 */
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	/**
	 * 获取卖方卡号
	 *
	 * @return seller_card_no - 卖方卡号
	 */
	@FieldDef(label = "卖方卡号", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSellerCardNo() {
		return sellerCardNo;
	}

	/**
	 * 设置卖方卡账号
	 *
	 * @param sellerCardNo 卖方卡号
	 */
	public void setSellerCardAccount(Long sellerCardAccount) {
		this.sellerCardAccount = sellerCardAccount;
	}

	/**
	 * 获取卖方卡账号
	 *
	 * @return seller_card_no - 卖方卡号
	 */
	@FieldDef(label = "卖方卡号", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	public Long getSellerCardAccount() {
		return sellerCardAccount;
	}

	/**
	 * 设置卖方卡号
	 *
	 * @param sellerCardNo 卖方卡号
	 */
	public void setSellerCardNo(String sellerCardNo) {
		this.sellerCardNo = sellerCardNo;
	}

	/**
	 * 获取卖方支付账号
	 *
	 * @return seller_account - 卖方支付账号
	 */
	@FieldDef(label = "卖方支付账号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public Long getSellerAccount() {
		return sellerAccount;
	}

	/**
	 * 设置卖方支付账号
	 *
	 * @param sellerAccount 卖方支付账号
	 */
	public void setSellerAccount(Long sellerAccount) {
		this.sellerAccount = sellerAccount;
	}

	/**
	 * 获取卖方姓名
	 *
	 * @return seller_name - 卖方姓名
	 */
	@FieldDef(label = "卖方姓名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSellerName() {
		return sellerName;
	}

	/**
	 * 设置卖方姓名
	 *
	 * @param sellerName 卖方姓名
	 */
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	/**
	 * 获取商品id
	 *
	 * @return goods_id - 商品id
	 */
	@FieldDef(label = "商品id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getGoodsId() {
		return goodsId;
	}

	/**
	 * 设置商品id
	 *
	 * @param goodsId 商品id
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	/**
	 * 获取商品名称
	 *
	 * @return goods_name - 商品名称
	 */
	@FieldDef(label = "商品名称", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getGoodsName() {
		return goodsName;
	}

	/**
	 * 设置商品名称
	 *
	 * @param goodsName 商品名称
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	/**
	 * 获取商品产地id
	 *
	 * @return goods_origin_city_id - 商品产地id
	 */
	@FieldDef(label = "商品产地id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getGoodsOriginCityId() {
		return goodsOriginCityId;
	}

	/**
	 * 设置商品产地id
	 *
	 * @param goodsOriginCityId 商品产地id
	 */
	public void setGoodsOriginCityId(Long goodsOriginCityId) {
		this.goodsOriginCityId = goodsOriginCityId;
	}

	/**
	 * 获取商品产地名称
	 *
	 * @return goods_origin_city_name - 商品产地名称
	 */
	@FieldDef(label = "商品产地名称", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getGoodsOriginCityName() {
		return goodsOriginCityName;
	}

	/**
	 * 设置商品产地名称
	 *
	 * @param goodsOriginCityName 商品产地名称
	 */
	public void setGoodsOriginCityName(String goodsOriginCityName) {
		this.goodsOriginCityName = goodsOriginCityName;
	}

	/**
	 * 获取件数
	 *
	 * @return unit_amount - 件数
	 */
	@FieldDef(label = "件数")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getUnitAmount() {
		return unitAmount;
	}

	/**
	 * 设置件数
	 *
	 * @param unitAmount 件数
	 */
	public void setUnitAmount(Integer unitAmount) {
		this.unitAmount = unitAmount;
	}

	/**
	 * 获取单价（分）
	 *
	 * @return unit_price - 单价（分）
	 */
	@FieldDef(label = "单价（分）")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getUnitPrice() {
		return unitPrice;
	}

	/**
	 * 设置单价（分）
	 *
	 * @param unitPrice 单价（分）
	 */
	public void setUnitPrice(Long unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * 获取件重(2位小数，转化需要除以100)
	 *
	 * @return unit_weight - 件重(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "件重(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getUnitWeight() {
		return unitWeight;
	}

	/**
	 * 设置件重(2位小数，转化需要除以100)
	 *
	 * @param unitWeight 件重(2位小数，转化需要除以100)
	 */
	public void setUnitWeight(Integer unitWeight) {
		this.unitWeight = unitWeight;
	}

	/**
	 * 获取取重(2位小数，转化需要除以100)
	 *
	 * @return fetched_weight - 取重(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "取重(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getFetchedWeight() {
		return fetchedWeight;
	}

	/**
	 * 设置取重(2位小数，转化需要除以100)
	 *
	 * @param fetchedWeight 取重(2位小数，转化需要除以100)
	 */
	public void setFetchedWeight(Integer fetchedWeight) {
		this.fetchedWeight = fetchedWeight;
	}

	/**
	 * 获取取重时间
	 *
	 * @return fetch_weight_time - 取重时间
	 */
	@FieldDef(label = "取重时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public LocalDateTime getFetchWeightTime() {
		return fetchWeightTime;
	}

	/**
	 * 设置取重时间
	 *
	 * @param fetchWeightTime 取重时间
	 */
	public void setFetchWeightTime(LocalDateTime fetchWeightTime) {
		this.fetchWeightTime = fetchWeightTime;
	}

	/**
	 * 获取毛重(2位小数，转化需要除以100)
	 *
	 * @return rough_weight - 毛重(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "毛重(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Integer getRoughWeight() {
		return roughWeight;
	}

	/**
	 * 设置毛重(2位小数，转化需要除以100)
	 *
	 * @param roughWeight 毛重(2位小数，转化需要除以100)
	 */
	public void setRoughWeight(Integer roughWeight) {
		this.roughWeight = roughWeight;
	}

	/**
	 * 获取净重(2位小数，转化需要除以100)
	 *
	 * @return net_weight - 净重(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "净重(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Integer getNetWeight() {
		return netWeight;
	}

	/**
	 * 设置净重(2位小数，转化需要除以100)
	 *
	 * @param netWeight 净重(2位小数，转化需要除以100)
	 */
	public void setNetWeight(Integer netWeight) {
		this.netWeight = netWeight;
	}

	/**
	 * 获取车牌号
	 *
	 * @return plate_number - 车牌号
	 */
	@FieldDef(label = "车牌号", maxLength = 15)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getPlateNumber() {
		return plateNumber;
	}

	/**
	 * 设置车牌号
	 *
	 * @param plateNumber 车牌号
	 */
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	/**
	 * 获取皮重(2位小数，转化需要除以100)
	 *
	 * @return tare_weight - 皮重(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "皮重(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getTareWeight() {
		return tareWeight;
	}

	/**
	 * 设置皮重(2位小数，转化需要除以100)
	 *
	 * @param tareWeight 皮重(2位小数，转化需要除以100)
	 */
	public void setTareWeight(Integer tareWeight) {
		this.tareWeight = tareWeight;
	}

	/**
	 * 获取除杂比例（百分比，转换需除以100）
	 *
	 * @return subtraction_rate - 除杂比例（百分比，转换需除以100）
	 */
	@FieldDef(label = "除杂比例（百分比，转换需除以100）")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getSubtractionRate() {
		return subtractionRate;
	}

	/**
	 * 设置除杂比例（百分比，转换需除以100）
	 *
	 * @param subtractionRate 除杂比例（百分比，转换需除以100）
	 */
	public void setSubtractionRate(Integer subtractionRate) {
		this.subtractionRate = subtractionRate;
	}

	/**
	 * 获取除杂重量(2位小数，转化需要除以100)
	 *
	 * @return subtraction_weight - 除杂重量(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "除杂重量(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getSubtractionWeight() {
		return subtractionWeight;
	}

	/**
	 * 设置除杂重量(2位小数，转化需要除以100)
	 *
	 * @param subtractionWeight 除杂重量(2位小数，转化需要除以100)
	 */
	public void setSubtractionWeight(Integer subtractionWeight) {
		this.subtractionWeight = subtractionWeight;
	}

	/**
	 * 获取估计净重(2位小数，转化需要除以100)
	 *
	 * @return estimated_net_weight - 估计净重(2位小数，转化需要除以100)
	 */
	@FieldDef(label = "估计净重(2位小数，转化需要除以100)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getEstimatedNetWeight() {
		return estimatedNetWeight;
	}

	/**
	 * 设置估计净重(2位小数，转化需要除以100)
	 *
	 * @param estimatedNetWeight 估计净重(2位小数，转化需要除以100)
	 */
	public void setEstimatedNetWeight(Integer estimatedNetWeight) {
		this.estimatedNetWeight = estimatedNetWeight;
	}

	/**
	 * 获取冻结金额(分)
	 *
	 * @return frozen_amount - 冻结金额(分)
	 */
	@FieldDef(label = "冻结金额(分)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getFrozenAmount() {
		return frozenAmount;
	}

	/**
	 * 设置冻结金额(分)
	 *
	 * @param frozenAmount 冻结金额(分)
	 */
	public void setFrozenAmount(Long frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	/**
	 * 获取皮重单据号
	 *
	 * @return tare_bill_number - 皮重单据号
	 */
	@FieldDef(label = "皮重单据号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getTareBillNumber() {
		return tareBillNumber;
	}

	/**
	 * 设置皮重单据号
	 *
	 * @param tareBillNumber 皮重单据号
	 */
	public void setTareBillNumber(String tareBillNumber) {
		this.tareBillNumber = tareBillNumber;
	}

	/**
	 * 获取状态
	 *
	 * @return state - 状态
	 */
	@FieldDef(label = "状态")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Integer getState() {
		return state;
	}

	/**
	 * 设置状态
	 *
	 * @param state 状态
	 */
	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * 获取创建时间
	 *
	 * @return created_time - 创建时间
	 */
	@FieldDef(label = "创建时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	/**
	 * 设置创建时间
	 *
	 * @param createdTime 创建时间
	 */
	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * 获取修改时间
	 *
	 * @return modified_time - 修改时间
	 */
	@FieldDef(label = "修改时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public LocalDateTime getModifiedTime() {
		return modifiedTime;
	}

	/**
	 * 设置修改时间
	 *
	 * @param modifiedTime 修改时间
	 */
	public void setModifiedTime(LocalDateTime modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	/**
	 * 获取结算时间
	 *
	 * @return settlement_time - 结算时间
	 */
	@FieldDef(label = "结算时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public LocalDateTime getSettlementTime() {
		return settlementTime;
	}

	/**
	 * 设置结算时间
	 *
	 * @param settlementTime 结算时间
	 */
	public void setSettlementTime(LocalDateTime settlementTime) {
		this.settlementTime = settlementTime;
	}

	/**
	 * 获取创建人id
	 *
	 * @return creator_id - 创建人id
	 */
	@FieldDef(label = "创建人id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getCreatorId() {
		return creatorId;
	}

	/**
	 * 设置创建人id
	 *
	 * @param creatorId 创建人id
	 */
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * 获取修改人id
	 *
	 * @return modifier_id - 修改人id
	 */
	@FieldDef(label = "修改人id")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getModifierId() {
		return modifierId;
	}

	/**
	 * 设置修改人id
	 *
	 * @param modifierId 修改人id
	 */
	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}
}