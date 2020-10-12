package com.dili.orders.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.enums.BusinessChargeItemEnum;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.customer.sdk.domain.Customer;
import com.dili.customer.sdk.dto.RelatedDto;
import com.dili.customer.sdk.dto.RelatedQuery;
import com.dili.customer.sdk.rpc.CustomerRpc;
import com.dili.customer.sdk.rpc.RelatedRpc;
import com.dili.jmsf.microservice.sdk.dto.TruckDTO;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.config.WeighingBillMQConfig;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.domain.PriceState;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillAgentInfo;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingBillState;
import com.dili.orders.domain.WeighingOperationType;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.domain.WeighingStatementState;
import com.dili.orders.dto.AccountBalanceDto;
import com.dili.orders.dto.AccountPasswordValidateDto;
import com.dili.orders.dto.AccountRequestDto;
import com.dili.orders.dto.ActionType;
import com.dili.orders.dto.CardQueryDto;
import com.dili.orders.dto.CreateTradeResponseDto;
import com.dili.orders.dto.FeeDto;
import com.dili.orders.dto.FeeType;
import com.dili.orders.dto.FeeUse;
import com.dili.orders.dto.FundItem;
import com.dili.orders.dto.PaymentStream;
import com.dili.orders.dto.PaymentTradeCommitDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;
import com.dili.orders.dto.PaymentTradePrepareDto;
import com.dili.orders.dto.PaymentTradeType;
import com.dili.orders.dto.PrintTemplateDataDto;
import com.dili.orders.dto.SerialRecordDo;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillPrintDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingStatementPrintDto;
import com.dili.orders.mapper.PriceApproveRecordMapper;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.mapper.WeighingBillOperationRecordMapper;
import com.dili.orders.mapper.WeighingStatementMapper;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.rpc.JmsfRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.ReferencePriceService;
import com.dili.orders.service.WeighingBillService;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.exception.AppException;
import com.dili.ss.util.BeanConver;
import com.dili.ss.util.MoneyUtils;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import io.seata.spring.annotation.GlobalTransactional;
import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Service
public class WeighingBillServiceImpl extends BaseServiceImpl<WeighingBill, Long> implements WeighingBillService {

	/**
	 * 结算单状态对应过磅单的操作记录配置，用于打印结算单时根据结算单状态获取过磅单操作记录
	 */
	private static final Map<WeighingStatementState, WeighingOperationType> WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG = new HashMap<WeighingStatementState, WeighingOperationType>();
	static {
		WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG.put(WeighingStatementState.FROZEN, WeighingOperationType.FREEZE);
		WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG.put(WeighingStatementState.PAID, WeighingOperationType.SETTLE);
		WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG.put(WeighingStatementState.REFUNDED, WeighingOperationType.WITHDRAW);
		WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG.put(WeighingStatementState.UNPAID, WeighingOperationType.WEIGH);
	}
	@Autowired
	private AccountRpc accountRpc;
	@Autowired
	private BusinessChargeItemRpc businessChargeItemRpc;
	@Autowired
	private ChargeRuleRpc chargeRuleRpc;
	@Value("${orders.checkPrice:false}")
	private Boolean checkPrice;
	@Autowired
	private CustomerRpc customerRpc;
	@Autowired
	private FirmRpc firmRpc;
	@Autowired
	private JmsfRpc jsmfRpc;
	@Autowired
	private RabbitMQMessageService mqService;
	@Autowired
	private PayRpc payRpc;
	@Autowired
	private PriceApproveRecordMapper priceApproveMapper;
	@Autowired
	private ReferencePriceService referencePriceService;

	@Autowired
	private RelatedRpc relatedRpc;

	@Autowired
	private RuntimeRpc runtimeRpc;
	@Autowired
	private UidRpc uidRpc;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private WeighingBillOperationRecordMapper wbrMapper;
	@Autowired
	private WeighingStatementMapper weighingStatementMapper;
//	@Autowired
//	private WeighingBillAgentInfoMapper agentInfoMapper;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> addWeighingBill(WeighingBill bill) {
		BaseOutput<String> output = this.uidRpc.bizNumber(OrdersConstant.WEIGHING_BILL_SERIAL_NO_GENERATE_RULE_CODE);
		if (!output.isSuccess()) {
			return BaseOutput.failure(output.getMessage());
		}
		bill.setSerialNo(output.getData());
		bill.setState(WeighingBillState.NO_SETTLEMENT.getValue());

		// 根据卡号查询账户信息
		// 查询买家账户信息
		UserAccountCardResponseDto buyerInfo = this.setWeighingBillBuyerInfo(bill);
		// 查询卖家账户信息
		UserAccountCardResponseDto sellerInfo = this.setWeighingBillSellerInfo(bill);

		int rows = this.getActualDao().insertSelective(bill);
		if (rows <= 0) {
			throw new AppException("保存过磅单失败");
		}

		Long firmId = this.getMarketIdByOperatorId(bill.getCreatorId());
		WeighingStatement statement = this.buildWeighingStatement(bill, firmId);
		statement.setCreatorId(bill.getCreatorId());
		rows = this.weighingStatementMapper.insertSelective(statement);
		if (rows <= 0) {
			throw new AppException("保存结算单失败");
		}
		// 设置代理人信息
//		WeighingBillAgentInfo agentInfo = null;
//		Customer buyerAgent = this.getCustomerAgent(buyerInfo, bill);
//		Customer sellerAgent = this.getCustomerAgent(sellerInfo, bill);
//		if (buyerAgent != null) {
//			agentInfo = new WeighingBillAgentInfo();
//			agentInfo.setWeighingBillId(bill.getId());
//			agentInfo.setWeighingBillSerialNo(bill.getSerialNo());
//			agentInfo.setWeighingStatementId(statement.getId());
//			agentInfo.setWeighingStatementSerialNo(statement.getSerialNo());
//			this.setBuyerAgentInfo(agentInfo, buyerAgent);
//		}
//		if (sellerAgent != null) {
//			agentInfo = agentInfo != null ? agentInfo : new WeighingBillAgentInfo();
//			this.setSellerAgentInfo(agentInfo, sellerAgent);
//		}
//		if (agentInfo != null) {
//			rows = this.agentInfoMapper.insertSelective(agentInfo);
//			if (rows <= 0) {
//				throw new AppException("保存代理人信息失败");
//			}
//		}

		WeighingBillOperationRecord wbor = this.buildOperationRecord(bill, statement, this.getUserById(bill.getCreatorId()), WeighingOperationType.WEIGH);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return rows > 0 ? BaseOutput.success().setData(statement) : BaseOutput.failure("保存过磅单失败");
	}

	// 设置买家代理人信息
	private void setBuyerAgentInfo(WeighingBillAgentInfo agentInfo, Customer buyerAgent) {
		agentInfo.setBuyerAgentCode(buyerAgent.getCode());
		agentInfo.setBuyerAgentId(buyerAgent.getId());
		agentInfo.setBuyerAgentName(buyerAgent.getName());
		agentInfo.setBuyerAgentType(buyerAgent.getCustomerMarket().getType());
	}

	private Customer getCustomerAgent(UserAccountCardResponseDto customerInfo, WeighingBill weighingBill) {
		// 卖家代理人
		RelatedQuery agentQuery = new RelatedQuery();
		agentQuery.setCustomerId(customerInfo.getCustomerId());
		agentQuery.setMarketId(weighingBill.getMarketId());
		BaseOutput<List<RelatedDto>> relatedOutput = this.relatedRpc.getParent(agentQuery);
		if (!relatedOutput.isSuccess()) {
			throw new AppException(relatedOutput.getMessage());
		}
		if (CollectionUtils.isEmpty(relatedOutput.getData())) {
			return null;
		}
		BaseOutput<Customer> agentOutput = this.customerRpc.get(relatedOutput.getData().get(0).getParent(), weighingBill.getMarketId());
		if (!agentOutput.isSuccess()) {
			throw new AppException(agentOutput.getMessage());
		}
		if (agentOutput.getData() == null) {
			throw new AppException("未查询到买方代理人");
		}
		return agentOutput.getData();
	}

	// 设置卖家代理人信息
	private void setSellerAgentInfo(WeighingBillAgentInfo agentInfo, Customer sellerAgent) {
		agentInfo.setSellerAgentCode(sellerAgent.getCode());
		agentInfo.setSellerAgentId(sellerAgent.getId());
		agentInfo.setSellerAgentName(sellerAgent.getName());
		agentInfo.setSellerAgentType(sellerAgent.getCustomerMarket().getType());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> autoClose() {
		Example example = new Example(WeighingBill.class);
		example.createCriteria().andLessThanOrEqualTo("createdTime", LocalDateTime.now()).andEqualTo("state", WeighingBillState.NO_SETTLEMENT.getValue());
		List<WeighingBill> wbList = this.getActualDao().selectByExample(example);
		wbList.forEach(wb -> {
			LocalDateTime now = LocalDateTime.now();
			wb.setState(WeighingBillState.CLOSED.getValue());
			wb.setModifiedTime(now);
			int rows = this.getActualDao().updateByPrimaryKeySelective(wb);
			if (rows <= 0) {
				throw new AppException("更新过磅单失败");
			}
			// 查询未结算单
			WeighingStatement wsQuery = new WeighingStatement();
			wsQuery.setWeighingBillId(wb.getId());
			wsQuery.setState(WeighingStatementState.UNPAID.getValue());
			WeighingStatement ws = this.weighingStatementMapper.selectOne(wsQuery);
			if (ws != null) {
				ws.setState(WeighingStatementState.CLOSED.getValue());
				ws.setModifiedTime(now);
				rows = this.weighingStatementMapper.updateByPrimaryKeySelective(ws);
				if (rows <= 0) {
					throw new AppException("更新结算单失败");
				}
			}
		});
		return BaseOutput.success();

	}

	@Override
	public WeighingBillDetailDto detail(Long id) {
		return this.getActualDao().selectDetailById(id);
	}

	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> freeze(String serialNo, String buyerPassword, Long operatorId) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		WeighingBill weighingBill = this.getActualDao().selectOne(query);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			return BaseOutput.failure("当前结算单状态不能进行冻结操作");
		}

		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setWeighingBillId(weighingBill.getId());
		WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
		if (weighingStatement == null) {
			return BaseOutput.failure("未查询到结算单信息");
		}

		// 判断余额是否足够
		AccountRequestDto balanceQuery = new AccountRequestDto();
		balanceQuery.setAccountId(weighingBill.getBuyerAccount());
		BaseOutput<AccountBalanceDto> balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
		if (!balanceOutput.isSuccess()) {
			return BaseOutput.failure(balanceOutput.getMessage());
		}
		if (balanceOutput.getData().getAvailableAmount() < weighingStatement.getFrozenAmount()) {
			return BaseOutput.failure(String.format("买方卡账户余额不足，还需充值%s元", MoneyUtils.centToYuan(weighingStatement.getFrozenAmount() - balanceOutput.getData().getAvailableAmount())));
		}

		// 冻结交易
		Long buyerAmount = weighingStatement.getFrozenAmount();
		// 创建支付订单
		PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
		prepareDto.setAccountId(weighingBill.getSellerAccount());
		prepareDto.setAmount(buyerAmount);
		prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
		prepareDto.setSerialNo(weighingBill.getSerialNo());
		prepareDto.setType(PaymentTradeType.PREAUTHORIZED.getValue());
		BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
		if (!paymentOutput.isSuccess()) {
			return BaseOutput.failure(paymentOutput.getMessage());
		}
		weighingStatement.setPayOrderNo(paymentOutput.getData().getTradeId());

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.FROZEN.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		weighingStatement.setState(WeighingStatementState.FROZEN.getValue());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		User operator = this.getUserById(operatorId);
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.FREEZE);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getBuyerAccount());
		dto.setBusinessId(weighingBill.getBuyerCardAccount());
		dto.setAmount(weighingStatement.getFrozenAmount());
		dto.setTradeId(weighingStatement.getPayOrderNo());
		dto.setPassword(buyerPassword);
		BaseOutput<PaymentTradeCommitResponseDto> freezeOutput = this.payRpc.commitTrade(dto);

		if (!freezeOutput.isSuccess()) {
			throw new AppException(freezeOutput.getMessage());
		}

		// 记账冻结流水
		PaymentTradeCommitResponseDto data = freezeOutput.getData();
		SerialRecordDo srDto = new SerialRecordDo();
		srDto.setCustomerType(weighingBill.getBuyerType());
		srDto.setAccountId(weighingBill.getBuyerCardAccount());
		srDto.setTradeNo(weighingStatement.getPayOrderNo());
		srDto.setTradeType(PaymentTradeType.PREAUTHORIZED.getValue());
		srDto.setSerialNo(weighingStatement.getSerialNo());
		srDto.setAction(ActionType.EXPENSE.getCode());
		srDto.setAmount(data.getFrozenAmount());
		srDto.setCardNo(weighingBill.getBuyerCardNo());
		srDto.setCustomerId(weighingBill.getBuyerId());
		srDto.setCustomerName(weighingBill.getBuyerName());
		srDto.setCustomerNo(weighingBill.getBuyerCode());
		srDto.setStartBalance(data.getBalance() - data.getFrozenBalance());
		srDto.setEndBalance(data.getBalance() - (data.getFrozenBalance() + data.getFrozenAmount()));
		srDto.setFirmId(this.getMarketIdByOperatorId(operatorId));
		srDto.setFundItem(FundItem.TRADE_FREEZE.getCode());
		srDto.setFundItemName(FundItem.TRADE_FREEZE.getName());
		srDto.setOperateTime(now);
		srDto.setOperatorId(operatorId);
		srDto.setOperatorName(operator.getRealName());
		srDto.setOperatorNo(operator.getUserName());
		srDto.setNotes(String.format("冻结，过磅单号%s", weighingBill.getSerialNo()));
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(Arrays.asList(srDto)));

		return BaseOutput.successData(weighingStatement);
	}

	public WeighingBillMapper getActualDao() {
		return (WeighingBillMapper) getDao();
	}

	@Override
	public PrintTemplateDataDto<WeighingBillPrintDto> getWeighingBillPrintData(String serialNo) {
		WeighingBill weighingBill = this.getWeighingBillBySerialNo(serialNo);
		if (weighingBill == null) {
			return null;
		}
		WeighingBillPrintDto dto = BeanConver.copyBean(weighingBill, WeighingBillPrintDto.class);
		// 设置过磅员信息
		WeighingBillOperationRecord wborQuery = new WeighingBillOperationRecord();
		wborQuery.setWeighingBillSerialNo(serialNo);
		wborQuery.setOperationType(WeighingOperationType.WEIGH.getValue());
		wborQuery.setSort("operation_time");
		wborQuery.setOrder("desc");
		List<WeighingBillOperationRecord> weighingOpList = this.wbrMapper.select(wborQuery);
		WeighingBillOperationRecord weighingOp = weighingOpList.get(0);
		dto.setWeighingOperatorName(weighingOp.getOperatorName());
		dto.setWeighingOperatorUserName(weighingOp.getOperatorUserName());
		return new PrintTemplateDataDto<WeighingBillPrintDto>("WeighingDocument", dto);
	}

	@Override
	public PrintTemplateDataDto<WeighingStatementPrintDto> getWeighingStatementPrintData(String serialNo) {
		WeighingStatement weighingStatement = this.getWeighingStatementBySerialNo(serialNo);
		if (weighingStatement == null) {
			return null;
		}
		WeighingStatementPrintDto dto = BeanConver.copyBean(weighingStatement, WeighingStatementPrintDto.class);
		// 设置过磅信息
		WeighingBill wbQuery = new WeighingBill();
		wbQuery.setId(weighingStatement.getWeighingBillId());
		WeighingBill wb = this.getActualDao().selectOne(wbQuery);
		dto.setUnitWeight(wb.getUnitWeight());
		dto.setUnitPrice(wb.getUnitPrice());
		dto.setMeasureType(wb.getMeasureType());
		dto.setTareWeight(wb.getTareWeight());
		dto.setRoughWeight(wb.getRoughWeight());
		dto.setNetWeight(wb.getNetWeight());
		dto.setUnitAmount(wb.getUnitAmount());
		dto.setBuyerCode(wb.getBuyerCode());
		dto.setSellerCode(wb.getSellerCode());
		dto.setGoodsName(wb.getGoodsName());
		dto.setPlateNumber(wb.getPlateNumber());
		dto.setTradeTypeId(wb.getTradeTypeId());
		dto.setSubtractionRate(wb.getSubtractionRate());
		dto.setSubtractionWeight(wb.getSubtractionWeight());

		// 设置结算员信息
		WeighingBillOperationRecord wborQuery = new WeighingBillOperationRecord();
		wborQuery.setWeighingBillId(weighingStatement.getWeighingBillId());
		wborQuery.setStatementSerialNo(serialNo);
		wborQuery.setOperationType(WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG.get(WeighingStatementState.valueOf(weighingStatement.getState())).getValue());
		WeighingBillOperationRecord settleOp = this.wbrMapper.selectOne(wborQuery);
		if (settleOp == null) {
			throw new AppException("未找到过磅操作信息");
		}
		dto.setSettlementOperatorName(settleOp.getOperatorName());
		dto.setSettlementOperatorUserName(settleOp.getOperatorUserName());

		// 设置过磅员信息
		wborQuery = new WeighingBillOperationRecord();
		wborQuery.setWeighingBillId(weighingStatement.getWeighingBillId());
		wborQuery.setStatementSerialNo(serialNo);
		wborQuery.setOperationType(WeighingOperationType.WEIGH.getValue());
		wborQuery.setSort("operation_time");
		wborQuery.setOrder("desc");
		List<WeighingBillOperationRecord> weighingOpList = this.wbrMapper.select(wborQuery);
		WeighingBillOperationRecord weighingOp = weighingOpList.get(0);
		dto.setWeighingOperatorName(weighingOp.getOperatorName());
		dto.setWeighingOperatorUserName(weighingOp.getOperatorUserName());

		// 查询买方余额
		AccountRequestDto balanceQuery = new AccountRequestDto();
		balanceQuery.setAccountId(wb.getBuyerAccount());
		BaseOutput<AccountBalanceDto> balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
		if (!balanceOutput.isSuccess()) {
			LOGGER.error(balanceOutput.getMessage());
			return null;
		}
		dto.setBuyerBalance(balanceOutput.getData().getAvailableAmount());
		String tempName = wb.getMeasureType().equals(MeasureType.WEIGHT.getValue()) ? "SettlementDocument" : "SettlementPieceDocument";
		return new PrintTemplateDataDto<WeighingStatementPrintDto>(tempName, dto);
	}

	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> invalidate(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		WeighingBill weighingBill = this.getActualDao().selectOne(query);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		Integer beforeUpdateState = weighingBill.getState();

		// 校验买卖双方密码
		AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
		buyerPwdDto.setAccountId(weighingBill.getBuyerAccount());
		buyerPwdDto.setPassword(buyerPassword);
		BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
		if (!pwdOutput.isSuccess()) {
			return pwdOutput;
		}

		AccountPasswordValidateDto sellerPwdDto = new AccountPasswordValidateDto();
		sellerPwdDto.setAccountId(weighingBill.getSellerAccount());
		sellerPwdDto.setPassword(sellerPassword);
		pwdOutput = this.payRpc.validateAccountPassword(sellerPwdDto);
		if (!pwdOutput.isSuccess()) {
			return pwdOutput;
		}

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.INVALIDATED.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		// 作废结算单
		WeighingStatement ws = this.getNoSettlementWeighingStatementByWeighingBillId(weighingBill.getId());
		if (ws != null) {
			ws.setState(WeighingStatementState.INVALIDATED.getValue());
			ws.setModifierId(operatorId);
			ws.setModifiedTime(LocalDateTime.now());
			rows = this.weighingStatementMapper.updateByPrimaryKeySelective(ws);
			if (rows <= 0) {
				throw new AppException("作废结算单失败");
			}
		}

		User operator = this.getUserById(operatorId);
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, ws, operator, WeighingOperationType.INVALIDATE);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 解冻
		if (beforeUpdateState.equals(WeighingBillState.FROZEN.getValue())) {
			// 退款
			WeighingStatement wsQuery = new WeighingStatement();
			wsQuery.setWeighingBillId(weighingBill.getId());
			WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
			if (weighingStatement == null) {
				return BaseOutput.failure("未查询到结算单信息");
			}
			PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
			cancelDto.setTradeId(weighingStatement.getPayOrderNo());
			BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
			if (!paymentOutput.isSuccess()) {
				throw new AppException(paymentOutput.getMessage());
			}
			this.recordUnfreezeAccountFlow(operatorId, weighingBill, ws, paymentOutput.getData());
		}

		return BaseOutput.success();
	}

	@Override
	public List<WeighingBillClientListDto> listByExampleModified(WeighingBillQueryDto weighingBill) {
		return this.getActualDao().selectByExampleModified(weighingBill);
	}

	@Override
	public PageOutput<List<WeighingBillListPageDto>> listPage(WeighingBillQueryDto query) {
		Integer page = query.getPage();
		page = (page == null) ? Integer.valueOf(1) : page;
		if (query.getRows() != null && query.getRows() >= 1) {
			// 为了线程安全,请勿改动下面两行代码的顺序
			PageHelper.startPage(page, query.getRows());
		}
		List<WeighingBillListPageDto> list = this.getActualDao().listPage(query);
		Page<WeighingBillListPageDto> pageList = (Page<WeighingBillListPageDto>) list;
		long total = pageList.getTotal();
		return PageOutput.success().setData(list).setTotal((int) total).setPageNum(pageList.getPageNum()).setPageSize(pageList.getPageSize());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> operatorInvalidate(Long id, Long operatorId) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		Integer beforeUpdateState = weighingBill.getState();

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.INVALIDATED.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		// 作废结算单
		WeighingStatement ws = this.getNoSettlementWeighingStatementByWeighingBillId(weighingBill.getId());
		if (ws != null) {
			ws.setState(WeighingStatementState.INVALIDATED.getValue());
			ws.setModifierId(operatorId);
			ws.setModifiedTime(LocalDateTime.now());
			rows = this.weighingStatementMapper.updateByPrimaryKeySelective(ws);
			if (rows <= 0) {
				throw new AppException("作废结算单失败");
			}
		}

		User operator = this.getUserById(operatorId);
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, ws, operator, WeighingOperationType.INVALIDATE);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 解冻
		if (beforeUpdateState.equals(WeighingBillState.FROZEN.getValue())) {
			// 退款
			WeighingStatement wsQuery = new WeighingStatement();
			wsQuery.setWeighingBillId(weighingBill.getId());
			WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
			if (weighingStatement == null) {
				return BaseOutput.failure("未查询到结算单信息");
			}
			PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
			cancelDto.setTradeId(weighingStatement.getPayOrderNo());
			BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
			if (!paymentOutput.isSuccess()) {
				throw new AppException(paymentOutput.getMessage());
			}
			this.recordUnfreezeAccountFlow(operatorId, weighingBill, ws, paymentOutput.getData());
		}
		return BaseOutput.success();
	}

	/**
	 * json 修改，再调用方法buildWeighingStatement的时候，重新创建了一个结算单，并且设置了PayOrderNo
	 * 在调用recordWithdrawAccountFlow的时候，卖家信息，在第一个streams中获取，买家信息在relation.streams中获取，按照刚哥的说法。
	 * 不知道是否存在问题，目前是可以撤销成功
	 * 
	 * @param id         过磅id
	 * @param operatorId 操作员id
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> operatorWithdraw(Long id, Long operatorId) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setWeighingBillId(weighingBill.getId());
		wsQuery.setState(WeighingStatementState.PAID.getValue());
		WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
		if (weighingStatement == null) {
			return BaseOutput.failure("结算单不存在");
		}
		LocalDate todayDate = LocalDate.now();
		LocalDateTime opTime = weighingStatement.getModifiedTime() == null ? weighingStatement.getCreatedTime() : weighingStatement.getModifiedTime();
		if (todayDate.toEpochDay() - opTime.toLocalDate().toEpochDay() > 90) {
			return BaseOutput.failure("只能对90天内过磅交易进行撤销操作");
		}
		if (!weighingBill.getState().equals(WeighingBillState.SETTLED.getValue())) {
			return BaseOutput.failure("当前状态不能撤销");
		}

		LocalDateTime now = LocalDateTime.now();
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		int rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		weighingBill.setState(WeighingBillState.NO_SETTLEMENT.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		User operator = this.getUserById(operatorId);
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.WITHDRAW);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 重新生成一条结算单
		WeighingStatement newWs = this.buildWeighingStatement(weighingBill, this.getMarketIdByOperatorId(operatorId));
		newWs.setCreatorId(operatorId);
		rows = this.weighingStatementMapper.insertSelective(newWs);
		if (rows <= 0) {
			throw new AppException("创建结算单失败");
		}
		// 设置代理人信息
//		WeighingBillAgentInfo agentInfoQuery = new WeighingBillAgentInfo();
//		agentInfoQuery.setWeighingStatementId(weighingStatement.getId());
//		WeighingBillAgentInfo agentInfo = this.agentInfoMapper.selectOne(agentInfoQuery);
//		agentInfo.setId(null);
//		agentInfo.setWeighingStatementId(newWs.getId());
//		agentInfo.setWeighingStatementSerialNo(newWs.getSerialNo());
//		rows = this.agentInfoMapper.insertSelective(agentInfo);
//		if (rows <= 0) {
//			throw new AppException("保存代理人信息失败");
//		}

		// 恢复皮重单
		if (StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 判断是否是d牌
			// 通过c端传入的数据，去jmsf获取皮重当相关信息
			BaseOutput<TruckDTO> output = this.jsmfRpc.recoverById(Long.valueOf(weighingBill.getTareBillNumber()));
			if (!output.isSuccess()) {
				return BaseOutput.failure(output.getMessage());
			}
		}

		// 退款
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		cancelDto.setTradeId(weighingStatement.getPayOrderNo());
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
		if (!paymentOutput.isSuccess()) {
			throw new AppException(paymentOutput.getMessage());
		}

		// 记录交易流水
		this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill, weighingStatement);

		return BaseOutput.success();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> settle(String serialNo, String buyerPassword, Long operatorId, Long marketId) {
		WeighingBill weighingBill = this.getWeighingBillBySerialNo(serialNo);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前结算单状态不能进行支付操作");
		}

		if (this.isFreeze(weighingBill)) {
			return this.freeze(serialNo, buyerPassword, operatorId);
		}

		// 查询未结算单
		WeighingStatement weighingStatement = this.getNoSettlementWeighingStatementByWeighingBillId(weighingBill.getId());
		if (weighingStatement == null) {
			return BaseOutput.failure("未查询到结算单信息");
		}

		// 判断余额是否足够
		AccountRequestDto balanceQuery = new AccountRequestDto();
		balanceQuery.setAccountId(weighingBill.getBuyerAccount());
		BaseOutput<AccountBalanceDto> balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
		if (!balanceOutput.isSuccess()) {
			return BaseOutput.failure(balanceOutput.getMessage());
		}
		Long banlance = balanceOutput.getData().getAvailableAmount();
		if (weighingStatement.getState().equals(WeighingStatementState.FROZEN.getValue()) && weighingStatement.getFrozenAmount() != null) {
			banlance += weighingStatement.getFrozenAmount();
		}
		if (banlance < weighingStatement.getBuyerActualAmount()) {
			return BaseOutput.failure(String.format("买方卡账户余额不足，还需充值%s元", MoneyUtils.centToYuan(weighingStatement.getBuyerActualAmount() - balanceOutput.getData().getAvailableAmount())));
		}

		if (this.checkPrice) {
			// 检查中间价
			if (weighingBill.getPriceState() == null && weighingBill.getCheckPrice() != null && weighingBill.getCheckPrice()) {
				// 获取商品中间价
				Long referencePrice = this.referencePriceService.getReferencePriceByGoodsId(weighingBill.getGoodsId(), this.getMarketIdByOperatorId(operatorId),
						weighingBill.getTradeType().toString());
				if (referencePrice != null && referencePrice > 0) {
					// 比较价格
					Long actualPrice = this.getConvertUnitPrice(weighingBill);
					if (actualPrice <= referencePrice) {
						// 没有审批过且价格异常需要走审批流程
						PriceApproveRecord approve = new PriceApproveRecord();
						approve.setBuyerCardNo(weighingBill.getBuyerCardNo());
						approve.setBuyerId(weighingBill.getBuyerId());
						approve.setBuyerName(weighingBill.getBuyerName());
						approve.setGoodsId(weighingBill.getGoodsId());
						approve.setGoodsName(weighingBill.getGoodsName());
						approve.setReferencePrice(referencePrice);
						approve.setSellerCardNo(weighingBill.getSellerCardNo());
						approve.setSellerId(weighingBill.getSellerId());
						approve.setSellerName(weighingBill.getSellerName());
						approve.setTradeType(weighingBill.getTradeType().toString());
						approve.setTradeWeight(this.getWeighingBillTradeWeight(weighingBill));
						approve.setUnitPrice(actualPrice);
						approve.setWeighingBillId(weighingBill.getId());
						approve.setWeighingBillSerialNo(serialNo);
						approve.setWeighingTime(this.getWeighingBillWeighingTime(weighingBill));
						approve.setMarketId(weighingBill.getMarketId());
						int rows = this.priceApproveMapper.insertSelective(approve);
						if (rows <= 0) {
							BaseOutput.failure("保存价格审批记录失败");
						}
						weighingBill.setPriceState(PriceState.APPROVING.getValue());
						rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
						if (rows <= 0) {
							BaseOutput.failure("更新过磅单价格状态失败");
						}
						BaseOutput<ProcessInstanceMapping> output = this.runtimeRpc.startProcessInstanceByKey(OrdersConstant.PRICE_APPROVE_PROCESS_DEFINITION_KEY, approve.getId().toString(),
								operatorId.toString(), new HashMap<String, Object>());
						if (!output.isSuccess()) {
							throw new AppException(output.getMessage());
						}
						approve.setProcessDefinitionId(output.getData().getProcessDefinitionId());
						approve.setProcessInstanceId(output.getData().getProcessInstanceId());
						this.priceApproveMapper.updateByPrimaryKeySelective(approve);
						return BaseOutput.failure("交易单价低于参考价，需人工审核");
					}
				}
			}
		}

		if (weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			BaseOutput<?> output = this.prepareTrade(weighingBill, weighingStatement);
			if (!output.isSuccess()) {
				throw new AppException(output.getMessage());
			}
		}

		// 删除皮重单
		// json 修改开始
		if (StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 判断是否是d牌
			// 通过c端传入的数据，去jmsf获取皮重当相关信息
			BaseOutput<TruckDTO> byId = this.jsmfRpc.getTruckById(Long.valueOf(weighingBill.getTareBillNumber()));
			// 判断是否查询成功
			if (!byId.isSuccess()) {
				throw new AppException("查询过磅单失败");
			}
			// 成功之后，判断是否是d牌，如果是d牌的话，不删除过磅单
			if (!Objects.equals(byId.getData().getCarType(), 1L)) {
				// 在判断车牌号是否相等，想的就删除过磅单
				if (Objects.equals(weighingBill.getPlateNumber(), byId.getData().getPlate())) {
					// 删除皮重单
					BaseOutput<Object> jmsfOutput = this.jsmfRpc.removeTareNumber(Long.valueOf(weighingBill.getTareBillNumber()));
					if (!jmsfOutput.isSuccess()) {
						throw new AppException("删除过磅单失败");
					}
				}
			}
		}
		// json 修改结束

		Integer originalState = weighingBill.getState();
		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.SETTLED.getValue());
		weighingBill.setSettlementTime(now);
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			throw new AppException("更新过磅单状态失败");

		}

		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		weighingStatement.setState(WeighingStatementState.PAID.getValue());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			throw new AppException("更新过磅单状态失败");
		}

		User operator = this.getUserById(operatorId);
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.SETTLE);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = null;
		// 判断单据状态，如果是冻结单走确认预授权扣款，否则走即时交易
		if (originalState.equals(WeighingBillState.FROZEN.getValue())) {
			paymentOutput = this.confirmTrade(weighingBill, weighingStatement, buyerPassword);
		} else {
			paymentOutput = this.commitTrade(weighingBill, weighingStatement, buyerPassword);
		}
		if (!paymentOutput.isSuccess()) {
			throw new AppException(paymentOutput.getMessage());
		}

		// 记录资金账户交易流水
		this.recordSettlementAccountFlow(weighingBill, weighingStatement, paymentOutput.getData(), operatorId);
		// 发送mq通知中间价计算模块计算中间价
		this.sendCalculateReferencePriceMessage(weighingBill, marketId, weighingStatement.getTradeAmount());
		return BaseOutput.successData(weighingStatement);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> updateWeighingBill(WeighingBill dto) {
		WeighingBill weighingBill = this.getWeighingBillBySerialNo(dto.getSerialNo());
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能修改过磅单");
		}
		this.updateWeihingBillInfo(weighingBill, dto);
		// 查询未结算单
		WeighingStatement ws = this.getNoSettlementWeighingStatementByWeighingBillId(weighingBill.getId());
		if (ws == null) {
			return BaseOutput.failure("未找到结算单");
		}

		// 判断重复冻结
		if (this.isRepeatFreeze(dto, ws)) {
			return BaseOutput.failure("不能重复冻结");
		}

		Long marketId = this.getMarketIdByOperatorId(dto.getModifierId());

		if (ws.getState().equals(WeighingStatementState.UNPAID.getValue())) {
			// 修改状态是“未结算”单据时，卖方信息不可修改，其它均可修改；->买方信息可以修改
			UserAccountCardResponseDto buyerInfo = this.setWeighingBillBuyerInfo(weighingBill);
			// 设置代理人信息
//			WeighingBillAgentInfo agentInfoQuery = new WeighingBillAgentInfo();
//			agentInfoQuery.setWeighingStatementId(ws.getId());
//			WeighingBillAgentInfo agentInfo = this.agentInfoMapper.selectOne(agentInfoQuery);
//			Customer buyerAgent = this.getCustomerAgent(buyerInfo, weighingBill);
//			if (buyerAgent != null) {
//				int rows = 0;
//				if (agentInfo != null) {
//					this.setBuyerAgentInfo(agentInfo, buyerAgent);
//					rows = this.agentInfoMapper.updateByPrimaryKeySelective(agentInfo);
//				} else {
//					this.setBuyerAgentInfo(agentInfo, buyerAgent);
//					agentInfo = new WeighingBillAgentInfo();
//					rows = this.agentInfoMapper.insertSelective(agentInfo);
//				}
//				if (rows <= 0) {
//					throw new AppException("保存代理人信息失败");
//				}
//			}
		} else {
			// 修改状态是“已冻结”单据时，“买方、卖方、毛重”不能修改；其它可以修改；->判断下毛重是否被修改
			if (this.isWeighingBillRoughWeightUpdated(weighingBill, dto)) {
				return BaseOutput.failure("冻结单毛重不能修改");
			}
		}
		// 更新结算单买卖家信息，重新算费用
		if (this.isFreeze(weighingBill)) {
			this.setWeighingStatementFrozenAmount(weighingBill, ws);
			if (weighingBill.getFrozenAmount() != null) {
				weighingBill.setFrozenAmount(ws.getFrozenAmount());
			}
		} else {
			this.setWeighingStatementTradeAmount(weighingBill, ws);
		}
		this.setWeighingStatementBuyerInfo(weighingBill, ws, marketId);
		this.setWeighingStatementSellerInfo(weighingBill, ws, marketId);

		int rows = this.getActualDao().updateByPrimaryKey(weighingBill);
		if (rows <= 0) {
			throw new AppException("更新过磅单失败");
		}
		ws.setModifierId(dto.getModifierId());
		ws.setModifiedTime(LocalDateTime.now());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(ws);

		// 插入一条过磅信息
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, ws, this.getUserById(weighingBill.getModifierId()), WeighingOperationType.WEIGH);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		return rows > 0 ? BaseOutput.successData(ws) : BaseOutput.failure("更新过磅单失败");
	}

	@GlobalTransactional(rollbackFor = Exception.class)
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> withdraw(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		WeighingBill weighingBill = this.getActualDao().selectOne(query);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setWeighingBillId(weighingBill.getId());
		wsQuery.setState(WeighingStatementState.PAID.getValue());
		WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
		if (weighingStatement == null) {
			return BaseOutput.failure("结算单不存在");
		}
		LocalDate todayDate = LocalDate.now();
		LocalDateTime opTime = weighingStatement.getModifiedTime() == null ? weighingStatement.getCreatedTime() : weighingStatement.getModifiedTime();
		if (!todayDate.equals(opTime.toLocalDate())) {
			return BaseOutput.failure("只能对当日的过磅交易进行撤销操作");
		}
		if (!weighingBill.getState().equals(WeighingBillState.SETTLED.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		// 校验买卖双方密码
		AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
		buyerPwdDto.setAccountId(weighingBill.getBuyerAccount());
		buyerPwdDto.setPassword(buyerPassword);
		BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
		if (!pwdOutput.isSuccess()) {
			return pwdOutput;
		}

		AccountPasswordValidateDto sellerPwdDto = new AccountPasswordValidateDto();
		sellerPwdDto.setAccountId(weighingBill.getSellerAccount());
		sellerPwdDto.setPassword(sellerPassword);
		pwdOutput = this.payRpc.validateAccountPassword(sellerPwdDto);
		if (!pwdOutput.isSuccess()) {
			return pwdOutput;
		}

		LocalDateTime now = LocalDateTime.now();
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		int rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		weighingBill.setState(WeighingBillState.NO_SETTLEMENT.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}
		User operator = this.getUserById(operatorId);
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.WITHDRAW);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 重新生成一条结算单
		WeighingStatement newWs = this.buildWeighingStatement(weighingBill, this.getMarketIdByOperatorId(operatorId));
		newWs.setCreatorId(operatorId);
		rows = this.weighingStatementMapper.insertSelective(newWs);
		if (rows <= 0) {
			throw new AppException("创建结算单失败");
		}

		// 设置代理人信息
//		WeighingBillAgentInfo agentInfoQuery = new WeighingBillAgentInfo();
//		agentInfoQuery.setWeighingStatementId(weighingStatement.getId());
//		WeighingBillAgentInfo agentInfo = this.agentInfoMapper.selectOne(agentInfoQuery);
//		agentInfo.setId(null);
//		agentInfo.setWeighingStatementId(newWs.getId());
//		agentInfo.setWeighingStatementSerialNo(newWs.getSerialNo());
//		rows = this.agentInfoMapper.insertSelective(agentInfo);
//		if (rows <= 0) {
//			throw new AppException("保存代理人信息失败");
//		}

		// 恢复皮重单
		if (StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 判断是否是d牌
			// 通过c端传入的数据，去jmsf获取皮重当相关信息
			BaseOutput<TruckDTO> output = this.jsmfRpc.recoverById(Long.valueOf(weighingBill.getTareBillNumber()));
			if (!output.isSuccess()) {
				return BaseOutput.failure(output.getMessage());
			}
		}

		// 退款
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		cancelDto.setTradeId(weighingStatement.getPayOrderNo());
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
		if (!paymentOutput.isSuccess()) {
			throw new AppException(paymentOutput.getMessage());
		}

		// 记录撤销交易流水
		this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill, weighingStatement);

		return BaseOutput.success();
	}

	private WeighingBillOperationRecord buildOperationRecord(WeighingBill wb, WeighingStatement ws, User operator, WeighingOperationType type) {
		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(wb.getId());
		wbor.setWeighingBillSerialNo(wb.getSerialNo());
		wbor.setStatementId(ws.getId());
		wbor.setStatementSerialNo(ws.getSerialNo());
		wbor.setOperationType(type.getValue());
		wbor.setOperationTypeName(type.getName());
		wbor.setOperatorId(operator.getId());
		wbor.setOperatorUserName(operator.getUserName());
		wbor.setOperatorName(operator.getRealName());
		return wbor;
	}

	private WeighingStatement buildWeighingStatement(WeighingBill weighingBill, Long marketId) {
		BaseOutput<String> output = this.uidRpc.bizNumber(OrdersConstant.WEIGHING_STATEMENT_SERIAL_NO_GENERATE_RULE_CODE);
		if (!output.isSuccess()) {
			throw new AppException(output.getMessage());
		}
		WeighingStatement ws = new WeighingStatement();
		ws.setSerialNo(output.getData());
		ws.setWeighingBillId(weighingBill.getId());
		ws.setWeighingSerialNo(weighingBill.getSerialNo());

		if (this.isFreeze(weighingBill)) {
			this.setWeighingStatementFrozenAmount(weighingBill, ws);
		} else {
			this.setWeighingStatementTradeAmount(weighingBill, ws);
		}
		this.setWeighingStatementBuyerInfo(weighingBill, ws, marketId);
		this.setWeighingStatementSellerInfo(weighingBill, ws, marketId);
		ws.setState(WeighingStatementState.UNPAID.getValue());
		return ws;

	}

	private BaseOutput<List<QueryFeeOutput>> calculatePoundage(WeighingBill weighingBill, WeighingStatement statement, Long marketId, String businessType) {
		List<Long> chargeItemIds = this.getBuyerSellerRuleId(businessType, marketId);
		List<QueryFeeInput> queryFeeInputList = new ArrayList<QueryFeeInput>(chargeItemIds.size());
		chargeItemIds.forEach(c -> {
			QueryFeeInput queryFeeInput = new QueryFeeInput();
			Map<String, Object> map = new HashMap<>();
			// 设置市场id
			queryFeeInput.setMarketId(marketId);
			// 设置业务类型
			queryFeeInput.setBusinessType(businessType);
			queryFeeInput.setChargeItem(c);
			if (businessType.equals(OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE)) {
				map.put("customerType", weighingBill.getBuyerType());
				map.put("customerId", weighingBill.getBuyerId());
			} else {
				map.put("customerType", weighingBill.getSellerType());
				map.put("customerId", weighingBill.getSellerId());
			}
			map.put("goodsId", weighingBill.getGoodsId());
			LocalDateTime tradeTime = statement.getCreatedTime();
			if (tradeTime == null) {
				tradeTime = LocalDateTime.now();
			}
			map.put("tradeTime", tradeTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			map.put("unitPrice", new BigDecimal(MoneyUtils.centToYuan(weighingBill.getUnitPrice())));
			if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
				// 公斤转换为斤
				map.put("totalWeight", new BigDecimal(MoneyUtils.centToYuan(weighingBill.getNetWeight() * 2)));
			} else {
				map.put("totalWeight", new BigDecimal(weighingBill.getUnitAmount() * weighingBill.getUnitWeight()).divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
			}
			map.put("tradeTypeId", weighingBill.getTradeTypeId());
			map.put("tradeAmount", new BigDecimal(MoneyUtils.centToYuan(statement.getTradeAmount())));
			queryFeeInput.setCalcParams(map);
			queryFeeInput.setConditionParams(map);
			queryFeeInputList.add(queryFeeInput);
		});
		return chargeRuleRpc.batchQueryFee(queryFeeInputList);
	}

	private BaseOutput<PaymentTradeCommitResponseDto> commitTrade(WeighingBill weighingBill, WeighingStatement weighingStatement, String password) {
		// 提交支付订单
		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getBuyerAccount());
		dto.setPassword(password);
		dto.setTradeId(weighingStatement.getPayOrderNo());
		dto.setBusinessId(weighingBill.getBuyerCardAccount());
		List<FeeDto> fees = new ArrayList<FeeDto>(2);
		// 买家手续费
		FeeDto buyerFee = new FeeDto();
		buyerFee.setAmount(weighingStatement.getBuyerPoundage());
		buyerFee.setType(FeeType.BUYER_POUNDAGE.getValue());
		buyerFee.setTypeName(FeeType.BUYER_POUNDAGE.getName());
		buyerFee.setUseFor(FeeUse.BUYER.getValue());
		fees.add(buyerFee);
		// 卖家手续费
		FeeDto sellerFee = new FeeDto();
		sellerFee.setAmount(weighingStatement.getSellerPoundage());
		sellerFee.setType(FeeType.SELLER_POUNDAGE.getValue());
		sellerFee.setTypeName(FeeType.SELLER_POUNDAGE.getName());
		sellerFee.setUseFor(FeeUse.SELLER.getValue());
		fees.add(sellerFee);
		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.commitTrade(dto);
		return commitOutput;
	}

	private BaseOutput<PaymentTradeCommitResponseDto> confirmTrade(WeighingBill weighingBill, WeighingStatement weighingStatement, String buyerPassword) {
		// 提交支付订单
		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getBuyerAccount());
		dto.setPassword(buyerPassword);
		dto.setTradeId(weighingStatement.getPayOrderNo());
		dto.setAmount(weighingStatement.getTradeAmount());
		List<FeeDto> fees = new ArrayList<FeeDto>(2);
		// 买家手续费
		FeeDto buyerFee = new FeeDto();
		buyerFee.setAmount(weighingStatement.getBuyerPoundage());
		buyerFee.setType(FeeType.BUYER_POUNDAGE.getValue());
		buyerFee.setTypeName(FeeType.BUYER_POUNDAGE.getName());
		buyerFee.setUseFor(FeeUse.BUYER.getValue());
		fees.add(buyerFee);
		// 卖家手续费
		FeeDto sellerFee = new FeeDto();
		sellerFee.setAmount(weighingStatement.getSellerPoundage());
		sellerFee.setType(FeeType.SELLER_POUNDAGE.getValue());
		sellerFee.setTypeName(FeeType.SELLER_POUNDAGE.getName());
		sellerFee.setUseFor(FeeUse.SELLER.getValue());
		fees.add(sellerFee);
		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.confirm(dto);
		return commitOutput;
	}

	private List<Long> getBuyerSellerRuleId(String businessType, Long marketId) {
		BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
		// 业务类型
		businessChargeItemDto.setBusinessType(businessType);
		// 是否必须
		businessChargeItemDto.setIsRequired(1);
		// 收费
		businessChargeItemDto.setChargeType(BusinessChargeItemEnum.ChargeType.收费.getCode());
		// 市场id
		businessChargeItemDto.setMarketId(marketId);
		BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
		// 判断是否成功
		if (!listBaseOutput.isSuccess()) {
			throw new AppException(listBaseOutput.getMessage());
		}
		if (CollectionUtils.isEmpty(listBaseOutput.getData())) {
			return null;
		}
		List<Long> ruleIds = new ArrayList<Long>(listBaseOutput.getData().size());
		listBaseOutput.getData().forEach(bci -> ruleIds.add(bci.getId()));
		return ruleIds;
	}

	// 获取单价，如果按件计价需要转换为按斤计价
	private Long getConvertUnitPrice(WeighingBill weighingBill) {
		Long actualPrice = null;
		if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			actualPrice = weighingBill.getUnitPrice();
		} else {
			// 转换为斤的价格，保留到分，四舍五入
			actualPrice = new BigDecimal(weighingBill.getUnitPrice()).divide(new BigDecimal(weighingBill.getUnitWeight()), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).longValue();
		}
		return actualPrice;
	}

	private Long getMarketIdByOperatorId(Long operatorId) {
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			throw new AppException(output.getMessage());
		}
		BaseOutput<Firm> firmOutput = this.firmRpc.getByCode(output.getData().getFirmCode());
		if (!firmOutput.isSuccess()) {
			throw new AppException(firmOutput.getMessage());
		}
		return firmOutput.getData().getId();
	}

	private WeighingStatement getNoSettlementWeighingStatementByWeighingBillId(Long weighingBillId) {
		// 查询未结算单
		Example example = new Example(WeighingStatement.class);
		example.createCriteria().andIn("state", Arrays.asList(WeighingStatementState.FROZEN.getValue(), WeighingStatementState.UNPAID.getValue())).andEqualTo("weighingBillId", weighingBillId);
		WeighingStatement ws = this.weighingStatementMapper.selectOneByExample(example);
		return ws;
	}

	private User getUserById(Long operatorId) {
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			throw new AppException(output.getMessage());
		}
		return output.getData();
	}

	private WeighingBill getWeighingBillBySerialNo(String serialNo) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		return this.getActualDao().selectOne(query);
	}

	private Integer getWeighingBillTradeWeight(WeighingBill weighingBill) {
		if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			return weighingBill.getNetWeight();
		} else {
			return weighingBill.getUnitWeight() * weighingBill.getUnitAmount() / 2;
		}
	}

	private LocalDateTime getWeighingBillWeighingTime(WeighingBill weighingBill) {
		if (weighingBill.getModifiedTime() != null) {
			return weighingBill.getModifiedTime();
		}
		return weighingBill.getCreatedTime();
	}

	private WeighingStatement getWeighingStatementBySerialNo(String serialNo) {
		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setSerialNo(serialNo);
		return this.weighingStatementMapper.selectOne(wsQuery);
	}

	// 判断是否需要走冻结流程
	private boolean isFreeze(WeighingBill weighingBill) {
		// 按件计重不需要冻结
		if (weighingBill.getMeasureType().equals(MeasureType.PIECE.getValue())) {
			return false;
		}
		// 若输入“毛重”，且“皮重，除杂比例，除杂重量”至少输入一个，则不论估计净重是否填写， 直接进入交易结算流程
		if (weighingBill.getTareWeight() != null) {
			return false;
		}
		if (weighingBill.getSubtractionRate() != null) {
			return false;
		}
		if (weighingBill.getSubtractionWeight() != null) {
			return false;
		}
		// 若只输入“毛重”，则净重=毛重，走结算流程
		if (weighingBill.getRoughWeight() != null && weighingBill.getEstimatedNetWeight() == null) {
			return false;
		}
		// 若只输入“毛重，估计净重”，则按估计净重走冻结单
		return true;
	}

	private boolean isRepeatFreeze(WeighingBill weighingBill, WeighingStatement ws) {
		return this.isFreeze(weighingBill) && ws.getState().equals(WeighingStatementState.FROZEN.getValue());
	}

	private boolean isWeighingBillRoughWeightUpdated(WeighingBill weighingBill, WeighingBill dto) {
		if (weighingBill.getRoughWeight() == null && dto.getRoughWeight() != null) {
			return true;
		}
		return !weighingBill.getRoughWeight().equals(dto.getRoughWeight());
	}

	private BaseOutput<?> prepareTrade(WeighingBill weighingBill, WeighingStatement ws) {
		// 创建支付订单
		PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
		prepareDto.setAccountId(weighingBill.getSellerAccount());
		prepareDto.setAmount(ws.getTradeAmount());
		prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
		prepareDto.setSerialNo(ws.getSerialNo());
		prepareDto.setType(PaymentTradeType.TRADE.getValue());
		BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
		if (!paymentOutput.isSuccess()) {
			return paymentOutput;
		}
		ws.setPayOrderNo(paymentOutput.getData().getTradeId());
		ws.setState(WeighingStatementState.PAID.getValue());
		return paymentOutput;
	}

	private void recordSettlementAccountFlow(WeighingBill weighingBill, WeighingStatement ws, PaymentTradeCommitResponseDto paymentResult, Long operatorId) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		LocalDateTime now = LocalDateTime.now();
		User operator = this.getUserById(operatorId);
		Long firmId = this.getMarketIdByOperatorId(operatorId);

		Integer tradeType = PaymentTradeType.TRADE.getValue();
		// 买家入账
		if (ws.getFrozenAmount() != null && ws.getFrozenAmount() > 0) {
			tradeType = PaymentTradeType.PREAUTHORIZED.getValue();
			// 解冻
			SerialRecordDo frozenRecord = new SerialRecordDo();
			frozenRecord.setTradeNo(ws.getPayOrderNo());
			frozenRecord.setTradeType(tradeType);
			frozenRecord.setSerialNo(ws.getSerialNo());
			frozenRecord.setCustomerType(weighingBill.getBuyerType());
			frozenRecord.setAccountId(weighingBill.getBuyerCardAccount());
			frozenRecord.setAction(ActionType.INCOME.getCode());
			frozenRecord.setAmount(Math.abs(paymentResult.getFrozenAmount()));
			frozenRecord.setCardNo(weighingBill.getBuyerCardNo());
			frozenRecord.setCustomerId(weighingBill.getBuyerId());
			frozenRecord.setCustomerName(weighingBill.getBuyerName());
			frozenRecord.setCustomerNo(weighingBill.getBuyerCode());
			frozenRecord.setStartBalance(paymentResult.getBalance() - paymentResult.getFrozenBalance());
			frozenRecord.setEndBalance(paymentResult.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
			frozenRecord.setFirmId(firmId);
			frozenRecord.setFundItem(FundItem.TRADE_UNFREEZE.getCode());
			frozenRecord.setFundItemName(FundItem.TRADE_UNFREEZE.getName());
			frozenRecord.setOperateTime(now);
			frozenRecord.setOperatorId(operatorId);
			frozenRecord.setOperatorName(operator.getRealName());
			frozenRecord.setOperatorNo(operator.getUserName());
			frozenRecord.setNotes(String.format("解冻，过磅单号%s", weighingBill.getSerialNo()));
			srList.add(frozenRecord);
		}
		// 买家支出
		PaymentStream buyerStream = paymentResult.getStreams().get(0);
		Long buyerBalance = buyerStream.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance());
		SerialRecordDo buyerExpense = new SerialRecordDo();
		buyerExpense.setTradeNo(ws.getPayOrderNo());
		buyerExpense.setTradeType(tradeType);
		buyerExpense.setSerialNo(ws.getSerialNo());
		buyerExpense.setCustomerType(weighingBill.getBuyerType());
		buyerExpense.setAccountId(weighingBill.getBuyerCardAccount());
		buyerExpense.setAction(ActionType.EXPENSE.getCode());
		buyerExpense.setAmount(Math.abs(buyerStream.getAmount()));
		buyerExpense.setCardNo(weighingBill.getBuyerCardNo());
		buyerExpense.setCustomerId(weighingBill.getBuyerId());
		buyerExpense.setCustomerName(weighingBill.getBuyerName());
		buyerExpense.setCustomerNo(weighingBill.getBuyerCode());
		buyerExpense.setStartBalance(buyerBalance);
		buyerExpense.setEndBalance(buyerBalance + buyerStream.getAmount());
		buyerExpense.setFirmId(firmId);
		buyerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		buyerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		buyerExpense.setOperateTime(now);
		buyerExpense.setOperatorId(operatorId);
		buyerExpense.setOperatorName(operator.getRealName());
		buyerExpense.setOperatorNo(operator.getUserName());
		buyerExpense.setNotes(String.format("买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerExpense);
		// 买家手续费
		PaymentStream buyerPoundageStream = paymentResult.getStreams().stream().filter(s -> s.getType().equals(FeeType.BUYER_POUNDAGE.getValue().longValue())).findFirst().orElse(null);
		buyerBalance = buyerPoundageStream.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance());
		SerialRecordDo buyerPoundage = new SerialRecordDo();
		buyerPoundage.setTradeNo(ws.getPayOrderNo());
		buyerPoundage.setTradeType(tradeType);
		buyerPoundage.setSerialNo(ws.getSerialNo());
		buyerPoundage.setCustomerType(weighingBill.getBuyerType());
		buyerPoundage.setAccountId(weighingBill.getBuyerCardAccount());
		buyerPoundage.setAction(ActionType.EXPENSE.getCode());
		buyerPoundage.setAmount(Math.abs(buyerPoundageStream.getAmount()));
		buyerPoundage.setCardNo(weighingBill.getBuyerCardNo());
		buyerPoundage.setCustomerId(weighingBill.getBuyerId());
		buyerPoundage.setCustomerName(weighingBill.getBuyerName());
		buyerPoundage.setCustomerNo(weighingBill.getBuyerCode());
		buyerPoundage.setStartBalance(buyerBalance);
		buyerPoundage.setEndBalance(buyerBalance + buyerPoundageStream.getAmount());
		buyerPoundage.setFirmId(this.getMarketIdByOperatorId(operatorId));
		buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		buyerPoundage.setOperateTime(now);
		buyerPoundage.setOperatorId(operatorId);
		buyerPoundage.setOperatorName(operator.getRealName());
		buyerPoundage.setOperatorNo(operator.getUserName());
		buyerPoundage.setNotes(String.format("买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerPoundage);
		// 卖家收入
		PaymentStream sellerStream = paymentResult.getRelation().getStreams().get(0);
		Long sellerBalance = sellerStream.getBalance() - (paymentResult.getRelation().getFrozenAmount() + paymentResult.getRelation().getFrozenBalance());
		SerialRecordDo sellerIncome = new SerialRecordDo();
		sellerIncome.setTradeNo(ws.getPayOrderNo());
		sellerIncome.setTradeType(tradeType);
		sellerIncome.setSerialNo(ws.getSerialNo());
		sellerIncome.setCustomerType(weighingBill.getSellerType());
		sellerIncome.setAccountId(weighingBill.getSellerCardAccount());
		sellerIncome.setAction(ActionType.INCOME.getCode());
		sellerIncome.setAmount(sellerStream.getAmount());
		sellerIncome.setCardNo(weighingBill.getSellerCardNo());
		sellerIncome.setCustomerId(weighingBill.getSellerId());
		sellerIncome.setCustomerName(weighingBill.getSellerName());
		sellerIncome.setCustomerNo(weighingBill.getSellerCode());
		sellerIncome.setStartBalance(sellerBalance);
		sellerIncome.setEndBalance(sellerBalance + sellerStream.getAmount());
		sellerIncome.setFirmId(firmId);
		sellerIncome.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		sellerIncome.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		sellerIncome.setOperateTime(now);
		sellerIncome.setOperatorId(operatorId);
		sellerIncome.setOperatorName(operator.getRealName());
		sellerIncome.setOperatorNo(operator.getUserName());
		sellerIncome.setNotes(String.format("卖方，结算单号%s", ws.getSerialNo()));
		srList.add(sellerIncome);
		// 卖家手续费
		PaymentStream sellerPoundageStream = paymentResult.getRelation().getStreams().stream().filter(s -> s.getType().equals(FeeType.SELLER_POUNDAGE.getValue().longValue())).findFirst().orElse(null);
		sellerBalance = sellerPoundageStream.getBalance() - (paymentResult.getRelation().getFrozenAmount() + paymentResult.getRelation().getFrozenBalance());
		SerialRecordDo sellerPoundage = new SerialRecordDo();
		sellerPoundage.setTradeNo(ws.getPayOrderNo());
		sellerPoundage.setTradeType(tradeType);
		sellerPoundage.setSerialNo(ws.getSerialNo());
		sellerPoundage.setCustomerType(weighingBill.getSellerType());
		sellerPoundage.setAccountId(weighingBill.getSellerCardAccount());
		sellerPoundage.setAction(ActionType.EXPENSE.getCode());
		sellerPoundage.setAmount(Math.abs(sellerPoundageStream.getAmount()));
		sellerPoundage.setCardNo(weighingBill.getSellerCardNo());
		sellerPoundage.setCustomerId(weighingBill.getSellerId());
		sellerPoundage.setCustomerName(weighingBill.getSellerName());
		sellerPoundage.setCustomerNo(weighingBill.getSellerCode());
		sellerPoundage.setStartBalance(sellerBalance);
		sellerPoundage.setEndBalance(sellerBalance + sellerPoundageStream.getAmount());
		sellerPoundage.setFirmId(firmId);
		sellerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		sellerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		sellerPoundage.setOperateTime(now);
		sellerPoundage.setOperatorId(operatorId);
		sellerPoundage.setOperatorName(operator.getRealName());
		sellerPoundage.setOperatorNo(operator.getUserName());
		sellerPoundage.setNotes(String.format("卖方，结算单号%s", ws.getSerialNo()));
		srList.add(sellerPoundage);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordUnfreezeAccountFlow(Long operatorId, WeighingBill weighingBill, WeighingStatement ws, PaymentTradeCommitResponseDto tradeResponse) {
		// 解冻
		User operator = this.getUserById(operatorId);
		LocalDateTime now = LocalDateTime.now();
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		SerialRecordDo frozenRecord = new SerialRecordDo();
		frozenRecord.setTradeNo(ws.getPayOrderNo());
		frozenRecord.setTradeType(PaymentTradeType.PREAUTHORIZED.getValue());
		frozenRecord.setSerialNo(ws.getSerialNo());
		frozenRecord.setCustomerType(weighingBill.getBuyerType());
		frozenRecord.setAccountId(weighingBill.getBuyerAccount());
		frozenRecord.setAction(ActionType.INCOME.getCode());
		frozenRecord.setAmount(Math.abs(tradeResponse.getFrozenAmount()));
		frozenRecord.setCardNo(weighingBill.getBuyerCardNo());
		frozenRecord.setCustomerId(weighingBill.getBuyerId());
		frozenRecord.setCustomerName(weighingBill.getBuyerName());
		frozenRecord.setCustomerNo(weighingBill.getBuyerCode());
		frozenRecord.setStartBalance(tradeResponse.getBalance() - tradeResponse.getFrozenBalance());
		frozenRecord.setEndBalance(tradeResponse.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance()));
		frozenRecord.setFirmId(this.getMarketIdByOperatorId(operatorId));
		frozenRecord.setFundItem(FundItem.TRADE_UNFREEZE.getCode());
		frozenRecord.setFundItemName(FundItem.TRADE_UNFREEZE.getName());
		frozenRecord.setOperateTime(now);
		frozenRecord.setOperatorId(operatorId);
		frozenRecord.setOperatorName(operator.getRealName());
		frozenRecord.setOperatorNo(operator.getUserName());
		frozenRecord.setNotes(String.format("作废，过磅单号%s", weighingBill.getSerialNo()));
		srList.add(frozenRecord);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordWithdrawAccountFlow(Long operatorId, PaymentTradeCommitResponseDto tradeResponse, WeighingBill weighingBill, WeighingStatement ws) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		Long firmId = this.getMarketIdByOperatorId(operatorId);
		User operator = this.getUserById(operatorId);
		LocalDateTime now = LocalDateTime.now();
		Integer tradeType = PaymentTradeType.TRADE.getValue();
		if (ws.getFrozenAmount() != null && ws.getFrozenAmount() > 0) {
			tradeType = PaymentTradeType.TRADE.getValue();
		}
		// 卖家手续费
		PaymentStream sellerPoundageStream = tradeResponse.getStreams().stream().filter(s -> s.getType().equals(FeeType.SELLER_POUNDAGE.getValue().longValue())).findFirst().orElse(null);
		Long sellerBalance = sellerPoundageStream.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance());
		SerialRecordDo sellerRefound = new SerialRecordDo();
		sellerRefound.setTradeNo(ws.getPayOrderNo());
		sellerRefound.setTradeType(tradeType);
		sellerRefound.setSerialNo(ws.getSerialNo());
		sellerRefound.setCustomerType(weighingBill.getBuyerType());
		sellerRefound.setAccountId(weighingBill.getSellerCardAccount());
		sellerRefound.setAction(ActionType.INCOME.getCode());
		sellerRefound.setAmount(sellerPoundageStream.getAmount());
		sellerRefound.setCardNo(weighingBill.getSellerCardNo());
		sellerRefound.setCustomerId(weighingBill.getSellerId());
		sellerRefound.setCustomerName(weighingBill.getSellerName());
		sellerRefound.setCustomerNo(weighingBill.getSellerCode());
		sellerRefound.setStartBalance(sellerBalance);
		sellerRefound.setEndBalance(sellerBalance + sellerPoundageStream.getAmount());
		sellerRefound.setFirmId(firmId);
		sellerRefound.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		sellerRefound.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		sellerRefound.setOperateTime(now);
		sellerRefound.setOperatorId(operatorId);
		sellerRefound.setOperatorNo(operator.getUserName());
		sellerRefound.setOperatorName(operator.getRealName());
		sellerRefound.setNotes(String.format("撤销，卖方，结算单号%s", ws.getSerialNo()));
		srList.add(sellerRefound);
		// 卖家退款
		PaymentStream sellerExpenseStream = tradeResponse.getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
		sellerBalance = sellerExpenseStream.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance());
		SerialRecordDo sellerExpense = new SerialRecordDo();
		sellerExpense.setTradeNo(ws.getPayOrderNo());
		sellerExpense.setTradeType(tradeType);
		sellerExpense.setSerialNo(ws.getSerialNo());
		sellerExpense.setCustomerType(weighingBill.getSellerType());
		sellerExpense.setAccountId(weighingBill.getSellerCardAccount());
		sellerExpense.setAction(ActionType.EXPENSE.getCode());
		sellerExpense.setAmount(Math.abs(sellerExpenseStream.getAmount()));
		sellerExpense.setCardNo(weighingBill.getSellerCardNo());
		sellerExpense.setCustomerId(weighingBill.getSellerId());
		sellerExpense.setCustomerName(weighingBill.getSellerName());
		sellerExpense.setCustomerNo(weighingBill.getSellerCode());
		sellerExpense.setStartBalance(sellerBalance);
		sellerExpense.setEndBalance(sellerBalance + sellerExpenseStream.getAmount());
		sellerExpense.setFirmId(firmId);
		sellerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		sellerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		sellerExpense.setOperateTime(now);
		sellerExpense.setOperatorId(operatorId);
		sellerExpense.setOperatorName(operator.getRealName());
		sellerExpense.setOperatorNo(operator.getUserName());
		sellerExpense.setNotes(String.format("撤销，卖方，结算单号%s", ws.getSerialNo()));
		srList.add(sellerExpense);

		// 买家手续费
		PaymentStream buyerPoundageStream = tradeResponse.getRelation().getStreams().stream().filter(s -> s.getType().equals(FeeType.BUYER_POUNDAGE.getValue().longValue())).findFirst().orElse(null);
		Long buyerBalance = buyerPoundageStream.getBalance() - (tradeResponse.getRelation().getFrozenAmount() + tradeResponse.getRelation().getFrozenBalance());
		SerialRecordDo buyerPoundage = new SerialRecordDo();
		buyerPoundage.setTradeNo(ws.getPayOrderNo());
		buyerPoundage.setTradeType(tradeType);
		buyerPoundage.setSerialNo(ws.getSerialNo());
		buyerPoundage.setCustomerType(weighingBill.getBuyerType());
		buyerPoundage.setAccountId(weighingBill.getBuyerCardAccount());
		buyerPoundage.setAction(ActionType.INCOME.getCode());
		buyerPoundage.setAmount(buyerPoundageStream.getAmount());
		buyerPoundage.setCardNo(weighingBill.getBuyerCardNo());
		buyerPoundage.setCustomerId(weighingBill.getBuyerId());
		buyerPoundage.setCustomerName(weighingBill.getBuyerName());
		buyerPoundage.setCustomerNo(weighingBill.getBuyerCode());
		buyerPoundage.setStartBalance(buyerBalance);
		buyerPoundage.setEndBalance(buyerBalance + buyerPoundageStream.getAmount());
		buyerPoundage.setFirmId(firmId);
		buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		buyerPoundage.setOperateTime(now);
		buyerPoundage.setOperatorId(operatorId);
		buyerPoundage.setOperatorName(operator.getRealName());
		buyerPoundage.setOperatorNo(operator.getUserName());
		buyerPoundage.setNotes(String.format("撤销，买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerPoundage);
		// 买家退款
		PaymentStream buyerRefundStream = tradeResponse.getRelation().getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
		buyerBalance = buyerRefundStream.getBalance() - (tradeResponse.getRelation().getFrozenAmount() + tradeResponse.getRelation().getFrozenBalance());
		SerialRecordDo buyerRefund = new SerialRecordDo();
		buyerRefund.setTradeNo(ws.getPayOrderNo());
		buyerRefund.setTradeType(tradeType);
		buyerRefund.setSerialNo(ws.getSerialNo());
		buyerRefund.setCustomerType(weighingBill.getBuyerType());
		buyerRefund.setAccountId(weighingBill.getBuyerCardAccount());
		buyerRefund.setAction(ActionType.INCOME.getCode());
		buyerRefund.setAmount(buyerRefundStream.getAmount());
		buyerRefund.setCardNo(weighingBill.getBuyerCardNo());
		buyerRefund.setCustomerId(weighingBill.getBuyerId());
		buyerRefund.setCustomerName(weighingBill.getBuyerName());
		buyerRefund.setCustomerNo(weighingBill.getBuyerCode());
		buyerRefund.setStartBalance(buyerBalance);
		buyerRefund.setEndBalance(buyerBalance + buyerRefundStream.getAmount());
		buyerRefund.setFirmId(firmId);
		buyerRefund.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		buyerRefund.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		buyerRefund.setOperateTime(now);
		buyerRefund.setOperatorId(operatorId);
		buyerRefund.setOperatorName(operator.getRealName());
		buyerRefund.setOperatorNo(operator.getUserName());
		buyerRefund.setNotes(String.format("撤销，买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerRefund);

		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void sendCalculateReferencePriceMessage(WeighingBill weighingBill, Long marketId, Long tradeAmount) {
		// 发送mq计算中间价
		Map<String, String> map = new HashMap<>();
		// 设置过磅单号
		map.put("serialNo", weighingBill.getSerialNo());
		// 设置计量方式
		map.put("measureType", weighingBill.getMeasureType());
		// 设置商品id
		map.put("goodsId", String.valueOf(weighingBill.getGoodsId()));
		// 设置市场id
		map.put("marketId", String.valueOf(marketId));
		// 设置件数
		map.put("unitAmount", String.valueOf(weighingBill.getUnitAmount()));
		// 设置单价
		map.put("unitPrice", String.valueOf(weighingBill.getUnitPrice()));
		// 设置件重
		map.put("unitWeight", String.valueOf(weighingBill.getUnitWeight()));
		// 设置净重
		map.put("netWeight", String.valueOf(weighingBill.getNetWeight()));
		// 设置结算日期
		map.put("settlementTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
		// 设置交易金额
		map.put("tradeAmount", String.valueOf(tradeAmount));
		// 交易类型
		map.put("tradeType", weighingBill.getTradeType());
		// mq发送消息
		this.mqService.send(WeighingBillMQConfig.EXCHANGE_REFERENCE_PRICE_CHANGE, WeighingBillMQConfig.ROUTING_REFERENCE_PRICE_CHANGE, JSON.toJSONString(map));
	}

	private UserAccountCardResponseDto setWeighingBillBuyerInfo(WeighingBill weighingBill) {
		// 根据卡号查询账户信息
		// 查询买家账户信息
		CardQueryDto buyerQueryDto = new CardQueryDto();
		buyerQueryDto.setCardNo(weighingBill.getBuyerCardNo());
		BaseOutput<UserAccountCardResponseDto> buyerOutput = this.accountRpc.getSingle(buyerQueryDto);
		if (!buyerOutput.isSuccess()) {
			throw new AppException(buyerOutput.getMessage());
		}
		// 设置买家信息
		weighingBill.setBuyerAccount(buyerOutput.getData().getFundAccountId());
		weighingBill.setBuyerId(buyerOutput.getData().getCustomerId());
		weighingBill.setBuyerCode(buyerOutput.getData().getCustomerCode());
		weighingBill.setBuyerName(buyerOutput.getData().getCustomerName());
		weighingBill.setBuyerContact(buyerOutput.getData().getCustomerContactsPhone());
		weighingBill.setBuyerCardAccount(buyerOutput.getData().getAccountId());
		weighingBill.setBuyerType(buyerOutput.getData().getCustomerMarketType());
		return buyerOutput.getData();

	}

	private UserAccountCardResponseDto setWeighingBillSellerInfo(WeighingBill weighingBill) {
		// 查询卖家账户信息
		CardQueryDto sellerQueryDto = new CardQueryDto();
		sellerQueryDto.setCardNo(weighingBill.getSellerCardNo());
		BaseOutput<UserAccountCardResponseDto> sellerOutput = this.accountRpc.getSingle(sellerQueryDto);
		if (!sellerOutput.isSuccess()) {
			throw new AppException(sellerOutput.getMessage());
		}
		// 设置卖家信息
		weighingBill.setSellerAccount(sellerOutput.getData().getFundAccountId());
		weighingBill.setSellerId(sellerOutput.getData().getCustomerId());
		weighingBill.setSellerCode(sellerOutput.getData().getCustomerCode());
		weighingBill.setSellerName(sellerOutput.getData().getCustomerName());
		weighingBill.setSellerContact(sellerOutput.getData().getCustomerContactsPhone());
		weighingBill.setSellerCardAccount(sellerOutput.getData().getAccountId());
		weighingBill.setSellerType(sellerOutput.getData().getCustomerMarketType());
		return sellerOutput.getData();
	}

	private void setWeighingStatementBuyerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		BaseOutput<List<QueryFeeOutput>> buyerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE);
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException(buyerFeeOutput.getMessage());
		}
		BigDecimal buyerTotalFee = new BigDecimal(0L);
		for (QueryFeeOutput qfo : buyerFeeOutput.getData()) {
			buyerTotalFee = buyerTotalFee.add(qfo.getTotalFee().setScale(2, RoundingMode.HALF_UP));
		}
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException("计算买家手续费失败");
		}
		if (!isFreeze(weighingBill)) {
			ws.setBuyerActualAmount(ws.getTradeAmount() + MoneyUtils.yuanToCent(buyerTotalFee.doubleValue()));
			ws.setBuyerPoundage(MoneyUtils.yuanToCent(buyerTotalFee.doubleValue()));
		}
		ws.setBuyerCardNo(weighingBill.getBuyerCardNo());
		ws.setBuyerId(weighingBill.getBuyerId());
		ws.setBuyerName(weighingBill.getBuyerName());
	}

	private void setWeighingStatementFrozenAmount(WeighingBill weighingBill, WeighingStatement ws) {
		ws.setFrozenAmount(new BigDecimal(weighingBill.getEstimatedNetWeight() * weighingBill.getUnitPrice() * 2).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP).longValue());
	}

	private void setWeighingStatementSellerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		BaseOutput<List<QueryFeeOutput>> sellerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_SELLER_POUNDAGE_BUSINESS_TYPE);
		if (!sellerFeeOutput.isSuccess()) {
			throw new AppException(sellerFeeOutput.getMessage());
		}
		BigDecimal sellerTotalFee = new BigDecimal(0L);
		for (QueryFeeOutput qfo : sellerFeeOutput.getData()) {
			sellerTotalFee = sellerTotalFee.add(qfo.getTotalFee().setScale(2, RoundingMode.HALF_UP));
		}
		if (!sellerFeeOutput.isSuccess()) {
			throw new AppException("计算卖家手续费失败");
		}
		if (!isFreeze(weighingBill)) {
			ws.setSellerActualAmount(ws.getTradeAmount() - MoneyUtils.yuanToCent(sellerTotalFee.doubleValue()));
			ws.setSellerPoundage(MoneyUtils.yuanToCent(sellerTotalFee.doubleValue()));
		}
		ws.setSellerCardNo(weighingBill.getSellerCardNo());
		ws.setSellerId(weighingBill.getSellerId());
		ws.setSellerName(weighingBill.getSellerName());
	}

	private void setWeighingStatementTradeAmount(WeighingBill weighingBill, WeighingStatement ws) {
		if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			ws.setTradeAmount(new BigDecimal(weighingBill.getNetWeight() * weighingBill.getUnitPrice() * 2).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP).longValue());
		} else {
			ws.setTradeAmount(new BigDecimal(weighingBill.getUnitAmount() * weighingBill.getUnitPrice()).longValue());
		}
	}

	private void updateWeihingBillInfo(WeighingBill weighingBill, WeighingBill dto) {
		weighingBill.setEstimatedNetWeight(dto.getEstimatedNetWeight());
		weighingBill.setFetchedWeight(dto.getFetchedWeight());
		weighingBill.setFetchWeightTime(dto.getFetchWeightTime());
		weighingBill.setGoodsCode(dto.getGoodsCode());
		weighingBill.setGoodsId(dto.getGoodsId());
		weighingBill.setGoodsKeyCode(dto.getGoodsKeyCode());
		weighingBill.setGoodsName(dto.getGoodsName());
		weighingBill.setGoodsOriginCityId(dto.getGoodsOriginCityId());
		weighingBill.setGoodsOriginCityName(dto.getGoodsOriginCityName());
		weighingBill.setMeasureType(dto.getMeasureType());
		weighingBill.setModifiedTime(LocalDateTime.now());
		weighingBill.setModifierId(dto.getModifierId());
		weighingBill.setPlateNumber(dto.getPlateNumber());
		weighingBill.setRoughWeight(dto.getRoughWeight());
		weighingBill.setSubtractionRate(dto.getSubtractionRate());
		weighingBill.setSubtractionWeight(dto.getSubtractionWeight());
		weighingBill.setTareBillNumber(dto.getTareBillNumber());
		weighingBill.setTareWeight(dto.getTareWeight());
		weighingBill.setTradeType(dto.getTradeType());
		weighingBill.setNetWeight(dto.getNetWeight());
		weighingBill.setUnitAmount(dto.getUnitAmount());
		weighingBill.setUnitPrice(dto.getUnitPrice());
		weighingBill.setUnitWeight(dto.getUnitWeight());
	}

}