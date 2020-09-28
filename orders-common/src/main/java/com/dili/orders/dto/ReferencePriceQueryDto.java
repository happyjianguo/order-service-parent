package com.dili.orders.dto;

import java.io.Serializable;

/**
 *
 * @Auther: miaoguoxin
 * @Date: 2020/9/25 15:53
 */
public class ReferencePriceQueryDto implements Serializable {
    /**商品ID*/
    private Long goodsId;
    /**市场ID*/
    private Long marketId;
    /**结算日期，yyyy-MM-dd ex:2020-10-10*/
    private String settlementDay;
    /**交易类型*/
    private String tradeType;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public String getSettlementDay() {
        return settlementDay;
    }

    public void setSettlementDay(String settlementDay) {
        this.settlementDay = settlementDay;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
