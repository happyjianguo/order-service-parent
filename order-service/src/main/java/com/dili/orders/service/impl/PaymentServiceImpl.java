package com.dili.orders.service.impl;

import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.domain.WeighingStatementState;
import com.dili.orders.dto.*;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.service.PaymentService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Autowired
    private PayRpc payRpc;
    @Autowired
    private WeighingBillMapper weighingBillMapper;

    @Override
    public PaymentTradeCommitResponseDto pay(WeighingStatement statement, String buyerPassword) {
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(statement.getWeighingBillId());
        BaseOutput<PaymentTradeCommitResponseDto> paymentOutput;
        BaseOutput<?> output = this.prepareTrade(weighingBill, statement);
        if (!output.isSuccess()) {
            LOGGER.error(String.format("交易过磅结算调用支付系统创建支付订单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
            throw new AppException(output.getMessage());
        }
        paymentOutput = this.commitTrade(weighingBill, statement, buyerPassword);
        if (paymentOutput == null) {
            throw new AppException("调用支付系统无响应");
        }
        if (!paymentOutput.isSuccess()) {
            LOGGER.error(String.format("交易过磅结算调用支付系统确认交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
            throw new AppException(paymentOutput.getMessage());
        }
        return paymentOutput.getData();
    }

    protected BaseOutput<?> prepareTrade(WeighingBill weighingBill, WeighingStatement ws) {
        // 创建支付订单
        PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
        prepareDto.setAccountId(weighingBill.getSellerAccount());
        prepareDto.setAmount(ws.getTradeAmount());
        prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
        prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + ws.getSerialNo());
        prepareDto.setType(PaymentTradeType.TRADE.getValue());
        prepareDto.setDescription("交易过磅");
        LOGGER.debug("开始调用支付系统创建支付订单，过磅单id：{}", weighingBill.getId());
        long start = System.currentTimeMillis();
        BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
        long end = System.currentTimeMillis();
        LOGGER.debug("结束调用支付系统创建支付订单，过磅单id：{}，耗时{}毫秒", weighingBill.getId(), end - start);
        if (!paymentOutput.isSuccess()) {
            return paymentOutput;
        }
        ws.setPayOrderNo(paymentOutput.getData().getTradeId());
        ws.setState(WeighingStatementState.PAID.getValue());
        return paymentOutput;
    }

    protected BaseOutput<PaymentTradeCommitResponseDto> commitTrade(WeighingBill weighingBill, WeighingStatement weighingStatement, String password) {
        // 提交支付订单
        PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
        dto.setAccountId(weighingBill.getBuyerAccount());
        dto.setPassword(password);
        dto.setTradeId(weighingStatement.getPayOrderNo());
        dto.setBusinessId(weighingBill.getBuyerCardAccount());
        List<FeeDto> fees = new ArrayList<FeeDto>(2);
        if (weighingStatement.getBuyerPoundage() != null && weighingStatement.getBuyerPoundage() > 0) {
            // 买家手续费
            FeeDto buyerFee = new FeeDto();
            buyerFee.setAmount(weighingStatement.getBuyerPoundage());
            buyerFee.setType(FundItem.TRADE_SERVICE_FEE.getCode());
            buyerFee.setTypeName(FundItem.TRADE_SERVICE_FEE.getName());
            buyerFee.setUseFor(FeeUse.BUYER.getValue());
            fees.add(buyerFee);
        }
        if (weighingStatement.getSellerPoundage() != null && weighingStatement.getSellerPoundage() > 0) {
            // 卖家手续费
            FeeDto sellerFee = new FeeDto();
            sellerFee.setAmount(weighingStatement.getSellerPoundage());
            sellerFee.setType(FundItem.TRADE_SERVICE_FEE.getCode());
            sellerFee.setTypeName(FundItem.TRADE_SERVICE_FEE.getName());
            sellerFee.setUseFor(FeeUse.SELLER.getValue());
            fees.add(sellerFee);
        }
        dto.setFees(fees);
        LOGGER.debug("开始调用支付系统提交订单，过磅单id：{}", weighingBill.getId());
        long start = System.currentTimeMillis();
        BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.commitTrade(dto);
        long end = System.currentTimeMillis();
        LOGGER.debug("结束调用支付系统提交订单，过磅单id：{}，耗时{}毫秒", weighingBill.getId(), end - start);
        return commitOutput;
    }

    protected BaseOutput<PaymentTradeCommitResponseDto> confirmTrade(WeighingBill weighingBill, WeighingStatement weighingStatement, String buyerPassword) {
        // 提交支付订单
        PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
        dto.setAccountId(weighingBill.getBuyerAccount());
        dto.setPassword(buyerPassword);
        dto.setTradeId(weighingStatement.getPayOrderNo());
        dto.setAmount(weighingStatement.getTradeAmount());
        List<FeeDto> fees = new ArrayList<FeeDto>(2);
        if (weighingStatement.getBuyerPoundage() != null && weighingStatement.getBuyerPoundage() > 0) {
            // 买家手续费
            FeeDto buyerFee = new FeeDto();
            buyerFee.setAmount(weighingStatement.getBuyerPoundage());
            buyerFee.setType(FundItem.TRADE_SERVICE_FEE.getCode());
            buyerFee.setTypeName(FundItem.TRADE_SERVICE_FEE.getName());
            buyerFee.setUseFor(FeeUse.BUYER.getValue());
            fees.add(buyerFee);
        }
        if (weighingStatement.getSellerPoundage() != null && weighingStatement.getSellerPoundage() > 0) {
            // 卖家手续费
            FeeDto sellerFee = new FeeDto();
            sellerFee.setAmount(weighingStatement.getSellerPoundage());
            sellerFee.setType(FundItem.TRADE_SERVICE_FEE.getCode());
            sellerFee.setTypeName(FundItem.TRADE_SERVICE_FEE.getName());
            sellerFee.setUseFor(FeeUse.SELLER.getValue());
            fees.add(sellerFee);
        }
        dto.setFees(fees);
        LOGGER.debug("开始调用支付系统提交预授权订单，过磅单id：{}", weighingBill.getId());
        long start = System.currentTimeMillis();
        BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.confirm(dto);
        long end = System.currentTimeMillis();
        LOGGER.debug("结束调用支付系统提交预授权订单，过磅单id：{}，耗时{}毫秒", weighingBill.getId(), end - start);
        return commitOutput;
    }

    @Override
    public PaymentTradeCommitResponseDto refound(String serialNo) {
        PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
        cancelDto.setTradeId(serialNo);
        BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
        if (paymentOutput == null) {
            throw new AppException("交易解冻调用支付系统无响应");
        }
        if (!paymentOutput.isSuccess()) {
            throw new AppException(paymentOutput.getMessage());
        }
        return paymentOutput.getData();
    }

    @Override
    public PaymentTradeCommitResponseDto freeze(WeighingStatement statement, String buyerPassword) {
        // 冻结交易
        Long buyerAmount = statement.getFrozenAmount();
        // 创建支付订单
        PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(statement.getWeighingBillId());
        prepareDto.setAccountId(weighingBill.getSellerAccount());
        prepareDto.setAmount(buyerAmount);
        prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
        prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + statement.getSerialNo());
        prepareDto.setType(PaymentTradeType.PREAUTHORIZED.getValue());
        prepareDto.setDescription("交易过磅");
        LOGGER.debug("开始调用支付系统创建支付订单，过磅单id：{}", weighingBill.getId());
        long start = System.currentTimeMillis();
        BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
        long end = System.currentTimeMillis();
        LOGGER.debug("结束调用支付系统创建支付订单，过磅单id：{}，耗时{}毫秒", weighingBill.getId(), end - start);
        if (paymentOutput == null) {
            throw new AppException("调用支付系统创建冻结支付订单无响应");
        }
        if (!paymentOutput.isSuccess()) {
            LOGGER.error(String.format("调用支付系统创建冻结支付订单失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
            throw new AppException(paymentOutput.getMessage());
        }
        statement.setPayOrderNo(paymentOutput.getData().getTradeId());

        PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
        dto.setAccountId(weighingBill.getBuyerAccount());
        dto.setBusinessId(weighingBill.getBuyerCardAccount());
        dto.setAmount(statement.getFrozenAmount());
        dto.setTradeId(statement.getPayOrderNo());
        dto.setPassword(buyerPassword);
        LOGGER.debug("开始调用支付系统提交支付订单，过磅单id：{}", weighingBill.getId());
        start = System.currentTimeMillis();
        BaseOutput<PaymentTradeCommitResponseDto> freezeOutput = this.payRpc.commitTrade(dto);
        end = System.currentTimeMillis();
        LOGGER.debug("结束调用支付系统提交支付订单，过磅单id：{}，耗时{}毫秒", weighingBill.getId(), end - start);

        if (freezeOutput == null) {
            throw new AppException("交易冻结调用支付系统无响应");
        }
        if (!freezeOutput.isSuccess()) {
            LOGGER.error(String.format("调用支付系统提交支付冻结订单失败:code=%s,message=%s", freezeOutput.getCode(), freezeOutput.getMessage()));
            throw new AppException(freezeOutput.getMessage());
        }
        return freezeOutput.getData();
    }

    @Override
    public PaymentTradeCommitResponseDto unfreeze(String serialNo) {
        return this.refound(serialNo);
    }

    @Override
    public PaymentTradeCommitResponseDto freezePay(WeighingStatement statement, String buyerPassword) {
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(statement.getWeighingBillId());
        BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.confirmTrade(weighingBill, statement, buyerPassword);
        if (paymentOutput == null) {
            throw new AppException("调用支付系统无响应");
        }
        if (!paymentOutput.isSuccess()) {
            LOGGER.error(String.format("交易过磅结算调用支付系统确认冻结交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
            throw new AppException(paymentOutput.getMessage());
        }
        return paymentOutput.getData();
    }

    @Override
    public AccountBalanceDto queryBalance(Long accountId) {
        // 判断余额是否足够
        AccountRequestDto balanceQuery = new AccountRequestDto();
        balanceQuery.setAccountId(accountId);
        BaseOutput<AccountBalanceDto> balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
        if (balanceOutput == null) {
            throw new AppException("调用支付系统查询买方账户余额无响应");
        }
        if (!balanceOutput.isSuccess()) {
            LOGGER.error(String.format("调用支付系统查询买方账户余额失败:code=%s,message=%s", balanceOutput.getCode(), balanceOutput.getMessage()));
            throw new AppException(balanceOutput.getMessage());
        }
        return balanceOutput.getData();
    }

    @Override
    public boolean validateAccountPassword(Long accountId, String password) {
        // 校验买卖双方密码
        AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
        buyerPwdDto.setAccountId(accountId);
        buyerPwdDto.setPassword(password);
        BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
        if (pwdOutput == null) {
            throw new AppException("作废过磅单调用支付系统查询买方密码无响应");
        }
        if (!pwdOutput.isSuccess()) {
            LOGGER.error(String.format("作废过磅单调用支付系统查询买方密码失败:code=%s,message=%s", pwdOutput.getCode(), pwdOutput.getMessage()));
            if (PaymentErrorCode.codeOf(pwdOutput.getCode()).equals(PaymentErrorCode.ACCOUNT_PASSWORD_INCORRECT_EXCEPTION)) {
                return false;
            } else {
                throw new AppException(pwdOutput.getMessage());
            }
        }
        return true;
    }
}
