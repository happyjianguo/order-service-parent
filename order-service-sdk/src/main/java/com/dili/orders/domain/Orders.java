package com.dili.orders.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-06-03 09:37:03.
 */
@Table(name = "`orders`")
public interface Orders extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="主键")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`code`")
    @FieldDef(label="编码", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getCode();

    void setCode(String code);

    @Column(name = "`creator_id`")
    @FieldDef(label="创建人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getCreatorId();

    void setCreatorId(Long creatorId);

    @Column(name = "`create_time`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreateTime();

    void setCreateTime(Date createTime);

    @Column(name = "`modify_time`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModifyTime();

    void setModifyTime(Date modifyTime);

    @Column(name = "`customer_id`")
    @FieldDef(label="客户ID")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getCustomerId();

    void setCustomerId(Long customerId);

    @Column(name = "`customer_name`")
    @FieldDef(label="客户名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCustomerName();

    void setCustomerName(String customerName);

    @Column(name = "`state`")
    @FieldDef(label="状态（1：已创建 2：已取消 3：已提交 4：未生效 5：已生效 6：已停租 7：已退款 8：已过期）")
    @EditMode(editor = FieldEditor.Number, required = false)
    Boolean getState();

    void setState(Boolean state);

    @Column(name = "`pay_state`")
    @FieldDef(label="支付状态（1：未交清 2：已交清）")
    @EditMode(editor = FieldEditor.Number, required = false)
    Boolean getPayState();

    void setPayState(Boolean payState);

    @Column(name = "`total_amount`")
    @FieldDef(label="金额")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getTotalAmount();

    void setTotalAmount(Long totalAmount);

    @Column(name = "`notes`")
    @FieldDef(label="备注", maxLength = 250)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNotes();

    void setNotes(String notes);

    @Column(name = "`market_id`")
    @FieldDef(label="市场")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getMarketId();

    void setMarketId(Long marketId);

    @Column(name = "`version`")
    @FieldDef(label="乐观锁，版本号")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getVersion();

    void setVersion(Byte version);
}