package com.dili.orders.service;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.AccountBalanceDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;

public interface PaymentService {

    PaymentTradeCommitResponseDto pay(WeighingStatement statement, String buyerPassword);

    PaymentTradeCommitResponseDto refound(String serialNo);

    PaymentTradeCommitResponseDto freezePay(WeighingStatement statement, String buyerPassword);

    AccountBalanceDto queryBanlance(Long accountId);

    boolean validateAccountPassword(Long accountId, String password);
}
