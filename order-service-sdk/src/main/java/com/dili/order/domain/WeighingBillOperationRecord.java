package com.dili.order.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-06-19 14:44:24.
 */
@Table(name = "`weighing_bill_operation_record`")
public interface WeighingBillOperationRecord extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`weighing_bill_id`")
    @FieldDef(label="过磅单id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getWeighingBillId();

    void setWeighingBillId(Long weighingBillId);

    @Column(name = "`operation_type`")
    @FieldDef(label="操作类型", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    Integer getOperationType();

    void setOperationType(Integer operationType);

    @Column(name = "`operation_type_name`")
    @FieldDef(label="操作类型名称", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getOperationTypeName();

    void setOperationTypeName(String operationTypeName);

    @Column(name = "`operator_id`")
    @FieldDef(label="操作人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getOperatorId();

    void setOperatorId(Long operatorId);

    @Column(name = "`operator_name`")
    @FieldDef(label="操作员姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getOperatorName();

    void setOperatorName(String operatorName);

    @Column(name = "`operation_time`")
    @FieldDef(label="操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getOperationTime();

    void setOperationTime(Date operationTime);
}