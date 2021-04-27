package com.dili.orders.service;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.AccountBalanceDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;

/**
 * 支付服务
 */
public interface PaymentService {

    /**
     * 支付
     *
     * @param statement     结算单
     * @param buyerPassword 买方密码
     * @return
     */
    PaymentTradeCommitResponseDto pay(WeighingStatement statement, String buyerPassword);

    /**
     * 退款
     *
     * @param serialNo 支付订单号
     * @return
     */
    PaymentTradeCommitResponseDto refound(String serialNo);

    /**
     * 冻结
     *
     * @param statement     结算单
     * @param buyerPassword 买方密码
     * @return
     */
    PaymentTradeCommitResponseDto freeze(WeighingStatement statement, String buyerPassword);

    /**
     * 解冻
     *
     * @param serialNo 支付订单号
     * @return
     */
    PaymentTradeCommitResponseDto unfreeze(String serialNo);

    /**
     * 冻结支付
     *
     * @param statement     结算单
     * @param buyerPassword 买方密码
     * @return
     */
    PaymentTradeCommitResponseDto freezePay(WeighingStatement statement, String buyerPassword);

    /**
     * 查询账户余额
     *
     * @param accountId 账户id
     * @return
     */
    AccountBalanceDto queryBalance(Long accountId);

    /**
     * 验证密码
     *
     * @param accountId 账户id
     * @param password  密码
     * @return
     */
    boolean validateAccountPassword(Long accountId, String password);
}
