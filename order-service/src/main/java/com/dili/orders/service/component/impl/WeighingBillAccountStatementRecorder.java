package com.dili.orders.service.component.impl;

import com.alibaba.fastjson.JSON;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.*;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.service.component.AccountStatementRecorder;
import com.dili.orders.service.component.UserLoader;
import com.dili.uap.sdk.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class WeighingBillAccountStatementRecorder implements AccountStatementRecorder {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeighingBillAccountStatementRecorder.class);

    @Autowired
    private UserLoader userLoader;
    @Autowired
    private WeighingBillMapper weighingBillMapper;
    @Autowired
    protected RabbitMQMessageService mqService;

    @Override
    public void recordSettlement(WeighingStatement ws, PaymentTradeCommitResponseDto paymentResult) {
        List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
        User operator = this.userLoader.getById(ws.getModifierId());
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(ws.getWeighingBillId());
        Long firmId = weighingBill.getMarketId();

        Integer tradeType = PaymentTradeType.TRADE.getValue();
        // 买家入账
        if (ws.getFrozenAmount() != null && ws.getFrozenAmount() > 0) {
            tradeType = PaymentTradeType.PREAUTHORIZED.getValue();
            // 解冻
            SerialRecordDo frozenRecord = this.buildBaseBuyerSerialRecordDo(ws);
            frozenRecord.setTradeType(tradeType);
            frozenRecord.setAction(ActionType.INCOME.getCode());
            frozenRecord.setAmount(Math.abs(paymentResult.getFrozenAmount()));
            frozenRecord.setStartBalance(paymentResult.getBalance() - paymentResult.getFrozenBalance());
            frozenRecord.setEndBalance(paymentResult.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
            frozenRecord.setFundItem(FundItem.TRADE_UNFREEZE.getCode());
            frozenRecord.setFundItemName(FundItem.TRADE_UNFREEZE.getName());
            frozenRecord.setOperateTime(paymentResult.getWhen());
            this.updateSerialRecordDoOperator(frozenRecord, operator);
            frozenRecord.setNotes(String.format("解冻，过磅单号%s", weighingBill.getSerialNo()));
            srList.add(frozenRecord);
        }
        // 买家支出
        PaymentStream buyerStream = paymentResult.getStreams().get(0);
        Long buyerBalance = buyerStream.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance());
        SerialRecordDo buyerExpense = this.buildBaseBuyerSerialRecordDo(ws);
        buyerExpense.setTradeType(tradeType);
        buyerExpense.setAction(ActionType.EXPENSE.getCode());
        buyerExpense.setAmount(Math.abs(buyerStream.getAmount()));
        buyerExpense.setStartBalance(buyerBalance);
        buyerExpense.setEndBalance(buyerBalance + buyerStream.getAmount());
        buyerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
        buyerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
        buyerExpense.setOperateTime(paymentResult.getWhen());
        this.updateSerialRecordDoOperator(buyerExpense, operator);
        buyerExpense.setNotes(String.format("买方，结算单号%s", ws.getSerialNo()));
        srList.add(buyerExpense);
        // 买家手续费
        PaymentStream buyerPoundageStream = paymentResult.getStreams().stream().filter(s -> s.getType().equals((long) FundItem.TRADE_SERVICE_FEE.getCode())).findFirst().orElse(null);
        if (buyerPoundageStream != null) {
            buyerBalance = buyerPoundageStream.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance());
        }
        SerialRecordDo buyerPoundage = this.buildBaseBuyerSerialRecordDo(ws);
        buyerPoundage.setTradeType(tradeType);
        buyerPoundage.setAction(ActionType.EXPENSE.getCode());
        buyerPoundage.setAmount(buyerPoundageStream != null ? Math.abs(buyerPoundageStream.getAmount()) : 0);
        buyerPoundage.setStartBalance(buyerBalance);
        buyerPoundage.setEndBalance(buyerBalance + (buyerPoundageStream != null ? buyerPoundageStream.getAmount() : 0));
        buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
        buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
        buyerPoundage.setOperateTime(paymentResult.getWhen());
        this.updateSerialRecordDoOperator(buyerPoundage, operator);
        buyerPoundage.setNotes(String.format("买方，结算单号%s", ws.getSerialNo()));
        srList.add(buyerPoundage);
        // 卖家收入
        PaymentStream sellerStream = paymentResult.getRelation().getStreams().get(0);
        Long sellerBalance = sellerStream.getBalance() - (paymentResult.getRelation().getFrozenAmount() + paymentResult.getRelation().getFrozenBalance());
        SerialRecordDo sellerIncome = this.buildBaseSellerSerialRecordDo(ws);
        sellerIncome.setTradeType(tradeType);
        sellerIncome.setAction(ActionType.INCOME.getCode());
        sellerIncome.setAmount(sellerStream.getAmount());
        sellerIncome.setStartBalance(sellerBalance);
        sellerIncome.setEndBalance(sellerBalance + sellerStream.getAmount());
        sellerIncome.setFundItem(FundItem.TRADE_PAYMENT.getCode());
        sellerIncome.setFundItemName(FundItem.TRADE_PAYMENT.getName());
        sellerIncome.setOperateTime(paymentResult.getWhen());
        this.updateSerialRecordDoOperator(sellerIncome, operator);
        sellerIncome.setNotes(String.format("卖方，结算单号%s", ws.getSerialNo()));
        srList.add(sellerIncome);
        // 卖家手续费
        PaymentStream sellerPoundageStream = paymentResult.getRelation().getStreams().stream().filter(s -> s.getType().equals((long) FundItem.TRADE_SERVICE_FEE.getCode())).findFirst().orElse(null);
        if (sellerPoundageStream != null) {
            sellerBalance = sellerPoundageStream.getBalance() - (paymentResult.getRelation().getFrozenAmount() + paymentResult.getRelation().getFrozenBalance());
        }
        SerialRecordDo sellerPoundage = this.buildBaseSellerSerialRecordDo(ws);
        sellerPoundage.setTradeType(tradeType);
        sellerPoundage.setAction(ActionType.EXPENSE.getCode());
        sellerPoundage.setAmount(sellerPoundageStream != null ? Math.abs(sellerPoundageStream.getAmount()) : 0);
        sellerPoundage.setStartBalance(sellerBalance);
        sellerPoundage.setEndBalance(sellerBalance + (sellerPoundageStream != null ? sellerPoundageStream.getAmount() : 0));
        sellerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
        sellerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
        sellerPoundage.setOperateTime(paymentResult.getWhen());
        this.updateSerialRecordDoOperator(sellerPoundage, operator);
        sellerPoundage.setNotes(String.format("卖方，结算单号%s", ws.getSerialNo()));
        srList.add(sellerPoundage);
        this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
    }

    @Override
    public void recordFreeze(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto freezePayResult) {
        User operator = this.userLoader.getById(weighingStatement.getModifierId());
        SerialRecordDo srDto = this.buildBaseBuyerSerialRecordDo(weighingStatement);
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(weighingStatement.getWeighingBillId());
        srDto.setTradeType(PaymentTradeType.PREAUTHORIZED.getValue());
        srDto.setAction(ActionType.EXPENSE.getCode());
        srDto.setAmount(freezePayResult.getFrozenAmount());
        srDto.setStartBalance(freezePayResult.getBalance() - freezePayResult.getFrozenBalance());
        srDto.setEndBalance(freezePayResult.getBalance() - (freezePayResult.getFrozenBalance() + freezePayResult.getFrozenAmount()));
        srDto.setFundItem(FundItem.TRADE_FREEZE.getCode());
        srDto.setFundItemName(FundItem.TRADE_FREEZE.getName());
        srDto.setOperateTime(freezePayResult.getWhen());
        this.updateSerialRecordDoOperator(srDto, operator);
        srDto.setNotes(String.format("冻结，过磅单号%s", weighingBill.getSerialNo()));
        this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(Arrays.asList(srDto)));
    }

    @Override
    public void recordWithdraw(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto refoundResult) {
        List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
        User operator = this.userLoader.getById(weighingStatement.getModifierId());
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(weighingStatement.getWeighingBillId());
        Integer tradeType = PaymentTradeType.TRADE.getValue();
        if (weighingStatement.getFrozenAmount() != null && weighingStatement.getFrozenAmount() > 0) {
            tradeType = PaymentTradeType.TRADE.getValue();
        }

        // 卖家退款
        PaymentStream sellerExpenseStream = refoundResult.getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
        Long sellerBalance = sellerExpenseStream.getBalance() - (refoundResult.getFrozenAmount() + refoundResult.getFrozenBalance());
        SerialRecordDo sellerExpense = this.buildBaseSellerSerialRecordDo(weighingStatement);
        sellerExpense.setTradeType(tradeType);
        sellerExpense.setAction(ActionType.EXPENSE.getCode());
        sellerExpense.setAmount(Math.abs(sellerExpenseStream.getAmount()));
        sellerExpense.setStartBalance(sellerBalance);
        sellerExpense.setEndBalance(sellerBalance + sellerExpenseStream.getAmount());
        sellerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
        sellerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
        sellerExpense.setOperateTime(refoundResult.getWhen());
        this.updateSerialRecordDoOperator(sellerExpense, operator);
        sellerExpense.setNotes(String.format("撤销，卖方，结算单号%s", weighingStatement.getSerialNo()));
        srList.add(sellerExpense);

        // 卖家手续费
        PaymentStream sellerPoundageStream = refoundResult.getStreams().stream().filter(s -> s.getType().equals((long) FundItem.TRADE_SERVICE_FEE.getCode())).findFirst().orElse(null);
        if (sellerPoundageStream != null) {
            sellerBalance = sellerPoundageStream.getBalance() - (refoundResult.getFrozenAmount() + refoundResult.getFrozenBalance());
        }
        SerialRecordDo sellerRefound = this.buildBaseSellerSerialRecordDo(weighingStatement);
        sellerRefound.setTradeType(tradeType);
        sellerRefound.setAction(ActionType.INCOME.getCode());
        sellerRefound.setAmount(sellerPoundageStream != null ? sellerPoundageStream.getAmount() : 0);
        sellerRefound.setStartBalance(sellerBalance);
        sellerRefound.setEndBalance(sellerBalance + (sellerPoundageStream != null ? sellerPoundageStream.getAmount() : 0));
        sellerRefound.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
        sellerRefound.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
        sellerRefound.setOperateTime(refoundResult.getWhen());
        this.updateSerialRecordDoOperator(sellerRefound, operator);
        sellerRefound.setNotes(String.format("撤销，卖方，结算单号%s", weighingStatement.getSerialNo()));
        srList.add(sellerRefound);

        // 买家退款
        PaymentStream buyerRefundStream = refoundResult.getRelation().getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
        Long buyerBalance = buyerRefundStream.getBalance() - (refoundResult.getRelation().getFrozenAmount() + refoundResult.getRelation().getFrozenBalance());
        SerialRecordDo buyerRefund = this.buildBaseBuyerSerialRecordDo(weighingStatement);
        buyerRefund.setTradeType(tradeType);
        buyerRefund.setAction(ActionType.INCOME.getCode());
        buyerRefund.setAmount(buyerRefundStream.getAmount());
        buyerRefund.setStartBalance(buyerBalance);
        buyerRefund.setEndBalance(buyerBalance + buyerRefundStream.getAmount());
        buyerRefund.setFundItem(FundItem.TRADE_PAYMENT.getCode());
        buyerRefund.setFundItemName(FundItem.TRADE_PAYMENT.getName());
        buyerRefund.setOperateTime(refoundResult.getWhen());
        this.updateSerialRecordDoOperator(buyerRefund, operator);
        buyerRefund.setNotes(String.format("撤销，买方，结算单号%s", weighingStatement.getSerialNo()));
        srList.add(buyerRefund);

        // 买家手续费
        PaymentStream buyerPoundageStream = refoundResult.getRelation().getStreams().stream().filter(s -> s.getType().equals((long) FundItem.TRADE_SERVICE_FEE.getCode())).findFirst().orElse(null);
        if (buyerPoundageStream != null) {
            buyerBalance = buyerPoundageStream.getBalance() - (refoundResult.getRelation().getFrozenAmount() + refoundResult.getRelation().getFrozenBalance());
        }
        SerialRecordDo buyerPoundage = this.buildBaseBuyerSerialRecordDo(weighingStatement);
        buyerPoundage.setTradeType(tradeType);
        buyerPoundage.setAction(ActionType.INCOME.getCode());
        buyerPoundage.setAmount(buyerPoundageStream != null ? buyerPoundageStream.getAmount() : 0);
        buyerPoundage.setStartBalance(buyerBalance);
        buyerPoundage.setEndBalance(buyerBalance + (buyerPoundageStream != null ? buyerPoundageStream.getAmount() : 0));
        buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
        buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
        buyerPoundage.setOperateTime(refoundResult.getWhen());
        this.updateSerialRecordDoOperator(buyerPoundage, operator);
        buyerPoundage.setNotes(String.format("撤销，买方，结算单号%s", weighingStatement.getSerialNo()));
        srList.add(buyerPoundage);

        this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
    }

    @Override
    public void recordInvalidate(WeighingStatement weighingStatement, PaymentTradeCommitResponseDto refoundResult) {
        // 解冻
        User operator = this.userLoader.getById(weighingStatement.getModifierId());
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(weighingStatement.getWeighingBillId());
        SerialRecordDo frozenRecord = this.buildBaseBuyerSerialRecordDo(weighingStatement);
        frozenRecord.setTradeType(PaymentTradeType.PREAUTHORIZED.getValue());
        frozenRecord.setAction(ActionType.INCOME.getCode());
        frozenRecord.setAmount(Math.abs(refoundResult.getFrozenAmount()));
        frozenRecord.setStartBalance(refoundResult.getBalance() - refoundResult.getFrozenBalance());
        frozenRecord.setEndBalance(refoundResult.getBalance() - (refoundResult.getFrozenAmount() + refoundResult.getFrozenBalance()));
        frozenRecord.setFundItem(FundItem.TRADE_UNFREEZE.getCode());
        frozenRecord.setFundItemName(FundItem.TRADE_UNFREEZE.getName());
        frozenRecord.setOperateTime(refoundResult.getWhen());
        this.updateSerialRecordDoOperator(frozenRecord, operator);
        frozenRecord.setNotes(String.format("作废，过磅单号%s", weighingBill.getSerialNo()));
        this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(Arrays.asList(frozenRecord)));
    }

    protected SerialRecordDo buildBaseBuyerSerialRecordDo(WeighingStatement ws) {
        SerialRecordDo frozenRecord = new SerialRecordDo();
        frozenRecord.setTradeNo(ws.getPayOrderNo());
        frozenRecord.setSerialNo(ws.getSerialNo());
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(ws.getWeighingBillId());
        frozenRecord.setCustomerType(weighingBill.getBuyerType());
        frozenRecord.setAccountId(weighingBill.getBuyerCardAccount());
        frozenRecord.setCardNo(weighingBill.getBuyerCardNo());
        frozenRecord.setHoldName(weighingBill.getBuyerCardHolderName());
        frozenRecord.setCustomerId(weighingBill.getBuyerId());
        frozenRecord.setCustomerName(weighingBill.getBuyerName());
        frozenRecord.setCustomerNo(weighingBill.getBuyerCode());
        frozenRecord.setFirmId(weighingBill.getMarketId());
        return frozenRecord;
    }

    protected SerialRecordDo buildBaseSellerSerialRecordDo(WeighingStatement ws) {
        SerialRecordDo frozenRecord = new SerialRecordDo();
        frozenRecord.setTradeNo(ws.getPayOrderNo());
        frozenRecord.setSerialNo(ws.getSerialNo());
        WeighingBill weighingBill = this.weighingBillMapper.selectByPrimaryKey(ws.getWeighingBillId());
        frozenRecord.setCustomerType(weighingBill.getSellerType());
        frozenRecord.setAccountId(weighingBill.getSellerCardAccount());
        frozenRecord.setCardNo(weighingBill.getSellerCardNo());
        frozenRecord.setHoldName(weighingBill.getSellerCardHolderName());
        frozenRecord.setCustomerId(weighingBill.getSellerId());
        frozenRecord.setCustomerName(weighingBill.getSellerName());
        frozenRecord.setCustomerNo(weighingBill.getSellerCode());
        frozenRecord.setFirmId(weighingBill.getMarketId());
        return frozenRecord;
    }

    protected void updateSerialRecordDoOperator(SerialRecordDo serialRecord, User operator) {
        serialRecord.setOperatorId(operator.getId());
        serialRecord.setOperatorName(operator.getRealName());
        serialRecord.setOperatorNo(operator.getUserName());
    }

}
