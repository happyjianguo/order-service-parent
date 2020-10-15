package com.dili.orders.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.fasterxml.jackson.annotation.JsonFormat;

import tk.mybatis.mapper.annotation.Version;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-10-10 17:26:22.
 */
@Table(name = "`price_approve_record`")
public class PriceApproveRecord extends BaseDomain {
	@Id
	@Column(name = "`id`")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 过磅id
	 */
	@Column(name = "`weighing_bill_id`")
	private Long weighingBillId;

	/**
	 * 过磅单号
	 */
	@Column(name = "`weighing_bill_serial_no`")
	private String weighingBillSerialNo;

	/**
	 * 结算单id
	 */
	@Column(name = "`statement_id`")
	private Long statementId;

	/**
	 * 结算单号
	 */
	@Column(name = "`statement_serial_no`")
	private String statementSerialNo;

	/**
	 * 买家id
	 */
	@Column(name = "`buyer_id`")
	private Long buyerId;

	/**
	 * 买家姓名
	 */
	@Column(name = "`buyer_name`")
	private String buyerName;

	/**
	 * 买家卡号
	 */
	@Column(name = "`buyer_card_no`")
	private String buyerCardNo;

	/**
	 * 卖家id
	 */
	@Column(name = "`seller_id`")
	private Long sellerId;

	/**
	 * 卖家姓名
	 */
	@Column(name = "`seller_name`")
	private String sellerName;

	/**
	 * 卖家卡号
	 */
	@Column(name = "`seller_card_no`")
	private String sellerCardNo;

	/**
	 * 市场id
	 */
	@Column(name = "`market_id`")
	private Long marketId;

	/**
	 * 交易类型编码
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
	 * 交易量（斤）
	 */
	@Column(name = "`trade_weight`")
	private Integer tradeWeight;

	/**
	 * 单价
	 */
	@Column(name = "`unit_price`")
	private Long unitPrice;

	/**
	 * 参考最低价
	 */
	@Column(name = "`reference_price`")
	private Long referencePrice;

	/**
	 * 状态
	 */
	@Column(name = "`state`")
	private Integer state;

	/**
	 * 审核员id
	 */
	@Column(name = "`approver_id`")
	private Long approverId;

	/**
	 * 审核员姓名
	 */
	@Column(name = "`approver_name`")
	private String approverName;

	/**
	 * 审核时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "`approve_time`")
	private LocalDateTime approveTime;

	/**
	 * 备注
	 */
	@Column(name = "`notes`")
	private String notes;

	/**
	 * 过磅时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "`weighing_time`")
	private LocalDateTime weighingTime;

	/**
	 * 流程实例id
	 */
	@Column(name = "`process_instance_id`")
	private String processInstanceId;

	/**
	 * 流程定义id
	 */
	@Column(name = "`process_definition_id`")
	private String processDefinitionId;

	/**
	 * 版本号，乐观锁控制
	 */
	@Version
	@Column(name = "`version`")
	private Integer version;

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
	 * 获取过磅id
	 *
	 * @return weighing_bill_id - 过磅id
	 */
	@FieldDef(label = "过磅id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getWeighingBillId() {
		return weighingBillId;
	}

	/**
	 * 设置过磅id
	 *
	 * @param weighingBillId 过磅id
	 */
	public void setWeighingBillId(Long weighingBillId) {
		this.weighingBillId = weighingBillId;
	}

	/**
	 * 获取过磅单号
	 *
	 * @return weighing_bill_serial_no - 过磅单号
	 */
	@FieldDef(label = "过磅单号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getWeighingBillSerialNo() {
		return weighingBillSerialNo;
	}

	/**
	 * 设置过磅单号
	 *
	 * @param weighingBillSerialNo 过磅单号
	 */
	public void setWeighingBillSerialNo(String weighingBillSerialNo) {
		this.weighingBillSerialNo = weighingBillSerialNo;
	}

	/**
	 * 获取结算单id
	 *
	 * @return statement_id - 结算单id
	 */
	@FieldDef(label = "结算单id")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getStatementId() {
		return statementId;
	}

	/**
	 * 设置结算单id
	 *
	 * @param statementId 结算单id
	 */
	public void setStatementId(Long statementId) {
		this.statementId = statementId;
	}

	/**
	 * 获取结算单号
	 *
	 * @return statement_serial_no - 结算单号
	 */
	@FieldDef(label = "结算单号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getStatementSerialNo() {
		return statementSerialNo;
	}

	/**
	 * 设置结算单号
	 *
	 * @param statementSerialNo 结算单号
	 */
	public void setStatementSerialNo(String statementSerialNo) {
		this.statementSerialNo = statementSerialNo;
	}

	/**
	 * 获取买家id
	 *
	 * @return buyer_id - 买家id
	 */
	@FieldDef(label = "买家id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getBuyerId() {
		return buyerId;
	}

	/**
	 * 设置买家id
	 *
	 * @param buyerId 买家id
	 */
	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	/**
	 * 获取买家姓名
	 *
	 * @return buyer_name - 买家姓名
	 */
	@FieldDef(label = "买家姓名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * 设置买家姓名
	 *
	 * @param buyerName 买家姓名
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * 获取买家卡号
	 *
	 * @return buyer_card_no - 买家卡号
	 */
	@FieldDef(label = "买家卡号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getBuyerCardNo() {
		return buyerCardNo;
	}

	/**
	 * 设置买家卡号
	 *
	 * @param buyerCardNo 买家卡号
	 */
	public void setBuyerCardNo(String buyerCardNo) {
		this.buyerCardNo = buyerCardNo;
	}

	/**
	 * 获取卖家id
	 *
	 * @return seller_id - 卖家id
	 */
	@FieldDef(label = "卖家id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getSellerId() {
		return sellerId;
	}

	/**
	 * 设置卖家id
	 *
	 * @param sellerId 卖家id
	 */
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	/**
	 * 获取卖家姓名
	 *
	 * @return seller_name - 卖家姓名
	 */
	@FieldDef(label = "卖家姓名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSellerName() {
		return sellerName;
	}

	/**
	 * 设置卖家姓名
	 *
	 * @param sellerName 卖家姓名
	 */
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	/**
	 * 获取卖家卡号
	 *
	 * @return seller_card_no - 卖家卡号
	 */
	@FieldDef(label = "卖家卡号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSellerCardNo() {
		return sellerCardNo;
	}

	/**
	 * 设置卖家卡号
	 *
	 * @param sellerCardNo 卖家卡号
	 */
	public void setSellerCardNo(String sellerCardNo) {
		this.sellerCardNo = sellerCardNo;
	}

	/**
	 * 获取市场id
	 *
	 * @return market_id - 市场id
	 */
	@FieldDef(label = "市场id")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getMarketId() {
		return marketId;
	}

	/**
	 * 设置市场id
	 *
	 * @param marketId 市场id
	 */
	public void setMarketId(Long marketId) {
		this.marketId = marketId;
	}

	/**
	 * 获取交易类型编码
	 *
	 * @return trade_type - 交易类型编码
	 */
	@FieldDef(label = "交易类型编码", maxLength = 10)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getTradeType() {
		return tradeType;
	}

	/**
	 * 设置交易类型编码
	 *
	 * @param tradeType 交易类型编码
	 */
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
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
	@EditMode(editor = FieldEditor.Text, required = true)
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
	 * 获取交易量（斤）
	 *
	 * @return trade_weight - 交易量（斤）
	 */
	@FieldDef(label = "交易量（斤）")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Integer getTradeWeight() {
		return tradeWeight;
	}

	/**
	 * 设置交易量（斤）
	 *
	 * @param tradeWeight 交易量（斤）
	 */
	public void setTradeWeight(Integer tradeWeight) {
		this.tradeWeight = tradeWeight;
	}

	/**
	 * 获取单价
	 *
	 * @return unit_price - 单价
	 */
	@FieldDef(label = "单价")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getUnitPrice() {
		return unitPrice;
	}

	/**
	 * 设置单价
	 *
	 * @param unitPrice 单价
	 */
	public void setUnitPrice(Long unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * 获取参考最低价
	 *
	 * @return reference_price - 参考最低价
	 */
	@FieldDef(label = "参考最低价")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getReferencePrice() {
		return referencePrice;
	}

	/**
	 * 设置参考最低价
	 *
	 * @param referencePrice 参考最低价
	 */
	public void setReferencePrice(Long referencePrice) {
		this.referencePrice = referencePrice;
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
	 * 获取审核员id
	 *
	 * @return approver_id - 审核员id
	 */
	@FieldDef(label = "审核员id")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getApproverId() {
		return approverId;
	}

	/**
	 * 设置审核员id
	 *
	 * @param approverId 审核员id
	 */
	public void setApproverId(Long approverId) {
		this.approverId = approverId;
	}

	/**
	 * 获取审核员姓名
	 *
	 * @return approver_name - 审核员姓名
	 */
	@FieldDef(label = "审核员姓名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getApproverName() {
		return approverName;
	}

	/**
	 * 设置审核员姓名
	 *
	 * @param approverName 审核员姓名
	 */
	public void setApproverName(String approverName) {
		this.approverName = approverName;
	}

	/**
	 * 获取审核时间
	 *
	 * @return approve_time - 审核时间
	 */
	@FieldDef(label = "审核时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public LocalDateTime getApproveTime() {
		return approveTime;
	}

	/**
	 * 设置审核时间
	 *
	 * @param approveTime 审核时间
	 */
	public void setApproveTime(LocalDateTime approveTime) {
		this.approveTime = approveTime;
	}

	/**
	 * 获取备注
	 *
	 * @return notes - 备注
	 */
	@FieldDef(label = "备注", maxLength = 200)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getNotes() {
		return notes;
	}

	/**
	 * 设置备注
	 *
	 * @param notes 备注
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * 获取过磅时间
	 *
	 * @return weighing_time - 过磅时间
	 */
	@FieldDef(label = "过磅时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	public LocalDateTime getWeighingTime() {
		return weighingTime;
	}

	/**
	 * 设置过磅时间
	 *
	 * @param weighingTime 过磅时间
	 */
	public void setWeighingTime(LocalDateTime weighingTime) {
		this.weighingTime = weighingTime;
	}

	/**
	 * 获取流程实例id
	 *
	 * @return process_instance_id - 流程实例id
	 */
	@FieldDef(label = "流程实例id", maxLength = 64)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getProcessInstanceId() {
		return processInstanceId;
	}

	/**
	 * 设置流程实例id
	 *
	 * @param processInstanceId 流程实例id
	 */
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	/**
	 * 获取流程定义id
	 *
	 * @return process_definition_id - 流程定义id
	 */
	@FieldDef(label = "流程定义id", maxLength = 64)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	/**
	 * 设置流程定义id
	 *
	 * @param processDefinitionId 流程定义id
	 */
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}