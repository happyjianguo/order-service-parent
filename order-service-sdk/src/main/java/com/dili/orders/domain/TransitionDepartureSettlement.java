package com.dili.orders.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 转/离场结算表
 * This file was generated on 2020-06-17 08:52:43.
 */
@Table(name = "`transition_departure_settlement`")
public class TransitionDepartureSettlement extends BaseDomain {

    /**
     * 查询使用，客户id
     */
    @Transient
    private Long accountId;

    /**
     * 客户余额，打印使用
     */
    @Transient
    private String customerBalance;

    /**
     * 客户身份类型英文
     */
    @Column(name = "`customer_market_type_code`")
    private String customerMarketTypeCode;

    /**
     * 客户身份类型中文
     */
    @Column(name = "`customer_market_type_name`")
    private String customerMarketTypeName;

    /**
     * 市场id
     */
    @Column(name = "`market_id`")
    private Long marketId;
    /**
     * 单据id
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
     * 转离场申请单id
     */
    @Column(name = "`apply_id`")
    private Long applyId;

    /**
     * 转离场申请单code
     */
    @Column(name = "`apply_code`")
    private Long applyCode;

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
     * 客户所属部门id
     */
    @Column(name = "`customer_department_id`")
    private Long customerDepartmentId;

    /**
     * 客户所属部门名称
     */
    @Column(name = "`customer_department_name`")
    private String customerDepartmentName;


    /**
     * 客户所属父id
     */
    @Column(name = "`customer_parent_id`")
    private Long customerParentId;


    /**
     * 交易类型id（外省菜，省内菜等）（数据字典）
     */
    @Column(name = "`trans_type_id`")
    private String transTypeId;

    /**
     * 商品id
     */
    @Column(name = "`category_id`")
    private Long categoryId;

    /**
     * 商品名称
     */
    @Column(name = "`category_name`")
    private String categoryName;

    /**
     * 车牌号
     */
    @Column(name = "`plate`")
    private String plate;

    /**
     * 业务类型（1.转场/2.离场）
     */
    @Column(name = "`biz_type`")
    private Integer bizType;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 毛重（公斤）
     */
    @Column(name = "`gross_weight`")
    private Integer grossWeight;

    /**
     * 皮重（公斤）
     */
    @Column(name = "`tare_weight`")
    private Integer tareWeight;

    /**
     * 净重（公斤）
     */
    @Column(name = "`net_weight`")
    private Integer netWeight;

    /**
     * 收费金额
     */
    @Column(name = "`charge_amount`")
    private Long chargeAmount;

    /**
     * 支付状态（1.未结算/2.已结算/3.已撤销/4.已关闭）
     */
    @Column(name = "`pay_status`")
    private Integer payStatus;

    /**
     * 操作员id
     */
    @Column(name = "`operator_id`")
    private Long operatorId;

    /**
     * 操作员姓名
     */
    @Column(name = "`operator_name`")
    private String operatorName;

    /**
     * 操作员工号（登录用户名）
     */
    @Column(name = "`operator_code`")
    private String operatorCode;

    /**
     * 结算时间
     */
    @Column(name = "`pay_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    /**
     * 撤销员id
     */
    @Column(name = "`revocator_id`")
    private Long revocatorId;

    /**
     * 撤销员姓名
     */
    @Column(name = "`revocator_name`")
    private String revocatorName;

    /**
     * 撤销时间
     */
    @Column(name = "`revocator_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime revocatorTime;

    /**
     * 撤销原因
     */
    @Column(name = "`revocator_reason`")
    private String revocatorReason;

    /**
     * 支付单号
     */
    @Column(name = "`payment_no`")
    private String paymentNo;

    /**
     * 车辆类型编号(关联数据字典)
     */
    @Column(name = "`car_type_id`")
    private Long carTypeId;

    /**
     * 车辆类型名称(关联数据字典)
     */
    @Column(name = "`car_type_name`")
    private String carTypeName;

    /**
     * 是否删除（逻辑删除）
     */
    @Column(name = "`del`")
    private Integer del;

    /**
     * 是否删除（逻辑删除）
     */
    @Column(name = "`jmsf_id`")
    private Long jmsfId;

    /**
     * 查询使用，开始时间
     */
    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginTime;

    /**
     * 查询使用，结束时间
     */
    @Transient
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JSONField(format = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;

    /**
     * 查询使用，客户编号或客户姓名
     */
    @Transient
    private String customer;

    /**
     * 查询使用，操作员用户名（登录用户名）或姓名
     */
    @Transient
    private String operator;

    /**
     * 查询使用，用户id
     */
    @Transient
    private Long userId;

    /**
     * 查询使用，部门ids
     */
    @Transient
    private List<Integer> departments;


    /**
     * 获取单据id
     *
     * @return id - 单据id
     */
    @FieldDef(label = "单据id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * 设置单据id
     *
     * @param id 单据id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取单据编号
     *
     * @return code - 单据编号
     */
    @FieldDef(label = "单据编号", maxLength = 20)
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
     * 获取转离场申请单id
     *
     * @return apply_id - 转离场申请单id
     */
    @FieldDef(label = "转离场申请单id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getApplyId() {
        return applyId;
    }

    /**
     * 设置转离场申请单id
     *
     * @param applyId 转离场申请单id
     */
    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    /**
     * 获取客户id
     *
     * @return customer_id - 客户id
     */
    @FieldDef(label = "客户id")
    @EditMode(editor = FieldEditor.Number, required = false)
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
    @FieldDef(label = "客户编号", maxLength = 20)
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
    @FieldDef(label = "客户名称", maxLength = 20)
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
    @FieldDef(label = "客户卡号", maxLength = 20)
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
     * 获取客户所属部门id
     *
     * @return customer_department_id - 客户所属部门id
     */
    @FieldDef(label = "客户所属部门id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getCustomerDepartmentId() {
        return customerDepartmentId;
    }

    /**
     * 设置客户所属部门id
     *
     * @param customerDepartmentId 客户所属部门id
     */
    public void setCustomerDepartmentId(Long customerDepartmentId) {
        this.customerDepartmentId = customerDepartmentId;
    }

    /**
     * 获取客户所属部门名称
     *
     * @return customer_department_name - 客户所属部门名称
     */
    @FieldDef(label = "客户所属部门名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCustomerDepartmentName() {
        return customerDepartmentName;
    }

    /**
     * 设置客户所属部门名称
     *
     * @param customerDepartmentName 客户所属部门名称
     */
    public void setCustomerDepartmentName(String customerDepartmentName) {
        this.customerDepartmentName = customerDepartmentName;
    }

    /**
     * 获取交易类型id（外省菜，省内菜等）（数据字典）
     *
     * @return trans_type_id - 交易类型id（外省菜，省内菜等）（数据字典）
     */
    @FieldDef(label = "交易类型id（外省菜，省内菜等）（数据字典）", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getTransTypeId() {
        return transTypeId;
    }

    /**
     * 设置交易类型id（外省菜，省内菜等）（数据字典）
     *
     * @param transTypeId 交易类型id（外省菜，省内菜等）（数据字典）
     */
    public void setTransTypeId(String transTypeId) {
        this.transTypeId = transTypeId;
    }

    /**
     * 获取商品id
     *
     * @return category_id - 商品id
     */
    @FieldDef(label = "商品id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * 设置商品id
     *
     * @param categoryId 商品id
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 获取商品名称
     *
     * @return category_name - 商品名称
     */
    @FieldDef(label = "商品名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置商品名称
     *
     * @param categoryName 商品名称
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取车牌号
     *
     * @return plate - 车牌号
     */
    @FieldDef(label = "车牌号", maxLength = 20)
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
        if (StringUtils.isNotBlank(plate) && !Objects.equals(plate.trim(), "")) {
            this.plate = plate.trim();
        } else {
            this.plate = plate;
        }
    }

    /**
     * 获取业务类型（1.转场/2.离场）
     *
     * @return biz_type - 业务类型（1.转场/2.离场）
     */
    @FieldDef(label = "业务类型（1.转场/2.离场）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getBizType() {
        return bizType;
    }

    /**
     * 设置业务类型（1.转场/2.离场）
     *
     * @param bizType 业务类型（1.转场/2.离场）
     */
    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    @FieldDef(label = "修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(LocalDateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    @FieldDef(label = "创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取毛重（公斤）
     *
     * @return gross_weight - 毛重（公斤）
     */
    @FieldDef(label = "毛重（公斤）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getGrossWeight() {
        return grossWeight;
    }

    /**
     * 设置毛重（公斤）
     *
     * @param grossWeight 毛重（公斤）
     */
    public void setGrossWeight(Integer grossWeight) {
        this.grossWeight = grossWeight;
    }

    /**
     * 获取皮重（公斤）
     *
     * @return tare_weight - 皮重（公斤）
     */
    @FieldDef(label = "皮重（公斤）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getTareWeight() {
        return tareWeight;
    }

    /**
     * 设置皮重（公斤）
     *
     * @param tareWeight 皮重（公斤）
     */
    public void setTareWeight(Integer tareWeight) {
        this.tareWeight = tareWeight;
    }

    /**
     * 获取净重（公斤）
     *
     * @return net_weight - 净重（公斤）
     */
    @FieldDef(label = "净重（公斤）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getNetWeight() {
        return netWeight;
    }

    /**
     * 设置净重（公斤）
     *
     * @param netWeight 净重（公斤）
     */
    public void setNetWeight(Integer netWeight) {
        this.netWeight = netWeight;
    }

    /**
     * 获取收费金额
     *
     * @return charge_amount - 收费金额
     */
    @FieldDef(label = "收费金额")
    @EditMode(editor = FieldEditor.Text, required = false)
    public Long getChargeAmount() {
        return chargeAmount;
    }

    /**
     * 设置收费金额
     *
     * @param chargeAmount 收费金额
     */
    public void setChargeAmount(Long chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    /**
     * 获取支付状态（1.未结算/2.已结算/3.已撤销/4.已关闭）
     *
     * @return pay_status - 支付状态（1.未结算/2.已结算/3.已撤销/4.已关闭）
     */
    @FieldDef(label = "支付状态（1.未结算/2.已结算/3.已撤销/4.已关闭）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getPayStatus() {
        return payStatus;
    }

    /**
     * 设置支付状态（1.未结算/2.已结算/3.已撤销/4.已关闭）
     *
     * @param payStatus 支付状态（1.未结算/2.已结算/3.已撤销/4.已关闭）
     */
    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    /**
     * 获取操作员id
     *
     * @return operator_id - 操作员id
     */
    @FieldDef(label = "操作员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getOperatorId() {
        return operatorId;
    }

    /**
     * 设置操作员id
     *
     * @param operatorId 操作员id
     */
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * 获取操作员姓名
     *
     * @return operator_name - 操作员姓名
     */
    @FieldDef(label = "操作员姓名", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * 设置操作员姓名
     *
     * @param operatorName 操作员姓名
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * 获取操作员工号（登录用户名）
     *
     * @return operator_code - 操作员工号（登录用户名）
     */
    @FieldDef(label = "操作员工号（登录用户名）", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperatorCode() {
        return operatorCode;
    }

    /**
     * 设置操作员工号（登录用户名）
     *
     * @param operatorCode 操作员工号（登录用户名）
     */
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    /**
     * 获取结算时间
     *
     * @return pay_time - 结算时间
     */
    @FieldDef(label = "结算时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getPayTime() {
        return payTime;
    }

    /**
     * 设置结算时间
     *
     * @param payTime 结算时间
     */
    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    /**
     * 获取撤销员id
     *
     * @return revocator_id - 撤销员id
     */
    @FieldDef(label = "撤销员id")
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
     * 获取撤销员姓名
     *
     * @return revocator_name - 撤销员姓名
     */
    @FieldDef(label = "撤销员姓名", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getRevocatorName() {
        return revocatorName;
    }

    /**
     * 设置撤销员姓名
     *
     * @param revocatorName 撤销员姓名
     */
    public void setRevocatorName(String revocatorName) {
        this.revocatorName = revocatorName;
    }

    /**
     * 获取撤销时间
     *
     * @return revocator_time - 撤销时间
     */
    @FieldDef(label = "撤销时间")
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
     * 获取撤销原因
     *
     * @return revocator_reason - 撤销原因
     */
    @FieldDef(label = "撤销原因", maxLength = 60)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getRevocatorReason() {
        return revocatorReason;
    }

    /**
     * 设置撤销原因
     *
     * @param revocatorReason 撤销原因
     */
    public void setRevocatorReason(String revocatorReason) {
        this.revocatorReason = revocatorReason;
    }

    /**
     * 获取支付单号
     *
     * @return payment_no - 支付单号
     */
    @FieldDef(label = "支付单号", maxLength = 40)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getPaymentNo() {
        return paymentNo;
    }

    /**
     * 设置支付单号
     *
     * @param paymentNo 支付单号
     */
    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Long getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Long carTypeId) {
        this.carTypeId = carTypeId;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    /**
     * 获取是否删除（逻辑删除）
     *
     * @return del - 是否删除（逻辑删除）
     */
    @FieldDef(label = "是否删除（逻辑删除）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getDel() {
        return del;
    }

    /**
     * 设置是否删除（逻辑删除）
     *
     * @param del 是否删除（逻辑删除）
     */
    public void setDel(Integer del) {
        this.del = del;
    }

    public LocalDate getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDate beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Integer> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Integer> departments) {
        this.departments = departments;
    }

    public Long getApplyCode() {
        return applyCode;
    }

    public void setApplyCode(Long applyCode) {
        this.applyCode = applyCode;
    }

    public Long getJmsfId() {
        return jmsfId;
    }

    public void setJmsfId(Long jmsfId) {
        this.jmsfId = jmsfId;
    }

    public Long getCustomerParentId() {
        return customerParentId;
    }

    public void setCustomerParentId(Long customerParentId) {
        this.customerParentId = customerParentId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getCustomerMarketTypeCode() {
        return customerMarketTypeCode;
    }

    public void setCustomerMarketTypeCode(String customerMarketTypeCode) {
        this.customerMarketTypeCode = customerMarketTypeCode;
    }

    public String getCustomerMarketTypeName() {
        return customerMarketTypeName;
    }

    public void setCustomerMarketTypeName(String customerMarketTypeName) {
        this.customerMarketTypeName = customerMarketTypeName;
    }

    public String getCustomerBalance() {
        return customerBalance;
    }

    public void setCustomerBalance(String customerBalance) {
        this.customerBalance = customerBalance;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}