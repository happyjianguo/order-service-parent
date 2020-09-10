package com.dili.orders.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.*;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * 参考价临时表
 * This file was generated on 2020-08-14 17:44:32.
 */
@Table(name = "`weighing_reference_price`")
public class WeighingReferencePrice extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品ID
     */
    @Column(name = "`goods_id`")
    private Long goodsId;

    /**
     * 市场ID
     */
    @Column(name = "`market_id`")
    private Long marketId;

    /**
     * 总平均价
     */
    @Column(name = "`total_avg_count`")
    private Long totalAvgCount;

    /**
     * 去掉最高最低价后的平均价
     */
    @Column(name = "`part_avg_count`")
    private Long partAvgCount;

    /**
     * 交易笔数
     */
    @Column(name = "`trans_count`")
    private Integer transCount;

    /**
     * 交易价格数
     */
    @Column(name = "`trans_price_count`")
    private Integer transPriceCount;

    /**
     * 更新时间
     */
    @Column(name = "`settlement_time`")
    private Date settlementTime;
    /**
     * 交易类型
     */
    @Column(name = "`tarde_type`")
    private String tardeType;


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
     * 获取商品ID
     *
     * @return goods_id - 商品ID
     */
    @FieldDef(label="商品ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getGoodsId() {
        return goodsId;
    }

    /**
     * 设置商品ID
     *
     * @param goodsId 商品ID
     */
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    /**
     * 获取市场ID
     *
     * @return market_id - 市场ID
     */
    @FieldDef(label="市场ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getMarketId() {
        return marketId;
    }

    /**
     * 设置市场ID
     *
     * @param marketId 市场ID
     */
    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    /**
     * 获取总平均价
     *
     * @return total_avg_count - 总平均价
     */
    @FieldDef(label="总平均价")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getTotalAvgCount() {
        return totalAvgCount;
    }

    /**
     * 设置总平均价
     *
     * @param totalAvgCount 总平均价
     */
    public void setTotalAvgCount(Long totalAvgCount) {
        this.totalAvgCount = totalAvgCount;
    }

    /**
     * 获取去掉最高最低价后的平均价
     *
     * @return part_avg_count - 去掉最高最低价后的平均价
     */
    @FieldDef(label="去掉最高最低价后的平均价")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getPartAvgCount() {
        return partAvgCount;
    }

    /**
     * 设置去掉最高最低价后的平均价
     *
     * @param partAvgCount 去掉最高最低价后的平均价
     */
    public void setPartAvgCount(Long partAvgCount) {
        this.partAvgCount = partAvgCount;
    }

    /**
     * 获取交易笔数
     *
     * @return trans_count - 交易笔数
     */
    @FieldDef(label="交易笔数")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getTransCount() {
        return transCount;
    }

    /**
     * 设置交易笔数
     *
     * @param transCount 交易笔数
     */
    public void setTransCount(Integer transCount) {
        this.transCount = transCount;
    }

    /**
     * 获取交易价格数
     *
     * @return trans_price_count - 交易价格数
     */
    @FieldDef(label="交易价格数")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getTransPriceCount() {
        return transPriceCount;
    }

    /**
     * 设置交易价格数
     *
     * @param transPriceCount 交易价格数
     */
    public void setTransPriceCount(Integer transPriceCount) {
        this.transPriceCount = transPriceCount;
    }

    /**
     * 获取更新时间
     *
     * @return settlement_time - 更新时间
     */
    @FieldDef(label="更新时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getSettlementTime() {
        return settlementTime;
    }

    /**
     * 设置更新时间
     *
     * @param settlementTime 更新时间
     */
    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }

    /**
     * 获取交易类型
     *
     * @return trade_type - 交易类型
     */
    @FieldDef(label="交易类型")
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getTardeType() {
        return tardeType;
    }

    /**
     * 设置交易类型
     *
     * @param trade_type 交易类型
     */
    public void setTardeType(String tardeType) {
        this.tardeType = tardeType;
    }
}