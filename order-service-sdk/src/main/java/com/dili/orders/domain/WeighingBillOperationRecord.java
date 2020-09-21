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

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-09-21 11:43:17.
 */
@Table(name = "`weighing_bill_operation_record`")
public class WeighingBillOperationRecord extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Column(name = "`statement_id`")
    private Long statementId;

    /**
     * 结算单号
     */
    @Column(name = "`statement_serial_no`")
    private String statementSerialNo;

    /**
     * 操作类型
     */
    @Column(name = "`operation_type`")
    private Integer operationType;

    /**
     * 操作类型名称
     */
    @Column(name = "`operation_type_name`")
    private String operationTypeName;

    /**
     * 操作人id
     */
    @Column(name = "`operator_id`")
    private Long operatorId;

    /**
     * 操作人用户名
     */
    @Column(name = "`operator_user_name`")
    private String operatorUserName;

    /**
     * 操作员姓名
     */
    @Column(name = "`operator_name`")
    private String operatorName;

    /**
     * 操作时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "`operation_time`")
    private LocalDateTime operationTime;

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
    @EditMode(editor = FieldEditor.Text, required = false)
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
    @FieldDef(label="结算单id")
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
    @FieldDef(label="结算单号", maxLength = 50)
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
     * 获取操作类型
     *
     * @return operation_type - 操作类型
     */
    @FieldDef(label="操作类型")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getOperationType() {
        return operationType;
    }

    /**
     * 设置操作类型
     *
     * @param operationType 操作类型
     */
    public void setOperationType(Integer operationType) {
        this.operationType = operationType;
    }

    /**
     * 获取操作类型名称
     *
     * @return operation_type_name - 操作类型名称
     */
    @FieldDef(label="操作类型名称", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperationTypeName() {
        return operationTypeName;
    }

    /**
     * 设置操作类型名称
     *
     * @param operationTypeName 操作类型名称
     */
    public void setOperationTypeName(String operationTypeName) {
        this.operationTypeName = operationTypeName;
    }

    /**
     * 获取操作人id
     *
     * @return operator_id - 操作人id
     */
    @FieldDef(label="操作人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getOperatorId() {
        return operatorId;
    }

    /**
     * 设置操作人id
     *
     * @param operatorId 操作人id
     */
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * 获取操作人用户名
     *
     * @return operator_user_name - 操作人用户名
     */
    @FieldDef(label="操作人用户名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getOperatorUserName() {
        return operatorUserName;
    }

    /**
     * 设置操作人用户名
     *
     * @param operatorUserName 操作人用户名
     */
    public void setOperatorUserName(String operatorUserName) {
        this.operatorUserName = operatorUserName;
    }

    /**
     * 获取操作员姓名
     *
     * @return operator_name - 操作员姓名
     */
    @FieldDef(label="操作员姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
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
     * 获取操作时间
     *
     * @return operation_time - 操作时间
     */
    @FieldDef(label="操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    /**
     * 设置操作时间
     *
     * @param operationTime 操作时间
     */
    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
}