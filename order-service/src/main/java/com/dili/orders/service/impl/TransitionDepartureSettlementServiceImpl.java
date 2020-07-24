package com.dili.orders.service.impl;

import com.dili.jmsf.microservice.sdk.dto.VehicleAccessDTO;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.dto.*;
import com.dili.orders.mapper.TransitionDepartureApplyMapper;
import com.dili.orders.mapper.TransitionDepartureSettlementMapper;
import com.dili.orders.rpc.*;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.orders.service.TransitionDepartureSettlementService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    @Override
    public PageOutput<List<TransitionDepartureSettlement>> listByQueryParams(TransitionDepartureSettlement transitionDepartureSettlement) {
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
    public void scheduleUpdate() throws ParseException {
        TransitionDepartureSettlement transitionDepartureSettlement = new TransitionDepartureSettlement();
        //拿到前一天的0时和23:59:59时
        Map<String, String> beforeDate = getBeforeDate();
        //设置查询参数
        //查询日期使用的是Date类型
        transitionDepartureSettlement.setBeginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beforeDate.get("beginTime")));
        transitionDepartureSettlement.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beforeDate.get("endTime")));
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
    public BaseOutput<TransitionDepartureSettlement> insertTransitionDepartureSettlement(TransitionDepartureSettlement transitionDepartureSettlement) {
        //设置支付状态为未结算
        transitionDepartureSettlement.setPayStatus(1);
        //根据申请单id拿到申请单，修改申请单的支付状态为1（未结算）
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());
        if (Objects.isNull(transitionDepartureApply)) {
            throw new RuntimeException("转离场支付-->未找到相关申请单");
        }
        transitionDepartureApply.setPayStatus(1);
        int i = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        if (i <= 0) {
            return BaseOutput.failure("转离场保存-->修改申请单失败");
        }
        //更新完成之后，插入缴费单信息，必须在这之前发起请求，到支付系统，拿到支付单号
        //如果交费金额为0，则不走支付
        if (!Objects.equals(transitionDepartureSettlement.getChargeAmount(), 0L)) {
            PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
            BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
            if (!oneAccountCard.isSuccess()) {
                throw new RuntimeException("转离场结算单新增-->根据卡号获取账户信息失败");
            }
            //请求与支付，两边的账户id对应关系如下
            paymentTradePrepareDto.setAccountId(oneAccountCard.getData().getFundAccountId());
            paymentTradePrepareDto.setType(12);
            paymentTradePrepareDto.setBusinessId(oneAccountCard.getData().getAccountId());
            paymentTradePrepareDto.setAmount(transitionDepartureSettlement.getChargeAmount());
            BaseOutput<CreateTradeResponseDto> prepare = payRpc.prepareTrade(paymentTradePrepareDto);
            if (!prepare.isSuccess()) {
                throw new RuntimeException("转离场结算单新增-->创建交易失败");
            }
            //设置交易单号
            transitionDepartureSettlement.setPaymentNo(prepare.getData().getTradeId());
        }
        //根据uid设置结算单的code
        transitionDepartureSettlement.setCode(uidRpc.bizNumber("sg_zlc_settlement").getData());
        int insert = getActualDao().insert(transitionDepartureSettlement);
        if (insert <= 0) {
            throw new RuntimeException("转离场结算单新增-->创建转离场结算单失败");
        }
        return BaseOutput.successData(transitionDepartureSettlement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseOutput pay(Long id, String password, Long marketId, Long departmentId, String operatorCode, Long operatorId, String operatorName, String operatorUserName) {
        //根据id获取当前的结算单信息
        TransitionDepartureSettlement transitionDepartureSettlement = get(id);
        //判断结算单的支付状态是否为1（未结算）,不是则直接返回
        if (transitionDepartureSettlement.getPayStatus() != 1) {
            return BaseOutput.failure("只有未结算的结算单可以结算");
        }
        //设置为已支付状态
        transitionDepartureSettlement.setPayStatus(2);
        //根据结算单apply_id获取到对应申请单
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());
        //判断申请单是否存在
        if (Objects.isNull(transitionDepartureApply)) {
            return BaseOutput.failure("申请单不存在");
        }
        //获取到申请单
        //设置申请单支付状态为已支付
        transitionDepartureApply.setPayStatus(2);
        int i = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        if (i <= 0) {
            return BaseOutput.failure("修改申请单状态失败");
        }
        //设置支付时间
        transitionDepartureSettlement.setPayTime(LocalDateTime.now());
        //更新操作员
        transitionDepartureSettlement.setOperatorId(operatorId);
        transitionDepartureSettlement.setOperatorName(operatorName);
        transitionDepartureSettlement.setOperatorCode(operatorCode);
        //修改结算单的支付状态
//        int i1 = getActualDao().updateByPrimaryKeySelective(transitionDepartureSettlement);
//        if (i1 <= 0) {
//            throw new RuntimeException("转离场结算单支付-->修改结算单失败");
//        }

        //支付请求成功之后，再调用新增操作记录的接口
//        SerialDto serialDto = new SerialDto();
//        List<SerialRecordDo> serialRecordList = new ArrayList<>();
//        SerialRecordDo serialRecordDo = new SerialRecordDo();
//        serialRecordDo.setAccountId(oneAccountCard.getData().getAccountId());
//        serialRecordDo.setCardNo(oneAccountCard.getData().getCardNo());
//        serialRecordDo.setAmount(transitionDepartureSettlement.getChargeAmount());
//        serialRecordDo.setCustomerId(oneAccountCard.getData().getCustomerId());
//        serialRecordDo.setCustomerName(transitionDepartureSettlement.getCustomerName());
//        serialRecordDo.setAction(ActionType.EXPENSE.getCode());
//        serialRecordDo.setOperatorId(operatorId);
//        serialRecordDo.setOperatorName(operatorName);
//        serialRecordDo.setOperatorNo(operatorUserName);
//        serialRecordDo.setFirmId(marketId);
//        //判断是转场还是离场1.转场 2.离场
//        if (Objects.equals(transitionDepartureSettlement.getBizType(), 1)) {
//            serialRecordDo.setNotes("车辆转场" + transitionDepartureSettlement.getCode());
//            serialRecordDo.setFundItem(FundItem.TRANSFER.getCode());
//            serialRecordDo.setFundItemName(FundItem.TRANSFER.getName());
//        } else {
//            serialRecordDo.setNotes("车辆离场" + transitionDepartureSettlement.getCode());
//            serialRecordDo.setFundItem(FundItem.LEAVE.getCode());
//            serialRecordDo.setFundItemName(FundItem.LEAVE.getName());
//        }
//        //判断是否走了支付
//        if (Objects.nonNull(data)) {
//            serialRecordDo.setEndBalance(data.getBalance() - transitionDepartureSettlement.getChargeAmount());
//            serialRecordDo.setStartBalance(data.getBalance());
//            serialRecordDo.setOperateTime(data.getWhen());
//        } else {
//            BaseOutput<AccountSimpleResponseDto> oneAccountCard1 = cardRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
//            if (!oneAccountCard1.isSuccess()) {
//                throw new RuntimeException("查询失败-->根据卡号查询账户信息，余额等");
//            }
//            serialRecordDo.setEndBalance(oneAccountCard1.getData().getAccountFund().getBalance());
//            serialRecordDo.setStartBalance(oneAccountCard1.getData().getAccountFund().getBalance());
//            serialRecordDo.setOperateTime(LocalDateTime.now());
//        }
//        serialRecordList.add(serialRecordDo);
//        serialDto.setSerialRecordList(serialRecordList);
//        BaseOutput baseOutput = accountRpc.batchSave(serialDto);
//        if (!baseOutput.isSuccess()) {
//            throw new RuntimeException("操作交易记录-->新增失败");
//        }

        //设置进门收费相关信息，并调用新增
        VehicleAccessDTO vehicleAccessDTO = new VehicleAccessDTO();
        vehicleAccessDTO.setMarketId(marketId);
        vehicleAccessDTO.setPlateNumber(transitionDepartureSettlement.getPlate());
        vehicleAccessDTO.setVehicleTypeId(transitionDepartureSettlement.getCarTypeId());
        vehicleAccessDTO.setBarrierType(3);
        vehicleAccessDTO.setEntryTime(new Date());
        vehicleAccessDTO.setAmount(transitionDepartureSettlement.getChargeAmount());
        vehicleAccessDTO.setPayType(3);
        vehicleAccessDTO.setCasherId(operatorId);
        vehicleAccessDTO.setCasherName(operatorName);
        vehicleAccessDTO.setCasherDepartmentId(departmentId);
        vehicleAccessDTO.setPayTime(new Date());
        vehicleAccessDTO.setOperatorId(operatorId);
        vehicleAccessDTO.setOperatorName(operatorName);
        vehicleAccessDTO.setCreated(new Date());
        //判断进门收费新增是否成功
        BaseOutput<VehicleAccessDTO> vehicleAccessDTOBaseOutput = jmsfRpc.add(vehicleAccessDTO);
        if (!vehicleAccessDTOBaseOutput.isSuccess()) {
            throw new RuntimeException("进门收费单-->新增失败");
        }
        //将进门收费返回的id设置到结算单中
        transitionDepartureSettlement.setJmsfId(vehicleAccessDTOBaseOutput.getData().getId());
        int i2 = getActualDao().updateByPrimaryKeySelective(transitionDepartureSettlement);
        //判断是否修改成功
        //进门收费成功过后，拿到data，取出id，然后设置到结算单中去
        if (i2 <= 0) {
            throw new RuntimeException("转离场结算单支付-->修改结算单失败");
        }
        //再调用支付
        //首先根据卡号拿倒账户信息
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
        if (!oneAccountCard.isSuccess()) {
            throw new RuntimeException("转离场结算单支付-->查询账户失败");
        }
        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;
        //判断是否支付金额是否为0，不为零再走支付
        if (!Objects.equals(transitionDepartureSettlement.getChargeAmount(), 0L)) {
            //构建支付对象
            UserAccountCardResponseDto userAccountCardResponseDto = oneAccountCard.getData();
            PaymentTradeCommitDto paymentTradeCommitDto = new PaymentTradeCommitDto();
            //设置自己账户id
            paymentTradeCommitDto.setAccountId(userAccountCardResponseDto.getFundAccountId());
            //设置账户id
            paymentTradeCommitDto.setBusinessId(userAccountCardResponseDto.getAccountId());
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
            //调用rpc支付
            BaseOutput<PaymentTradeCommitResponseDto> pay = payRpc.pay(paymentTradeCommitDto);
            if (!pay.isSuccess()) {
                throw new RuntimeException("转离场结算单支付-->支付rpc请求失败");
            }
            data = pay.getData();
        }
        return BaseOutput.successData(transitionDepartureSettlement);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseOutput<TransitionDepartureSettlement> revocator(TransitionDepartureSettlement transitionDepartureSettlement) {

        //判断结算单的支付状态是否为2（已结算）,不是则直接返回
        if (transitionDepartureSettlement.getPayStatus() != 2) {
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
        transitionDepartureSettlement.setPayStatus(3);

        //根据结算单的apply_id拿到申请单信息
        TransitionDepartureApply transitionDepartureApply = transitionDepartureApplyService.get(transitionDepartureSettlement.getApplyId());
        if (Objects.isNull(transitionDepartureApply)) {
            return BaseOutput.failure("申请单不存在");
        }

        //设置申请单支付状态为已撤销
        transitionDepartureApply.setPayStatus(3);

        //先更新申请单，判断是否更新成功，没有更新成功则抛出异常
        int i = transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        if (i <= 0) {
            return BaseOutput.failure("申请单修改失败");
        }
        //修改结算单的支付状态
        transitionDepartureSettlement.setPayStatus(3);
        int i1 = getActualDao().updateByPrimaryKeySelective(transitionDepartureSettlement);

        //判断结算单修改是否成功，不成功则抛出异常
        if (i1 <= 0) {
            throw new RuntimeException("转离场结算单撤销-->结算单修改失败");
        }

        //支付请求成功之后，再调用新增操作记录的接口
//        SerialDto serialDto = new SerialDto();
//        List<SerialRecordDo> serialRecordList = new ArrayList<>();
//        SerialRecordDo serialRecordDo = new SerialRecordDo();
//        serialRecordDo.setAccountId(oneAccountCard.getData().getAccountId());
//        serialRecordDo.setCardNo(oneAccountCard.getData().getCardNo());
//        serialRecordDo.setAmount(transitionDepartureSettlement.getChargeAmount());
//        serialRecordDo.setCustomerId(oneAccountCard.getData().getCustomerId());
//        serialRecordDo.setCustomerName(transitionDepartureSettlement.getCustomerName());
//        serialRecordDo.setAction(ActionType.EXPENSE.getCode());
//        serialRecordDo.setOperatorId(transitionDepartureSettlement.getOperatorId());
//        serialRecordDo.setOperatorName(transitionDepartureSettlement.getOperatorName());
//        serialRecordDo.setOperatorNo(transitionDepartureSettlement.getOperatorCode());
//        serialRecordDo.setFirmId(oneAccountCard.getData().getFirmId());
//
//        //判断是转场还是离场1.转场 2.离场
//        if (Objects.equals(transitionDepartureSettlement.getBizType(), 1)) {
//            serialRecordDo.setNotes("撤销车辆转场" + transitionDepartureSettlement.getCode());
//            serialRecordDo.setFundItem(FundItem.TRANSFER.getCode());
//            serialRecordDo.setFundItemName(FundItem.TRANSFER.getName());
//        } else {
//            serialRecordDo.setNotes("撤销车辆离场" + transitionDepartureSettlement.getCode());
//            serialRecordDo.setFundItem(FundItem.LEAVE.getCode());
//            serialRecordDo.setFundItemName(FundItem.LEAVE.getName());
//        }
//        //判断是否走了支付
//        if (Objects.nonNull(data)) {
//            serialRecordDo.setEndBalance(data.getBalance());
//            serialRecordDo.setStartBalance(data.getBalance());
//            serialRecordDo.setOperateTime(data.getWhen());
//        } else {
//            BaseOutput<AccountSimpleResponseDto> oneAccountCard1 = cardRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
//            if (!oneAccountCard1.isSuccess()) {
//                throw new RuntimeException("查询失败-->根据卡号查询账户信息，余额等");
//            }
//            serialRecordDo.setEndBalance(oneAccountCard1.getData().getAccountFund().getBalance());
//            serialRecordDo.setStartBalance(oneAccountCard1.getData().getAccountFund().getBalance());
//            serialRecordDo.setOperateTime(LocalDateTime.now());
//        }
//        serialRecordList.add(serialRecordDo);
//        serialDto.setSerialRecordList(serialRecordList);
//        BaseOutput baseOutput = accountRpc.batchSave(serialDto);
//        if (!baseOutput.isSuccess()) {
//            throw new RuntimeException("操作交易记录-->新增失败");
//        }

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
            throw new RuntimeException("进门收费-->撤销失败");
        }
        //调用卡号查询账户信息
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getOneAccountCard(transitionDepartureSettlement.getCustomerCardNo());
        //判断调用卡号拿到账户信息是否成功
        if (!oneAccountCard.isSuccess()) {
            throw new RuntimeException("转离场结算单撤销-->调用卡号拿到账户失败");
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
                throw new RuntimeException("转离场结算单撤销-->调用撤销交易rpc失败");
            }
            data = paymentTradeCommitResponseDtoBaseOutput.getData();
        }
        return BaseOutput.successData(transitionDepartureSettlement);
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
}