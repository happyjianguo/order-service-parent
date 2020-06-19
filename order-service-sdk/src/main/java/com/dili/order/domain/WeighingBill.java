package com.dili.order.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-06-19 14:20:28.
 */
@Table(name = "`weighing_bill`")
public interface WeighingBill extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`measure_type`")
    @FieldDef(label="计量方式", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getMeasureType();

    void setMeasureType(String measureType);

    @Column(name = "`trade_type`")
    @FieldDef(label="交易类型", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getTradeType();

    void setTradeType(String tradeType);

    @Column(name = "`buyer_id`")
    @FieldDef(label="买方id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getBuyerId();

    void setBuyerId(Long buyerId);

    @Column(name = "`buyer_card_number`")
    @FieldDef(label="买方卡号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getBuyerCardNumber();

    void setBuyerCardNumber(String buyerCardNumber);

    @Column(name = "`buyer_account`")
    @FieldDef(label="买方支付账号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getBuyerAccount();

    void setBuyerAccount(String buyerAccount);

    @Column(name = "`buyer_name`")
    @FieldDef(label="买方姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getBuyerName();

    void setBuyerName(String buyerName);

    @Column(name = "`seller_id`")
    @FieldDef(label="卖方id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getSellerId();

    void setSellerId(Long sellerId);

    @Column(name = "`seller_card_number`")
    @FieldDef(label="卖方卡号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSellerCardNumber();

    void setSellerCardNumber(String sellerCardNumber);

    @Column(name = "`seller_account`")
    @FieldDef(label="卖方支付账号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSellerAccount();

    void setSellerAccount(String sellerAccount);

    @Column(name = "`seller_name`")
    @FieldDef(label="卖方姓名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSellerName();

    void setSellerName(String sellerName);

    @Column(name = "`goods_id`")
    @FieldDef(label="商品id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getGoodsId();

    void setGoodsId(Long goodsId);

    @Column(name = "`goods_origin`")
    @FieldDef(label="商品产地")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getGoodsOrigin();

    void setGoodsOrigin(Long goodsOrigin);

    @Column(name = "`unit_amount`")
    @FieldDef(label="件数")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getUnitAmount();

    void setUnitAmount(Integer unitAmount);

    @Column(name = "`unit_price`")
    @FieldDef(label="单价")
    @EditMode(editor = FieldEditor.Text, required = true)
    BigDecimal getUnitPrice();

    void setUnitPrice(BigDecimal unitPrice);

    @Column(name = "`unit_weight`")
    @FieldDef(label="件重")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getUnitWeight();

    void setUnitWeight(BigDecimal unitWeight);

    @Column(name = "`fetched_weight`")
    @FieldDef(label="取重")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getFetchedWeight();

    void setFetchedWeight(BigDecimal fetchedWeight);

    @Column(name = "`fetch_weight_time`")
    @FieldDef(label="取重时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getFetchWeightTime();

    void setFetchWeightTime(Date fetchWeightTime);

    @Column(name = "`rough_weight`")
    @FieldDef(label="毛重")
    @EditMode(editor = FieldEditor.Text, required = true)
    BigDecimal getRoughWeight();

    void setRoughWeight(BigDecimal roughWeight);

    @Column(name = "`net_weight`")
    @FieldDef(label="净重")
    @EditMode(editor = FieldEditor.Text, required = true)
    BigDecimal getNetWeight();

    void setNetWeight(BigDecimal netWeight);

    @Column(name = "`plate_number`")
    @FieldDef(label="车牌号", maxLength = 15)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getPlateNumber();

    void setPlateNumber(String plateNumber);

    @Column(name = "`tare_weight`")
    @FieldDef(label="皮重")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getTareWeight();

    void setTareWeight(BigDecimal tareWeight);

    @Column(name = "`subtraction_rate`")
    @FieldDef(label="除杂比例")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getSubtractionRate();

    void setSubtractionRate(BigDecimal subtractionRate);

    @Column(name = "`subtraction_weight`")
    @FieldDef(label="除杂重量")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getSubtractionWeight();

    void setSubtractionWeight(BigDecimal subtractionWeight);

    @Column(name = "`estimated_net_weight`")
    @FieldDef(label="估计净重")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getEstimatedNetWeight();

    void setEstimatedNetWeight(BigDecimal estimatedNetWeight);

    @Column(name = "`frozen_amount`")
    @FieldDef(label="冻结金额")
    @EditMode(editor = FieldEditor.Text, required = false)
    BigDecimal getFrozenAmount();

    void setFrozenAmount(BigDecimal frozenAmount);

    @Column(name = "`tare_bill_number`")
    @FieldDef(label="皮重单据号", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getTareBillNumber();

    void setTareBillNumber(String tareBillNumber);

    @Column(name = "`state`")
    @FieldDef(label="状态")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getState();

    void setState(Integer state);

    @Column(name = "`created_time`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreatedTime();

    void setCreatedTime(Date createdTime);

    @Column(name = "`modified_time`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModifiedTime();

    void setModifiedTime(Date modifiedTime);

    @Column(name = "`settlement_time`")
    @FieldDef(label="结算时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getSettlementTime();

    void setSettlementTime(Date settlementTime);

    @Column(name = "`creator_id`")
    @FieldDef(label="创建人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getCreatorId();

    void setCreatorId(Long creatorId);
}