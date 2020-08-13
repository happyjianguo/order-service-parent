package com.dili.orders.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-08-11 17:31:37.
 */
@Table(name = "`comprehensive_fee`")
public class ComprehensiveFee extends BaseDomain {
    /**
     * 综合收费id
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 单据编号
     */
    @Column(name = "`code`")
    private String code;

    /**
     * 客户id
     */
    @Column(name = "`customer_id`")
    private Long customerId;

    /**
     * 客户编号
     */
    @Column(name = "`customer_code`")
    private String customerCode;

    /**
     * 客户名称
     */
    @Column(name = "`customer_name`")
    private String customerName;

    /**
     * 客户卡号
     */
    @Column(name = "`customer_card_no`")
    private String customerCardNo;

    /**
     * 检测数量
     */
    @Column(name = "`inspection_num`")
    private Integer inspectionNum;

    /**
     * 缴费金额
     */
    @Column(name = "`charge_amount`")
    private Long chargeAmount;

    /**
     * 车牌号
     */
    @Column(name = "`plate`")
    private String plate;

    /**
     * 单据类型
     */
    @Column(name = "`order_type`")
    private Integer orderType;

    /**
     * 单据状态
     */
    @Column(name = "`order_status`")
    private Integer orderStatus;

    /**
     * 检测商品
     */
    @Column(name = "`inspection_item`")
    private String inspectionItem;

    /**
     * 备注
     */
    @Column(name = "`remark`")
    private String remark;

    /**
     * 结算员id
     */
    @Column(name = "`operator_id`")
    private Long operatorId;

    /**
     * 结算员名称
     */
    @Column(name = "`operator_name`")
    private String operatorName;

    /**
     * 结算时间
     */
    @Column(name = "`operator_time`")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operatorTime;

    /**
     * 撤销员id
     */
    @Column(name = "`revocator_id`")
    private Long revocatorId;

    /**
     * 撤销员
     */
    @Column(name = "`revocator_name`")
    private String revocatorName;

    /**
     * 撤销时间
     */
    @Column(name = "`revocator_time`")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime revocatorTime;

    /**
     * 创建时间
     */
    @Column(name = "`created_time`")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 创建人id
     */
    @Column(name = "`creator_id`")
    private Long creatorId;

    /**
     * 修改时间
     */
    @Column(name = "`modified_time`")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;

    /**
     * 修改人id
     */
    @Column(name = "`modifier_id`")
    private Long modifierId;

    /**
     * 版本号
     */
    @Column(name = "`version`")
    private Integer version;

    /**
     * 查询使用，用户id
     */
    @Transient
    private Long userId;


    /**
     * 查询使用，结算开始时间
     */
    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operatorTimeStart;

    /**
     * 查询使用，结算结束时间
     */
    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operatorTimeEnd;

    /**
     * 获取综合收费id
     *
     * @return id - 综合收费id
     */
    @FieldDef(label="综合收费id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * 设置综合收费id
     *
     * @param id 综合收费id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取单据编号
     *
     * @return code - 单据编号
     */
    @FieldDef(label="单据编号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCode() {
        return code;
    }

    /**
     * 设置单据编号
     *
     * @param code 单据编号
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取客户id
     *
     * @return customer_id - 客户id
     */
    @FieldDef(label="客户id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * 设置客户id
     *
     * @param customerId 客户id
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * 获取客户编号
     *
     * @return customer_code - 客户编号
     */
    @FieldDef(label="客户编号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCustomerCode() {
        return customerCode;
    }

    /**
     * 设置客户编号
     *
     * @param customerCode 客户编号
     */
    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    /**
     * 获取客户名称
     *
     * @return customer_name - 客户名称
     */
    @FieldDef(label="客户名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCustomerName() {
        return customerName;
    }

    /**
     * 设置客户名称
     *
     * @param customerName 客户名称
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 获取客户卡号
     *
     * @return customer_card_no - 客户卡号
     */
    @FieldDef(label="客户卡号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCustomerCardNo() {
        return customerCardNo;
    }

    /**
     * 设置客户卡号
     *
     * @param customerCardNo 客户卡号
     */
    public void setCustomerCardNo(String customerCardNo) {
        this.customerCardNo = customerCardNo;
    }

    /**
     * 获取检测数量
     *
     * @return inspection_num - 检测数量
     */
    @FieldDef(label="检测数量")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getInspectionNum() {
        return inspectionNum;
    }

    /**
     * 设置检测数量
     *
     * @param inspectionNum 检测数量
     */
    public void setInspectionNum(Integer inspectionNum) {
        this.inspectionNum = inspectionNum;
    }

    /**
     * 获取缴费金额
     *
     * @return charge_amount - 缴费金额
     */
    @FieldDef(label="缴费金额")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getChargeAmount() {
        return chargeAmount;
    }

    /**
     * 设置缴费金额
     *
     * @param chargeAmount 缴费金额
     */
    public void setChargeAmount(Long chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    /**
     * 获取车牌号
     *
     * @return plate - 车牌号
     */
    @FieldDef(label="车牌号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getPlate() {
        return plate;
    }

    /**
     * 设置车牌号
     *
     * @param plate 车牌号
     */
    public void setPlate(String plate) {
        this.plate = plate;
    }

    /**
     * 获取单据类型
     *
     * @return order_type - 单据类型
     */
    @FieldDef(label="单据类型")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getOrderType() {
        return orderType;
    }

    /**
     * 设置单据类型
     *
     * @param orderType 单据类型
     */
    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    /**
     * 获取单据状态
     *
     * @return order_status - 单据状态
     */
    @FieldDef(label="单据状态")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getOrderStatus() {
        return orderStatus;
    }

    /**
     * 设置单据状态
     *
     * @param orderStatus 单据状态
     */
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * 获取检测商品
     *
     * @return inspection_item - 检测商品
     */
    @FieldDef(label="检测商品", maxLength = 2000)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getInspectionItem() {
        return inspectionItem;
    }

    /**
     * 设置检测商品
     *
     * @param inspectionItem 检测商品
     */
    public void setInspectionItem(String inspectionItem) {
        this.inspectionItem = inspectionItem;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    @FieldDef(label="备注", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取结算员id
     *
     * @return operator_id - 结算员id
     */
    @FieldDef(label="结算员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getOperatorId() {
        return operatorId;
    }

    /**
     * 设置结算员id
     *
     * @param operatorId 结算员id
     */
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * 获取结算员名称
     *
     * @return operator_name - 结算员名称
     */
    @FieldDef(label="结算员名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * 设置结算员名称
     *
     * @param operatorName 结算员名称
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * 获取结算时间
     *
     * @return operator_time - 结算时间
     */
    @FieldDef(label="结算时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getOperatorTime() {
        return operatorTime;
    }

    /**
     * 设置结算时间
     *
     * @param operatorTime 结算时间
     */
    public void setOperatorTime(LocalDateTime operatorTime) {
        this.operatorTime = operatorTime;
    }

    /**
     * 获取撤销员id
     *
     * @return revocator_id - 撤销员id
     */
    @FieldDef(label="撤销员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getRevocatorId() {
        return revocatorId;
    }

    /**
     * 设置撤销员id
     *
     * @param revocatorId 撤销员id
     */
    public void setRevocatorId(Long revocatorId) {
        this.revocatorId = revocatorId;
    }

    /**
     * 获取撤销员
     *
     * @return revocator_name - 撤销员
     */
    @FieldDef(label="撤销员", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getRevocatorName() {
        return revocatorName;
    }

    /**
     * 设置撤销员
     *
     * @param revocatorName 撤销员
     */
    public void setRevocatorName(String revocatorName) {
        this.revocatorName = revocatorName;
    }

    /**
     * 获取撤销时间
     *
     * @return revocator_time - 撤销时间
     */
    @FieldDef(label="撤销时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getRevocatorTime() {
        return revocatorTime;
    }

    /**
     * 设置撤销时间
     *
     * @param revocatorTime 撤销时间
     */
    public void setRevocatorTime(LocalDateTime revocatorTime) {
        this.revocatorTime = revocatorTime;
    }

    /**
     * 获取创建时间
     *
     * @return created_time - 创建时间
     */
    @FieldDef(label="创建时间")
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
     * 获取创建人id
     *
     * @return creator_id - 创建人id
     */
    @FieldDef(label="创建人id")
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
     * 获取修改时间
     *
     * @return modified_time - 修改时间
     */
    @FieldDef(label="修改时间")
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
     * 获取修改人id
     *
     * @return modifier_id - 修改人id
     */
    @FieldDef(label="修改人id")
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
     * 获取版本号
     *
     * @return version - 版本号
     */
    @FieldDef(label="版本号")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getVersion() {
        return version;
    }

    /**
     * 设置版本号
     *
     * @param version 版本号
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getOperatorTimeStart() {
        return operatorTimeStart;
    }

    public void setOperatorTimeStart(Date operatorTimeStart) {
        this.operatorTimeStart = operatorTimeStart;
    }

    public Date getOperatorTimeEnd() {
        return operatorTimeEnd;
    }

    public void setOperatorTimeEnd(Date operatorTimeEnd) {
        this.operatorTimeEnd = operatorTimeEnd;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}