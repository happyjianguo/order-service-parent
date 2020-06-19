package com.dili.order.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.math.BigDecimal;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-06-19 14:43:53.
 */
@Table(name = "`weighing_statement`")
public interface WeighingStatement extends IBaseDomain {
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

    @Column(name = "`buyer_id`")
    @FieldDef(label="买家id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getBuyerId();

    void setBuyerId(Long buyerId);

    @Column(name = "`buyer_card_number`")
    @FieldDef(label="买家卡号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getBuyerCardNumber();

    void setBuyerCardNumber(String buyerCardNumber);

    @Column(name = "`buyer_name`")
    @FieldDef(label="买家姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getBuyerName();

    void setBuyerName(String buyerName);

    @Column(name = "`buyer_actual_amount`")
    @FieldDef(label="买方实际支付金额")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getBuyerActualAmount();

    void setBuyerActualAmount(BigDecimal buyerActualAmount);

    @Column(name = "`buyer_poundage`")
    @FieldDef(label="买方手续费")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getBuyerPoundage();

    void setBuyerPoundage(BigDecimal buyerPoundage);

    @Column(name = "`seller_id`")
    @FieldDef(label="卖家id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getSellerId();

    void setSellerId(Long sellerId);

    @Column(name = "`seller_card_number`")
    @FieldDef(label="卖家卡号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSellerCardNumber();

    void setSellerCardNumber(String sellerCardNumber);

    @Column(name = "`seller_actual_amount`")
    @FieldDef(label="卖方实收金额")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getSellerActualAmount();

    void setSellerActualAmount(BigDecimal sellerActualAmount);

    @Column(name = "`trade_amount`")
    @FieldDef(label="交易金额")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getTradeAmount();

    void setTradeAmount(BigDecimal tradeAmount);

    @Column(name = "`frozen_amount`")
    @FieldDef(label="冻结金额")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getFrozenAmount();

    void setFrozenAmount(BigDecimal frozenAmount);

    @Column(name = "`state`")
    @FieldDef(label="状态")
    @EditMode(editor = FieldEditor.Text, required = true)
    Byte getState();

    void setState(Byte state);

    @Column(name = "`pay_order_number`")
    @FieldDef(label="支付订单号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getPayOrderNumber();

    void setPayOrderNumber(String payOrderNumber);
}