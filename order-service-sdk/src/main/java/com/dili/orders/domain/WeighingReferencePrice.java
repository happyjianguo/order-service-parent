package com.dili.orders.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 参考价实体
 */
@Table(name = "`weighing_reference_price`")
public class WeighingReferencePrice extends BaseDomain {

    /**商品ID*/
    @Column(name = "`goods_id`")
    private Long goodsId;
    /**市场ID*/
    @Column(name = "`market_id`")
    private Long marketId;
    /**总平均价*/
    @Column(name = "`total_avg_count`")
    private Long totalAvgCount;
    /**去掉最高最低价后的平均价*/
    @Column(name = "`part_avg_count`")
    private Long partAvgCount;
    /**交易笔数*/
    @Column(name = "`trans_count`")
    private Integer transCount;
    /**交易价格数*/
    @Column(name = "`trans_price_count`")
    private Integer transPriceCount;
    /**更新时间*/
    @Column(name = "`settlement_time`")
    private Date settlementTime;
    /**结算日期，yyyy-MM-dd ex:2020-10-10*/
    @Column(name = "`settlement_day`")
    private String settlementDay;
    /**交易类型*/
    @Column(name = "`trade_type`")
    private String tradeType;

    public static WeighingReferencePrice copyFromDailyData(WeighingSettlementBillDaily daily, Long totalAvgCount, Long referenceAvgCount) {
        WeighingReferencePrice referencePrice = new WeighingReferencePrice();
        referencePrice.setGoodsId(daily.getGoodsId());
        referencePrice.setMarketId(daily.getMarketId());
        referencePrice.setSettlementTime(daily.getSettlementTime());
        referencePrice.setSettlementDay(daily.getSettlementDay());
        //获取交易价格数目
        referencePrice.setTransPriceCount(daily.getTradePriceCount());
        // 获取交易类型
        referencePrice.setTradeType(daily.getTradeType());
        //取交易笔数
        referencePrice.setTransCount(daily.getTradeCount());
        referencePrice.setTotalAvgCount(totalAvgCount);
        referencePrice.setPartAvgCount(referenceAvgCount);
        return referencePrice;
    }

    @FieldDef(label = "商品ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    @FieldDef(label = "市场ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    @FieldDef(label = "总平均价")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getTotalAvgCount() {
        return totalAvgCount;
    }

    public void setTotalAvgCount(Long totalAvgCount) {
        this.totalAvgCount = totalAvgCount;
    }

    @FieldDef(label = "去掉最高最低价后的平均价")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getPartAvgCount() {
        return partAvgCount;
    }

    public void setPartAvgCount(Long partAvgCount) {
        this.partAvgCount = partAvgCount;
    }

    @FieldDef(label = "交易笔数")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getTransCount() {
        return transCount;
    }

    public void setTransCount(Integer transCount) {
        this.transCount = transCount;
    }

    @FieldDef(label = "交易价格数")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Integer getTransPriceCount() {
        return transPriceCount;
    }

    public void setTransPriceCount(Integer transPriceCount) {
        this.transPriceCount = transPriceCount;
    }

    @FieldDef(label = "更新时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(Date settlementTime) {
        this.settlementTime = settlementTime;
    }

    @FieldDef(label = "交易类型")
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    @FieldDef(label = "结算日期")
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSettlementDay() {
        return settlementDay;
    }

    public void setSettlementDay(String settlementDay) {
        this.settlementDay = settlementDay;
    }
}
