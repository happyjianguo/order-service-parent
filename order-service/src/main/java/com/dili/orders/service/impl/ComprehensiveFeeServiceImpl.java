package com.dili.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.dto.CategoryDTO;
import com.dili.assets.sdk.rpc.CategoryRpc;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.domain.*;
import com.dili.orders.dto.*;
import com.dili.orders.mapper.ComprehensiveFeeMapper;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.time.LocalDate;
import java.util.List;

import com.dili.uap.sdk.rpc.UserRpc;

/**
 * 检测收费服务实现类
 *
 *@author  Henry.Huang
 *@date  2020/08/20
 *
 */
@Service
public class ComprehensiveFeeServiceImpl extends BaseServiceImpl<ComprehensiveFee, Long> implements ComprehensiveFeeService {

    @Autowired
    private UidRpc uidRpc;

    @Autowired
    private UserRpc userRpc;

    @Autowired
    private ComprehensiveFeeMapper comprehensiveFeeMapper;

    @Autowired
    private PayRpc payRpc;

    @Autowired
    private RabbitMQMessageService mqService;

    @Autowired
    private AccountRpc accountRpc;

    @Autowired
    private RabbitMQMessageService rabbitMQMessageService;

    @Autowired
    CategoryRpc categoryRpc;


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
        comprehensiveFee.setOrderStatus(ComprehensiveFeeState.NO_SETTLEMEN.getValue());
        //设置默认版本号为0
        comprehensiveFee.setVersion(0);
        //根据uid设置结算单的code
        //根据uid设置结算单的code
        BaseOutput<String> output = this.uidRpc.bizNumber("sg_comprehensive_fee");
        if (!output.isSuccess()) {
            LOGGER.error(output.getMessage());
            return BaseOutput.failure("生成综合收费编号失败");
        }
        comprehensiveFee.setCode(output.getData());
        int insert = getActualDao().insert(comprehensiveFee);
        if (insert <= 0) {
            throw new AppException("检测收费新增-->创建检测收费单失败");
        }
        return BaseOutput.successData(comprehensiveFee);
    }


    @Override
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput pay(Long id, String password, Long marketId, Long operatorId, String operatorName, String operatorUserName) {
        //根据id获取当前的结算单信息
        ComprehensiveFee comprehensiveFee = get(id);
        Integer orderType = comprehensiveFee.getOrderType();
        String updateError = "检测收费保存-->修改检测收费单失败";
        String cardError = "检测收费新增-->根据卡号获取账户信息失败";
        String cardIdError = "检测收费新增-->创建交易失败";
        String updateAgainError = "检测收费保存-->修改检测收费单失败";
        String cardQueryError = "检测收费支付-->查询账户失败";
        String amountError = "检测收费金额-->缴费金额必须大于零";
        String typeName = "检测收费单号";
        int fundItemCode = FundItem.TEST_FEE.getCode();
        String fundItemName = FundItem.TEST_FEE.getName();
        if(ComprehensiveFeeType.QUERY_CHARGE.getValue().equals(orderType)){
            updateError = "查询收费保存-->修改查询收费单失败";
            cardError = "查询收费新增-->根据卡号获取账户信息失败";
            cardIdError = "查询收费新增-->创建交易失败";
            updateAgainError = "查询收费保存-->修改查询收费单失败";
            cardQueryError = "查询收费支付-->查询账户失败";
            amountError = "查询收费金额-->缴费金额必须大于零";
            typeName = "查询收费单号";
            fundItemCode = FundItem.QUERY_FEE.getCode();
            fundItemName = FundItem.QUERY_FEE.getName();
        }
        //判断结算单的支付状态是否为1（未结算）,不是则直接返回
        if (comprehensiveFee.getOrderStatus() != ComprehensiveFeeState.NO_SETTLEMEN.getValue()) {
            return BaseOutput.failure("只有未结算的结算单可以结算");
        }
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
        comprehensiveFee.setVersion(comprehensiveFee.getVersion()+1);
        int i = updateSelective(comprehensiveFee);
        if (i <= 0) {
            return BaseOutput.failure(updateError);
        }

        //更新完成之后，插入缴费单信息，必须在这之前发起请求，到支付系统，拿到支付单号
        //如果交费金额为0，则不走支付
        //交易ID
        BaseOutput<CreateTradeResponseDto> prepare=null;
        if (!Objects.equals(comprehensiveFee.getChargeAmount(), 0L)) {
            PaymentTradePrepareDto paymentTradePrepareDto = new PaymentTradePrepareDto();
            CardQueryDto dto=new CardQueryDto();
            dto.setCardNo(comprehensiveFee.getCustomerCardNo());
            BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getSingle(dto);
            if (!oneAccountCard.isSuccess()) {
                BaseOutput.failure(cardError);
                LOGGER.error(oneAccountCard.getMessage());
                throw new AppException(cardError);
            }
            //请求与支付，两边的账户id对应关系如下
            paymentTradePrepareDto.setAccountId(oneAccountCard.getData().getFundAccountId());
            //12为缴费类型
            paymentTradePrepareDto.setType(12);
            paymentTradePrepareDto.setBusinessId(oneAccountCard.getData().getAccountId());
            paymentTradePrepareDto.setAmount(comprehensiveFee.getChargeAmount());
            paymentTradePrepareDto.setSerialNo(comprehensiveFee.getCode());
            prepare = payRpc.prepareTrade(paymentTradePrepareDto);
            if (!prepare.isSuccess()) {
                BaseOutput.failure(cardIdError);
                LOGGER.error(prepare.getMessage());
                throw new AppException(cardIdError);
            }
            //设置交易单号
            comprehensiveFee.setPaymentNo(prepare.getData().getTradeId());

            int j = updateSelective(comprehensiveFee);
            if (j <= 0) {
                return BaseOutput.failure(updateAgainError);
            }
        }

        //再调用支付
        //首先根据卡号拿倒账户信息
        CardQueryDto dto=new CardQueryDto();
        dto.setCardNo(comprehensiveFee.getCustomerCardNo());
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getSingle(dto);
        if (!oneAccountCard.isSuccess()) {
            BaseOutput.failure(cardQueryError);
            LOGGER.error(oneAccountCard.getMessage());
            throw new AppException(cardQueryError);
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
            feeDto.setType(FundItem.TEST_FEE.getCode());
            feeDto.setTypeName(typeName);
            feeDtos.add(feeDto);
            paymentTradeCommitDto.setFees(feeDtos);
            //调用rpc支付
            BaseOutput<PaymentTradeCommitResponseDto> pay = payRpc.pay(paymentTradeCommitDto);
            if (!pay.isSuccess()) {
                BaseOutput.failure(pay.getMessage());
                LOGGER.error(pay.getMessage());
                throw new AppException(pay.getMessage());
            }
            data = pay.getData();
        } else{
            BaseOutput.failure(amountError);
            LOGGER.error("支付金额为0，不走支付。");
            throw new AppException(amountError);
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
        serialRecordDo.setNotes(typeName + comprehensiveFee.getCode());
        serialRecordDo.setFundItem(fundItemCode);
        serialRecordDo.setFundItemName(fundItemName);
        //12为缴费类型
        serialRecordDo.setTradeType(12);
        serialRecordDo.setSerialNo(comprehensiveFee.getCode());
        serialRecordDo.setCustomerType(comprehensiveFee.getCustomerType());
        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setEndBalance(data.getBalance() - comprehensiveFee.getChargeAmount()-data.getFrozenBalance());
            serialRecordDo.setStartBalance(data.getBalance()-data.getFrozenBalance());
            serialRecordDo.setOperateTime(data.getWhen());
        }
        if(Objects.nonNull(prepare.getData())){
            serialRecordDo.setTradeNo(prepare.getData().getTradeId());
        }
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));

        //根据商品ID获取商品名称
        String inspectionItem = comprehensiveFee.getInspectionItem();
        comprehensiveFee.setInspectionItem(getItemNameByItemId(inspectionItem));
        return BaseOutput.successData(comprehensiveFee);
    }

    public String getItemNameByItemId(String inspectionItem) {
        String returnName = "";
        if (StringUtils.isNotBlank(inspectionItem)) {
            List<String> ids = Arrays.asList(inspectionItem.split(","));
            CategoryDTO categoryDTO = new CategoryDTO();

            categoryDTO.setIds(ids);
            List<CategoryDTO> list = categoryRpc.getTree(categoryDTO).getData();
            if (list != null && list.size() > 0) {
                StringBuffer name=new StringBuffer("");
                for (CategoryDTO cgdto : list) {
                    name.append(",");
                    name.append(cgdto.getName());
                }

                if (name.length() > 0) {
                    returnName = name.substring(1);
                }
            }
        }
        return returnName;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BaseOutput scheduleUpdate() throws ParseException {
        LOGGER.info("综合收费将前天未结算单据关闭定时任务开始");
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
        LOGGER.info("综合收费将前天未结算单据关闭定时任务结束");
        return BaseOutput.success("综合收费将前一天未结算单据关闭定时任务执行成功!");

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

    /**
     * 撤销控制
     *
     * @return
     */

    @Override
    @GlobalTransactional
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput<Object> revocator(Long id, Long operatorId, String userName,String operatorPassword, String operatorName) {
        String cardQueryError = "检测收费支付-->查询账户失败";
        String typeName = "撤销，检测收费单号";
        int fundItemCode = FundItem.TEST_FEE.getCode();
        String fundItemName = FundItem.TEST_FEE.getName();
        //根据id获取到comprehensive对象
        ComprehensiveFee comprehensiveFee = this.getActualDao().selectByPrimaryKey(id);
        if (comprehensiveFee == null) {
            return BaseOutput.failure("检测单不存在");
        }

        LocalDate todayDate = LocalDate.now();
        LocalDateTime opTime = comprehensiveFee.getModifiedTime() == null ? comprehensiveFee.getCreatedTime() : comprehensiveFee.getModifiedTime();
        if (!todayDate.equals(opTime.toLocalDate())) {
            return BaseOutput.failure("只能对当日的检测交易进行撤销操作");
        }
        if (!comprehensiveFee.getOrderStatus().equals(ComprehensiveFeeState.SETTLED.getValue())) {
            return BaseOutput.failure("当前状态不能撤销");
        }

        if ("".equals(operatorPassword)) {
            return BaseOutput.failure("请输入密码");
        }
        // 校验操作员密码
        BaseOutput<Object> pwdOutput = this.userRpc.validatePassword(operatorId, operatorPassword);
        if (!pwdOutput.isSuccess()) {
            LOGGER.error(pwdOutput.getMessage());
            return BaseOutput.failure("操作员密码错误");
        }
        //调用卡号查询账户信息
        CardQueryDto dto = new CardQueryDto();
        dto.setCardNo(comprehensiveFee.getCustomerCardNo());
        BaseOutput<UserAccountCardResponseDto> oneAccountCard = accountRpc.getSingle(dto);
        if (!oneAccountCard.isSuccess()) {
            BaseOutput.failure(cardQueryError);
            LOGGER.error(oneAccountCard.getMessage());
            throw new AppException(cardQueryError);
        }

        //新建支付返回实体，后面操作记录会用到
        PaymentTradeCommitResponseDto data = null;

        // 退款
        //判断是否存在交易单号，0元则无交易单号，所有不走支付撤销
        if (StringUtils.isNotBlank(comprehensiveFee.getPaymentNo())) {
            PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
            cancelDto.setTradeId(comprehensiveFee.getPaymentNo());
            BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
            if (!paymentOutput.isSuccess()) {
                LOGGER.error(paymentOutput.getMessage());
                throw new AppException("退款失败");
            }
            data = paymentOutput.getData();
        }

        //更新检测单状态和修改时间
        LocalDateTime now = LocalDateTime.now();
        comprehensiveFee.setModifiedTime(now);
        comprehensiveFee.setRevocatorName(userName);
        comprehensiveFee.setRevocatorTime(now);
        comprehensiveFee.setOrderStatus(ComprehensiveFeeState.WITHDRAWN.getValue());
        //更新comprehensive
        int rows = this.comprehensiveFeeMapper.updateByPrimaryKeySelective(comprehensiveFee);
        if (rows <= 0) {
            LOGGER.error("更新检测单状态失败");
            return BaseOutput.failure("更新检测单状态失败");
        }

        //对接操作记录
        List<SerialRecordDo> serialRecordList = new ArrayList<>();
        SerialRecordDo serialRecordDo = new SerialRecordDo();
        serialRecordDo.setAccountId(oneAccountCard.getData().getAccountId());
        serialRecordDo.setCardNo(oneAccountCard.getData().getCardNo());
        serialRecordDo.setCustomerId(oneAccountCard.getData().getCustomerId());
        serialRecordDo.setCustomerName(comprehensiveFee.getCustomerName());
        serialRecordDo.setCustomerNo(comprehensiveFee.getCustomerCode());
        serialRecordDo.setOperatorId(comprehensiveFee.getOperatorId());
        serialRecordDo.setOperatorName(comprehensiveFee.getOperatorName());
        serialRecordDo.setOperatorNo(operatorName);
        serialRecordDo.setFirmId(oneAccountCard.getData().getFirmId());
        serialRecordDo.setOperateTime(LocalDateTime.now());
        serialRecordDo.setNotes(typeName + comprehensiveFee.getCode());
        serialRecordDo.setFundItem(fundItemCode);
        serialRecordDo.setFundItemName(fundItemName);
        //40为撤销缴费
        serialRecordDo.setTradeType(40);
        serialRecordDo.setSerialNo(comprehensiveFee.getCode());
        serialRecordDo.setCustomerType(comprehensiveFee.getCustomerType());
        serialRecordDo.setTradeNo(comprehensiveFee.getPaymentNo());

        //判断是否走了支付
        if (Objects.nonNull(data)) {
            serialRecordDo.setAmount(data.getAmount());
            //期初余额
            serialRecordDo.setStartBalance(data.getBalance() - data.getFrozenBalance());
            serialRecordDo.setEndBalance(data.getBalance() + data.getAmount() - data.getFrozenBalance());
            serialRecordDo.setOperateTime(data.getWhen());
            serialRecordDo.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        }
        serialRecordList.add(serialRecordDo);
        rabbitMQMessageService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(serialRecordList));
        return BaseOutput.success();
    }

    private void recordAccountFlow(ComprehensiveFee comprehensiveFee, PaymentTradeCommitResponseDto data, PaymentStream stream, FundItem fundItem, Long operatorId) {
        SerialRecordDo dto = new SerialRecordDo();
        dto.setAccountId(comprehensiveFee.getCustomerId());
        dto.setAction(data.getAmount() > 0 ? ActionType.INCOME.getCode() : ActionType.EXPENSE.getCode());
        dto.setAmount(data.getAmount() + data.getFrozenAmount());
        dto.setCardNo(comprehensiveFee.getCustomerCardNo());
        dto.setCustomerId(comprehensiveFee.getCustomerId());
        dto.setCustomerName(comprehensiveFee.getCustomerName());
        dto.setCustomerNo(comprehensiveFee.getCustomerCode());
        dto.setEndBalance(stream.getBalance() - (data.getFrozenBalance() + data.getFrozenAmount()));
        dto.setFirmId(comprehensiveFee.getMarketId());
        dto.setFundItem(fundItem.getCode());
        dto.setFundItemName(fundItem.getName());
        LocalDateTime now = LocalDateTime.now();
        dto.setOperateTime(now);
        dto.setOperatorId(operatorId);
        dto.setOperatorName(comprehensiveFee.getOperatorName());
        this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(dto));
    }
}
