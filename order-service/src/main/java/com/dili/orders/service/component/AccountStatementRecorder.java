package com.dili.orders.service.component;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;

/**
 * 账户流水记录类
 */
public interface AccountStatementRecorder {

    void recordSettlement(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto paymentResult);

    void recordFreeze(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto freezePayResult);

    void recordWithdraw(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto refoundResult);

    void recordInvalidate(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto refoundResult);
}
