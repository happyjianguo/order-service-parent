package com.dili.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.dto.CarTypeForBusinessDTO;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.jmsf.microservice.sdk.dto.VehicleAccessDTO;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.dto.*;
import com.dili.orders.glossary.BizTypeEnum;
import com.dili.orders.glossary.CustomerType;
import com.dili.orders.glossary.PayStatusEnum;
import com.dili.orders.mapper.TransitionDepartureApplyMapper;
import com.dili.orders.mapper.TransitionDepartureSettlementMapper;
import com.dili.orders.rpc.*;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.orders.service.TransitionDepartureSettlementService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.uap.sdk.rpc.UserRpc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 由于支付没有介入seta，所有把支付相关操作放在最后
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@Service
public class TransitionDepartureSettlementServiceImpl extends BaseServiceImpl<TransitionDepartureSettlement, Long> implements TransitionDepartureSettlementService {


    public TransitionDepartureSettlementMapper getActualDao() {
        return (TransitionDepartureSettlementMapper) getDao();
    }

    @Autowired
    private TransitionDepartureApplyMapper transitionDepartureApplyMapper;

    @Autowired
    private TransitionDepartureApplyService transitionDepartureApplyService;

    @Autowired
    private UidRpc uidRpc;

    @Autowired
    private JmsfRpc jmsfRpc;

    @Autowired
    private AccountRpc accountRpc;

    @Autowired
    private PayRpc payRpc;

    @Autowired
    private CardRpc cardRpc;

    @Autowired
    private UserRpc userRpc;

    @Autowired
    private AssetsRpc assetsRpc;

    @Autowired
    private RabbitMQMessageService rabbitMQMessageService;

    @Override
    public PageOutput<List<TransitionDepartureSettlement>> listByQueryParams(TransitionDepartureSettlement transitionDepartureSettlement) {
        //判断是否传入日期，没有传入的话默认当天
//        if (Objects.isNull(transitionDepartureSettlement.getBeginTime())) {
//            transitionDepartureSettlement.setBeginTime(LocalDate.now());
//        }
//        if (Objects.isNull(transitionDepartureSettlement.getEndTime())) {
//            transitionDepartureSettlement.setEndTime(LocalDate.now());
//        }
        Integer page = transitionDepartureSettlement.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (transitionDepartureSettlement.getRows() != null && transitionDepartureSettlement.getRows() >= 1) {
            PageHelper.startPage(page, transitionDepartureSettlement.getRows());
        }
        List<TransitionDepartureSettlement> list = getActualDao().listByQueryParams(transitionDepartureSettlement);
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<TransitionDepartureSettlement>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total.intValue()).setPageSize(transitionDepartureSettlement.getPage()).setPages(totalPage);
        return output;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void scheduleUpdate() {
        TransitionDepartureSettlement transitionDepartureSettlement = new TransitionDepartureSettlement();
        //拿到前一天的0时和23:59:59时
//        Map<String, String> beforeDate = getBeforeDate();
        //设置查询参数
        //查询日期使用的是Date类型
        transitionDepartureSettlement.setBeginTime(LocalDate.now().plusDays(-1));
        transitionDepartureSettlement.setEndTime(LocalDate.now().plusDays(-1));
        //根据日期筛选出前一天的所有未结算的单子
        List<TransitionDepartureSettlement> list = getActualDao().scheduleUpdateSelect(transitionDepartureSettlement);
        if (CollectionUtils.isNotEmpty(list)) {
            HashSet<Long> applyIds = new HashSet<>();
            HashSet<Long> settlementIds = new HashSet<>();
            //循环遍历，获取申请单id，更新拿到申请id和结算单id
            for (int i = 0; i < list.size(); i++) {
                TransitionDepartureSettlement transitionDepartureSettlement1 = list.get(i);
                applyIds.add(transitionDepartureSettlement1.getApplyId());
                settlementIds.add(transitionDepartureSettlement1.getId());
            }
            //如果申请单id集合和结算单id集合不为空则更新
            if (CollectionUtils.isNotEmpty(applyIds) && CollectionUtils.isNotEmpty(settlementIds)) {
                transitionDepartureApplyMapper.scheduleUpdate(applyIds);
                getActualDao().scheduleUpdate(settlementIds);
            }
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseOutput<TransitionDepartureSettlement> insertTransitionDepartureSettlement(TransitionDepartureSettlement transitionDepartureSettlement, Long marketId) {
        //设置支付状态为未结算
//        transitionDepartureSettlement.setPayStatus(1);
        transitionDepartureSettlement.setPayStatus(PayStatusEnum.UNSETTLED.getCode());
        //根据申请单id拿到申请单，修改申请单的支付状态为1（未结算）
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());
        if (Objects.isNull(transitionDepartureApply)) {
            throw new RuntimeException("转离场支付未找到相关申请单");
        }
        transitionDepartureSettlement.setPlate(transitionDepartureSettlement.getPlate().toUpperCase());
        //进门收费新增需要保存车型明，车型code。车型id
        CarTypeForBusinessDTO carTypeForJmsfDTO = new CarTypeForBusinessDTO();
        carTypeForJmsfDTO.setBusinessCode(MyBusinessType.KCJM.getCode());
        carTypeForJmsfDTO.setMarketId(marketId);
        carTypeForJmsfDTO.setId(transitionDepartureSettlement.getCarTypeId());
        BaseOutput<List<CarTypeForBusinessDTO>> listBaseOutput = assetsRpc.listCarType(carTypeForJmsfDTO);
        if (!listBaseOutput.isSuccess()) {
            throw new RuntimeException("进门收费车型查询失败");
        }

        //因为可以修改，所以需要从新获取车型id和名称
        //同时更新申请单的，车辆信息
        transitionDepartureApply.setCarTypeId(transitionDepartureSettlement.getCarTypeId());
        transitionDepartureApply.setCarTypeName(listBaseOutput.getData().get(0).getCarTypeName());
        //车牌号也允许修改，所以也要设置车牌号
        transitionDepartureApply.setPlate(transitionDepartureSettlement.getPlate());
        //更新申请单的结算状态为未结算
//        transitionDepartureApply.setPayStatus(1);
        transitionDepartureApply.setPayStatus(PayStatusEnum.UNSETTLED.getCode());
        int i = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        if (i <= 0) {
            return BaseOutput.failure("转离场保存修改申请单失败");
        }
        //因为涉及到可能金额会变动，同意放到支付中获取
        //更新完成之后，插入缴费单信息，必须在这之前发起请求，到支付系统，拿到支付单号
        //如果交费金额为0，则不走支付
//        if (!Objects.equals(transitionDepartureSettlement.getChargeAmount(), 0L)) {
//            PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
//            BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
//            if (!oneAccountCard.isSuccess()) {
//                throw new RuntimeException("转离场结算单新增根据卡号获取账户信息失败");
//            }
//            //请求与支付，两边的账户id对应关系如下
//            paymentTradePrepareDto.setAccountId(oneAccountCard.getData().getFundAccountId());
//            paymentTradePrepareDto.setType(12);
//            paymentTradePrepareDto.setBusinessId(oneAccountCard.getData().getAccountId());
//            paymentTradePrepareDto.setAmount(transitionDepartureSettlement.getChargeAmount());
//            BaseOutput<CreateTradeResponseDto> prepare = payRpc.prepareTrade(paymentTradePrepareDto);
//            if (!prepare.isSuccess()) {
//                throw new RuntimeException("转离场结算单新增创建交易失败");
//            }
//            //设置交易单号
//            transitionDepartureSettlement.setPaymentNo(prepare.getData().getTradeId());
//        }
        //根据uid设置结算单的code
        BaseOutput<String> sg_zlc_settlement = uidRpc.bizNumber("sg_zlc_settlement");
        if (!sg_zlc_settlement.isSuccess()) {
            throw new RuntimeException(sg_zlc_settlement.getMessage());
        }
        transitionDepartureSettlement.setCode(sg_zlc_settlement.getData());
        transitionDepartureSettlement.setCarTypeName(listBaseOutput.getData().get(0).getCarTypeName());
        int insert = getActualDao().insert(transitionDepartureSettlement);
        if (insert <= 0) {
            throw new RuntimeException("转离场结算单新增创建转离场结算单失败");
        }
        return BaseOutput.successData(transitionDepartureSettlement);
    }

    /**
     * 先根据卡号查询账户信息和账户资金信息，先掉进门，再调用支付判断密码是否正确，，操作记录
     * 在此过程中，支付会先预支付，获得交易流水号，再调用结算，如果成功，则更新结算单先关信息
     *
     * @param id
     * @param password
     * @param marketId
     * @param departmentId
     * @param operatorCode
     * @param operatorId
     * @param operatorName
     * @param operatorUserName
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @GlobalTransactional
    public BaseOutput pay(Long id, String password, Long marketId, Long departmentId, String operatorCode, Long operatorId, String operatorName, String operatorUserName) {
        //根据id获取当前的结算单信息
        TransitionDepartureSettlement transitionDepartureSettlement = get(id);

        //判断结算单的支付状态是否为1（未结算）,不是则直接返回
//        if (transitionDepartureSettlement.getPayStatus() != 1) {
//            return BaseOutput.failure("只有未结算的结算单可以结算");
//        }
        if (!Objects.equals(transitionDepartureSettlement.getPayStatus(), PayStatusEnum.UNSETTLED.getCode())) {
            return BaseOutput.failure("只有未结算的结算单可以结算");
        }

        //判断余额是否够用
        BaseOutput<AccountSimpleResponseDto> oneAccountCard1 = cardRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
        if (!oneAccountCard1.isSuccess()) {
            return BaseOutput.failure("根据卡查询客户失败");
        }
        //获取账户信息
        UserAccountCardResponseDto accountInfo = oneAccountCard1.getData().getAccountInfo();
        //获取账户资金信息
        BalanceResponseDto accountFund = oneAccountCard1.getData().getAccountFund();

        //余额不足
        if (Math.abs(accountFund.getBalance() - transitionDepartureSettlement.getChargeAmount()) < 0) {
            return BaseOutput.failure("余额不足，请充值");
        }

        //先校验一次密码，如果密码不正确直接返回
        AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
        buyerPwdDto.setAccountId(accountInfo.getFundAccountId());
        buyerPwdDto.setPassword(password);
        BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
        //密码不正确豁其他问题直接返回
        if (!pwdOutput.isSuccess()) {
            return pwdOutput;
        }

        //设置为已支付状态
//        transitionDepartureSettlement.setPayStatus(2);
        transitionDepartureSettlement.setPayStatus(PayStatusEnum.SETTLED.getCode());
        //设置客户身份类型
        transitionDepartureSettlement.setCustomerMarketType(accountInfo.getCustomerMarketType());
        //设置客户身份类型中文
        transitionDepartureSettlement.setCustomerMarketTypeCN(CustomerType.getTypeName(transitionDepartureSettlement.getCustomerMarketType()));
        //根据结算单apply_id获取到对应申请单
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());

        //判断申请单是否存在
        if (Objects.isNull(transitionDepartureApply)) {
            return BaseOutput.failure("申请单不存在");
        }

        //获取到申请单
        //设置申请单支付状态为已支付
//        transitionDepartureApply.setPayStatus(2);
        transitionDepartureApply.setPayStatus(PayStatusEnum.SETTLED.getCode());
        int i = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        if (i <= 0) {
            return BaseOutput.failure("修改申请单状态失败");
        }

        //设置支付时间
        transitionDepartureSettlement.setPayTime(LocalDateTime.now());

        //更新操作员（有可能不是同一个操作员，所以需要更新）
        transitionDepartureSettlement.setOperatorId(operatorId);
        transitionDepartureSettlement.setOperatorName(operatorName);
        transitionDepartureSettlement.setOperatorCode(operatorCode);

        //设置进门收费相关信息，并调用新增
        VehicleAccessDTO vehicleAccessDTO = new VehicleAccessDTO();
        vehicleAccessDTO.setMarketId(marketId);
        vehicleAccessDTO.setPlateNumber(transitionDepartureSettlement.getPlate());

        //进门收费新增需要保存车型明，车型code。车型id，因为结算单中没有冗余，所以需要到进门收费中去查询
        CarTypeForBusinessDTO carTypeForJmsfDTO = new CarTypeForBusinessDTO();
        carTypeForJmsfDTO.setBusinessCode(MyBusinessType.KCJM.getCode());
        carTypeForJmsfDTO.setMarketId(marketId);
        carTypeForJmsfDTO.setId(transitionDepartureSettlement.getCarTypeId());
        BaseOutput<List<CarTypeForBusinessDTO>> listBaseOutput = assetsRpc.listCarType(carTypeForJmsfDTO);
        if (!listBaseOutput.isSuccess()) {
            throw new RuntimeException("进门收费车型查询失败");
        }
        //获取到进门收费车型信息之后，因为是根据车类型id查询的，所以只有一条数据，所以调用如下
        vehicleAccessDTO.setVehicleTypeId(listBaseOutput.getData().get(0).getId());
        //新增车类型名
        vehicleAccessDTO.setVehicleTypeName(listBaseOutput.getData().get(0).getCarTypeName());
        //新增车类型code
        vehicleAccessDTO.setVehicleTypeCode(listBaseOutput.getData().get(0).getCode());
        vehicleAccessDTO.setBarrierType(3);
        vehicleAccessDTO.setAmount(transitionDepartureSettlement.getChargeAmount());
        vehicleAccessDTO.setPayType(3);
        vehicleAccessDTO.setCasherId(operatorId);
        vehicleAccessDTO.setCasherName(operatorName);
        vehicleAccessDTO.setCasherDepartmentId(departmentId);
        vehicleAccessDTO.setPayTime(LocalDateTime.now());
        vehicleAccessDTO.setOperatorId(operatorId);
        vehicleAccessDTO.setOperatorName(operatorName);
        vehicleAccessDTO.setCreated(LocalDateTime.now());
        vehicleAccessDTO.setCardNo(transitionDepartureSettlement.getCustomerCardNo());
        vehicleAccessDTO.setCustomerName(transitionDepartureSettlement.getCustomerName());
        vehicleAccessDTO.setCustomerId(String.valueOf(transitionDepartureSettlement.getCustomerId()));
        //进门收费也需要客户身份类型，进门暂未增加字段，后期补上
        vehicleAccessDTO.setCustomerType(transitionDepartureSettlement.getCustomerMarketTypeCN());
        vehicleAccessDTO.setCustomerTypeCode(transitionDepartureSettlement.getCustomerMarketType());
        //判断进门收费新增是否成功
        BaseOutput<VehicleAccessDTO> vehicleAccessDTOBaseOutput = jmsfRpc.add(vehicleAccessDTO);
        if (!vehicleAccessDTOBaseOutput.isSuccess()) {
            throw new RuntimeException(vehicleAccessDTOBaseOutput.getMessage());
        }

        //将进门收费返回的id设置到结算单中
        transitionDepartureSettlement.setJmsfId(vehicleAccessDTOBaseOutput.getData().getId());

        //再调用支付
        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;
        //判断是否支付金额是否为0，不为零再走支付
        if (!Objects.equals(transitionDepartureSettlement.getChargeAmount(), 0L)) {
            //先创建预支付，再调用支付接口
            PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
            //请求与支付，两边的账户id对应关系如下
            paymentTradePrepareDto.setAccountId(accountInfo.getFundAccountId());
            paymentTradePrepareDto.setType(12);
            paymentTradePrepareDto.setBusinessId(accountInfo.getAccountId());
            paymentTradePrepareDto.setAmount(transitionDepartureSettlement.getChargeAmount());
            //创建预支付信息
            BaseOutput<CreateTradeResponseDto> prepare = payRpc.prepareTrade(paymentTradePrepareDto);
            if (!prepare.isSuccess()) {
                throw new RuntimeException("转离场结算单新增创建交易失败");
            }

            //结算单中设置交易单号
            transitionDepartureSettlement.setPaymentNo(prepare.getData().getTradeId());

            //构建支付对象
            PaymentTradeCommitDto paymentTradeCommitDto = new PaymentTradeCommitDto();
            //设置自己账户id
            paymentTradeCommitDto.setAccountId(accountInfo.getFundAccountId());
            //设置账户id
            paymentTradeCommitDto.setBusinessId(accountInfo.getAccountId());
            //设置密码
            paymentTradeCommitDto.setPassword(password);
            //设置交易单号
            paymentTradeCommitDto.setTradeId(transitionDepartureSettlement.getPaymentNo());
            //设置费用
            List<FeeDto> feeDtos = new ArrayList();
            FeeDto feeDto = new FeeDto();
            feeDto.setAmount(transitionDepartureSettlement.getChargeAmount());
            feeDto.setType(31);
            feeDto.setTypeName("转离场收费");
            feeDtos.add(feeDto);
            paymentTradeCommitDto.setFees(feeDtos);

            //调用支付接口
            BaseOutput<PaymentTradeCommitResponseDto> pay = payRpc.pay(paymentTradeCommitDto);
            if (!pay.isSuccess()) {
                throw new RuntimeException(pay.getMessage());
            }
            data = pay.getData();
        }

        //再更新结算单信息
        int i2 = getActualDao().updateByPrimaryKeySelective(transitionDepartureSettlement);
        //判断是否修改成功
        if (i2 <= 0) {
            throw new RuntimeException("转离场结算单支付修改结算单失败");
        }
        //对接操作记录
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        serialRecordDo.setAccountId(accountInfo.getAccountId());
        serialRecordDo.setCardNo(accountInfo.getCardNo());
        serialRecordDo.setAmount(transitionDepartureSettlement.getChargeAmount());
        serialRecordDo.setCustomerId(accountInfo.getCustomerId());
        serialRecordDo.setCustomerName(transitionDepartureSettlement.getCustomerName());
        serialRecordDo.setCustomerNo(transitionDepartureSettlement.getCustomerCode());
        serialRecordDo.setOperatorId(operatorId);
        serialRecordDo.setOperatorName(operatorName);
        serialRecordDo.setOperatorNo(operatorUserName);
        serialRecordDo.setFirmId(marketId);
        serialRecordDo.setOperateTime(LocalDateTime.now());
        //判断是转场还是离场1.转场 2.离场
        if (Objects.equals(transitionDepartureSettlement.getBizType(), BizTypeEnum.TRANSITION.getCode())) {
            serialRecordDo.setNotes("转离场单号" + transitionDepartureSettlement.getCode());
            serialRecordDo.setFundItem(FundItem.TRANSFER_FEE.getCode());
            serialRecordDo.setFundItemName(FundItem.TRANSFER_FEE.getName());
        } else {
            serialRecordDo.setNotes("转离场单号" + transitionDepartureSettlement.getCode());
            serialRecordDo.setFundItem(FundItem.LEAVE_FEE.getCode());
            serialRecordDo.setFundItemName(FundItem.LEAVE_FEE.getName());
        }
        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setStartBalance(data.getBalance());
            //返回的值是负值，还是加就行了
            serialRecordDo.setEndBalance(data.getBalance() + data.getAmount());
            serialRecordDo.setOperateTime(data.getWhen());
            serialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        }
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        return BaseOutput.successData(transitionDepartureSettlement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @GlobalTransactional
    public BaseOutput<TransitionDepartureSettlement> revocator(TransitionDepartureSettlement transitionDepartureSettlement, Long revocatorId, String revocatorPassword) {

//        //只能撤销当天的结算单
//        if (!compareDate(transitionDepartureSettlement.getPayTime())) {
//            return BaseOutput.failure("只能撤销当天的结算单");
//        }

        // 校验操作员密码
        BaseOutput<Object> userOutput = this.userRpc.validatePassword(revocatorId, revocatorPassword);
        if (!userOutput.isSuccess()) {
            return BaseOutput.failure("操作员密码错误");
        }

        //判断结算单的支付状态是否为2（已结算）,不是则直接返回
        if (!Objects.equals(transitionDepartureSettlement.getPayStatus(), PayStatusEnum.SETTLED.getCode())) {
            return BaseOutput.failure("只有已结算的结算单可以撤销");
        }

        //根据jmsfid拿到进门单，判断是否已经出场，出场则不能撤销
        BaseOutput<VehicleAccessDTO> access = jmsfRpc.getAccess(transitionDepartureSettlement.getJmsfId());
        if (!access.isSuccess()) {
            return BaseOutput.failure("进门数据请求失败");
        }

        //判断是否已经出场，2为已出场
        if (Objects.equals(access.getData().getAccessStatus(), 2)) {
            return BaseOutput.failure("车辆已出场，不能撤销");
        }

        //判断当前的这个结算单是否是今天的
        LocalDate createTime = transitionDepartureSettlement.getCreateTime().toLocalDate();

        //如果为0，则表示为当天
        if (LocalDate.now().compareTo(createTime) != 0) {
            return BaseOutput.failure("只有当天的结算单可以撤销");
        }

        //设置为已撤销的支付状态
//        transitionDepartureSettlement.setPayStatus(3);
        transitionDepartureSettlement.setPayStatus(PayStatusEnum.RESCINDED.getCode());

        //根据结算单的apply_id拿到申请单信息
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());
        if (Objects.isNull(transitionDepartureApply)) {
            return BaseOutput.failure("申请单不存在");
        }

        //设置申请单支付状态为已撤销
//        transitionDepartureApply.setPayStatus(3);
        transitionDepartureApply.setPayStatus(PayStatusEnum.RESCINDED.getCode());

        //先更新申请单，判断是否更新成功，没有更新成功则抛出异常
        int i = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        if (i <= 0) {
            return BaseOutput.failure("申请单修改失败");
        }
        //修改结算单的支付状态
        int i1 = getActualDao().updateByPrimaryKeySelective(transitionDepartureSettlement);
        //判断结算单修改是否成功，不成功则抛出异常
        if (i1 <= 0) {
            throw new RuntimeException("转离场结算单撤销结算单修改失败");
        }

        //通知进门，将对应撤销的单子作废掉
        VehicleAccessDTO vehicleAccessDTO = new VehicleAccessDTO();
        //设置进门收费id
        vehicleAccessDTO.setId(transitionDepartureSettlement.getJmsfId());
        //设置撤销人员id
        vehicleAccessDTO.setCancelId(transitionDepartureSettlement.getRevocatorId());
        //设置撤销人员姓名
        vehicleAccessDTO.setCancelName(transitionDepartureSettlement.getOperatorName());
        //设置撤销原因
        vehicleAccessDTO.setCancelReason("转离场收费撤销");
        BaseOutput<Integer> integerBaseOutput = jmsfRpc.cancelAccess(vehicleAccessDTO);
        if (!integerBaseOutput.isSuccess()) {
            throw new RuntimeException(integerBaseOutput.getMessage());
        }
        //调用卡号查询账户信息
        CardQueryDto dto = new CardQueryDto();
        dto.setCardNo(transitionDepartureSettlement.getCustomerCardNo());
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getSingle(dto);
        //判断调用卡号拿到账户信息是否成功
        if (!oneAccountCard.isSuccess()) {
            throw new RuntimeException("转离场结算单撤销调用卡号拿到账户失败");
        }
        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;

        //判断是否存在交易单号，0元则无交易单号，所有不走支付撤销
        if (StringUtils.isNotBlank(transitionDepartureSettlement.getPaymentNo())) {
            //再掉卡务撤销交易
            //设置撤销交易的dto
            PaymentTradeCommitDto paymentTradeCommitDto = new PaymentTradeCommitDto();
            paymentTradeCommitDto.setTradeId(transitionDepartureSettlement.getPaymentNo());
            BaseOutput<PaymentTradeCommitResponseDto> paymentTradeCommitResponseDtoBaseOutput = payRpc.cancel(paymentTradeCommitDto);
            if (!paymentTradeCommitResponseDtoBaseOutput.isSuccess()) {
                throw new RuntimeException("转离场结算单撤销调用撤销交易rpc失败");
            }
            data = paymentTradeCommitResponseDtoBaseOutput.getData();
        }
//        支付请求成功之后，再调用新增操作记录的接口
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        serialRecordDo.setAccountId(oneAccountCard.getData().getAccountId());
        serialRecordDo.setCardNo(oneAccountCard.getData().getCardNo());
        serialRecordDo.setCustomerId(oneAccountCard.getData().getCustomerId());
        serialRecordDo.setCustomerName(transitionDepartureSettlement.getCustomerName());
        serialRecordDo.setCustomerNo(transitionDepartureSettlement.getCustomerCode());
        serialRecordDo.setOperatorId(transitionDepartureSettlement.getOperatorId());
        serialRecordDo.setOperatorName(transitionDepartureSettlement.getOperatorName());
        serialRecordDo.setOperatorNo(transitionDepartureSettlement.getOperatorCode());
        serialRecordDo.setFirmId(oneAccountCard.getData().getFirmId());
        serialRecordDo.setOperateTime(LocalDateTime.now());

        //判断是转场还是离场1.转场 2.离场
//        if (Objects.equals(transitionDepartureSettlement.getBizType(), 1)) {
        if (Objects.equals(transitionDepartureSettlement.getBizType(), BizTypeEnum.TRANSITION.getCode())) {
            serialRecordDo.setNotes("撤销，转离场单号" + transitionDepartureSettlement.getCode());
            serialRecordDo.setFundItem(FundItem.TRANSFER_FEE.getCode());
            serialRecordDo.setFundItemName(FundItem.TRANSFER_FEE.getName());
        } else {
            serialRecordDo.setNotes("撤销，转离场单号" + transitionDepartureSettlement.getCode());
            serialRecordDo.setFundItem(FundItem.LEAVE_FEE.getCode());
            serialRecordDo.setFundItemName(FundItem.LEAVE_FEE.getName());
        }
        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setAmount(data.getAmount());
            //期初余额
            serialRecordDo.setStartBalance(data.getBalance());
            serialRecordDo.setEndBalance(data.getBalance() + data.getAmount());
            serialRecordDo.setOperateTime(data.getWhen());
            serialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        }
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        return BaseOutput.successData(transitionDepartureSettlement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateSettlementAndApply(TransitionDepartureSettlement transitionDepartureSettlement, Long marketId) {
        //根据结算单的申请单id获取申请单信息
        //根据结算单apply_id获取到对应申请单
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());
        //判断申请单是否存在，如果不存在，则抛出异常
        if (Objects.isNull(transitionDepartureApply)) {
            throw new RuntimeException("获取申请单信息失败");
        }
        //将车牌号转成大写
        transitionDepartureSettlement.setPlate(transitionDepartureSettlement.getPlate().toUpperCase());
        //根据车型id获取车型信息
        CarTypeForBusinessDTO carTypeForJmsfDTO = new CarTypeForBusinessDTO();
        //设置业务类型
        carTypeForJmsfDTO.setBusinessCode(MyBusinessType.KCJM.getCode());
        //设置市场id
        carTypeForJmsfDTO.setMarketId(marketId);
        //设置id
        carTypeForJmsfDTO.setId(transitionDepartureSettlement.getCarTypeId());
        BaseOutput<List<CarTypeForBusinessDTO>> listBaseOutput = assetsRpc.listCarType(carTypeForJmsfDTO);
        if (!listBaseOutput.isSuccess()) {
            throw new RuntimeException("查询车辆类型失败");
        }
        transitionDepartureSettlement.setCarTypeName(listBaseOutput.getData().get(0).getCarTypeName());
        //结算单更新
        int i = getActualDao().updateByPrimaryKeySelective(transitionDepartureSettlement);
        //判断结算单是否更新成功
        if (i <= 0) {
            throw new RuntimeException("结算单更新失败");
        }
        //跟新申请单信息
        //这里更新申请单信息，其实也只有车牌号和车类型，其他都是不需要修改的
        Boolean flag = false;

        //判断车类型是否相等，如果不相等则更新，相等则不更新
        if (!Objects.equals(transitionDepartureApply.getCarTypeId(), transitionDepartureSettlement.getCarTypeId())) {
            transitionDepartureApply.setCarTypeId(listBaseOutput.getData().get(0).getId());
            transitionDepartureApply.setCarTypeName(listBaseOutput.getData().get(0).getCarTypeName());
            flag = true;
        }
        //判断车牌号是否相等，不相等则更新
        if (!Objects.equals(transitionDepartureApply.getPlate(), transitionDepartureSettlement.getPlate())) {
            transitionDepartureApply.setPlate(transitionDepartureSettlement.getPlate());
            flag = true;
        }
        //如果，车牌或者是车类型不同的情况，则需要调用update，所以使用flag标志一下，看是否是真的需要更新
        if (flag) {
            int i1 = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
            //判断申请单是否更新成功
            if (i1 <= 0) {
                throw new RuntimeException("申请单更新失败");
            }
        }
    }


    /**
     * 定时任务在次日的凌晨五分执行，拿到前一天的日期
     *
     * @return
     */
    private Map<String, String> getBeforeDate() {
        Map<String, String> map = new HashMap<>();
        //格式化一下时间
        DateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //当前时间
        Date dNow = new Date();

        //得到日历
        Calendar calendar = Calendar.getInstance();

        //把当前时间赋给日历
        calendar.setTime(dNow);

        //设置为前一天
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        //得到前一天的时间
        Date dBefore = calendar.getTime();

        //格式化前一天
        String defaultStartDate = dateFmt.format(dBefore);

        //前一天的0时
        defaultStartDate = defaultStartDate.substring(0, 10) + " 00:00:00";
        map.put("beginTime", defaultStartDate);

        //前一天的24时
        String defaultEndDate = defaultStartDate.substring(0, 10) + " 23:59:59";

        map.put("endTime", defaultEndDate);

        return map;
    }

    private boolean compareDate(LocalDateTime localDateTime) {
        LocalDateTime today_start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime today_end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return localDateTime.isAfter(today_start) && localDateTime.isBefore(today_end);
    }
}