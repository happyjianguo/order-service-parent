package com.dili.orders.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-09-27 18:12:47.
 */
@Table(name = "`weighing_bill_agent_info`")
public class WeighingBillAgentInfo extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    /**
     * 买家代理人编码
     */
    @Column(name = "`buyer_agent_code`")
    private String buyerAgentCode;

    /**
     * 买家代理人身份类型
     */
    @Column(name = "`buyer_agent_type`")
    private String buyerAgentType;

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

    /**
     * 卖家代理人编码
     */
    @Column(name = "`seller_agent_code`")
    private String sellerAgentCode;

    /**
     * 卖家代理人身份类型
     */
    @Column(name = "`seller_agent_type`")
    private String sellerAgentType;

    /**
     * 过磅单id
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
    @Column(name = "`weighing_statement_id`")
    private Long weighingStatementId;

    /**
     * 结算单编号
     */
    @Column(name = "`weighing_statement_serial_no`")
    private String weighingStatementSerialNo;

    /**
     * @return id
     */
    @FieldDef(label="id")
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
     * 获取买家代理人id
     *
     * @return buyer_agent_id - 买家代理人id
     */
    @FieldDef(label="买家代理人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getBuyerAgentId() {
        return buyerAgentId;
    }

    /**
     * 设置买家代理人id
     *
     * @param buyerAgentId 买家代理人id
     */
    public void setBuyerAgentId(Long buyerAgentId) {
        this.buyerAgentId = buyerAgentId;
    }

    /**
     * 获取买家代理人姓名
     *
     * @return buyer_agent_name - 买家代理人姓名
     */
    @FieldDef(label="买家代理人姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getBuyerAgentName() {
        return buyerAgentName;
    }

    /**
     * 设置买家代理人姓名
     *
     * @param buyerAgentName 买家代理人姓名
     */
    public void setBuyerAgentName(String buyerAgentName) {
        this.buyerAgentName = buyerAgentName;
    }

    /**
     * 获取买家代理人编码
     *
     * @return buyer_agent_code - 买家代理人编码
     */
    @FieldDef(label="买家代理人编码", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getBuyerAgentCode() {
        return buyerAgentCode;
    }

    /**
     * 设置买家代理人编码
     *
     * @param buyerAgentCode 买家代理人编码
     */
    public void setBuyerAgentCode(String buyerAgentCode) {
        this.buyerAgentCode = buyerAgentCode;
    }

    /**
     * 获取买家代理人身份类型
     *
     * @return buyer_agent_type - 买家代理人身份类型
     */
    @FieldDef(label="买家代理人身份类型", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getBuyerAgentType() {
        return buyerAgentType;
    }

    /**
     * 设置买家代理人身份类型
     *
     * @param buyerAgentType 买家代理人身份类型
     */
    public void setBuyerAgentType(String buyerAgentType) {
        this.buyerAgentType = buyerAgentType;
    }

    /**
     * 获取卖家代理人id
     *
     * @return seller_agent_id - 卖家代理人id
     */
    @FieldDef(label="卖家代理人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getSellerAgentId() {
        return sellerAgentId;
    }

    /**
     * 设置卖家代理人id
     *
     * @param sellerAgentId 卖家代理人id
     */
    public void setSellerAgentId(Long sellerAgentId) {
        this.sellerAgentId = sellerAgentId;
    }

    /**
     * 获取卖家代理人姓名
     *
     * @return seller_agent_name - 卖家代理人姓名
     */
    @FieldDef(label="卖家代理人姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSellerAgentName() {
        return sellerAgentName;
    }

    /**
     * 设置卖家代理人姓名
     *
     * @param sellerAgentName 卖家代理人姓名
     */
    public void setSellerAgentName(String sellerAgentName) {
        this.sellerAgentName = sellerAgentName;
    }

    /**
     * 获取卖家代理人编码
     *
     * @return seller_agent_code - 卖家代理人编码
     */
    @FieldDef(label="卖家代理人编码", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSellerAgentCode() {
        return sellerAgentCode;
    }

    /**
     * 设置卖家代理人编码
     *
     * @param sellerAgentCode 卖家代理人编码
     */
    public void setSellerAgentCode(String sellerAgentCode) {
        this.sellerAgentCode = sellerAgentCode;
    }

    /**
     * 获取卖家代理人身份类型
     *
     * @return seller_agent_type - 卖家代理人身份类型
     */
    @FieldDef(label="卖家代理人身份类型", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSellerAgentType() {
        return sellerAgentType;
    }

    /**
     * 设置卖家代理人身份类型
     *
     * @param sellerAgentType 卖家代理人身份类型
     */
    public void setSellerAgentType(String sellerAgentType) {
        this.sellerAgentType = sellerAgentType;
    }

    /**
     * 获取过磅单id
     *
     * @return weighing_bill_id - 过磅单id
     */
    @FieldDef(label="过磅单id")
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
     * @return weighing_bill_serial_no - 过磅单号
     */
    @FieldDef(label="过磅单号", maxLength = 50)
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
     * @return weighing_statement_id - 结算单id
     */
    @FieldDef(label="结算单id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getWeighingStatementId() {
        return weighingStatementId;
    }

    /**
     * 设置结算单id
     *
     * @param weighingStatementId 结算单id
     */
    public void setWeighingStatementId(Long weighingStatementId) {
        this.weighingStatementId = weighingStatementId;
    }

    /**
     * 获取结算单编号
     *
     * @return weighing_statement_serial_no - 结算单编号
     */
    @FieldDef(label="结算单编号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getWeighingStatementSerialNo() {
        return weighingStatementSerialNo;
    }

    /**
     * 设置结算单编号
     *
     * @param weighingStatementSerialNo 结算单编号
     */
    public void setWeighingStatementSerialNo(String weighingStatementSerialNo) {
        this.weighingStatementSerialNo = weighingStatementSerialNo;
    }
}