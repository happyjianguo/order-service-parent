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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 回款记录表
 * This file was generated on 2021-01-25 14:49:04.
 */
@Table(name = "`collection_record`")
public class CollectionRecord extends BaseDomain {

    /**
     * id集合字符串形式
     */
    private String ids;

    /**
     * 根据ids判断操作是否可行
     */
    @Transient
    private List<Long> collectionRecordIds = new ArrayList<>();

    /**
     * 市场id
     */
    @Column(name = "`market_id`")
    private Long marketId;

    /**
     * 批量回款日期查询
     */
    @Transient
    private List<LocalDate> batchCollectionDate = new ArrayList<>();


    /**
     * 查询开始时间
     */
    @Transient
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginDateTime;

    /**
     * 查询结束时间
     */
    @Transient
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    /**
     * 回款单主键
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 回款单code
     */
    @Column(name = "`code`")
    private String code;

    /**
     * 回款操作时间
     */
    @Column(name = "`operation_time`")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operationTime;

    /**
     * 查询使用，当做客户的id使用
     */
    @Column(name = "`account_seller_id`")
    private Long accountSellerId;

    /**
     * 查询使用，当做客户的id使用
     */
    @Column(name = "`account_buyer_id`")
    private Long accountBuyerId;

    /**
     * 交易结算日期
     */
    @Column(name = "`settlement_date`")
    private String settlementDate;

    /**
     * 买方客户id
     */
    @Column(name = "`buyer_id`")
    private Long buyerId;

    /**
     * 买家客户名称
     */
    @Column(name = "`buyer_name`")
    private String buyerName;

    /**
     * 买家客户卡号
     */
    @Column(name = "`buyer_card_no`")
    private String buyerCardNo;

    /**
     * 卖家客户id
     */
    @Column(name = "`seller_id`")
    private Long sellerId;

    /**
     * 卖家客户名称
     */
    @Column(name = "`seller_name`")
    private String sellerName;

    /**
     * 卖家卡号
     */
    @Column(name = "`seller_card_no`")
    private String sellerCardNo;

    /**
     * 应回款金额
     */
    @Column(name = "`amount_receivables`")
    private Long amountReceivables;

    /**
     * 实回款金额
     */
    @Column(name = "`amount_actually`")
    private Long amountActually;

    /**
     * 回款途径
     */
    @Column(name = "`payment_ways`")
    private Integer paymentWays;

    /**
     * 代付卡号
     */
    @Column(name = "`payment_card_number`")
    private String paymentCardNumber;

    /**
     * 备注
     */
    @Column(name = "`remark`")
    private String remark;

    /**
     * 操作员id
     */
    @Column(name = "`operation_id`")
    private Long operationId;

    /**
     * 操作员姓名
     */
    @Column(name = "`operation_name`")
    private String operationName;

    /**
     * 操作员工号
     */
    @Column(name = "`operation_user_name`")
    private String operationUserName;

    /**
     * 操作员所属部门id
     */
    @Column(name = "`operation_department_id`")
    private Long operationDepartmentId;

    /**
     * 操作员所属部门名称
     */
    @Column(name = "`operation_department_name`")
    private String operationDepartmentName;

    /**
     * 乐观锁
     */
    @Version
    @Column(name = "`version`")
    private Integer version;

    /**
     * 数据权限部门，可以存在多个
     */
    @Transient
    private List<Long> departmentIds = new ArrayList<>();

    /**
     * 交易支付流水
     */
    @Column(name = "`payment_no`")
    private String paymentNo;

    /**
     * 交易流水回传的时间
     */
    @Column(name = "`pay_time`")
    private LocalDateTime payTime;


    /**
     * 获取回款单主键
     *
     * @return id - 回款单主键
     */
    @FieldDef(label = "回款单主键")
    @EditMode(editor = FieldEditor.Number, required = true)

    public Long getId() {
        return id;
    }

    /**
     * 设置回款单主键
     *
     * @param id 回款单主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取回款单code
     *
     * @return code - 回款单code
     */
    @FieldDef(label = "回款单code", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCode() {
        return code;
    }

    /**
     * 设置回款单code
     *
     * @param code 回款单code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取回款操作时间
     *
     * @return operation_time - 回款操作时间
     */
    @FieldDef(label = "回款操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    /**
     * 设置回款操作时间
     *
     * @param operationTime 回款操作时间
     */
    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    /**
     * 获取交易结算日期
     *
     * @return settlement_date - 交易结算日期
     */
    @FieldDef(label = "交易结算日期", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getSettlementDate() {
        return settlementDate;
    }

    /**
     * 设置交易结算日期
     *
     * @param settlementDate 交易结算日期
     */
    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    /**
     * 获取买方客户id
     *
     * @return buyer_id - 买方客户id
     */
    @FieldDef(label = "买方客户id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getBuyerId() {
        return buyerId;
    }

    /**
     * 设置买方客户id
     *
     * @param buyerId 买方客户id
     */
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    /**
     * 获取买家客户名称
     *
     * @return buyer_name - 买家客户名称
     */
    @FieldDef(label = "买家客户名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * 设置买家客户名称
     *
     * @param buyerName 买家客户名称
     */
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    /**
     * 获取买家客户卡号
     *
     * @return buyer_card_no - 买家客户卡号
     */
    @FieldDef(label = "买家客户卡号", maxLength = 0)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getBuyerCardNo() {
        return buyerCardNo;
    }

    /**
     * 设置买家客户卡号
     *
     * @param buyerCardNo 买家客户卡号
     */
    public void setBuyerCardNo(String buyerCardNo) {
        this.buyerCardNo = buyerCardNo;
    }

    /**
     * 获取卖家客户id
     *
     * @return seller_id - 卖家客户id
     */
    @FieldDef(label = "卖家客户id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getSellerId() {
        return sellerId;
    }

    /**
     * 设置卖家客户id
     *
     * @param sellerId 卖家客户id
     */
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * 获取卖家客户名称
     *
     * @return seller_name - 卖家客户名称
     */
    @FieldDef(label = "卖家客户名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getSellerName() {
        return sellerName;
    }

    /**
     * 设置卖家客户名称
     *
     * @param sellerName 卖家客户名称
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /**
     * 获取卖家卡号
     *
     * @return seller_card_no - 卖家卡号
     */
    @FieldDef(label = "卖家卡号", maxLength = 0)
    @EditMode(editor = FieldEditor.Text, required = false)
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
     * 获取应回款金额
     *
     * @return amount_receivables - 应回款金额
     */
    @FieldDef(label = "应回款金额")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getAmountReceivables() {
        return amountReceivables;
    }

    /**
     * 设置应回款金额
     *
     * @param amountReceivables 应回款金额
     */
    public void setAmountReceivables(Long amountReceivables) {
        this.amountReceivables = amountReceivables;
    }

    /**
     * 获取实回款金额
     *
     * @return amount_actually - 实回款金额
     */
    @FieldDef(label = "实回款金额")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getAmountActually() {
        return amountActually;
    }

    /**
     * 设置实回款金额
     *
     * @param amountActually 实回款金额
     */
    public void setAmountActually(Long amountActually) {
        this.amountActually = amountActually;
    }

    /**
     * 获取回款途径
     *
     * @return payment_ways - 回款途径
     */
    @FieldDef(label = "回款途径")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getPaymentWays() {
        return paymentWays;
    }

    /**
     * 设置回款途径
     *
     * @param paymentWays 回款途径
     */
    public void setPaymentWays(Integer paymentWays) {
        this.paymentWays = paymentWays;
    }

    /**
     * 获取代付卡号
     *
     * @return payment_card_number - 代付卡号
     */
    @FieldDef(label = "代付卡号", maxLength = 0)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getPaymentCardNumber() {
        return paymentCardNumber;
    }

    /**
     * 设置代付卡号
     *
     * @param paymentCardNumber 代付卡号
     */
    public void setPaymentCardNumber(String paymentCardNumber) {
        this.paymentCardNumber = paymentCardNumber;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    @FieldDef(label = "备注", maxLength = 255)
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
     * 获取操作员id
     *
     * @return operation_id - 操作员id
     */
    @FieldDef(label = "操作员id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getOperationId() {
        return operationId;
    }

    /**
     * 设置操作员id
     *
     * @param operationId 操作员id
     */
    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    /**
     * 获取操作员姓名
     *
     * @return operation_name - 操作员姓名
     */
    @FieldDef(label = "操作员姓名", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperationName() {
        return operationName;
    }

    /**
     * 设置操作员姓名
     *
     * @param operationName 操作员姓名
     */
    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    /**
     * 获取操作员所属部门id
     *
     * @return operation_department_id - 操作员所属部门id
     */
    @FieldDef(label = "操作员所属部门id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getOperationDepartmentId() {
        return operationDepartmentId;
    }

    /**
     * 设置操作员所属部门id
     *
     * @param operationDepartmentId 操作员所属部门id
     */
    public void setOperationDepartmentId(Long operationDepartmentId) {
        this.operationDepartmentId = operationDepartmentId;
    }

    /**
     * 获取操作员所属部门名称
     *
     * @return operation_department_name - 操作员所属部门名称
     */
    @FieldDef(label = "操作员所属部门名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperationDepartmentName() {
        return operationDepartmentName;
    }

    /**
     * 设置操作员所属部门名称
     *
     * @param operationDepartmentName 操作员所属部门名称
     */
    public void setOperationDepartmentName(String operationDepartmentName) {
        this.operationDepartmentName = operationDepartmentName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public LocalDateTime getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(LocalDateTime beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Long getAccountSellerId() {
        return accountSellerId;
    }

    public void setAccountSellerId(Long accountSellerId) {
        this.accountSellerId = accountSellerId;
    }

    public Long getAccountBuyerId() {
        return accountBuyerId;
    }

    public void setAccountBuyerId(Long accountBuyerId) {
        this.accountBuyerId = accountBuyerId;
    }

    public List<LocalDate> getBatchCollectionDate() {
        return batchCollectionDate;
    }

    public void setBatchCollectionDate(List<LocalDate> batchCollectionDate) {
        this.batchCollectionDate = batchCollectionDate;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getOperationUserName() {
        return operationUserName;
    }

    public void setOperationUserName(String operationUserName) {
        this.operationUserName = operationUserName;
    }

    public List<Long> getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(List<Long> departmentIds) {
        this.departmentIds = departmentIds;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public List<Long> getCollectionRecordIds() {
        return collectionRecordIds;
    }

    public void setCollectionRecordIds(List<Long> collectionRecordIds) {
        this.collectionRecordIds = collectionRecordIds;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
        if (StringUtils.isNotBlank(ids)) {
            this.collectionRecordIds = Arrays.asList(ids.split(",")).stream().map(Long::valueOf).collect(Collectors.toList());
        }
    }
}