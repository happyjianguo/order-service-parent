package com.dili.orders.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-06-29 11:33:26.
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
     * 操作员姓名
     */
    @Column(name = "`operator_name`")
    private String operatorName;

    /**
     * 操作时间
     */
    @JsonFormat
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