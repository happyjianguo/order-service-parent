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
 * This file was generated on 2021-01-30 16:37:59.
 */
@Table(name = "`weighing_statement`")
public class WeighingStatement extends BaseDomain {
	/**
	 * 主键
	 */
	@Id
	@Column(name = "`id`")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 交易单据号
	 */
	@Column(name = "`serial_no`")
	private String serialNo;

	/**
	 * 过磅单id
	 */
	@Column(name = "`weighing_bill_id`")
	private Long weighingBillId;

	/**
	 * 过磅单号
	 */
	@Column(name = "`weighing_serial_no`")
	private String weighingSerialNo;

	/**
	 * 买家id
	 */
	@Column(name = "`buyer_id`")
	private Long buyerId;

	/**
	 * 买家卡号
	 */
	@Column(name = "`buyer_card_no`")
	private String buyerCardNo;

	/**
	 * 买家姓名
	 */
	@Column(name = "`buyer_name`")
	private String buyerName;

	/**
	 * 买方实际支付金额（分）
	 */
	@Column(name = "`buyer_actual_amount`")
	private Long buyerActualAmount;

	/**
	 * 买方手续费(分)
	 */
	@Column(name = "`buyer_poundage`")
	private Long buyerPoundage;

	/**
	 * 买方手续费缴费支付订单号
	 */
	@Column(name = "`buyer_poundage_pay_order_no`")
	private String buyerPoundagePayOrderNo;

	/**
	 * 卖家id
	 */
	@Column(name = "`seller_id`")
	private Long sellerId;

	/**
	 * 卖家卡号
	 */
	@Column(name = "`seller_card_no`")
	private String sellerCardNo;

	/**
	 * 卖家姓名
	 */
	@Column(name = "`seller_name`")
	private String sellerName;

	/**
	 * 卖方实收金额（分）
	 */
	@Column(name = "`seller_actual_amount`")
	private Long sellerActualAmount;

	/**
	 * 卖家手续费
	 */
	@Column(name = "`seller_poundage`")
	private Long sellerPoundage;

	/**
	 * 卖方交易手续费缴费支付订单号
	 */
	@Column(name = "`seller_poundage_pay_order_no`")
	private String sellerPoundagePayOrderNo;

	/**
	 * 交易金额(分)
	 */
	@Column(name = "`trade_amount`")
	private Long tradeAmount;

	/**
	 * 冻结金额(分)
	 */
	@Column(name = "`frozen_amount`")
	private Long frozenAmount;

	/**
	 * 人工费
	 */
	@Column(name = "`staff_charges`")
	private Long staffCharges;

	/**
	 * 包装费
	 */
	@Column(name = "`packing_charges`")
	private Long packingCharges;

	/**
	 * 代收费
	 */
	@Column(name = "`collection_charges`")
	private Long collectionCharges;

	/**
	 * 回款状态：1待回款，2已回款
	 */
	@Column(name = "`payment_state`")
	private Integer paymentState;

	/**
	 * 结算方式：1园区卡，2赊销
	 */
	@Column(name = "`payment_type`")
	private Integer paymentType;

	/**
	 * 回款单id
	 */
	@Column(name = "`collection_record_id`")
	private Long collectionRecordId;

	/**
	 * 状态
	 */
	@Column(name = "`state`")
	private Integer state;

	/**
	 * 支付订单号
	 */
	@Column(name = "`pay_order_no`")
	private String payOrderNo;

	/**
	 * 冻结订单号
	 */
	@Column(name = "`frozen_order_no`")
	private String frozenOrderNo;

	/**
	 * 创建人id
	 */
	@Column(name = "`creator_id`")
	private Long creatorId;

	/**
	 * 创建时间
	 */
	@Column(name = "`created_time`")
	private LocalDateTime createdTime;

	/**
	 * 修改人id
	 */
	@Column(name = "`modifier_id`")
	private Long modifierId;

	/**
	 * 更新时间
	 */
	@Column(name = "`modified_time`")
	private LocalDateTime modifiedTime;

	/**
	 * 版本号，事务控制
	 */
	@Column(name = "`version`")
	private Integer version;

	/**
	 * 最后操作时间
	 */
	@Column(name = "`last_operation_time`")
	private LocalDateTime lastOperationTime;

	/**
	 * 最后操作员id
	 */
	@Column(name = "`last_operator_id`")
	private Long lastOperatorId;

	/**
	 * 最后操作员名称
	 */
	@Column(name = "`last_operator_name`")
	private String lastOperatorName;

	/**
	 * 最后操作员工号
	 */
	@Column(name = "`last_operator_user_name`")
	private String lastOperatorUserName;

	/**
	 * 获取主键
	 *
	 * @return id - 主键
	 */
	@FieldDef(label = "主键")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getId() {
		return id;
	}

	/**
	 * 设置主键
	 *
	 * @param id 主键
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取交易单据号
	 *
	 * @return serial_no - 交易单据号
	 */
	@FieldDef(label = "交易单据号", maxLength = 20)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSerialNo() {
		return serialNo;
	}

	/**
	 * 设置交易单据号
	 *
	 * @param serialNo 交易单据号
	 */
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	/**
	 * 获取过磅单id
	 *
	 * @return weighing_bill_id - 过磅单id
	 */
	@FieldDef(label = "过磅单id")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getWeighingBillId() {
		return weighingBillId;
	}

	/**
	 * 设置过磅单id
	 *
	 * @param weighingBillId 过磅单id
	 */
	public void setWeighingBillId(Long weighingBillId) {
		this.weighingBillId = weighingBillId;
	}

	/**
	 * 获取过磅单号
	 *
	 * @return weighing_serial_no - 过磅单号
	 */
	@FieldDef(label = "过磅单号", maxLength = 20)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getWeighingSerialNo() {
		return weighingSerialNo;
	}

	/**
	 * 设置过磅单号
	 *
	 * @param weighingSerialNo 过磅单号
	 */
	public void setWeighingSerialNo(String weighingSerialNo) {
		this.weighingSerialNo = weighingSerialNo;
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
	 * 获取买方实际支付金额（分）
	 *
	 * @return buyer_actual_amount - 买方实际支付金额（分）
	 */
	@FieldDef(label = "买方实际支付金额（分）")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getBuyerActualAmount() {
		return buyerActualAmount;
	}

	/**
	 * 设置买方实际支付金额（分）
	 *
	 * @param buyerActualAmount 买方实际支付金额（分）
	 */
	public void setBuyerActualAmount(Long buyerActualAmount) {
		this.buyerActualAmount = buyerActualAmount;
	}

	/**
	 * 获取买方手续费(分)
	 *
	 * @return buyer_poundage - 买方手续费(分)
	 */
	@FieldDef(label = "买方手续费(分)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getBuyerPoundage() {
		return buyerPoundage;
	}

	/**
	 * 设置买方手续费(分)
	 *
	 * @param buyerPoundage 买方手续费(分)
	 */
	public void setBuyerPoundage(Long buyerPoundage) {
		this.buyerPoundage = buyerPoundage;
	}

	public String getBuyerPoundagePayOrderNo() {
		return buyerPoundagePayOrderNo;
	}

	public void setBuyerPoundagePayOrderNo(String buyerPoundagePayOrderNo) {
		this.buyerPoundagePayOrderNo = buyerPoundagePayOrderNo;
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
	 * 获取卖家姓名
	 *
	 * @return seller_name - 卖家姓名
	 */
	@FieldDef(label = "卖家姓名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
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
	 * 获取卖方实收金额（分）
	 *
	 * @return seller_actual_amount - 卖方实收金额（分）
	 */
	@FieldDef(label = "卖方实收金额（分）")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getSellerActualAmount() {
		return sellerActualAmount;
	}

	/**
	 * 设置卖方实收金额（分）
	 *
	 * @param sellerActualAmount 卖方实收金额（分）
	 */
	public void setSellerActualAmount(Long sellerActualAmount) {
		this.sellerActualAmount = sellerActualAmount;
	}

	/**
	 * 获取卖家手续费
	 *
	 * @return seller_poundage - 卖家手续费
	 */
	@FieldDef(label = "卖家手续费")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getSellerPoundage() {
		return sellerPoundage;
	}

	/**
	 * 设置卖家手续费
	 *
	 * @param sellerPoundage 卖家手续费
	 */
	public void setSellerPoundage(Long sellerPoundage) {
		this.sellerPoundage = sellerPoundage;
	}

	public String getSellerPoundagePayOrderNo() {
		return sellerPoundagePayOrderNo;
	}

	public void setSellerPoundagePayOrderNo(String sellerPoundagePayOrderNo) {
		this.sellerPoundagePayOrderNo = sellerPoundagePayOrderNo;
	}

	/**
	 * 获取交易金额(分)
	 *
	 * @return trade_amount - 交易金额(分)
	 */
	@FieldDef(label = "交易金额(分)")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getTradeAmount() {
		return tradeAmount;
	}

	/**
	 * 设置交易金额(分)
	 *
	 * @param tradeAmount 交易金额(分)
	 */
	public void setTradeAmount(Long tradeAmount) {
		this.tradeAmount = tradeAmount;
	}

	public Long getCollectionRecordId() {
		return collectionRecordId;
	}

	public void setCollectionRecordId(Long collectionRecordId) {
		this.collectionRecordId = collectionRecordId;
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

	public Long getStaffCharges() {
		return staffCharges;
	}

	public void setStaffCharges(Long staffCharges) {
		this.staffCharges = staffCharges;
	}

	public Long getPackingCharges() {
		return packingCharges;
	}

	public void setPackingCharges(Long packingCharges) {
		this.packingCharges = packingCharges;
	}

	public Long getCollectionCharges() {
		return collectionCharges;
	}

	public void setCollectionCharges(Long collectionCharges) {
		this.collectionCharges = collectionCharges;
	}

	/**
	 * 获取回款状态：1待回款，2已回款
	 *
	 * @return payment_state - 回款状态：1待回款，2已回款
	 */
	@FieldDef(label = "回款状态：1待回款，2已回款")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Integer getPaymentState() {
		return paymentState;
	}

	/**
	 * 设置回款状态：1待回款，2已回款
	 *
	 * @param paymentState 回款状态：1待回款，2已回款
	 */
	public void setPaymentState(Integer paymentState) {
		this.paymentState = paymentState;
	}

	/**
	 * 获取结算方式：1园区卡，2赊销
	 *
	 * @return payment_type - 结算方式：1园区卡，2赊销
	 */
	@FieldDef(label = "结算方式：1园区卡，2赊销")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Integer getPaymentType() {
		return paymentType;
	}

	/**
	 * 设置结算方式：1园区卡，2赊销
	 *
	 * @param paymentType 结算方式：1园区卡，2赊销
	 */
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * 获取状态
	 *
	 * @return state - 状态
	 */
	@FieldDef(label = "状态")
	@EditMode(editor = FieldEditor.Text, required = true)
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
	 * 获取支付订单号
	 *
	 * @return pay_order_no - 支付订单号
	 */
	@FieldDef(label = "支付订单号", maxLength = 40)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getPayOrderNo() {
		return payOrderNo;
	}

	/**
	 * 设置支付订单号
	 *
	 * @param payOrderNo 支付订单号
	 */
	public void setPayOrderNo(String payOrderNo) {
		this.payOrderNo = payOrderNo;
	}

	/**
	 * 获取冻结订单号
	 *
	 * @return frozen_order_no - 冻结订单号
	 */
	@FieldDef(label = "冻结订单号", maxLength = 40)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getFrozenOrderNo() {
		return frozenOrderNo;
	}

	/**
	 * 设置冻结订单号
	 *
	 * @param frozenOrderNo 冻结订单号
	 */
	public void setFrozenOrderNo(String frozenOrderNo) {
		this.frozenOrderNo = frozenOrderNo;
	}

	/**
	 * 获取创建人id
	 *
	 * @return creator_id - 创建人id
	 */
	@FieldDef(label = "创建人id")
	@EditMode(editor = FieldEditor.Number, required = false)
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

	/**
	 * 获取更新时间
	 *
	 * @return modified_time - 更新时间
	 */
	@FieldDef(label = "更新时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public LocalDateTime getModifiedTime() {
		return modifiedTime;
	}

	/**
	 * 设置更新时间
	 *
	 * @param modifiedTime 更新时间
	 */
	public void setModifiedTime(LocalDateTime modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	/**
	 * 获取版本号，事务控制
	 *
	 * @return version - 版本号，事务控制
	 */
	@FieldDef(label = "版本号，事务控制")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Integer getVersion() {
		return version;
	}

	/**
	 * 设置版本号，事务控制
	 *
	 * @param version 版本号，事务控制
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * 获取最后操作时间
	 *
	 * @return last_operation_time - 最后操作时间
	 */
	@FieldDef(label = "最后操作时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public LocalDateTime getLastOperationTime() {
		return lastOperationTime;
	}

	/**
	 * 设置最后操作时间
	 *
	 * @param lastOperationTime 最后操作时间
	 */
	public void setLastOperationTime(LocalDateTime lastOperationTime) {
		this.lastOperationTime = lastOperationTime;
	}

	/**
	 * 获取最后操作员id
	 *
	 * @return last_operator_id - 最后操作员id
	 */
	@FieldDef(label = "最后操作员id")
	@EditMode(editor = FieldEditor.Number, required = false)
	public Long getLastOperatorId() {
		return lastOperatorId;
	}

	/**
	 * 设置最后操作员id
	 *
	 * @param lastOperatorId 最后操作员id
	 */
	public void setLastOperatorId(Long lastOperatorId) {
		this.lastOperatorId = lastOperatorId;
	}

	/**
	 * 获取最后操作员名称
	 *
	 * @return last_operator_name - 最后操作员名称
	 */
	@FieldDef(label = "最后操作员名称", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getLastOperatorName() {
		return lastOperatorName;
	}

	/**
	 * 设置最后操作员名称
	 *
	 * @param lastOperatorName 最后操作员名称
	 */
	public void setLastOperatorName(String lastOperatorName) {
		this.lastOperatorName = lastOperatorName;
	}

	/**
	 * 获取最后操作员工号
	 *
	 * @return last_operator_user_name - 最后操作员工号
	 */
	@FieldDef(label = "最后操作员工号", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getLastOperatorUserName() {
		return lastOperatorUserName;
	}

	/**
	 * 设置最后操作员工号
	 *
	 * @param lastOperatorUserName 最后操作员工号
	 */
	public void setLastOperatorUserName(String lastOperatorUserName) {
		this.lastOperatorUserName = lastOperatorUserName;
	}
}