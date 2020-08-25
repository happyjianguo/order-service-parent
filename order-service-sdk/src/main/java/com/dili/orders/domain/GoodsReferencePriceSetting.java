package com.dili.orders.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2020-08-11 17:31:37.
 */
@Table(name = "`goods_reference_price_setting`")
public class GoodsReferencePriceSetting extends BaseDomain {
    /**
     * 品类参考价id
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 市场id
     */
    @Column(name = "`market_id`")
    private Long marketId;

    /**
     * 商品id
     */
    @Column(name = "`goods_id`")
    private Long goodsId;

    /**
     * 品类名称
     */
    @Column(name = "`goods_name`")
    private String goodsName;

    /**
     * 参考价规则
     */
    @Column(name = "`reference_rule`")
    private Integer referenceRule;

    /**
     * 固定价格
     */
    @Column(name = "`fixed_price`")
    private Long fixedPrice;

    /**
     * 父级商品id
     */
    @Column(name = "`parent_goods_id`")
    private Long parentGoodsId;

    /**
     * 创建时间
     */
    @Column(name = "`created_time`")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 创建人id
     */
    @Column(name = "`creator_id`")
    private Long creatorId;

    /**
     * 修改时间
     */
    @Column(name = "`modified_time`")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedTime;

    /**
     * 修改人id
     */
    @Column(name = "`modifier_id`")
    private Long modifierId;

    /**
     * 版本号
     */
    @Column(name = "`version`")
    private Integer version;

    /**
     * 品类参考价id
     *
     * @return oid - 品类参考价id
     */
    @Override
    @FieldDef(label="品类参考价id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * 设置品类参考价id
     *
     * @param id 品类参考价id
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取市场id
     *
     * @return market_id - 市场id
     */
    @FieldDef(label="市场id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getMarketId() { return marketId; }

    /**
     * 设置市场id
     *
     * @param marketId 市场id
     */
    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    /**
     * 获取商品id
     *
     * @return goods_id - 商品id
     */
    @FieldDef(label="商品id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getGoodsId() {
        return goodsId;
    }

    /**
     * 设置商品id
     *
     * @param goodsId 商品id
     */
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取品类名称
     *
     * @return goods_name - 品类名称
     */
    @FieldDef(label="品类名称", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getGoodsName() {
        return goodsName;
    }

    /**
     * 设置品类名称
     *
     * @param goodsName 品类名称
     */
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    /**
     * 获取参考价规则
     *
     * @return reference_rule - reference_rule
     */
    @FieldDef(label="reference_rule")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getReferenceRule() {
        return referenceRule;
    }

    /**
     * 设置参考价规则
     *
     * @param referenceRule 参考价规则
     */
    public void setReferenceRule(Integer referenceRule) {
        this.referenceRule = referenceRule;
    }

    /**
     * 获取固定价格
     *
     * @return fixed_price - 固定价格
     */
    @FieldDef(label="固定价格")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getFixedPrice() {
        return fixedPrice;
    }

    /**
     * 设置固定价格
     *
     * @param fixedPrice 固定价格
     */
    public void setFixedPrice(Long fixedPrice) {
        this.fixedPrice = fixedPrice;
    }
    /**
     * 获取父级商品id
     *
     * @return parent_goods_id - 父级商品id
     */
    @FieldDef(label="固定价格")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getParentGoodsId() {
        return parentGoodsId;
    }

    /**
     * 设置父级商品id
     *
     * @param parentGoodsId 父级商品id
     */
    public void setParentGoodsId(Long parentGoodsId) {
        this.parentGoodsId = parentGoodsId;
    }

    /**
     * 获取创建时间
     *
     * @return created_time - 创建时间
     */
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * 设置创建时间
     *
     * @param createdTime 创建时间
     */
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * 获取创建人id
     *
     * @return creator_id - 创建人id
     */
    @FieldDef(label="创建人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * 设置创建人id
     *
     * @param creatorId 创建人id
     */
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * 获取修改时间
     *
     * @return modified_time - 修改时间
     */
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifiedTime 修改时间
     */
    public void setModifiedTime(LocalDateTime modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    /**
     * 获取修改人id
     *
     * @return modifier_id - 修改人id
     */
    @FieldDef(label="修改人id")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getModifierId() {
        return modifierId;
    }

    /**
     * 设置修改人id
     *
     * @param modifierId 修改人id
     */
    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    /**
     * 获取版本号
     *
     * @return version - 版本号
     */
    @FieldDef(label="版本号")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Integer getVersion() {
        return version;
    }

    /**
     * 设置版本号
     *
     * @param version 版本号
     */
    public void setVersion(Integer version) {
        this.version = version;
    }
}