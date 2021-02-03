package com.dili.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.domain.*;
import com.dili.orders.dto.*;
import com.dili.orders.domain.PaymentWays;
import com.dili.orders.mapper.CollectionRecordMapper;
import com.dili.orders.mapper.WeighingBillOperationRecordMapper;
import com.dili.orders.mapper.WeighingStatementMapper;
import com.dili.orders.rpc.CardRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.CollectionRecordService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.ObjectView;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
@Service
public class CollectionRecordServiceImpl extends BaseServiceImpl<CollectionRecord, Long> implements CollectionRecordService {


    @Autowired
    private CollectionRecordMapper mapper;

    @Autowired
    private WeighingStatementMapper weighingStatementMapper;

    @Autowired
    private WeighingBillOperationRecordMapper weighingBillOperationRecordMapper;

    @Autowired
    private PayRpc payRpc;

    @Autowired
    private CardRpc cardRpc;

    @Autowired
    private RabbitMQMessageService rabbitMQMessageService;

    @Autowired
    private UidRpc uidRpc;


    @Override
    public PageOutput<List<CollectionRecord>> listByQueryParams(CollectionRecord collectionRecord) {
        Integer page = collectionRecord.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (collectionRecord.getRows() != null && collectionRecord.getRows() >= 1) {
            PageHelper.startPage(page, collectionRecord.getRows());
        }
        List<CollectionRecord> list = mapper.listByQueryParams(collectionRecord);
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<CollectionRecord>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total).setPageSize(collectionRecord.getPage()).setPages(totalPage);
        return output;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @GlobalTransactional(rollbackFor = Exception.class)
    public BaseOutput insertAndPay(CollectionRecord collectionRecord, String password) {
        //获取当前时间
        LocalDateTime now = LocalDateTime.now();

        //不为空则查询数据库是否有相应回款的单据，交易过磅那里查询
        List<WeighingCollectionStatementDto> list = weighingStatementMapper.listByIds(collectionRecord);
        if (CollectionUtils.isEmpty(list)) {
            return BaseOutput.failure("没有相关回款数据");
        }

        //获取idlist的长度匹配查询出来的数据长度，如果不能匹配，则无法继续操作
        if (!Objects.equals(collectionRecord.getCollectionRecordIds().size(), list.size())) {
            return BaseOutput.failure("数据失效，请刷新页面");
        }

        BaseOutput<AccountSimpleResponseDto> buyerAccountSimple = null;
        //判断是自付还是代付，如果是代付，需要使用代付的卡号去查询

        //获取买家卡账户相关信息
        if (Objects.equals(collectionRecord.getPaymentWays(), PaymentWays.TOBEREVIEWED.getCode())) {
            buyerAccountSimple = cardRpc.getOneAccountCard(collectionRecord.getBuyerCardNo());
            if (!buyerAccountSimple.isSuccess()) {
                return buyerAccountSimple;
            }
            //自付的时候将代付卡号置空
            collectionRecord.setPaymentCardNumber(null);
            //设置买家账户id
            collectionRecord.setAccountBuyerId(buyerAccountSimple.getData().getAccountInfo().getAccountId());
        } else {
            //如果是代付的话，买家卡账户还是填写卖家的，不填写代付人的卡账户
            buyerAccountSimple = cardRpc.getOneAccountCard(collectionRecord.getPaymentCardNumber());
            //判断请求是否成功
            if (!buyerAccountSimple.isSuccess()) {
                return buyerAccountSimple;
            }
            //判断是否返回有数据
            if (Objects.isNull(buyerAccountSimple.getData()) || Objects.isNull(buyerAccountSimple.getData().getAccountInfo())) {
                return BaseOutput.failure("代付卡没有查询到相关信息");
            }
            //代付的情况下，只能是主卡，判断是否是主卡
            if (!Objects.equals(buyerAccountSimple.getData().getAccountInfo().getCardType().intValue(), 10)) {
                //如果不是主卡的话，返回错误信息
                return BaseOutput.failure("代付卡只能是主卡");
            }
            BaseOutput<AccountSimpleResponseDto> oneAccountCard = cardRpc.getOneAccountCard(collectionRecord.getBuyerCardNo());
            //判断是否成功
            if (!oneAccountCard.isSuccess()) {
                return oneAccountCard;
            }
            //设置买家账户id
            collectionRecord.setAccountBuyerId(oneAccountCard.getData().getAccountInfo().getAccountId());
        }
        //获取卖家卡账户相关信息
        BaseOutput<AccountSimpleResponseDto> sellerAccountSimple = cardRpc.getOneAccountCard(collectionRecord.getSellerCardNo());

        if (!buyerAccountSimple.isSuccess()) {
            return BaseOutput.failure("根据卡查询买家失败");
        }

        if (!sellerAccountSimple.isSuccess()) {
            return BaseOutput.failure("根据卡查询卖家失败");
        }
        // 获取账户信息
        UserAccountCardResponseDto buyerAccountInfo = buyerAccountSimple.getData().getAccountInfo();
        // 获取账户资金信息
        BalanceResponseDto buyerAccountFund = buyerAccountSimple.getData().getAccountFund();

        // 余额不足
        if (Math.abs(buyerAccountFund.getBalance() - collectionRecord.getAmountActually()) < 0) {
            return BaseOutput.failure("付款账户余额不足");
        }
        // 先校验一次密码，如果密码不正确直接返回
        AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
        buyerPwdDto.setAccountId(buyerAccountInfo.getFundAccountId());
        buyerPwdDto.setPassword(password);
        BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
        // 密码不正确豁其他问题直接返回
        if (!pwdOutput.isSuccess()) {
            return pwdOutput;
        }
        //根据业务类型生成code
        BaseOutput<String> sg_collection_record_fee = uidRpc.bizNumber(UidStatic.SG_COLLECTION_RECORD_CODE);
        if (!sg_collection_record_fee.isSuccess()) {
            throw new RuntimeException(sg_collection_record_fee.getMessage());
        }
        //设置code
        collectionRecord.setCode(sg_collection_record_fee.getData());
        //设置操作时间
        collectionRecord.setOperationTime(now);

        // 判断是否支付金额是否为0，不为零再走支付
//        PaymentTradeCommitResponseDto data = null;
//        if (!Objects.equals(collectionRecord.getAmountActually(), 0L)) {

        // 先创建预支付，再调用支付接口
        PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
        // 请求与支付，两边的账户id对应关系如下
        paymentTradePrepareDto.setSerialNo("HK_" + collectionRecord.getCode());
        //设置收款方的账号相关信息
        paymentTradePrepareDto.setAccountId(sellerAccountSimple.getData().getAccountInfo().getFundAccountId());
        paymentTradePrepareDto.setType(TradeType.TRANSFER.getCode());
        paymentTradePrepareDto.setBusinessId(sellerAccountSimple.getData().getAccountInfo().getAccountId());
        paymentTradePrepareDto.setAmount(collectionRecord.getAmountActually());
        paymentTradePrepareDto.setDescription("赊销回款");
        // 创建预支付信息
        BaseOutput<CreateTradeResponseDto> prepare = payRpc.prepareTrade(paymentTradePrepareDto);
        if (!prepare.isSuccess()) {
            throw new RuntimeException("回款新增创建交易失败");
        }
        //预支付创建成功之后，需要保存交易流水号
        collectionRecord.setPaymentNo(prepare.getData().getTradeId());

        // 构建支付对象
        PaymentTradeCommitDto paymentTradeCommitDto = new PaymentTradeCommitDto();
        // 设置自己账户id
        paymentTradeCommitDto.setAccountId(buyerAccountInfo.getFundAccountId());
//            // 设置账户id
//            paymentTradeCommitDto.setBusinessId(buyerAccountInfo.getAccountId());
        // 设置密码
        paymentTradeCommitDto.setPassword(password);
        // 设置交易单号
        paymentTradeCommitDto.setTradeId(collectionRecord.getPaymentNo());

        // 设置费用
//            List<FeeDto> feeDtos = new ArrayList();
//            FeeDto feeDto = new FeeDto();
//            //设置金额，实际回款金额
//            feeDto.setAmount(collectionRecord.getAmountActually());
//            //设置资金项目类型id
//            feeDto.setType(FundItem.TRANSFER.getCode());
//            //设置资金项目类型名称
//            feeDto.setTypeName(FundItem.TRANSFER.getName());
//            feeDtos.add(feeDto);
//            paymentTradeCommitDto.setFees(feeDtos);
        // 调用支付接口
        BaseOutput<PaymentTradeCommitResponseDto> pay = payRpc.commit6(paymentTradeCommitDto);
        if (!pay.isSuccess()) {
            throw new RuntimeException(pay.getMessage());
        }
        PaymentTradeCommitResponseDto data = pay.getData();
        collectionRecord.setPayTime(data.getWhen());
        collectionRecord.setOperationTime(data.getWhen());
        now = data.getWhen();
//        }
        /**
         * 先创建回款记录单，因为交易过磅的需要汇款记录的的id
         */
        //设置交易结算日期，多个以逗号隔开
//        collectionRecord.setSettlementDate(String.join(",", collectionRecord.getBatchCollectionDate().stream().map(x -> ofPattern("yyyy-MM-dd").format(x)).collect(Collectors.toList())));
        //设置买家卡账户id
//        collectionRecord.setAccountBuyerId(buyerAccountInfo.getAccountId());
        //设置卖家卡账户id
        collectionRecord.setAccountSellerId(sellerAccountSimple.getData().getAccountInfo().getAccountId());
        //插入回款记录数据
        int insert = mapper.insert(collectionRecord);
        //判断是新增成功
        if (insert <= 0) {
            throw new RuntimeException("新增回款记录失败");
        }
        //创建批量数据
        List<WeighingBillOperationRecord> weighingBillOperationRecordList = new ArrayList<>();
        //交易过磅数据修改，交易过磅记录新增
        for (int i = 0; i < list.size(); i++) {
            //创建结算单
            WeighingStatement weighingStatement = new WeighingStatement();
            //设置结算单id
            weighingStatement.setId(list.get(i).getWeighingStatementId());
            //设置结算单version
            weighingStatement.setVersion(list.get(i).getVersion());
            //设置回款状态，已回款
            weighingStatement.setPaymentState(PaymentState.RECEIVED.getValue());
            //设置最后操作时间
            weighingStatement.setLastOperationTime(now);
            //设置最后操作人id
            weighingStatement.setLastOperatorId(collectionRecord.getOperationId());
            //设置最后操作人姓名
            weighingStatement.setLastOperatorName(collectionRecord.getOperationName());
            //设置最后操作人工号
            weighingStatement.setLastOperatorUserName(collectionRecord.getOperationUserName());
            //设置回款单id
            weighingStatement.setCollectionRecordId(collectionRecord.getId());
            //更新交易过磅单数据
            int iweighingStatement = weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
            if (iweighingStatement <= 0) {
                return BaseOutput.failure("过磅结算单修改失败");
            }

            //结算单更新之后，再新增一条过磅单的记录
            WeighingBillOperationRecord weighingBillOperationRecord = new WeighingBillOperationRecord();
            //设置操作时间
            weighingBillOperationRecord.setOperationTime(now);
            //设置操作员id
            weighingBillOperationRecord.setOperatorId(collectionRecord.getOperationId());
            //设置操作员姓名
            weighingBillOperationRecord.setOperatorName(collectionRecord.getOperationName());
            //设置操作员工号
            weighingBillOperationRecord.setOperatorUserName(collectionRecord.getOperationUserName());
            //设置过磅单id
            weighingBillOperationRecord.setWeighingBillId(Long.valueOf(list.get(i).getWeighingBillId()));
            //设置过磅单号
            weighingBillOperationRecord.setWeighingBillSerialNo(list.get(i).getWeighingBillCode());
            //设置结算单id
            weighingBillOperationRecord.setStatementId(Long.valueOf(list.get(i).getWeighingStatementId()));
            //设置结算单code
            weighingBillOperationRecord.setStatementSerialNo(list.get(i).getWeighingStatementCode());
            //设置操作类型id
            weighingBillOperationRecord.setOperationType(WeighingOperationType.COLLECTION.getValue());
            //设置操作类型名称
            weighingBillOperationRecord.setOperationTypeName(WeighingOperationType.COLLECTION.getName());
            //加入list中
            weighingBillOperationRecordList.add(weighingBillOperationRecord);
            //计算合计金额,不需要计算，因为有一个实际付款金额，以那个金额为准
            //amount += Long.valueOf(list.get(i).get("tradeAmount"));
        }
        //批量插入数据
        int w = weighingBillOperationRecordMapper.insertList(weighingBillOperationRecordList);
        if (w <= 0) {
            return BaseOutput.failure("新增过磅单交易记录失败");
        }

        //记录交易流水
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        //获取买家交易流水记录
        SerialRecordDo buyerSerialRecordDo = getSerialRecordDo(buyerAccountInfo, collectionRecord);
        //获取卖家交易流水记录
        SerialRecordDo sellerSerialRecordDo = getSerialRecordDo(sellerAccountSimple.getData().getAccountInfo(), collectionRecord);

        // 判断是否走了支付
//        if (Objects.nonNull(data)) {
        //买家设置交易流水code
        buyerSerialRecordDo.setTradeNo(collectionRecord.getPaymentNo());
        //买家设置期初余额
        buyerSerialRecordDo.setStartBalance(data.getBalance() - data.getFrozenBalance());
        //买家设置期末余额
        buyerSerialRecordDo.setEndBalance(data.getBalance() + data.getAmount() - data.getFrozenBalance());
        //买家设置操作时间
        buyerSerialRecordDo.setOperateTime(data.getWhen());
        //买家设置动作
        buyerSerialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        //设置买家卡，持卡人姓名
        buyerSerialRecordDo.setHoldName(buyerAccountInfo.getHoldName());

        //卖家设置交易流水code
        sellerSerialRecordDo.setTradeNo(collectionRecord.getPaymentNo());
        //卖家设置期初金额
        sellerSerialRecordDo.setStartBalance(data.getRelation().getBalance() - data.getRelation().getFrozenBalance());
        //卖家设置期末余额
        sellerSerialRecordDo.setEndBalance(data.getRelation().getBalance() + data.getRelation().getAmount() - data.getRelation().getFrozenBalance());
        //卖家设置操作时间
        sellerSerialRecordDo.setOperateTime(data.getWhen());
        //卖家设置动作
        sellerSerialRecordDo.setAction(data.getRelation().getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        //设置卖家卡，持卡人
        sellerSerialRecordDo.setHoldName(sellerAccountSimple.getData().getAccountInfo().getHoldName());

//        }
        //设置持卡人姓名

        // 操作记录，记录客户类型
        serialRecordList.add(buyerSerialRecordDo);
        serialRecordList.add(sellerSerialRecordDo);

        //记录日志
        //将结算单id取出转换为String类型，并用 ‘，’分割
        String wsids = String.join(",", list.stream().map(x -> String.valueOf(x.getWeighingStatementId())).collect(Collectors.toList()));
        //将结算单code取出转换为String类型，并用 ‘，’分割
        String wsCodes = String.join(",", list.stream().map(x -> x.getWeighingStatementCode()).collect(Collectors.toList()));

        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, collectionRecord.getCode());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, collectionRecord.getId());
        LoggerContext.put("statementId", wsids);
        LoggerContext.put("statementSerialNo", wsCodes);
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, collectionRecord.getOperationId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, collectionRecord.getOperationName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, collectionRecord.getMarketId());
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        return BaseOutput.success();
    }

    @Override
    public BaseOutput<List<Map<String, String>>> groupListForDetail(CollectionRecord collectionRecord) {
        return BaseOutput.successData(weighingStatementMapper.groupListForDetail(collectionRecord));
    }

    @Override
    public BaseOutput listForDetail(CollectionRecord collectionRecord) {
        return BaseOutput.successData(weighingStatementMapper.listByIds(collectionRecord));
    }

    private SerialRecordDo getSerialRecordDo(UserAccountCardResponseDto accountInfo, CollectionRecord collectionRecord) {
        // 对接操作记录
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        serialRecordDo.setAccountId(accountInfo.getAccountId());
        serialRecordDo.setCardNo(accountInfo.getCardNo());
        serialRecordDo.setAmount(collectionRecord.getAmountActually());
        serialRecordDo.setCustomerId(accountInfo.getCustomerId());
        serialRecordDo.setCustomerName(accountInfo.getCustomerName());
        serialRecordDo.setCustomerNo(accountInfo.getCustomerCode());
        serialRecordDo.setOperatorId(collectionRecord.getOperationId());
        serialRecordDo.setOperatorName(collectionRecord.getOperationName());
        serialRecordDo.setOperatorNo(collectionRecord.getOperationUserName());
        serialRecordDo.setFirmId(collectionRecord.getMarketId());
        serialRecordDo.setOperateTime(collectionRecord.getPayTime());
        serialRecordDo.setNotes("赊销回款" + collectionRecord.getCode());
        serialRecordDo.setSerialNo(collectionRecord.getCode());
        //设置资金项目类型code
        serialRecordDo.setFundItem(FundItem.TRANSFER.getCode());
        //设置资金项目类型名称
        serialRecordDo.setFundItemName(FundItem.TRANSFER.getName());
        //设置交易类型code
        serialRecordDo.setTradeType(TradeType.TRANSFER.getCode());
        return serialRecordDo;
    }
}