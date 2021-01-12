package com.dili.orders.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingStatement;
import com.fasterxml.jackson.annotation.JsonFormat;

import tk.mybatis.mapper.annotation.Version;

public class WeighingBillListStatisticsDto {

	/**
	 *
	 */
	@Transient
	private static final long serialVersionUID = -4231427191843086522L;

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
	 * 交易类型
	 */
	@Column(name = "`trade_type_id`")
	private Long tradeTypeId;

	/**
	 * 买方id
	 */
	@Column(name = "`buyer_id`")
	private Long buyerId;

	/**
	 * 买方编号
	 */
	@Column(name = "`buyer_code`")
	private String buyerCode;

	/**
	 * 买方卡号
	 */
	@Column(name = "`buyer_card_no`")
	private String buyerCardNo;

	/**
	 * 买家卡账户
	 */
	@Column(name = "`buyer_card_account`")
	private Long buyerCardAccount;

	/**
	 * 买家持卡人姓名
	 */
	@Column(name = "`buyer_card_holder_name`")
	private String buyerCardHolderName;

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
	 * 买家联系方式
	 */
	@Column(name = "`buyer_contact`")
	private String buyerContact;

	/**
	 * 卖家身份类型
	 */
	@Column(name = "`buyer_type`")
	private String buyerType;

	/**
	 * 买家代理人id
	 */
	@Column(name = "`buyer_agent_id`")
	private Long buyerAgentId;

	/**
	 * 买家代理人姓名
	 */
	@Column(name = "`buyer_agent_name`")
	private String buyerAgentName;

	/** 买家身份号 */
	@Column(name = "buyer_certificate_number")
	private String buyerCertificateNumber;

	/**
	 * 卖方id
	 */
	@Column(name = "`seller_id`")
	private Long sellerId;

	/**
	 * 卖方编号
	 */
	@Column(name = "`seller_code`")
	private String sellerCode;

	/**
	 * 卖方卡号
	 */
	@Column(name = "`seller_card_no`")
	private String sellerCardNo;

	/**
	 * 卖家卡账户
	 */
	@Column(name = "`seller_card_account`")
	private Long sellerCardAccount;

	/**
	 * 卖方持卡人姓名
	 */
	@Column(name = "`seller_card_holder_name`")
	private String sellerCardHolderName;

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
	 * 卖家联系方式
	 */
	@Column(name = "`seller_contact`")
	private String sellerContact;

	/**
	 * 卖家身份类型
	 */
	@Column(name = "`seller_type`")
	private String sellerType;

	/**
	 * 卖家代理人id
	 */
	@Column(name = "`seller_agent_id`")
	private Long sellerAgentId;

	/**
	 * 卖家代理人姓名
	 */
	@Column(name = "`seller_agent_name`")
	private String sellerAgentName;

	/** 买家身份号 */
	@Column(name = "seller_certificate_number")
	private String sellerCertificateNumber;

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
	 * 商品编码
	 */
	@Column(name = "`goods_code`")
	private String goodsCode;

	/**
	 * 商品快捷码
	 */
	@Column(name = "`goods_key_code`")
	private String goodsKeyCode;

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
	 * 单价（分），元/斤
	 */
	@Column(name = "`unit_price`")
	private Long unitPrice;

	/**
	 * 件重(2位小数，转化需要除以100)，单位斤
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
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "`fetch_weight_time`")
	private LocalDateTime fetchWeightTime;

	/**
	 * 毛重(2位小数，转化需要除以100)
	 */
	@Column(name = "`rough_weight`")
	private Long roughWeight;

	/**
	 * 净重(2位小数，转化需要除以100)
	 */
	@Column(name = "`net_weight`")
	private Long netWeight;

	/**
	 * 车牌号
	 */
	@Column(name = "`plate_number`")
	private String plateNumber;

	/**
	 * 皮重(2位小数，转化需要除以100)
	 */
	@Column(name = "`tare_weight`")
	private Long tareWeight;

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
	private Long estimatedNetWeight;

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
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "`created_time`")
	private LocalDateTime createdTime;

	/**
	 * 修改时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "`modified_time`")
	private LocalDateTime modifiedTime;

	/**
	 * 结算时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "`settlement_time`")
	private LocalDateTime settlementTime;

	/**
	 * 过磅单所属部门，来自创建用户所属部门
	 */
	@Column(name = "`department_id`")
	private Long departmentId;

	/**
	 * 部门名称
	 */
	@Column(name = "`department_name`")
	private String departmentName;

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
	 * 是否校验中间价
	 */
	@Column(name = "`check_price`")
	private Boolean checkPrice;

	/**
	 * 价格状态，1待审核，2审核通过，3审核拒绝
	 */
	@Column(name = "`price_state`")
	private Integer priceState;

	/**
	 * 市场id
	 */
	@Column(name = "`market_id`")
	private Long marketId;

	/**
	 * 版本号，乐观锁控制
	 */
	@Version
	@Column(name = "`version`")
	private Integer version;

	private WeighingStatement statement;
	private WeighingBillOperationRecord operationRecord;
	private String tradeTypeName;
	private Long unitWeightPrice;
	private Long unitPiecePrice;
	/** 检测数值 */
	private String latestPdResult;
	/** 检测结果描述 */
	private String detectStateDesc;

	public String getDetectStateDesc() {
		return detectStateDesc;
	}

	public void setDetectStateDesc(String detectStateDesc) {
		this.detectStateDesc = detectStateDesc;
	}

	public String getLatestPdResult() {
		return latestPdResult;
	}

	public void setLatestPdResult(String latestPdResult) {
		this.latestPdResult = latestPdResult;
	}

	public WeighingStatement getStatement() {
		return statement;
	}

	public void setStatement(WeighingStatement statement) {
		this.statement = statement;
	}

	public WeighingBillOperationRecord getOperationRecord() {
		return operationRecord;
	}

	public void setOperationRecord(WeighingBillOperationRecord operationRecord) {
		this.operationRecord = operationRecord;
	}

	public Long getUnitWeightPrice() {
		return unitWeightPrice;
	}

	public void setUnitWeightPrice(Long unitWeightPrice) {
		this.unitWeightPrice = unitWeightPrice;
	}

	public Long getUnitPiecePrice() {
		return unitPiecePrice;
	}

	public void setUnitPiecePrice(Long unitPiecePrice) {
		this.unitPiecePrice = unitPiecePrice;
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getWeighingOperationTime() {
		return this.getModifiedTime() != null ? this.getModifiedTime() : this.getCreatedTime();
	}

	public String getTradeTypeName() {
		return tradeTypeName;
	}

	public void setTradeTypeName(String tradeTypeName) {
		this.tradeTypeName = tradeTypeName;
	}

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

	public Long getTradeTypeId() {
		return tradeTypeId;
	}

	public void setTradeTypeId(Long tradeTypeId) {
		this.tradeTypeId = tradeTypeId;
	}

	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}

	public String getBuyerCardNo() {
		return buyerCardNo;
	}

	public void setBuyerCardNo(String buyerCardNo) {
		this.buyerCardNo = buyerCardNo;
	}

	public Long getBuyerCardAccount() {
		return buyerCardAccount;
	}

	public void setBuyerCardAccount(Long buyerCardAccount) {
		this.buyerCardAccount = buyerCardAccount;
	}

	public String getBuyerCardHolderName() {
		return buyerCardHolderName;
	}

	public void setBuyerCardHolderName(String buyerCardHolderName) {
		this.buyerCardHolderName = buyerCardHolderName;
	}

	public Long getBuyerAccount() {
		return buyerAccount;
	}

	public void setBuyerAccount(Long buyerAccount) {
		this.buyerAccount = buyerAccount;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerContact() {
		return buyerContact;
	}

	public void setBuyerContact(String buyerContact) {
		this.buyerContact = buyerContact;
	}

	public String getBuyerType() {
		return buyerType;
	}

	public void setBuyerType(String buyerType) {
		this.buyerType = buyerType;
	}

	public Long getBuyerAgentId() {
		return buyerAgentId;
	}

	public void setBuyerAgentId(Long buyerAgentId) {
		this.buyerAgentId = buyerAgentId;
	}

	public String getBuyerAgentName() {
		return buyerAgentName;
	}

	public void setBuyerAgentName(String buyerAgentName) {
		this.buyerAgentName = buyerAgentName;
	}

	public String getBuyerCertificateNumber() {
		return buyerCertificateNumber;
	}

	public void setBuyerCertificateNumber(String buyerCertificateNumber) {
		this.buyerCertificateNumber = buyerCertificateNumber;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getSellerCardNo() {
		return sellerCardNo;
	}

	public void setSellerCardNo(String sellerCardNo) {
		this.sellerCardNo = sellerCardNo;
	}

	public Long getSellerCardAccount() {
		return sellerCardAccount;
	}

	public void setSellerCardAccount(Long sellerCardAccount) {
		this.sellerCardAccount = sellerCardAccount;
	}

	public String getSellerCardHolderName() {
		return sellerCardHolderName;
	}

	public void setSellerCardHolderName(String sellerCardHolderName) {
		this.sellerCardHolderName = sellerCardHolderName;
	}

	public Long getSellerAccount() {
		return sellerAccount;
	}

	public void setSellerAccount(Long sellerAccount) {
		this.sellerAccount = sellerAccount;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getSellerContact() {
		return sellerContact;
	}

	public void setSellerContact(String sellerContact) {
		this.sellerContact = sellerContact;
	}

	public String getSellerType() {
		return sellerType;
	}

	public void setSellerType(String sellerType) {
		this.sellerType = sellerType;
	}

	public Long getSellerAgentId() {
		return sellerAgentId;
	}

	public void setSellerAgentId(Long sellerAgentId) {
		this.sellerAgentId = sellerAgentId;
	}

	public String getSellerAgentName() {
		return sellerAgentName;
	}

	public void setSellerAgentName(String sellerAgentName) {
		this.sellerAgentName = sellerAgentName;
	}

	public String getSellerCertificateNumber() {
		return sellerCertificateNumber;
	}

	public void setSellerCertificateNumber(String sellerCertificateNumber) {
		this.sellerCertificateNumber = sellerCertificateNumber;
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

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getGoodsKeyCode() {
		return goodsKeyCode;
	}

	public void setGoodsKeyCode(String goodsKeyCode) {
		this.goodsKeyCode = goodsKeyCode;
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

	public Long getRoughWeight() {
		return roughWeight;
	}

	public void setRoughWeight(Long roughWeight) {
		this.roughWeight = roughWeight;
	}

	public Long getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(Long netWeight) {
		this.netWeight = netWeight;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public Long getTareWeight() {
		return tareWeight;
	}

	public void setTareWeight(Long tareWeight) {
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

	public Long getEstimatedNetWeight() {
		return estimatedNetWeight;
	}

	public void setEstimatedNetWeight(Long estimatedNetWeight) {
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(LocalDateTime modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public LocalDateTime getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(LocalDateTime settlementTime) {
		this.settlementTime = settlementTime;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getModifierId() {
		return modifierId;
	}

	public void setModifierId(Long modifierId) {
		this.modifierId = modifierId;
	}

	public Boolean getCheckPrice() {
		return checkPrice;
	}

	public void setCheckPrice(Boolean checkPrice) {
		this.checkPrice = checkPrice;
	}

	public Integer getPriceState() {
		return priceState;
	}

	public void setPriceState(Integer priceState) {
		this.priceState = priceState;
	}

	public Long getMarketId() {
		return marketId;
	}

	public void setMarketId(Long marketId) {
		this.marketId = marketId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
