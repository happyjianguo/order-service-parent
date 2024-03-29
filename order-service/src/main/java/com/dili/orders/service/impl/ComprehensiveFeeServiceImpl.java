package com.dili.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.rpc.AssetsRpc;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.domain.*;
import com.dili.orders.dto.*;
import com.dili.orders.mapper.ComprehensiveFeeMapper;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.rpc.CardRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.time.LocalDate;
import java.util.List;

import com.dili.uap.sdk.rpc.UserRpc;

/**
 * 检测收费服务实现类
 *
 * @author Henry.Huang
 * @date 2020/08/20
 */
@Service
public class ComprehensiveFeeServiceImpl extends BaseServiceImpl<ComprehensiveFee, Long> implements ComprehensiveFeeService {

    @Autowired
    private UidRpc uidRpc;

    @Autowired
    private UserRpc userRpc;

    @Autowired
    private PayRpc payRpc;

    @Autowired
    private CardRpc cardRpc;

    @Autowired
    private AccountRpc accountRpc;

    @Autowired
    private RabbitMQMessageService rabbitMQMessageService;

    @Autowired
    AssetsRpc assetsRpc;


    public ComprehensiveFeeMapper getActualDao() {
        return (ComprehensiveFeeMapper) getDao();
    }


    @Override
    public PageOutput<List<ComprehensiveFee>> listByQueryParams(ComprehensiveFee comprehensiveFee) {
        Integer page = comprehensiveFee.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (comprehensiveFee.getRows() != null && comprehensiveFee.getRows() >= 1) {
            PageHelper.startPage(page, comprehensiveFee.getRows());
        }
        List<ComprehensiveFee> list = getActualDao().listByQueryParams(comprehensiveFee);
        //检测收费列表将身份类型json翻译成逗号相隔
        if(comprehensiveFee!=null&&ComprehensiveFeeType.TESTING_CHARGE.getValue().equals(comprehensiveFee.getOrderType())){
            for (ComprehensiveFee obj:list) {
                obj.setCustomerType(getCustomerTypeAndName(obj.getCustomerType()).getSubTypeTranslate());
            }
        }
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<ComprehensiveFee>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total).setPageSize(comprehensiveFee.getPage()).setPages(totalPage);
        return output;
    }

    @Override
    public BaseOutput<ComprehensiveFee> selectCountAndTotal(ComprehensiveFee comprehensiveFee) {
        ComprehensiveFee result=getActualDao().selectCountAndTotal(comprehensiveFee);
        return BaseOutput.successData(result);
    }

    @Override
    public BaseOutput<ComprehensiveFee> insertComprehensiveFee(ComprehensiveFee comprehensiveFee) {
        //设置检查收费单为未结算
        comprehensiveFee.setOrderStatus(ComprehensiveFeeState.NO_SETTLEMEN.getValue());
        //设置默认版本号为0
        comprehensiveFee.setVersion(0);
        //根据uid设置结算单的code
        BaseOutput<String> output = this.uidRpc.bizNumber(UidStatic.SG_COMPREHENSIVE_STATEMENT_CODE);
        if (!output.isSuccess()) {
            LOGGER.error(output.getMessage());
            return BaseOutput.failure(output.getMessage());
        }
        comprehensiveFee.setCode(output.getData());
        int insert = getActualDao().insert(comprehensiveFee);
        if (insert <= 0) {
            throw new AppException("检测收费新增-->创建检测收费单失败");
        }
        // 记录日志系统
        String businessName=ComprehensiveFeeType.TESTING_CHARGE.getName();;
        String businessNameKey="businessName";
        if(comprehensiveFee.getOrderType().equals(ComprehensiveFeeType.QUERY_CHARGE.getValue())){
            businessName=ComprehensiveFeeType.QUERY_CHARGE.getName();;
        }
        LoggerContext.put(businessNameKey, businessName);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, comprehensiveFee.getCode());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, comprehensiveFee.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, comprehensiveFee.getOperatorId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, comprehensiveFee.getOperatorName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, comprehensiveFee.getMarketId());
        return BaseOutput.successData(comprehensiveFee);
    }


    @Override
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<ComprehensiveFee> pay(Long id, String password, Long marketId, Long operatorId, String operatorName, String operatorUserName) {
        //根据id获取当前的结算单信息
        ComprehensiveFee comprehensiveFee = get(id);
        Integer orderType = comprehensiveFee.getOrderType();
        String updateError = "检测收费保存-->修改检测收费单失败";
        String updateAgainError = "检测收费保存-->修改检测收费单失败";
        String amountError = "检测收费金额-->缴费金额必须大于零";
        String typeName = "检测收费单号";
        int fundItemCode = FundItem.TEST_FEE.getCode();
        String fundItemName = FundItem.TEST_FEE.getName();
        //缴费原因备注
        String dec=ComprehensiveFeeType.TESTING_CHARGE.getName();
        //卡务操作流水前缀
        String serialNoPrefix="JC_";
        //操作类型名称，用于记录业务日志
        String businessName=ComprehensiveFeeType.TESTING_CHARGE.getName();
        if (ComprehensiveFeeType.QUERY_CHARGE.getValue().equals(orderType)) {
            updateError = "查询收费保存-->修改查询收费单失败";
            updateAgainError = "查询收费保存-->修改查询收费单失败";
            amountError = "查询收费金额-->缴费金额必须大于零";
            typeName = "查询收费单号";
            fundItemCode = FundItem.QUERY_FEE.getCode();
            fundItemName = FundItem.QUERY_FEE.getName();
            dec=ComprehensiveFeeType.QUERY_CHARGE.getName();
            serialNoPrefix="CX_";
            businessName=ComprehensiveFeeType.QUERY_CHARGE.getName();;
        }
        //判断结算单的支付状态是否为1（未结算）,不是则直接返回
        if (!comprehensiveFee.getOrderStatus().equals(ComprehensiveFeeState.NO_SETTLEMEN.getValue())) {
            return BaseOutput.failure("只有未结算的结算单可以结算");
        }
        setComprehensiveFeeValue(operatorId, operatorName, comprehensiveFee);
        //查询余额
        BaseOutput<AccountSimpleResponseDto> oneAccountCardForBalance = cardRpc.getOneAccountCard(comprehensiveFee.getCustomerCardNo());
        if (!oneAccountCardForBalance.isSuccess()) {
            return BaseOutput.failure("根据卡查询客户失败");
        }
        //获取账户资金信息
        BalanceResponseDto accountFund = oneAccountCardForBalance.getData().getAccountFund();
        comprehensiveFee.setBalance(accountFund.getBalance() - comprehensiveFee.getChargeAmount());
        int i = updateSelective(comprehensiveFee);
        if (i <= 0) {
            return BaseOutput.failure(updateError);
        }
        //更新完成之后，插入缴费单信息，必须在这之前发起请求，到支付系统，拿到支付单号。如果交费金额为0，则不走支付
        //首先根据卡号拿倒账户信息
        CardQueryDto dtoForpay = new CardQueryDto();
        dtoForpay.setCardNo(comprehensiveFee.getCustomerCardNo());
        BaseOutput<UserAccountCardResponseDto> oneAccountCardForPay = accountRpc.getSingle(dtoForpay);
        if (!oneAccountCardForPay.isSuccess()) {
            BaseOutput.failure(oneAccountCardForPay.getMessage());
            LOGGER.error(oneAccountCardForPay.getMessage());
            throw new AppException(oneAccountCardForPay.getMessage());
        }
        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;
        //准备单据
        BaseOutput<CreateTradeResponseDto> prepare = null;
        //操作记录
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        if (!Objects.equals(comprehensiveFee.getChargeAmount(), 0L)) {
            PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
            CardQueryDto dto = new CardQueryDto();
            dto.setCardNo(comprehensiveFee.getCustomerCardNo());
            BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getSingle(dto);
            if (!oneAccountCard.isSuccess()) {
                BaseOutput.failure(oneAccountCard.getMessage());
                LOGGER.error(oneAccountCard.getMessage());
                throw new AppException(oneAccountCard.getMessage());
            }
            //请求与支付，两边的账户id对应关系如下
            paymentTradePrepareDto.setAccountId(oneAccountCard.getData().getFundAccountId());
            //12为缴费类型
            paymentTradePrepareDto.setType(TradeType.FEE.getCode());
            paymentTradePrepareDto.setBusinessId(oneAccountCard.getData().getAccountId());
            paymentTradePrepareDto.setAmount(comprehensiveFee.getChargeAmount());
            paymentTradePrepareDto.setSerialNo(serialNoPrefix+comprehensiveFee.getCode());
            paymentTradePrepareDto.setDescription(dec);
            prepare = payRpc.prepareTrade(paymentTradePrepareDto);
            if (!prepare.isSuccess()) {
                BaseOutput.failure(prepare.getMessage());
                LOGGER.error(prepare.getMessage());
                throw new AppException(prepare.getMessage());
            }
            //设置交易单号
            comprehensiveFee.setPaymentNo(prepare.getData().getTradeId());
            int j = updateSelective(comprehensiveFee);
            if (j <= 0) {
                return BaseOutput.failure(updateAgainError);
            }

            //构建支付对象
            UserAccountCardResponseDto userAccountCardResponseDto = oneAccountCardForPay.getData();
            PaymentTradeCommitDto paymentTradeCommitDto = new PaymentTradeCommitDto();
            setPaymentTradeCommitDtoValue(password, comprehensiveFee, typeName, userAccountCardResponseDto, paymentTradeCommitDto);
            //调用rpc支付
            BaseOutput<PaymentTradeCommitResponseDto> pay = payRpc.pay(paymentTradeCommitDto);
            if (!pay.isSuccess()) {
                BaseOutput.failure(pay.getMessage());
                LOGGER.error(pay.getMessage());
                throw new AppException(pay.getMessage());
            }
            data = pay.getData();
        } else {
            BaseOutput<AccountSimpleResponseDto> oneAccountCard = cardRpc.getOneAccountCard(comprehensiveFee.getCustomerCardNo());
            //获取账户信息
            UserAccountCardResponseDto accountInfo = oneAccountCard.getData().getAccountInfo();
            //校验密码后密码通过则支付成功，不通过 则提示密码错误
            //先校验一次密码，如果密码不正确直接返回
            AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
            buyerPwdDto.setAccountId(accountInfo.getFundAccountId());
            buyerPwdDto.setPassword(password);
            BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
            //密码不正确豁其他问题直接返回
            if (!pwdOutput.isSuccess()) {
                BaseOutput.failure(pwdOutput.getMessage());
                LOGGER.error(pwdOutput.getMessage());
                throw new AppException(pwdOutput.getMessage());
            }
            serialRecordDo.setStartBalance(accountFund.getAvailableAmount());
            serialRecordDo.setEndBalance(accountFund.getAvailableAmount());
            serialRecordDo.setOperateTime(LocalDateTime.now());
            serialRecordDo.setAction(ActionType.EXPENSE.getCode());
            serialRecordDo.setTradeChannel(null);
        }
        //对接操作记录
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setAmount(data.getAmount());
            //期初余额
            serialRecordDo.setStartBalance(data.getBalance() - data.getFrozenBalance());
            serialRecordDo.setEndBalance(data.getBalance() + data.getAmount() - data.getFrozenBalance());
            serialRecordDo.setOperateTime(data.getWhen());
            serialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        }
        if (prepare != null && Objects.nonNull(prepare.getData())) {
            serialRecordDo.setTradeNo(prepare.getData().getTradeId());
            serialRecordDo.setTradeType(TradeType.FEE.getCode());
        }
        setSerialRecordDoValue(marketId, operatorId, operatorName, operatorUserName, comprehensiveFee, typeName, fundItemCode, fundItemName, oneAccountCardForPay, serialRecordDo);
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        // 记录日志系统
        //操作名称
        String businessNameKey="businessName";
        LoggerContext.put(businessNameKey, businessName);
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, comprehensiveFee.getCode());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, comprehensiveFee.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, comprehensiveFee.getOperatorId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, comprehensiveFee.getOperatorName());
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, comprehensiveFee.getMarketId());
        return BaseOutput.successData(comprehensiveFee);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseOutput<String> scheduleUpdate() {
        LOGGER.info("综合收费将前天未结算单据关闭定时任务开始");
        ComprehensiveFee comprehensiveFee = new ComprehensiveFee();
        //设置查询参数，查询日期使用的是Date类型
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN);
        LocalDateTime today_end = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MAX);
        comprehensiveFee.setOperatorTimeStart(today_start);
        comprehensiveFee.setOperatorTimeEnd(today_end);
        //根据日期筛选出前一天的所有未结算的单子
        List<ComprehensiveFee> list = getActualDao().scheduleUpdateSelect(comprehensiveFee);
        if (CollectionUtils.isNotEmpty(list)) {
            HashSet<Long> cfIds = new HashSet<>();
            //循环遍历，获取申请单id，更新拿到申请id和结算单id
            for (int i = 0; i < list.size(); i++) {
                ComprehensiveFee comprehensiveFeeUpdate = list.get(i);
                cfIds.add(comprehensiveFeeUpdate.getId());
            }
            //如果申请单id集合和结算单id集合不为空则更新
            if (CollectionUtils.isNotEmpty(cfIds)) {
                getActualDao().scheduleUpdate(cfIds);
            }
        }
        LOGGER.info("综合收费将前天未结算单据关闭定时任务结束");
        return BaseOutput.success("综合收费将前一天未结算单据关闭定时任务执行成功!");

    }

    /**
     * 撤销控制
     *
     * @return
     */
    @Override
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<ComprehensiveFee> revocator(ComprehensiveFee comprehensiveFee, Long operatorId, String realName, String operatorPassword, String userName) {
        //更新检测收费单
        int rows = getActualDao().updateByIdAndVersion(comprehensiveFee);
        //判断结算单修改是否成功，不成功则抛出异常
        if (rows <= 0) {
            LOGGER.error("当前状态不能撤销或已撤销，请刷新列表页面查看");
            BaseOutput.failure("当前状态不能撤销或已撤销，请刷新列表页面查看");
            throw new AppException("当前状态不能撤销或已撤销，请刷新列表页面查看");
        }
        String typeName = "撤销，检测收费单号";
        int fundItemCode = FundItem.TEST_FEE.getCode();
        String fundItemName = FundItem.TEST_FEE.getName();
        //判断当前的这个结算单是否是今天的
        LocalDate createTime = comprehensiveFee.getCreatedTime().toLocalDate();
        //如果为0，则表示为当天
        if (LocalDate.now().compareTo(createTime) != 0) {
            BaseOutput.failure("只有当天的结算单可以撤销");
            throw new AppException("只有当天的结算单可以撤销");
        }
        // 校验操作员密码
        BaseOutput<Object> pwdOutput = this.userRpc.validatePassword(operatorId, operatorPassword);
        if (!pwdOutput.isSuccess()) {
            LOGGER.error(pwdOutput.getMessage());
            BaseOutput.failure(pwdOutput.getMessage());
            throw new AppException(pwdOutput.getMessage());
        }
        //查询余额
        BaseOutput<AccountSimpleResponseDto> oneAccountCardForBalance = cardRpc.getOneAccountCard(comprehensiveFee.getCustomerCardNo());
        if (!oneAccountCardForBalance.isSuccess()) {
            LOGGER.error(oneAccountCardForBalance.getMessage());
            BaseOutput.failure(oneAccountCardForBalance.getMessage());
            throw new AppException(oneAccountCardForBalance.getMessage());
        }
        //获取账户资金信息
        BalanceResponseDto accountFund = oneAccountCardForBalance.getData().getAccountFund();
        //调用卡号查询账户信息
        CardQueryDto dto = new CardQueryDto();
        dto.setCardNo(comprehensiveFee.getCustomerCardNo());
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getSingle(dto);
        if (!oneAccountCard.isSuccess()) {
            BaseOutput.failure(oneAccountCard.getMessage());
            LOGGER.error(oneAccountCard.getMessage());
            throw new AppException(oneAccountCard.getMessage());
        }
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;
        //判断支付金额是否为0元，0元不走支付
        if (!Objects.equals(comprehensiveFee.getChargeAmount(), 0L)) {
            // 退款，并判断是否存在交易单号
            if (StringUtils.isNotBlank(comprehensiveFee.getPaymentNo())) {
                PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
                cancelDto.setTradeId(comprehensiveFee.getPaymentNo());
                BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
                if (!paymentOutput.isSuccess()) {
                    LOGGER.error(paymentOutput.getMessage());
                    throw new AppException(paymentOutput.getMessage());
                }
                data = paymentOutput.getData();
            }
        } else {
            serialRecordDo.setStartBalance(accountFund.getAvailableAmount());
            serialRecordDo.setEndBalance(accountFund.getAvailableAmount());
            serialRecordDo.setOperateTime(LocalDateTime.now());
            serialRecordDo.setAction(ActionType.INCOME.getCode());
            serialRecordDo.setTradeChannel(null);
        }

        //对接操作记录
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        setSerialRecordDoValue(comprehensiveFee.getMarketId(), operatorId, realName, userName, comprehensiveFee, typeName, fundItemCode, fundItemName, oneAccountCard, serialRecordDo);
        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setTradeType(TradeType.CANCEL.getCode());
            serialRecordDo.setTradeNo(comprehensiveFee.getPaymentNo());
            serialRecordDo.setAmount(data.getAmount());
            //期初余额
            serialRecordDo.setStartBalance(data.getBalance() - data.getFrozenBalance());
            serialRecordDo.setEndBalance(data.getBalance() + data.getAmount() - data.getFrozenBalance());
            serialRecordDo.setOperateTime(data.getWhen());
            serialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        }
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        // 记录日志系统
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, comprehensiveFee.getCode());
        LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, comprehensiveFee.getId());
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, operatorId);
        LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, realName);
        LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, comprehensiveFee.getMarketId());
        return BaseOutput.successData(comprehensiveFee);
    }

    /**
     * 设置支付准备类的值
     *
     * @param password                   密码
     * @param comprehensiveFee           综合收费单据
     * @param typeName                   单据类型名称
     * @param userAccountCardResponseDto 构建支付对象
     * @param paymentTradeCommitDto      即时交易请求参数
     */
    private void setPaymentTradeCommitDtoValue(String password, ComprehensiveFee comprehensiveFee, String typeName, UserAccountCardResponseDto userAccountCardResponseDto, PaymentTradeCommitDto paymentTradeCommitDto) {
        //设置自己账户id
        paymentTradeCommitDto.setAccountId(userAccountCardResponseDto.getFundAccountId());
        //设置账户id
        paymentTradeCommitDto.setBusinessId(userAccountCardResponseDto.getAccountId());
        //设置密码
        paymentTradeCommitDto.setPassword(password);
        //设置交易单号
        paymentTradeCommitDto.setTradeId(comprehensiveFee.getPaymentNo());
        //设置费用
        List<FeeDto> feeDtos = new ArrayList();
        FeeDto feeDto = new FeeDto();
        feeDto.setAmount(comprehensiveFee.getChargeAmount());
        feeDto.setType(FundItem.TEST_FEE.getCode());
        feeDto.setTypeName(typeName);
        feeDtos.add(feeDto);
        paymentTradeCommitDto.setFees(feeDtos);
    }

    /**
     * 设置综合收费单据值
     *
     * @param operatorId       操作人ID
     * @param operatorName     错做人名称
     * @param comprehensiveFee 单据
     */
    private void setComprehensiveFeeValue(Long operatorId, String operatorName, ComprehensiveFee comprehensiveFee) {
        //设置为已支付状态
        comprehensiveFee.setOrderStatus(ComprehensiveFeeState.SETTLED.getValue());
        //设置支付时间
        comprehensiveFee.setOperatorTime(LocalDateTime.now());
        //更新操作员
        comprehensiveFee.setOperatorId(operatorId);
        comprehensiveFee.setOperatorName(operatorName);
        //更新更新人和更新时间
        comprehensiveFee.setModifierId(operatorId);
        comprehensiveFee.setModifiedTime(LocalDateTime.now());
    }

    /**
     * 设置操作记录值
     *
     * @param marketId         市场id
     * @param operatorId       操作人ID
     * @param operatorName     操作人名称
     * @param operatorUserName 操作人用户
     * @param comprehensiveFee 综合收费单据
     * @param typeName         单据类型名称
     * @param fundItemCode     资金类型编码
     * @param fundItemName     资金类型名称
     * @param oneAccountCard   卡信息返回数据
     * @param serialRecordDo   操作记录DO
     */
    private void setSerialRecordDoValue(Long marketId, Long operatorId, String operatorName, String operatorUserName, ComprehensiveFee comprehensiveFee, String typeName, int fundItemCode, String fundItemName, BaseOutput<UserAccountCardResponseDto> oneAccountCard, SerialRecordDo serialRecordDo) {
        serialRecordDo.setAccountId(oneAccountCard.getData().getAccountId());
        serialRecordDo.setCardNo(oneAccountCard.getData().getCardNo());
        serialRecordDo.setAmount(comprehensiveFee.getChargeAmount());
        serialRecordDo.setCustomerId(oneAccountCard.getData().getCustomerId());
        serialRecordDo.setCustomerName(comprehensiveFee.getCustomerName());
        serialRecordDo.setCustomerNo(comprehensiveFee.getCustomerCode());
        serialRecordDo.setOperatorId(operatorId);
        serialRecordDo.setOperatorName(operatorName);
        serialRecordDo.setOperatorNo(operatorUserName);
        serialRecordDo.setFirmId(marketId);
        serialRecordDo.setNotes(typeName + comprehensiveFee.getCode());
        serialRecordDo.setFundItem(fundItemCode);
        serialRecordDo.setFundItemName(fundItemName);
        serialRecordDo.setSerialNo(comprehensiveFee.getCode());
        serialRecordDo.setCustomerType(getCustomerTypeAndName(comprehensiveFee.getCustomerType()).getSubType());
        serialRecordDo.setHoldName(comprehensiveFee.getHoldName());
    }

    /**
     * 将json数据转换成以逗号相隔
     * @param customerType
     * @return
     */
    private CustomerView getCustomerTypeAndName(String customerType) {
        CustomerView customerView= new CustomerView();
        if(customerType!=null&&!"".equals(customerType)){
            List<Map<String,String>> customerTypeJsonInfo= JSON.parseObject(customerType,List.class);
            StringBuffer subType=new StringBuffer();
            StringBuffer subTypeTranslate=new StringBuffer();
            String sliptStr=",";
            for (Map map:customerTypeJsonInfo) {
                subType.append(map.get("subType"));
                subType.append(sliptStr);
                subTypeTranslate.append(map.get("subTypeTranslate"));
                subTypeTranslate.append(sliptStr);
            }
            int length=subType.length();
            if(length>0){
                customerType=subType.substring(0,length-1);
            }
            if(subTypeTranslate.length()>0){
                customerView.setSubTypeTranslate(subTypeTranslate.substring(0,subTypeTranslate.length()-1));
            }
            customerView.setSubType(customerType);
        }
        return customerView;
    }
}
