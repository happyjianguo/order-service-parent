package com.dili.orders.dto;

/**
 * 创建交易返回dto
 */
public class CreateTradeResponseDto {

    /**
     * 交易ID
     */
    private String tradeId;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
}
