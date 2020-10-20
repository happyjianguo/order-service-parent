package com.dili.orders.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import tk.mybatis.mapper.annotation.Version;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 由MyBatis Generator工具自动生成
 * 转/离场申请表
 * This file was generated on 2020-06-17 08:51:33.
 */
@Table(name = "`transition_departure_apply`")
public class TransitionDepartureApply extends BaseDomain {

    @Version
    @Column(name = "`version`")
    private Integer version;

    /**
     * 查询使用，客户id
     */
    @Transient
    private Long accountId;

    /**
     * 单据编号
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
     * 交易类型id（外省菜，省内菜等）（数据字典）
     */
    @Column(name = "`trans_type_id`")
    private String transTypeId;

    /**
     * 交易类型名称（外省菜，省内菜等）（数据字典）
     */
    @Column(name = "`trans_type_name`")
    private String transTypeName;

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
     * 发起人员id
     */
    @Column(name = "`originator_id`")
    private Long originatorId;

    /**
     * 发起人员姓名
     */
    @Column(name = "`originator_name`")
    private String originatorName;

    /**
     * 发起人工号
     */
    @Column(name = "`originator_code`")
    private String originatorCode;

    /**
     * 发起时间（即为创建时间）
     */
    @Column(name = "`originator_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime originatorTime;

    /**
     * 转离场理由
     */
    @Column(name = "`transition_departure_reason`")
    private String transitionDepartureReason;

    /**
     * 审批状态（1.待审核/2.通过/3.拒绝）默认为1
     */
    @Column(name = "`approval_state`")
    private Integer approvalState;

    /**
     * 审批人员id
     */
    @Column(name = "`approval_id`")
    private Long approvalId;

    /**
     * 审批人员姓名
     */
    @Column(name = "`approval_name`")
    private String approvalName;

    /**
     * 审批人员工号（登录用户名）
     */
    @Column(name = "`approval_code`")
    private String approvalCode;

    /**
     * 审核说明
     */
    @Column(name = "`approval_reason`")
    private String approvalReason;
    /**
     * 审批时间
     */
    @Column(name = "`approval_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;


    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyTime;

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
     * 支付状态（支付单最新状态）
     */
    @Column(name = "`pay_status`")
    private Integer payStatus;

    /**
     * 是否删除（逻辑删除）
     */
    @Column(name = "`del`")
    private Integer del;

    /**
     * 联系电话
     */
    @Column(name = "`contacts_phone`")
    private String contactsPhone;

    /**
     * 联系电话
     */
    @Column(name = "`addr`")
    private String addr;

    @Transient
    private List<TransitionDepartureSettlement> list;

    /**
     * 针对于只查询最新一条数据，直接使用这个
     */
    @Transient
    private TransitionDepartureSettlement transitionDepartureSettlement;

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
     * 查询使用，发起人用户名（登录用户名）或姓名
     */
    @Transient
    private String originator;

    /**
     * 查询使用，操作员用户名（登录用户名）或姓名
     */
    @Transient
    private String operator;


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
     * 获取发起人员id
     *
     * @return originator_id - 发起人员id
     */
    @FieldDef(label = "发起人员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getOriginatorId() {
        return originatorId;
    }

    /**
     * 设置发起人员id
     *
     * @param originatorId 发起人员id
     */
    public void setOriginatorId(Long originatorId) {
        this.originatorId = originatorId;
    }

    /**
     * 获取发起人员姓名
     *
     * @return originator_name - 发起人员姓名
     */
    @FieldDef(label = "发起人员姓名", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOriginatorName() {
        return originatorName;
    }

    /**
     * 设置发起人员姓名
     *
     * @param originatorName 发起人员姓名
     */
    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }

    /**
     * 获取发起人工号
     *
     * @return originator_code - 发起人工号
     */
    @FieldDef(label = "发起人工号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOriginatorCode() {
        return originatorCode;
    }

    /**
     * 设置发起人工号
     *
     * @param originatorCode 发起人工号
     */
    public void setOriginatorCode(String originatorCode) {
        this.originatorCode = originatorCode;
    }

    /**
     * 获取发起时间（即为创建时间）
     *
     * @return originator_time - 发起时间（即为创建时间）
     */
    @FieldDef(label = "发起时间（即为创建时间）")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public LocalDateTime getOriginatorTime() {
        return originatorTime;
    }

    /**
     * 设置发起时间（即为创建时间）
     *
     * @param originatorTime 发起时间（即为创建时间）
     */
    public void setOriginatorTime(LocalDateTime originatorTime) {
        this.originatorTime = originatorTime;
    }

    /**
     * 获取转离场理由
     *
     * @return transition_departure_reason - 转离场理由
     */
    @FieldDef(label = "转离场理由", maxLength = 60)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getTransitionDepartureReason() {
        return transitionDepartureReason;
    }

    /**
     * 设置转离场理由
     *
     * @param transitionDepartureReason 转离场理由
     */
    public void setTransitionDepartureReason(String transitionDepartureReason) {
        if (StringUtils.isNotBlank(transitionDepartureReason) && !Objects.equals(transitionDepartureReason.trim(), "")) {
            this.transitionDepartureReason = transitionDepartureReason.trim();
        } else {
            this.transitionDepartureReason = transitionDepartureReason;
        }

    }

    /**
     * 获取审批状态（1.待审核/2.通过/3.拒绝）默认为1
     *
     * @return approval_state - 审批状态（1.待审核/2.通过/3.拒绝）默认为1
     */
    @FieldDef(label = "审批状态（1.待审核/2.通过/3.拒绝）默认为1")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getApprovalState() {
        return approvalState;
    }

    /**
     * 设置审批状态（1.待审核/2.通过/3.拒绝）默认为1
     *
     * @param approvalState 审批状态（1.待审核/2.通过/3.拒绝）默认为1
     */
    public void setApprovalState(Integer approvalState) {
        this.approvalState = approvalState;
    }

    /**
     * 获取审批人员id
     *
     * @return approval_id - 审批人员id
     */
    @FieldDef(label = "审批人员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getApprovalId() {
        return approvalId;
    }

    /**
     * 设置审批人员id
     *
     * @param approvalId 审批人员id
     */
    public void setApprovalId(Long approvalId) {
        this.approvalId = approvalId;
    }

    /**
     * 获取审批人员姓名
     *
     * @return approval_name - 审批人员姓名
     */
    @FieldDef(label = "审批人员姓名", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getApprovalName() {
        return approvalName;
    }

    /**
     * 设置审批人员姓名
     *
     * @param approvalName 审批人员姓名
     */
    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }

    /**
     * 获取审批人员工号（登录用户名）
     *
     * @return approval_code - 审批人员工号（登录用户名）
     */
    @FieldDef(label = "审批人员工号（登录用户名）", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getApprovalCode() {
        return approvalCode;
    }

    /**
     * 设置审批人员工号（登录用户名）
     *
     * @param approvalCode 审批人员工号（登录用户名）
     */
    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    /**
     * 获取审核说明
     *
     * @return approval_reason - 审核说明
     */
    @FieldDef(label = "审核说明", maxLength = 60)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getApprovalReason() {
        return approvalReason;
    }

    /**
     * 设置审核说明
     *
     * @param approvalReason 审核说明
     */
    public void setApprovalReason(String approvalReason) {
        if (StringUtils.isNotBlank(approvalReason) && Objects.equals(approvalReason.trim(), "")) {
            this.approvalReason = approvalReason.trim();
        } else {
            this.approvalReason = approvalReason;
        }
    }

    /**
     * 获取审批时间
     *
     * @return approval_time - 审批时间
     */
    @FieldDef(label = "审批时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    /**
     * 设置审批时间
     *
     * @param approvalTime 审批时间
     */
    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
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
     * 获取车辆类型编号(关联数据字典)
     *
     * @return car_type_id - 车辆类型编号(关联数据字典)
     */
    @FieldDef(label = "车辆类型编号(关联数据字典)")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getCarTypeId() {
        return carTypeId;
    }

    /**
     * 设置车辆类型编号(关联数据字典)
     *
     * @param carTypeId 车辆类型编号(关联数据字典)
     */
    public void setCarTypeId(Long carTypeId) {
        this.carTypeId = carTypeId;
    }

    /**
     * 获取车辆类型名称(关联数据字典)
     *
     * @return car_type_name - 车辆类型名称(关联数据字典)
     */
    @FieldDef(label = "车辆类型名称(关联数据字典)", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCarTypeName() {
        return carTypeName;
    }

    /**
     * 设置车辆类型名称(关联数据字典)
     *
     * @param carTypeName 车辆类型名称(关联数据字典)
     */
    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    /**
     * 获取支付状态（支付单最新状态）
     *
     * @return pay_status - 支付状态（支付单最新状态）
     */
    @FieldDef(label = "支付状态（支付单最新状态）")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getPayStatus() {
        return payStatus;
    }

    /**
     * 设置支付状态（支付单最新状态）
     *
     * @param payStatus 支付状态（支付单最新状态）
     */
    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
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

    public List<TransitionDepartureSettlement> getList() {
        return list;
    }

    public void setList(List<TransitionDepartureSettlement> list) {
        this.list = list;
    }

    public TransitionDepartureSettlement getTransitionDepartureSettlement() {
        return transitionDepartureSettlement;
    }

    public void setTransitionDepartureSettlement(TransitionDepartureSettlement transitionDepartureSettlement) {
        this.transitionDepartureSettlement = transitionDepartureSettlement;
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

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
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

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        if (StringUtils.isNotBlank(addr) && !Objects.equals(addr.trim(), "")) {
            this.addr = addr.trim();
        } else {
            this.addr = addr;
        }
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTransTypeName() {
        return transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }
}