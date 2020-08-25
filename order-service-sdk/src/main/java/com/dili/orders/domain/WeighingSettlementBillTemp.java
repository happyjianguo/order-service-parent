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
@Table(name = "`weighing_settlement_bill_temp`")
public class WeighingSettlementBillTemp extends BaseDomain {

    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 过磅单号
     */
    @Column(name = "`serial_no`")
    private String serialNo;

    /**
     * 计量方式
     */
    @Column(name = "`measure_type`")
    private String measureType;

    /**
     * 市场ID
     */
    @Column(name = "`market_id`")
    private Long marketId;

    /**
     * 商品ID
     */
    @Column(name = "`goods_id`")
    private Long goodsId;

    /**
     * 件数
     */
    @Column(name = "`unit_amount`")
    private Integer unitAmount;

    /**
     * 单价
     */
    @Column(name = "`unit_price`")
    private Long unitPrice;

    /**
     * 件重
     */
    @Column(name = "`unit_weight`")
    private Integer unitWeight;

    /**
     * 重量
     */
    @Column(name = "`net_weight`")
    private Integer netWeight;

    /**
     * 更新时间
     */
    @Column(name = "`settlement_time`")
    private Date settlementTime;

    /**
     * 交易额
     */
    @Column(name = "`trade_amount`")
    private Long tradeAmount;

    /**
     * 最大价格
     */
    @Column(name = "`max_price`")
    private Long maxPrice;

    /**
     * 最小价格
     */
    @Column(name = "`min_price`")
    private Long minPrice;

    /**
     * 最大交易额
     */
    @Column(name = "`max_trade_amount`")
    private Long maxTradeAmount;

    /**
     * 最小交易额
     */
    @Column(name = "`min_trade_amount`")
    private Long minTradeAmount;

    /**
     * 最大交易量
     */
    @Column(name = "`max_trade_weight`")
    private int maxTradeWeight;

    /**
     * 最小交易量
     */
    @Column(name = "`min_trade_weight`")
    private int minTradeWeight;

    /**
     * 交易笔数
     */
    @Column(name = "`trade_count`")
    private int tradeCount;

    /**
     * 交易价格数
     */
    @Column(name = "`trade_price_count`")
    private int tradePriceCount;

    /**
     * 总交易额
     */
    @Column(name = "`total_trade_amount`")
    private Long totalTradeAmount;

    /**
     * 总交易量
     */
    @Column(name = "`total_trade_weight`")
    private int totalTradeWeight;


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
     * 获取过磅单号
     *
     * @return serial_no - 过磅单号
     */
    @FieldDef(label="过磅单号")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public String getSerialNo() { return serialNo; }

    /**
     * 设置过磅单号
     *
     * @param serialNo 过磅单号
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * 获取计量方式
     *
     * @return measure_type - 计量方式
     */
    @FieldDef(label="计量方式")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public String getMeasureType() {
        return measureType;
    }

    /**
     * 设置计量方式
     *
     * @param measureType 计量方式
     */
    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    /**
     * 获取件数
     *
     * @return unit_amount - 件数
     */
    @FieldDef(label="件数")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Integer getUnitAmount() {
        return unitAmount;
    }

    /**
     * 设置件数
     *
     * @param unitAmount 件数
     */
    public void setUnitAmount(Integer unitAmount) {
        this.unitAmount = unitAmount;
    }

    /**
     * 获取单价
     *
     * @return unit_price - 单价
     */
    @FieldDef(label="单价")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Long getUnitPrice() {
        return unitPrice;
    }

    /**
     * 设置单价
     *
     * @param unitPrice 单价
     */
    public void setUnitPrice(Long unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 获取件重
     *
     * @return unit_weight - 件重
     */
    @FieldDef(label="件重")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Integer getUnitWeight() {
        return unitWeight;
    }

    /**
     * 设置件重
     *
     * @param unitWeight 件重
     */
    public void setUnitWeight(Integer unitWeight) {
        this.unitWeight = unitWeight;
    }

    /**
     * 获取重量
     *
     * @return net_weight - 重量
     */
    @FieldDef(label="重量")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Integer getNetWeight() {
        return netWeight;
    }

    /**
     * 设置重量
     *
     * @param netWeight 重量
     */
    public void setNetWeight(Integer netWeight) {
        this.netWeight = netWeight;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxTradeAmount() {
        return maxTradeAmount;
    }

    public void setMaxTradeAmount(Long maxTradeAmount) {
        this.maxTradeAmount = maxTradeAmount;
    }

    public Long getMinTradeAmount() {
        return minTradeAmount;
    }

    public void setMinTradeAmount(Long minTradeAmount) {
        this.minTradeAmount = minTradeAmount;
    }

    public int getMaxTradeWeight() {
        return maxTradeWeight;
    }

    public void setMaxTradeWeight(int maxTradeWeight) {
        this.maxTradeWeight = maxTradeWeight;
    }

    public int getMinTradeWeight() {
        return minTradeWeight;
    }

    public void setMinTradeWeight(int minTradeWeight) {
        this.minTradeWeight = minTradeWeight;
    }

    public int getTradeCount() {
        return tradeCount;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }

    public int getTradePriceCount() {
        return tradePriceCount;
    }

    public void setTradePriceCount(int tradePriceCount) {
        this.tradePriceCount = tradePriceCount;
    }

    public Long getTotalTradeAmount() {
        return totalTradeAmount;
    }

    public void setTotalTradeAmount(Long totalTradeAmount) {
        this.totalTradeAmount = totalTradeAmount;
    }

    public int getTotalTradeWeight() {
        return totalTradeWeight;
    }

    public void setTotalTradeWeight(int totalTradeWeight) {
        this.totalTradeWeight = totalTradeWeight;
    }

    public Long getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(Long tradeAmount) {
        this.tradeAmount = tradeAmount;
    }
}