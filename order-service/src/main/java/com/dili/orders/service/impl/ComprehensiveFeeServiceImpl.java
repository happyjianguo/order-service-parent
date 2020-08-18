package com.dili.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.jmsf.microservice.sdk.dto.VehicleAccessDTO;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.dto.*;
import com.dili.orders.mapper.ComprehensiveFeeMapper;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ComprehensiveFeeServiceImpl extends BaseServiceImpl<ComprehensiveFee,Long> implements ComprehensiveFeeService {

    @Autowired
    private UidRpc uidRpc;

    @Autowired
    private AccountRpc accountRpc;

    @Autowired
    private PayRpc payRpc;

    @Autowired
    private RabbitMQMessageService rabbitMQMessageService;

    public ComprehensiveFeeMapper getActualDao(){return (ComprehensiveFeeMapper)getDao();}

    @Override
    public PageOutput<List<ComprehensiveFee>> listByQueryParams(ComprehensiveFee comprehensiveFee) {
        Integer page = comprehensiveFee.getPage();
        page = (page == null) ? Integer.valueOf(1) : page;
        if (comprehensiveFee.getRows() != null && comprehensiveFee.getRows() >= 1) {
            PageHelper.startPage(page, comprehensiveFee.getRows());
        }

        List<ComprehensiveFee> list = getActualDao().listByQueryParams(comprehensiveFee);
        Long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
        int totalPage = list instanceof Page ? ((Page) list).getPages() : 1;
        int pageNum = list instanceof Page ? ((Page) list).getPageNum() : 1;
        PageOutput<List<ComprehensiveFee>> output = PageOutput.success();
        output.setData(list).setPageNum(pageNum).setTotal(total.intValue()).setPageSize(comprehensiveFee.getPage()).setPages(totalPage);
        return output;
    }

    @Override
    public BaseOutput<ComprehensiveFee> insertComprehensiveFee(ComprehensiveFee comprehensiveFee) {
        //设置检查收费单为未结算
        comprehensiveFee.setOrderStatus(1);
        //设置单据类型为检测收费
        comprehensiveFee.setOrderType(1);
        //设置默认版本号为0
        comprehensiveFee.setVersion(0);
        //根据uid设置结算单的code
        comprehensiveFee.setCode(uidRpc.bizNumber("sg_comprehensive_fee").getData());
        int insert = getActualDao().insert(comprehensiveFee);
        if (insert <= 0) {
            throw new RuntimeException("检测收费新增-->创建检测收费单失败");
        }
        return BaseOutput.successData(comprehensiveFee);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseOutput pay(Long id, String password, Long marketId, Long departmentId, String operatorCode, Long operatorId, String operatorName, String operatorUserName) {
        //根据id获取当前的结算单信息
        ComprehensiveFee comprehensiveFee = get(id);
        //判断结算单的支付状态是否为1（未结算）,不是则直接返回
        if (comprehensiveFee.getOrderStatus() != 1) {
            return BaseOutput.failure("只有未结算的结算单可以结算");
        }
        //设置为已支付状态
        comprehensiveFee.setOrderStatus(2);
        //设置支付时间
        comprehensiveFee.setOperatorTime(LocalDateTime.now());
        //更新操作员
        comprehensiveFee.setOperatorId(operatorId);
        comprehensiveFee.setOperatorName(operatorName);
        int i = updateSelective(comprehensiveFee);
        if (i <= 0) {
            return BaseOutput.failure("检测收费保存-->修改申请单失败");
        }

        //更新完成之后，插入缴费单信息，必须在这之前发起请求，到支付系统，拿到支付单号
        //如果交费金额为0，则不走支付
        //交易ID
        if (!Objects.equals(comprehensiveFee.getChargeAmount(), 0L)) {
            PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
            BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getOneAccountCard(comprehensiveFee.getCustomerCardNo());
            if (!oneAccountCard.isSuccess()) {
                throw new RuntimeException("检测收费结算单新增-->根据卡号获取账户信息失败");
            }
            //请求与支付，两边的账户id对应关系如下
            paymentTradePrepareDto.setAccountId(oneAccountCard.getData().getFundAccountId());
            paymentTradePrepareDto.setType(12);
            paymentTradePrepareDto.setBusinessId(oneAccountCard.getData().getAccountId());
            paymentTradePrepareDto.setAmount(comprehensiveFee.getChargeAmount());
            BaseOutput<CreateTradeResponseDto> prepare = payRpc.prepareTrade(paymentTradePrepareDto);
            if (!prepare.isSuccess()) {
                throw new RuntimeException("转离场结算单新增-->创建交易失败");
            }
            //设置交易单号
            comprehensiveFee.setPaymentNo(prepare.getData().getTradeId());

            int j = updateSelective(comprehensiveFee);
            if (j <= 0) {
                return BaseOutput.failure("检测收费保存-->修改申请单失败");
            }
        }


        //再调用支付
        //首先根据卡号拿倒账户信息
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getOneAccountCard(comprehensiveFee.getCustomerCardNo());
        if (!oneAccountCard.isSuccess()) {
            throw new RuntimeException("转离场结算单支付-->查询账户失败");
        }
        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;
        //判断是否支付金额是否为0，不为零再走支付
        if (!Objects.equals(comprehensiveFee.getChargeAmount(), 0L)) {
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
            paymentTradeCommitDto.setTradeId(comprehensiveFee.getPaymentNo());
            //设置费用
            List<FeeDto> feeDtos = new ArrayList();
            FeeDto feeDto = new FeeDto();
            feeDto.setAmount(comprehensiveFee.getChargeAmount());
            feeDto.setType(25);
            feeDto.setTypeName("检测收费");
            feeDtos.add(feeDto);
            paymentTradeCommitDto.setFees(feeDtos);
            //调用rpc支付
            BaseOutput<PaymentTradeCommitResponseDto> pay = payRpc.pay(paymentTradeCommitDto);
            if (!pay.isSuccess()) {
                throw new RuntimeException("检测收费结算单支付-->支付rpc请求失败");
            }
            data = pay.getData();
        }
        //对接操作记录
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        serialRecordDo.setAccountId(oneAccountCard.getData().getAccountId());
        serialRecordDo.setCardNo(oneAccountCard.getData().getCardNo());
        serialRecordDo.setAmount(comprehensiveFee.getChargeAmount());
        serialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        serialRecordDo.setCustomerId(oneAccountCard.getData().getCustomerId());
        serialRecordDo.setCustomerName(comprehensiveFee.getCustomerName());
        serialRecordDo.setCustomerNo(comprehensiveFee.getCustomerCode());
        serialRecordDo.setOperatorId(operatorId);
        serialRecordDo.setOperatorName(operatorName);
        serialRecordDo.setOperatorNo(operatorUserName);
        serialRecordDo.setFirmId(marketId);
        serialRecordDo.setNotes("检测收费" + comprehensiveFee.getCode());
        serialRecordDo.setFundItem(FundItem.TEST_FEE.getCode());
        serialRecordDo.setFundItemName(FundItem.TEST_FEE.getName());
        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setEndBalance(data.getBalance() - comprehensiveFee.getChargeAmount());
            serialRecordDo.setStartBalance(data.getBalance());
            serialRecordDo.setOperateTime(data.getWhen());
        }
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        return BaseOutput.successData(comprehensiveFee);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void scheduleUpdate() throws ParseException {
        ComprehensiveFee comprehensiveFee = new ComprehensiveFee();
        //拿到前一天的0时和23:59:59时
        Map<String, String> beforeDate = getBeforeDate();
        //设置查询参数
        //查询日期使用的是Date类型
        comprehensiveFee.setOperatorTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beforeDate.get("beginTime")));
        comprehensiveFee.setOperatorTimeEnd(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(beforeDate.get("endTime")));
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
