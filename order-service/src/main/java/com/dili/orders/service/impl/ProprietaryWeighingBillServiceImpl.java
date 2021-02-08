package com.dili.orders.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.customer.sdk.domain.CharacterType;
import com.dili.customer.sdk.domain.dto.CustomerExtendDto;
import com.dili.customer.sdk.enums.CustomerEnum;
import com.dili.jmsf.microservice.sdk.dto.TruckDTO;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.PaymentState;
import com.dili.orders.domain.PaymentType;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.domain.PriceState;
import com.dili.orders.domain.TradingBillType;
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
import com.dili.orders.dto.CreateTradeResponseDto;
import com.dili.orders.dto.FeeDto;
import com.dili.orders.dto.FeeUse;
import com.dili.orders.dto.FundItem;
import com.dili.orders.dto.PaymentErrorCode;
import com.dili.orders.dto.PaymentStream;
import com.dili.orders.dto.PaymentTradeCommitDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;
import com.dili.orders.dto.PaymentTradePrepareDto;
import com.dili.orders.dto.PaymentTradeType;
import com.dili.orders.dto.SerialRecordDo;
import com.dili.orders.dto.TradeType;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.service.ProprietaryWeighingBillService;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.exception.AppException;
import com.dili.ss.util.MoneyUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;

import io.seata.spring.annotation.GlobalTransactional;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Service
public class ProprietaryWeighingBillServiceImpl extends WeighingBillServiceImpl implements ProprietaryWeighingBillService {

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> addWeighingBill(WeighingBill bill) {
		if (bill.getTradingBillType() == null) {
			return BaseOutput.failure("单据类型不能为空");
		}
		if (!bill.getTradingBillType().equals(TradingBillType.PROPRIETARY.getValue())) {
			return BaseOutput.failure("单据类型不正确");
		}
		LOGGER.info("新增交易过磅接收参数：{}", JSON.toJSONString(bill));
		BaseOutput<String> output = this.uidRpc.bizNumber(OrdersConstant.WEIGHING_BILL_SERIAL_NO_GENERATE_RULE_CODE);
		if (output == null) {
			return BaseOutput.failure("调用过磅单号生成服务无响应");
		}
		if (!output.isSuccess()) {
			return BaseOutput.failure(output.getMessage());
		}
		bill.setSerialNo(output.getData());
		bill.setState(WeighingBillState.NO_SETTLEMENT.getValue());

		// 设置交易类型id
		bill.setTradeTypeId(this.getTradeIdByCode(bill.getTradeType()));

		User operator = this.getUserById(bill.getCreatorId());
		bill.setDepartmentId(operator.getDepartmentId());
		BaseOutput<Department> deptOutput = this.departmentRpc.get(operator.getDepartmentId());
		if (deptOutput == null) {
			LOGGER.error("查询操作员所在部门信息失败，接口无响应");
			return BaseOutput.failure("查询部门信息失败");
		}
		if (!deptOutput.isSuccess()) {
			LOGGER.error("查询操作员所在部门信息失败，message:[{}]", deptOutput.getMessage());
			return BaseOutput.failure("查询部门信息失败");
		}
		if (deptOutput.getData() == null) {
			return BaseOutput.failure("未查询到操作员的部门信息");
		}
		bill.setDepartmentName(deptOutput.getData().getName());

		// 根据卡号查询账户信息
		// 查询买家账户信息
		this.setWeighingBillBuyerInfo(bill);
		// 查询卖家账户信息
		this.setWeighingBillSellerInfo(bill);

		int rows = this.getActualDao().insertSelective(bill);
		if (rows <= 0) {
			throw new AppException("保存过磅单失败");
		}

		LocalDateTime now = LocalDateTime.now();
		Long firmId = this.getMarketIdByOperatorId(bill.getCreatorId());
		WeighingStatement statement = this.buildWeighingStatement(bill, firmId);
		statement.setCreatorId(bill.getCreatorId());
		statement.setLastOperationTime(now);
		statement.setLastOperatorId(bill.getCreatorId());
		statement.setLastOperatorName(operator.getRealName());
		statement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.insertSelective(statement);
		if (rows <= 0) {
			throw new AppException("保存结算单失败");
		}

		WeighingBillOperationRecord wbor = this.buildOperationRecord(bill, statement, operator, WeighingOperationType.WEIGH, now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		BaseOutput<WeighingStatement> resultOutput = rows > 0 ? BaseOutput.success().setData(statement) : BaseOutput.failure("保存过磅单失败");
		if (resultOutput.isSuccess()) {
			// 记录日志系统
			LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, bill.getSerialNo());
			LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, bill.getId());
			LoggerContext.put("statementId", statement.getId());
			LoggerContext.put("statementSerialNo", statement.getSerialNo());
			LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, bill.getCreatorId());
			LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, operator.getRealName());
			LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, bill.getMarketId());
		}
		return resultOutput;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> updateWeighingBill(WeighingBill bill) {
		if (bill.getTradingBillType() == null) {
			return BaseOutput.failure("单据类型不能为空");
		}
		if (!bill.getTradingBillType().equals(TradingBillType.PROPRIETARY.getValue())) {
			return BaseOutput.failure("单据类型不正确");
		}
		return super.updateWeighingBill(bill);
	}

//	@Override
//	protected void setWeighingStatementTradeAmount(WeighingBill weighingBill, WeighingStatement ws) {
////		BigDecimal tradeAmount = new BigDecimal(0);
////		if (weighingBill.getNetWeight() != null && weighingBill.getUnitPrice() != null) {
////			// 单价*2*净重
////			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getNetWeight() * weighingBill.getUnitPrice() * 2).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP));
////		}
////		if (weighingBill.getCollectionCharges() != null && weighingBill.getNetWeight() != null) {
////			// 代收费/100*2*净重
////			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getCollectionCharges() * 2 * weighingBill.getNetWeight()).divide(new BigDecimal(10000), 0, RoundingMode.HALF_UP));
////		}
////		if (weighingBill.getStaffCharges() != null && weighingBill.getUnitAmount() != null) {
////			// 人工费*件数
////			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getStaffCharges() * weighingBill.getUnitAmount()));
////		}
////		if (weighingBill.getCollectionCharges() != null && weighingBill.getUnitAmount() != null) {
////			// 包装费*件数
////			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getCollectionCharges() * weighingBill.getUnitAmount()));
////		}
////		ws.setTradeAmount(tradeAmount.longValue());
//		ws.setTradeAmount(new BigDecimal(weighingBill.getNetWeight() * weighingBill.getUnitPrice() * 2).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP).longValue());
//	}

	@Override
	protected void setBuyerCustomerMarketType(WeighingBill weighingBill, UserAccountCardResponseDto buyerAccountInfo) {
		// 买方代理人先取买方角色
		BaseOutput<CustomerExtendDto> output = this.customerRpc.get(buyerAccountInfo.getCustomerId(), weighingBill.getMarketId());
		if (output == null) {
			throw new AppException("查询买家信息服务无响应");
		}
		if (!output.isSuccess()) {
			LOGGER.error("查询买家信息失败:message：[{}]", output.getMessage());
			throw new AppException("查询买方信息失败");
		}

		CustomerExtendDto buyerInfo = output.getData();
		CharacterType buyerType = buyerInfo.getCharacterTypeList().stream().filter(c -> c.getCharacterType().equals(CustomerEnum.CharacterType.买家.getCode())).findFirst().orElse(null);
		if (buyerType == null) {
			throw new AppException("该卡客户身份类型不符");
		}
		weighingBill.setBuyerType(buyerType.getSubType());
		weighingBill.setBuyerRegionTag(buyerInfo.getCustomerMarket().getBusinessRegionTag());
	}

	@Override
	protected void updateWeihingBillInfo(WeighingBill weighingBill, WeighingBill dto) {
		super.updateWeihingBillInfo(weighingBill, dto);
		weighingBill.setStaffCharges(dto.getStaffCharges());
		weighingBill.setCollectionCharges(dto.getCollectionCharges());
		weighingBill.setPackingCharges(dto.getPackingCharges());
		weighingBill.setPackingType(dto.getPackingType());
	}

	@Override
	protected WeighingStatement buildWeighingStatement(WeighingBill weighingBill, Long marketId) {
		BaseOutput<String> output = this.uidRpc.bizNumber(OrdersConstant.WEIGHING_STATEMENT_SERIAL_NO_GENERATE_RULE_CODE);
		if (output == null) {
			throw new AppException("调用过磅单号生成服务无响应");
		}
		if (!output.isSuccess()) {
			throw new AppException(output.getMessage());
		}
		WeighingStatement ws = new WeighingStatement();
		ws.setPaymentType(weighingBill.getPaymentType());
		ws.setSerialNo(output.getData());
		ws.setWeighingBillId(weighingBill.getId());
		ws.setWeighingSerialNo(weighingBill.getSerialNo());
		ws.setStaffCharges(weighingBill.getStaffCharges());
		ws.setPackingCharges(weighingBill.getPackingCharges());
		ws.setCollectionCharges(weighingBill.getCollectionCharges());

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

	@Override
	public BaseOutput<WeighingStatement> freeze(Long id, String buyerPassword, Long operatorId) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
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

		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			// 判断余额是否足够
			AccountRequestDto balanceQuery = new AccountRequestDto();
			balanceQuery.setAccountId(weighingBill.getBuyerAccount());
			BaseOutput<AccountBalanceDto> balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
			if (balanceOutput == null) {
				return BaseOutput.failure("调用支付系统查询买方账户余额无响应");
			}
			if (!balanceOutput.isSuccess()) {
				LOGGER.error(String.format("调用支付系统查询买方账户余额失败:code=%s,message=%s", balanceOutput.getCode(), balanceOutput.getMessage()));
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
			prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + weighingStatement.getSerialNo());
			prepareDto.setType(PaymentTradeType.PREAUTHORIZED.getValue());
			prepareDto.setDescription("交易过磅");
			BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
			if (paymentOutput == null) {
				return BaseOutput.failure("调用支付系统创建冻结支付订单无响应");
			}
			if (!paymentOutput.isSuccess()) {
				LOGGER.error(String.format("调用支付系统创建冻结支付订单失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
				return BaseOutput.failure(paymentOutput.getMessage());
			}
			weighingStatement.setPayOrderNo(paymentOutput.getData().getTradeId());
		}

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

		BaseOutput<PaymentTradeCommitResponseDto> freezeOutput = null;
		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
			dto.setAccountId(weighingBill.getBuyerAccount());
			dto.setBusinessId(weighingBill.getBuyerCardAccount());
			dto.setAmount(weighingStatement.getFrozenAmount());
			dto.setTradeId(weighingStatement.getPayOrderNo());
			dto.setPassword(buyerPassword);
			freezeOutput = this.payRpc.commitTrade(dto);

			if (freezeOutput == null) {
				throw new AppException("交易冻结调用支付系统无响应");
			}
			if (!freezeOutput.isSuccess()) {
				LOGGER.error(String.format("调用支付系统提交支付冻结订单失败:code=%s,message=%s", freezeOutput.getCode(), freezeOutput.getMessage()));
				throw new AppException(freezeOutput.getMessage());
			}
		}

		User operator = this.getUserById(operatorId);
		weighingStatement.setLastOperationTime(freezeOutput != null ? freezeOutput.getData().getWhen() : now);
		weighingStatement.setLastOperatorId(operatorId);
		weighingStatement.setLastOperatorName(operator.getRealName());
		weighingStatement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		// 记录操作流水
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.FREEZE,
				freezeOutput != null ? freezeOutput.getData().getWhen() : now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
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
			srDto.setHoldName(weighingBill.getBuyerCardHolderName());
			srDto.setCustomerId(weighingBill.getBuyerId());
			srDto.setCustomerName(weighingBill.getBuyerName());
			srDto.setCustomerNo(weighingBill.getBuyerCode());
			srDto.setStartBalance(data.getBalance() - data.getFrozenBalance());
			srDto.setEndBalance(data.getBalance() - (data.getFrozenBalance() + data.getFrozenAmount()));
			srDto.setFirmId(this.getMarketIdByOperatorId(operatorId));
			srDto.setFundItem(FundItem.TRADE_FREEZE.getCode());
			srDto.setFundItemName(FundItem.TRADE_FREEZE.getName());
			srDto.setOperateTime(data.getWhen());
			srDto.setOperatorId(operatorId);
			srDto.setOperatorName(operator.getRealName());
			srDto.setOperatorNo(operator.getUserName());
			srDto.setNotes(String.format("冻结，过磅单号%s", weighingBill.getSerialNo()));
			this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(Arrays.asList(srDto)));
		}

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, weighingBill.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, weighingBill.getId());
		LoggerContext.put("statementId", weighingStatement.getId());
		LoggerContext.put("statementSerialNo", weighingStatement.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, operatorId);
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, operator.getRealName());
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, weighingBill.getMarketId());
		LoggerContext.put(LoggerConstant.LOG_OPERATION_TYPE_KEY, "freeze");

		return BaseOutput.successData(weighingStatement);
	}

	@Override
	protected void setWeighingStatementBuyerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		BaseOutput<List<QueryFeeOutput>> buyerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE);
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException(buyerFeeOutput.getMessage());
		}
		BigDecimal buyerTotalFee = new BigDecimal(0L);
		if (CollectionUtils.isEmpty(buyerFeeOutput.getData())) {
			throw new AppException("未匹配到计费规则，请联系管理员");
		}
		for (QueryFeeOutput qfo : buyerFeeOutput.getData()) {
			if (qfo.getTotalFee() != null) {
				buyerTotalFee = buyerTotalFee.add(qfo.getTotalFee().setScale(2, RoundingMode.HALF_UP));
			}
		}
		if (!isFreeze(weighingBill)) {
			ws.setBuyerActualAmount(ws.getTradeAmount() + MoneyUtils.yuanToCent(buyerTotalFee.doubleValue()));
			ws.setBuyerPoundage(MoneyUtils.yuanToCent(buyerTotalFee.doubleValue()));
		}
		ws.setBuyerCardNo(weighingBill.getBuyerCardNo());
		ws.setBuyerId(weighingBill.getBuyerId());
		ws.setBuyerName(weighingBill.getBuyerName());
	}

	@Override
	protected void setWeighingStatementSellerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		BaseOutput<List<QueryFeeOutput>> sellerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_SELLER_POUNDAGE_BUSINESS_TYPE);
		if (!sellerFeeOutput.isSuccess()) {
			throw new AppException(sellerFeeOutput.getMessage());
		}
		BigDecimal sellerTotalFee = new BigDecimal(0L);
		if (CollectionUtils.isEmpty(sellerFeeOutput.getData())) {
			throw new AppException("未匹配到计费规则，请联系管理员");
		}
		for (QueryFeeOutput qfo : sellerFeeOutput.getData()) {
			if (qfo.getTotalFee() != null) {
				sellerTotalFee = sellerTotalFee.add(qfo.getTotalFee().setScale(2, RoundingMode.HALF_UP));
			}
		}
		if (!isFreeze(weighingBill)) {
			ws.setSellerActualAmount(ws.getTradeAmount() - MoneyUtils.yuanToCent(sellerTotalFee.doubleValue()) + this.calculateServiceFee(weighingBill));
			ws.setSellerPoundage(MoneyUtils.yuanToCent(sellerTotalFee.doubleValue()));
		}
		ws.setSellerCardNo(weighingBill.getSellerCardNo());
		ws.setSellerId(weighingBill.getSellerId());
		ws.setSellerName(weighingBill.getSellerName());
	}

	@GlobalTransactional(timeoutMills = Integer.MAX_VALUE)
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> settle(Long id, String buyerPassword, Long operatorId, Long marketId) {
		WeighingBill weighingBill = this.getWeighingBillById(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前结算单状态不能进行支付操作");
		}

		if (this.isFreeze(weighingBill)) {
			return this.freeze(id, buyerPassword, operatorId);
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
			LOGGER.error(String.format("交易过磅结算调用支付系统查询买方余额失败:code=%s,message=%s", balanceOutput.getCode(), balanceOutput.getMessage()));
			return BaseOutput.failure(balanceOutput.getMessage());
		}
		Long buyerBanlance = balanceOutput.getData().getAvailableAmount();
		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			if (weighingStatement.getState().equals(WeighingStatementState.FROZEN.getValue()) && weighingStatement.getFrozenAmount() != null) {
				buyerBanlance += weighingStatement.getFrozenAmount();
			}
			if (buyerBanlance < weighingStatement.getBuyerActualAmount()) {
				return BaseOutput.failure(String.format("买方卡账户余额不足，还需充值%s元", MoneyUtils.centToYuan(weighingStatement.getBuyerActualAmount() - balanceOutput.getData().getAvailableAmount())));
			}
		} else {
			if (buyerBanlance < weighingStatement.getBuyerPoundage()) {
				return BaseOutput.failure(String.format("买方卡账户余额不足，还需充值%s元", MoneyUtils.centToYuan(weighingStatement.getBuyerPoundage() + weighingStatement.getSellerPoundage() - buyerBanlance)));
			}
			// 判断卖方余额是否足够-手续费
			balanceQuery = new AccountRequestDto();
			balanceQuery.setAccountId(weighingBill.getSellerAccount());
			balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
			if (!balanceOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅结算调用支付系统查询卖方余额失败:code=%s,message=%s", balanceOutput.getCode(), balanceOutput.getMessage()));
				return BaseOutput.failure(balanceOutput.getMessage());
			}
			Long sellerBanlance = balanceOutput.getData().getAvailableAmount();
			if (sellerBanlance < weighingStatement.getSellerPoundage()) {
				return BaseOutput.failure(String.format("卖方卡账户余额不足，还需充值%s元", MoneyUtils.centToYuan(weighingStatement.getSellerPoundage() - sellerBanlance)));
			}
		}

		if (this.checkPrice) {
			// 检查中间价
			if (weighingBill.getPriceState() == null && weighingBill.getCheckPrice() != null && weighingBill.getCheckPrice()) {
				// 获取商品中间价
				Long referencePrice = this.referencePriceService.getReferencePriceByGoodsId(weighingBill.getGoodsId(), this.getMarketIdByOperatorId(operatorId), weighingBill.getTradeType().toString(),
						weighingBill.getTradingBillType());
				if (referencePrice != null && referencePrice > 0) {
					// 比较价格
					Long actualPrice = this.getConvertUnitPrice(weighingBill);
					if (actualPrice < referencePrice) {
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
						approve.setWeighingBillSerialNo(weighingBill.getSerialNo());
						approve.setStatementId(weighingStatement.getId());
						approve.setStatementSerialNo(weighingStatement.getSerialNo());
						approve.setWeighingTime(this.getWeighingBillWeighingTime(weighingBill));
						approve.setMarketId(weighingBill.getMarketId());
						// 乐观锁版本默认0
						approve.setVersion(0);
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
						rows = this.priceApproveMapper.updateByPrimaryKeySelective(approve);
						if (rows <= 0) {
							throw new AppException("更新价格确认审批流程信息失败");
						}
						this.notifyApprovers(approve);
						return BaseOutput.failure("交易单价低于参考价，需人工审核");
					}
				}
			}
		}

		if (weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			BaseOutput<?> output = this.prepareTrade(weighingBill, weighingStatement);
			if (!output.isSuccess()) {
				LOGGER.error(String.format("交易过磅结算调用支付系统创建支付订单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
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
			if (byId == null) {
				throw new AppException("交易结算调用进门系统查询信息无响应");
			}
			if (!byId.isSuccess()) {
				throw new AppException("查询过磅单失败");
			}
			// 成功之后，判断是否是d牌，如果是d牌的话，不删除过磅单
			if (!Objects.equals(byId.getData().getCarType(), 1L)) {
				// 在判断车牌号是否相等，想的就删除过磅单
				if (Objects.equals(weighingBill.getPlateNumber(), byId.getData().getPlate())) {
					// 删除皮重单
					BaseOutput<Object> jmsfOutput = this.jsmfRpc.removeTareNumber(Long.valueOf(weighingBill.getTareBillNumber()));
					if (jmsfOutput == null) {
						throw new AppException("交易结算调用进门系统无响应");
					}
					if (!jmsfOutput.isSuccess()) {
						LOGGER.error(String.format("交易过磅结算调用进门收费系统删除皮重失败:code=%s,message=%s", jmsfOutput.getCode(), jmsfOutput.getMessage()));
						throw new AppException("删除过磅单失败");
					}
				}
			}
		}
		// json 修改结束

		Integer originalState = weighingBill.getState();
		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.SETTLED.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);

		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		weighingStatement.setState(WeighingStatementState.PAID.getValue());

		// 设置代理人信息
		WeighingBillAgentInfo agentInfo = null;

		UserAccountCardResponseDto buyerInfo = this.getBuyerInfo(weighingBill);
		UserAccountCardResponseDto sellerInfo = this.getSellerInfo(weighingBill);
		CustomerExtendDto buyerAgent = this.getCustomerAgent(buyerInfo, weighingBill);
		CustomerExtendDto sellerAgent = this.getCustomerAgent(sellerInfo, weighingBill);
		if (buyerAgent != null || sellerAgent != null) {
			agentInfo = new WeighingBillAgentInfo();
			agentInfo.setWeighingBillId(weighingBill.getId());
			agentInfo.setWeighingBillSerialNo(weighingBill.getSerialNo());
			agentInfo.setWeighingStatementId(weighingStatement.getId());
			agentInfo.setWeighingStatementSerialNo(weighingStatement.getSerialNo());
		}
		if (buyerAgent != null) {
			this.setBuyerAgentInfo(agentInfo, buyerAgent);
		}
		if (sellerAgent != null) {
			this.setSellerAgentInfo(agentInfo, sellerAgent);
		}
		if (agentInfo != null) {
			int rows = this.agentInfoMapper.insertSelective(agentInfo);
			if (rows <= 0) {
				throw new AppException("保存代理人信息失败");
			}
		}

		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> buyerPaymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> sellerPaymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> serviceFeePaymentOutput = null;
		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			weighingBill.setPaymentState(PaymentState.RECEIVED.getValue());
			weighingStatement.setPaymentState(PaymentState.RECEIVED.getValue());
			// 判断单据状态，如果是冻结单走确认预授权扣款，否则走即时交易
			if (originalState.equals(WeighingBillState.FROZEN.getValue())) {
				paymentOutput = this.confirmTrade(weighingBill, weighingStatement, buyerPassword);
				if (paymentOutput == null) {
					throw new AppException("调用支付系统无响应");
				}
				if (!paymentOutput.isSuccess()) {
					LOGGER.error(String.format("交易过磅结算调用支付系统确认冻结交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
					throw new AppException(paymentOutput.getMessage());
				}
			} else {
				paymentOutput = this.commitTrade(weighingBill, weighingStatement, buyerPassword);
				if (paymentOutput == null) {
					throw new AppException("调用支付系统无响应");
				}
				if (!paymentOutput.isSuccess()) {
					LOGGER.error(String.format("交易过磅结算调用支付系统确认交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
					throw new AppException(paymentOutput.getMessage());
				}
				serviceFeePaymentOutput = this.commitServiceFeeTrade(weighingBill, buyerPassword, weighingStatement);
				if (!serviceFeePaymentOutput.isSuccess()) {
					throw new AppException(serviceFeePaymentOutput.getMessage());
				}
			}
		} else {
			weighingBill.setPaymentState(PaymentState.UNRECEIVED.getValue());
			weighingStatement.setPaymentState(PaymentState.UNRECEIVED.getValue());
			// 交手续费
			buyerPaymentOutput = this.commitBuyerCreditTrade(weighingBill, weighingStatement);
			if (!buyerPaymentOutput.isSuccess()) {
				throw new AppException(buyerPaymentOutput.getMessage());
			}
			sellerPaymentOutput = this.commitSellerCreditTrade(weighingBill, weighingStatement);
			if (!sellerPaymentOutput.isSuccess()) {
				throw new AppException(sellerPaymentOutput.getMessage());
			}
		}

		User operator = this.getUserById(operatorId);
		weighingStatement.setLastOperationTime(paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		weighingStatement.setLastOperatorId(operatorId);
		weighingStatement.setLastOperatorName(operator.getRealName());
		weighingStatement.setLastOperatorUserName(operator.getUserName());
		int rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			throw new AppException("更新结算单状态失败");
		}

		weighingBill.setSettlementTime(paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			throw new AppException("更新过磅单状态失败");

		}

		// 记录操作流水
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.SETTLE,
				paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows < 0) {
			throw new AppException("保存操作记录失败");
		}

		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			// 记录资金账户交易流水
			this.recordSettlementAccountFlow(weighingBill, weighingStatement, paymentOutput.getData(), operatorId);
			this.recordServiceFeeAccountFlow(weighingBill, weighingStatement, serviceFeePaymentOutput.getData(), operatorId);
		} else {
			// 记录资金账户交易流水
			if (weighingStatement.getBuyerPoundage() > 0) {
				this.recordBuyerCreditAccountFlow(weighingBill, weighingStatement, buyerPaymentOutput.getData(), operatorId);
			}
			if (weighingStatement.getSellerPoundage() > 0) {
				this.recordSellerCreditAccountFlow(weighingBill, weighingStatement, sellerPaymentOutput.getData(), operatorId);
			}
		}

		// 发送mq通知中间价计算模块计算中间价
		this.sendCalculateReferencePriceMessage(weighingBill, marketId, weighingStatement.getTradeAmount());

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, weighingBill.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, weighingBill.getId());
		LoggerContext.put("statementId", weighingStatement.getId());
		LoggerContext.put("statementSerialNo", weighingStatement.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, operatorId);
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, operator.getRealName());
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, weighingBill.getMarketId());
		LoggerContext.put(LoggerConstant.LOG_OPERATION_TYPE_KEY, "settle");

		return BaseOutput.successData(weighingStatement);
	}

	private BaseOutput<PaymentTradeCommitResponseDto> commitServiceFeeTrade(WeighingBill weighingBill, String password, WeighingStatement weighingStatement) {
		// 提交包装费转账订单
		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getBuyerAccount());
		dto.setPassword(password);
		dto.setTradeId(weighingStatement.getServiceFeePayOrderNo());
		dto.setBusinessId(weighingBill.getBuyerCardAccount());
//		List<FeeDto> fees = new ArrayList<FeeDto>(1);
//		// 买家手续费
//		FeeDto buyerFee = new FeeDto();
//		buyerFee.setAmount(weighingStatement.getBuyerPoundage());
//		buyerFee.setType(FundItem.TRANSFER.getCode());
//		buyerFee.setTypeName(FundItem.TRANSFER.getName());
//		buyerFee.setUseFor(FeeUse.BUYER.getValue());
//		fees.add(buyerFee);
//		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.commit6(dto);
		return commitOutput;
	}

	private void recordServiceFeeAccountFlow(WeighingBill weighingBill, WeighingStatement ws, PaymentTradeCommitResponseDto paymentResult, Long operatorId) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		User operator = this.getUserById(operatorId);
		Long firmId = this.getMarketIdByOperatorId(operatorId);

		Integer tradeType = PaymentTradeType.TRANSFER.getValue();
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
		buyerExpense.setHoldName(weighingBill.getBuyerCardHolderName());
		buyerExpense.setCustomerId(weighingBill.getBuyerId());
		buyerExpense.setCustomerName(weighingBill.getBuyerName());
		buyerExpense.setCustomerNo(weighingBill.getBuyerCode());
		buyerExpense.setStartBalance(buyerBalance);
		buyerExpense.setEndBalance(buyerBalance + buyerStream.getAmount());
		buyerExpense.setFirmId(firmId);
		buyerExpense.setFundItem(FundItem.TRANSFER.getCode());
		buyerExpense.setFundItemName(FundItem.TRANSFER.getName());
		buyerExpense.setOperateTime(paymentResult.getWhen());
		buyerExpense.setOperatorId(operatorId);
		buyerExpense.setOperatorName(operator.getRealName());
		buyerExpense.setOperatorNo(operator.getUserName());
		buyerExpense.setNotes(String.format("买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerExpense);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	@Override
	protected BaseOutput<?> prepareTrade(WeighingBill weighingBill, WeighingStatement ws) {
		BaseOutput<CreateTradeResponseDto> paymentOutput = null;
		ws.setState(WeighingStatementState.PAID.getValue());
		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			// 创建支付订单
			PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
			prepareDto.setAccountId(weighingBill.getSellerAccount());
			prepareDto.setAmount(ws.getTradeAmount());
			prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
			prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + ws.getSerialNo());
			prepareDto.setType(PaymentTradeType.TRADE.getValue());
			prepareDto.setDescription("老农交易过磅");
			paymentOutput = this.payRpc.prepareTrade(prepareDto);
			if (!paymentOutput.isSuccess()) {
				return paymentOutput;
			}
			ws.setPayOrderNo(paymentOutput.getData().getTradeId());
			ws.setState(WeighingStatementState.PAID.getValue());
			if (!paymentOutput.isSuccess()) {
				return paymentOutput;
			}
			// 创建包装费转账交易
			prepareDto = new PaymentTradePrepareDto();
			prepareDto.setAccountId(weighingBill.getSellerAccount());
			prepareDto.setAmount(ws.getTradeAmount());
			prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
			prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + ws.getSerialNo());
			prepareDto.setType(PaymentTradeType.TRANSFER.getValue());
			StringBuilder sb = new StringBuilder("老农交易服务费：");
			Map<String, Long> feeMap = this.buildServiceFeeMap(weighingBill);
			feeMap.entrySet().forEach(e -> sb.append(e.getKey()).append("=").append(e.getValue()));
			prepareDto.setDescription(sb.toString());
			paymentOutput = this.payRpc.prepareTrade(prepareDto);
			if (!paymentOutput.isSuccess()) {
				return paymentOutput;
			}
			ws.setServiceFeePayOrderNo(paymentOutput.getData().getTradeId());
			return paymentOutput;
		} else {
			// 赊销调两次支付，买方卖方分开调用
			PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
			if (ws.getBuyerPoundage() > 0) {
				prepareDto.setAccountId(weighingBill.getBuyerAccount());
				prepareDto.setAmount(ws.getBuyerPoundage());
				prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + ws.getSerialNo());
				prepareDto.setType(TradeType.FEE.getCode());
				prepareDto.setDescription("自营交易赊销买方手续费");
				paymentOutput = this.payRpc.prepareTrade(prepareDto);
				if (!paymentOutput.isSuccess()) {
					return paymentOutput;
				}
				ws.setBuyerPoundagePayOrderNo(paymentOutput.getData().getTradeId());
			}

			if (ws.getSellerPoundage() > 0) {
				prepareDto = new PaymentTradePrepareDto();
				prepareDto.setAccountId(weighingBill.getSellerAccount());
				prepareDto.setAmount(ws.getSellerPoundage());
				prepareDto.setSerialNo(OrdersConstant.WEIGHING_MODULE_PREFIX + ws.getSerialNo());
				prepareDto.setType(TradeType.FEE.getCode());
				prepareDto.setDescription("自营交易赊销卖方手续费");
				paymentOutput = this.payRpc.prepareTrade(prepareDto);
				if (!paymentOutput.isSuccess()) {
					return paymentOutput;
				}
				ws.setSellerPoundagePayOrderNo(paymentOutput.getData().getTradeId());
			}
		}
		return paymentOutput;
	}

	@Override
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
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.commitTrade(dto);
		return commitOutput;
	}

	// 计算代收费、服务费、人工费、包装费
	private Long calculateServiceFee(WeighingBill weighingBill) {
		BigDecimal tradeAmount = new BigDecimal(0);
		if (weighingBill.getNetWeight() != null && weighingBill.getUnitPrice() != null) {
			// 单价*2*净重
			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getNetWeight() * weighingBill.getUnitPrice() * 2).divide(new BigDecimal(100), 0, RoundingMode.HALF_UP));
		}
		if (weighingBill.getCollectionCharges() != null && weighingBill.getNetWeight() != null) {
			// 代收费/100*2*净重
			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getCollectionCharges() * 2 * weighingBill.getNetWeight()).divide(new BigDecimal(10000), 0, RoundingMode.HALF_UP));
		}
		if (weighingBill.getStaffCharges() != null && weighingBill.getUnitAmount() != null) {
			// 人工费*件数
			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getStaffCharges() * weighingBill.getUnitAmount()));
		}
		if (weighingBill.getCollectionCharges() != null && weighingBill.getUnitAmount() != null) {
			// 包装费*件数
			tradeAmount = tradeAmount.add(new BigDecimal(weighingBill.getCollectionCharges() * weighingBill.getUnitAmount()));
		}
		return tradeAmount.longValue();
	}

	// 交易流水明细用
	private Map<String, Long> buildServiceFeeMap(WeighingBill weighingBill) {
		Map<String, Long> map = new HashMap<String, Long>();
		if (weighingBill.getCollectionCharges() != null && weighingBill.getNetWeight() != null) {
			// 代收费/100*2*净重
			map.put("代收费", new BigDecimal(weighingBill.getCollectionCharges() * 2 * weighingBill.getNetWeight()).divide(new BigDecimal(10000), 0, RoundingMode.HALF_UP).longValue());
		}
		if (weighingBill.getStaffCharges() != null && weighingBill.getUnitAmount() != null) {
			// 人工费*件数
			map.put("人工费", new BigDecimal(weighingBill.getStaffCharges() * weighingBill.getUnitAmount()).longValue());
		}
		if (weighingBill.getCollectionCharges() != null && weighingBill.getUnitAmount() != null) {
			// 包装费*件数
			map.put("包装费", new BigDecimal(weighingBill.getCollectionCharges() * weighingBill.getUnitAmount()).longValue());
		}
		return map;
	}

	private void recordSellerCreditAccountFlow(WeighingBill weighingBill, WeighingStatement ws, PaymentTradeCommitResponseDto paymentResult, Long operatorId) {
		User operator = this.getUserById(operatorId);
		// 卖家手续费
		PaymentStream sellerPoundageStream = paymentResult.getStreams().get(0);
		Long sellerBalance = paymentResult.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance());
		SerialRecordDo sellerPoundage = new SerialRecordDo();
		sellerPoundage.setTradeNo(ws.getPayOrderNo());
		sellerPoundage.setTradeType(PaymentTradeType.PAY.getValue());
		sellerPoundage.setSerialNo(ws.getSerialNo());
		sellerPoundage.setCustomerType(weighingBill.getSellerType());
		sellerPoundage.setAccountId(weighingBill.getSellerCardAccount());
		sellerPoundage.setAction(ActionType.EXPENSE.getCode());
		sellerPoundage.setAmount(sellerPoundageStream != null ? Math.abs(sellerPoundageStream.getAmount()) : 0);
		sellerPoundage.setCardNo(weighingBill.getSellerCardNo());
		sellerPoundage.setHoldName(weighingBill.getSellerCardHolderName());
		sellerPoundage.setCustomerId(weighingBill.getSellerId());
		sellerPoundage.setCustomerName(weighingBill.getSellerName());
		sellerPoundage.setCustomerNo(weighingBill.getSellerCode());
		sellerPoundage.setStartBalance(sellerBalance);
		sellerPoundage.setEndBalance(sellerBalance + (sellerPoundageStream != null ? sellerPoundageStream.getAmount() : 0));
		sellerPoundage.setFirmId(weighingBill.getMarketId());
		sellerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		sellerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		sellerPoundage.setOperateTime(paymentResult.getWhen());
		sellerPoundage.setOperatorId(operatorId);
		sellerPoundage.setOperatorName(operator.getRealName());
		sellerPoundage.setOperatorNo(operator.getUserName());
		sellerPoundage.setNotes(String.format("卖方，结算单号%s", ws.getSerialNo()));
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		srList.add(sellerPoundage);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordBuyerCreditAccountFlow(WeighingBill weighingBill, WeighingStatement ws, PaymentTradeCommitResponseDto paymentResult, Long operatorId) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		User operator = this.getUserById(operatorId);
		Long firmId = this.getMarketIdByOperatorId(operatorId);

		Integer tradeType = PaymentTradeType.PAY.getValue();
		// 买家手续费
		PaymentStream buyerPoundageStream = paymentResult.getStreams().get(0);
		Long buyerBalance = buyerPoundageStream.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance());
		SerialRecordDo buyerPoundage = new SerialRecordDo();
		buyerPoundage.setTradeNo(ws.getBuyerPoundagePayOrderNo());
		buyerPoundage.setTradeType(tradeType);
		buyerPoundage.setSerialNo(ws.getSerialNo());
		buyerPoundage.setCustomerType(weighingBill.getBuyerType());
		buyerPoundage.setAccountId(weighingBill.getBuyerCardAccount());
		buyerPoundage.setAction(ActionType.EXPENSE.getCode());
		buyerPoundage.setAmount(buyerPoundageStream != null ? Math.abs(buyerPoundageStream.getAmount()) : 0);
		buyerPoundage.setCardNo(weighingBill.getBuyerCardNo());
		buyerPoundage.setHoldName(weighingBill.getBuyerCardHolderName());
		buyerPoundage.setCustomerId(weighingBill.getBuyerId());
		buyerPoundage.setCustomerName(weighingBill.getBuyerName());
		buyerPoundage.setCustomerNo(weighingBill.getBuyerCode());
		buyerPoundage.setStartBalance(buyerBalance);
		buyerPoundage.setEndBalance(buyerBalance + (buyerPoundageStream != null ? buyerPoundageStream.getAmount() : 0));
		buyerPoundage.setFirmId(firmId);
		buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		buyerPoundage.setOperateTime(paymentResult.getWhen());
		buyerPoundage.setOperatorId(operatorId);
		buyerPoundage.setOperatorName(operator.getRealName());
		buyerPoundage.setOperatorNo(operator.getUserName());
		buyerPoundage.setNotes(String.format("买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerPoundage);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private BaseOutput<PaymentTradeCommitResponseDto> commitSellerCreditTrade(WeighingBill weighingBill, WeighingStatement ws) {
		// 提交买方手续费缴费订单
		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getSellerAccount());
		dto.setProtocolId(OrdersConstant.CREDIT_PAY_PROTOCAL_ID);
		dto.setTradeId(ws.getSellerPoundagePayOrderNo());
		dto.setBusinessId(weighingBill.getSellerCardAccount());
		List<FeeDto> fees = new ArrayList<FeeDto>(1);
		if (ws.getSellerPoundage() != null && ws.getSellerPoundage() > 0) {
			// 买家手续费
			FeeDto buyerFee = new FeeDto();
			buyerFee.setAmount(ws.getSellerPoundage());
			buyerFee.setType(FundItem.TRADE_SERVICE_FEE.getCode());
			buyerFee.setTypeName(FundItem.TRADE_SERVICE_FEE.getName());
			buyerFee.setUseFor(FeeUse.SELLER.getValue());
			fees.add(buyerFee);
		}
		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.pay(dto);
		if (!commitOutput.isSuccess()) {
			throw new AppException(commitOutput.getMessage());
		}
		return commitOutput;
	}

	private BaseOutput<PaymentTradeCommitResponseDto> commitBuyerCreditTrade(WeighingBill weighingBill, WeighingStatement ws) {
		// 提交买方手续费缴费订单
		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getBuyerAccount());
		dto.setProtocolId(OrdersConstant.CREDIT_PAY_PROTOCAL_ID);
		dto.setTradeId(ws.getBuyerPoundagePayOrderNo());
		dto.setBusinessId(weighingBill.getBuyerCardAccount());
		List<FeeDto> fees = new ArrayList<FeeDto>(1);
		if (ws.getBuyerPoundage() != null && ws.getBuyerPoundage() > 0) {
			// 买家手续费
			FeeDto buyerFee = new FeeDto();
			buyerFee.setAmount(ws.getBuyerPoundage());
			buyerFee.setType(FundItem.TRADE_SERVICE_FEE.getCode());
			buyerFee.setTypeName(FundItem.TRADE_SERVICE_FEE.getName());
			buyerFee.setUseFor(FeeUse.BUYER.getValue());
			fees.add(buyerFee);
		}
		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.pay(dto);
		if (!commitOutput.isSuccess()) {
			throw new AppException(commitOutput.getMessage());
		}
		return commitOutput;
	}

	@GlobalTransactional(timeoutMills = Integer.MAX_VALUE)
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> withdraw(Long id, String buyerPassword, String sellerPassword, Long operatorId) {
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
		if (!todayDate.equals(opTime.toLocalDate())) {
			return BaseOutput.failure("只能对当日的过磅交易进行撤销操作");
		}
		if (!weighingBill.getState().equals(WeighingBillState.SETTLED.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		if (weighingBill.getPaymentType().equals(PaymentType.CREDIT.getValue()) && weighingBill.getPaymentState().equals(PaymentState.RECEIVED.getValue())) {
			return BaseOutput.failure("只能对未回款的单据进行撤销");
		}

		// 校验买卖双方密码
		PaymentErrorCode buyerError = null;
		PaymentErrorCode sellerError = null;
		AccountPasswordValidateDto buyerPwdDto = new AccountPasswordValidateDto();
		buyerPwdDto.setAccountId(weighingBill.getBuyerAccount());
		buyerPwdDto.setPassword(buyerPassword);
		BaseOutput<Object> pwdOutput = this.payRpc.validateAccountPassword(buyerPwdDto);
		if (pwdOutput == null) {
			return BaseOutput.failure("交易过磅撤销调用支付系统验证买方密码无响应");
		}
		if (!pwdOutput.isSuccess()) {
			LOGGER.error(String.format("交易过磅撤销调用支付系统验证买方密码失败:code=%s,message=%s", pwdOutput.getCode(), pwdOutput.getMessage()));
			if (PaymentErrorCode.codeOf(pwdOutput.getCode()).equals(PaymentErrorCode.ACCOUNT_PASSWORD_INCORRECT_EXCEPTION)) {
				buyerError = PaymentErrorCode.BUYER_PASSWORD_INCORRECT_EXCEPTION;
			} else {
				return pwdOutput;
			}
		}

		AccountPasswordValidateDto sellerPwdDto = new AccountPasswordValidateDto();
		sellerPwdDto.setAccountId(weighingBill.getSellerAccount());
		sellerPwdDto.setPassword(sellerPassword);
		pwdOutput = this.payRpc.validateAccountPassword(sellerPwdDto);
		if (pwdOutput == null) {
			return BaseOutput.failure("交易过磅撤销调用支付系统验证卖方密码无响应");
		}
		if (!pwdOutput.isSuccess()) {
			LOGGER.error(String.format("交易过磅撤销调用支付系统验证卖方密码失败:code=%s,message=%s", pwdOutput.getCode(), pwdOutput.getMessage()));
			if (PaymentErrorCode.codeOf(pwdOutput.getCode()).equals(PaymentErrorCode.ACCOUNT_PASSWORD_INCORRECT_EXCEPTION)) {
				sellerError = PaymentErrorCode.SELLER_PASSWORD_INCORRECT_EXCEPTION;
			} else {
				return pwdOutput;
			}
		}

		if (buyerError != null && sellerError != null) {
			return BaseOutput.failure(PaymentErrorCode.BUYER_SELLER_PASSWORD_INCORRECT_EXCEPTION.getCode(), PaymentErrorCode.BUYER_SELLER_PASSWORD_INCORRECT_EXCEPTION.getName());
		} else if (buyerError != null) {
			return BaseOutput.failure(buyerError.getCode(), buyerError.getName());
		} else if (sellerError != null) {
			return BaseOutput.failure(sellerError.getCode(), sellerError.getName());
		}

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.REFUNDED.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKey(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		// 恢复皮重单
		if (StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 判断是否是d牌
			// 通过c端传入的数据，去jmsf获取皮重当相关信息
			BaseOutput<TruckDTO> output = this.jsmfRpc.recoverById(Long.valueOf(weighingBill.getTareBillNumber()));
			if (output == null) {
				throw new AppException("交易过磅撤销调用进门收费系统恢复皮重单无响应");
			}
			if (!output.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用进门收费系统恢复皮重单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
				throw new AppException(output.getMessage());
			}
		}

		// 退款
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> buyerPoundagePaymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> sellerPoundagePaymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> servicePaymentOutput = null;
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		if (weighingStatement.getPayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getPayOrderNo());
			paymentOutput = this.payRpc.cancel(cancelDto);
			if (paymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!paymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
				throw new AppException(paymentOutput.getMessage());
			}
		}
		if (weighingStatement.getSellerPoundagePayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getSellerPoundagePayOrderNo());
			sellerPoundagePaymentOutput = this.payRpc.cancel(cancelDto);
			if (sellerPoundagePaymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!sellerPoundagePaymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", sellerPoundagePaymentOutput.getCode(), sellerPoundagePaymentOutput.getMessage()));
				throw new AppException(sellerPoundagePaymentOutput.getMessage());
			}
		}
		if (weighingStatement.getBuyerPoundagePayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getBuyerPoundagePayOrderNo());
			buyerPoundagePaymentOutput = this.payRpc.cancel(cancelDto);
			if (buyerPoundagePaymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!buyerPoundagePaymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", buyerPoundagePaymentOutput.getCode(), buyerPoundagePaymentOutput.getMessage()));
				throw new AppException(buyerPoundagePaymentOutput.getMessage());
			}
		}
		if (weighingStatement.getServiceFeePayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getServiceFeePayOrderNo());
			servicePaymentOutput = this.payRpc.cancel(cancelDto);
			if (servicePaymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!servicePaymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", servicePaymentOutput.getCode(), servicePaymentOutput.getMessage()));
				throw new AppException(servicePaymentOutput.getMessage());
			}
		}

		// 重新生成一条过磅单
		if (paymentOutput != null) {
			this.rebuildWeighingBill(paymentOutput.getData(), weighingBill, operatorId);
		} else {
			PaymentTradeCommitResponseDto toBuildwb = new PaymentTradeCommitResponseDto();
			toBuildwb.setWhen(now);
			this.rebuildWeighingBill(toBuildwb, weighingBill, operatorId);
		}

		User operator = this.getUserById(operatorId);
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		weighingStatement.setLastOperationTime(paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		weighingStatement.setLastOperatorId(operatorId);
		weighingStatement.setLastOperatorName(operator.getRealName());
		weighingStatement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		// 记录操作流水
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.WITHDRAW,
				paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 记录撤销交易流水
		if (paymentOutput != null) {
			this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill, weighingStatement);
		}
		if (buyerPoundagePaymentOutput != null) {
			this.recordBuyerCreditWithdrawAccountFlow(operatorId, weighingStatement, buyerPoundagePaymentOutput.getData(), weighingBill);
		}
		if (sellerPoundagePaymentOutput != null) {
			this.recordSellerCreditWithdrawAccountFlow(operatorId, weighingStatement, sellerPoundagePaymentOutput.getData(), weighingBill);
		}
		if (servicePaymentOutput != null) {
			this.recordServiceFeeWithdrawAccountFlow(operatorId, weighingStatement, servicePaymentOutput.getData(), weighingBill);
		}

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, weighingBill.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, weighingBill.getId());
		LoggerContext.put("statementId", weighingStatement.getId());
		LoggerContext.put("statementSerialNo", weighingStatement.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, operatorId);
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, operator.getRealName());
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, weighingBill.getMarketId());

		return BaseOutput.success();
	}

	@GlobalTransactional(timeoutMills = Integer.MAX_VALUE)
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
		if (!todayDate.equals(opTime.toLocalDate())) {
			return BaseOutput.failure("只能对当日的过磅交易进行撤销操作");
		}
		if (!weighingBill.getState().equals(WeighingBillState.SETTLED.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		if (weighingBill.getPaymentType().equals(PaymentType.CREDIT.getValue()) && weighingBill.getPaymentState().equals(PaymentState.RECEIVED.getValue())) {
			return BaseOutput.failure("只能对未回款的单据进行撤销");
		}

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.REFUNDED.getValue());
		weighingBill.setModifierId(operatorId);
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKey(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		// 恢复皮重单
		if (StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 判断是否是d牌
			// 通过c端传入的数据，去jmsf获取皮重当相关信息
			BaseOutput<TruckDTO> output = this.jsmfRpc.recoverById(Long.valueOf(weighingBill.getTareBillNumber()));
			if (output == null) {
				throw new AppException("交易过磅撤销调用进门收费系统恢复皮重单无响应");
			}
			if (!output.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用进门收费系统恢复皮重单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
				throw new AppException(output.getMessage());
			}
		}

		// 退款
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> buyerPoundagePaymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> sellerPoundagePaymentOutput = null;
		BaseOutput<PaymentTradeCommitResponseDto> servicePaymentOutput = null;
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		if (weighingStatement.getPayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getPayOrderNo());
			paymentOutput = this.payRpc.cancel(cancelDto);
			if (paymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!paymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
				throw new AppException(paymentOutput.getMessage());
			}
		}
		if (weighingStatement.getSellerPoundagePayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getSellerPoundagePayOrderNo());
			sellerPoundagePaymentOutput = this.payRpc.cancel(cancelDto);
			if (sellerPoundagePaymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!sellerPoundagePaymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", sellerPoundagePaymentOutput.getCode(), sellerPoundagePaymentOutput.getMessage()));
				throw new AppException(sellerPoundagePaymentOutput.getMessage());
			}
		}
		if (weighingStatement.getBuyerPoundagePayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getBuyerPoundagePayOrderNo());
			buyerPoundagePaymentOutput = this.payRpc.cancel(cancelDto);
			if (buyerPoundagePaymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!buyerPoundagePaymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", buyerPoundagePaymentOutput.getCode(), buyerPoundagePaymentOutput.getMessage()));
				throw new AppException(buyerPoundagePaymentOutput.getMessage());
			}
		}
		if (weighingStatement.getServiceFeePayOrderNo() != null) {
			cancelDto.setTradeId(weighingStatement.getServiceFeePayOrderNo());
			servicePaymentOutput = this.payRpc.cancel(cancelDto);
			if (servicePaymentOutput == null) {
				throw new AppException("交易过磅撤销调用支付系统撤销交易无响应");
			}
			if (!servicePaymentOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅撤销调用支付系统撤销交易失败:code=%s,message=%s", servicePaymentOutput.getCode(), servicePaymentOutput.getMessage()));
				throw new AppException(servicePaymentOutput.getMessage());
			}
		}

		if (paymentOutput != null) {
			this.rebuildWeighingBill(paymentOutput.getData(), weighingBill, operatorId);
		} else {
			PaymentTradeCommitResponseDto toBuildwb = new PaymentTradeCommitResponseDto();
			toBuildwb.setWhen(now);
			this.rebuildWeighingBill(toBuildwb, weighingBill, operatorId);
		}

		User operator = this.getUserById(operatorId);
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(paymentOutput.getData().getWhen());
		weighingStatement.setLastOperationTime(paymentOutput.getData().getWhen());
		weighingStatement.setLastOperatorId(operatorId);
		weighingStatement.setLastOperatorName(operator.getRealName());
		weighingStatement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		// 记录操作流水
		// 记录操作流水
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.WITHDRAW,
				paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		if (paymentOutput != null) {
			// 记录撤销交易流水
			this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill, weighingStatement);
		}
		if (buyerPoundagePaymentOutput != null) {
			this.recordBuyerCreditWithdrawAccountFlow(operatorId, weighingStatement, buyerPoundagePaymentOutput.getData(), weighingBill);
		}
		if (sellerPoundagePaymentOutput != null) {
			this.recordSellerCreditWithdrawAccountFlow(operatorId, weighingStatement, sellerPoundagePaymentOutput.getData(), weighingBill);
		}
		if (servicePaymentOutput != null) {
			this.recordServiceFeeWithdrawAccountFlow(operatorId, weighingStatement, servicePaymentOutput.getData(), weighingBill);
		}

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, weighingBill.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, weighingBill.getId());
		LoggerContext.put("statementId", weighingStatement.getId());
		LoggerContext.put("statementSerialNo", weighingStatement.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, operatorId);
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, operator.getRealName());
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, weighingBill.getMarketId());

		return BaseOutput.success();
	}

	private void recordServiceFeeWithdrawAccountFlow(Long operatorId, WeighingStatement ws, PaymentTradeCommitResponseDto tradeResponse, WeighingBill weighingBill) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		Long firmId = this.getMarketIdByOperatorId(operatorId);
		User operator = this.getUserById(operatorId);
		Integer tradeType = PaymentTradeType.TRADE.getValue();
		if (ws.getFrozenAmount() != null && ws.getFrozenAmount() > 0) {
			tradeType = PaymentTradeType.TRADE.getValue();
		}

		// 卖家退款
		PaymentStream sellerExpenseStream = tradeResponse.getStreams().get(0);
		Long sellerBalance = sellerExpenseStream.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance());
		SerialRecordDo sellerExpense = new SerialRecordDo();
		sellerExpense.setTradeNo(ws.getServiceFeePayOrderNo());
		sellerExpense.setTradeType(tradeType);
		sellerExpense.setSerialNo(ws.getSerialNo());
		sellerExpense.setCustomerType(weighingBill.getSellerType());
		sellerExpense.setAccountId(weighingBill.getSellerCardAccount());
		sellerExpense.setAction(ActionType.EXPENSE.getCode());
		sellerExpense.setAmount(Math.abs(sellerExpenseStream.getAmount()));
		sellerExpense.setCardNo(weighingBill.getSellerCardNo());
		sellerExpense.setHoldName(weighingBill.getSellerCardHolderName());
		sellerExpense.setCustomerId(weighingBill.getSellerId());
		sellerExpense.setCustomerName(weighingBill.getSellerName());
		sellerExpense.setCustomerNo(weighingBill.getSellerCode());
		sellerExpense.setStartBalance(sellerBalance);
		sellerExpense.setEndBalance(sellerBalance + sellerExpenseStream.getAmount());
		sellerExpense.setFirmId(firmId);
		sellerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		sellerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		sellerExpense.setOperateTime(tradeResponse.getWhen());
		sellerExpense.setOperatorId(operatorId);
		sellerExpense.setOperatorName(operator.getRealName());
		sellerExpense.setOperatorNo(operator.getUserName());
		sellerExpense.setNotes(String.format("撤销，卖方，结算单号%s", ws.getSerialNo()));
		srList.add(sellerExpense);

		// 买家退款
		PaymentStream buyerRefundStream = tradeResponse.getRelation().getStreams().get(0);
		Long buyerBalance = buyerRefundStream.getBalance() - (tradeResponse.getRelation().getFrozenAmount() + tradeResponse.getRelation().getFrozenBalance());
		SerialRecordDo buyerRefund = new SerialRecordDo();
		buyerRefund.setTradeNo(ws.getServiceFeePayOrderNo());
		buyerRefund.setTradeType(tradeType);
		buyerRefund.setSerialNo(ws.getSerialNo());
		buyerRefund.setCustomerType(weighingBill.getBuyerType());
		buyerRefund.setAccountId(weighingBill.getBuyerCardAccount());
		buyerRefund.setAction(ActionType.INCOME.getCode());
		buyerRefund.setAmount(buyerRefundStream.getAmount());
		buyerRefund.setCardNo(weighingBill.getBuyerCardNo());
		buyerRefund.setHoldName(weighingBill.getBuyerCardHolderName());
		buyerRefund.setCustomerId(weighingBill.getBuyerId());
		buyerRefund.setCustomerName(weighingBill.getBuyerName());
		buyerRefund.setCustomerNo(weighingBill.getBuyerCode());
		buyerRefund.setStartBalance(buyerBalance);
		buyerRefund.setEndBalance(buyerBalance + buyerRefundStream.getAmount());
		buyerRefund.setFirmId(firmId);
		buyerRefund.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		buyerRefund.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		buyerRefund.setOperateTime(tradeResponse.getWhen());
		buyerRefund.setOperatorId(operatorId);
		buyerRefund.setOperatorName(operator.getRealName());
		buyerRefund.setOperatorNo(operator.getUserName());
		buyerRefund.setNotes(String.format("撤销，买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerRefund);

		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordBuyerCreditWithdrawAccountFlow(Long operatorId, WeighingStatement ws, PaymentTradeCommitResponseDto tradeResponse, WeighingBill weighingBill) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		Long firmId = this.getMarketIdByOperatorId(operatorId);
		User operator = this.getUserById(operatorId);
		Integer tradeType = PaymentTradeType.TRADE.getValue();
		if (ws.getFrozenAmount() != null && ws.getFrozenAmount() > 0) {
			tradeType = PaymentTradeType.TRADE.getValue();
		}

		// 卖家退款
		PaymentStream buyerExpenseStream = tradeResponse.getStreams().get(0);
		Long sellerBalance = buyerExpenseStream.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance());
		SerialRecordDo buyerExpense = new SerialRecordDo();
		buyerExpense.setTradeNo(ws.getBuyerPoundagePayOrderNo());
		buyerExpense.setTradeType(tradeType);
		buyerExpense.setSerialNo(ws.getSerialNo());
		buyerExpense.setCustomerType(weighingBill.getSellerType());
		buyerExpense.setAccountId(weighingBill.getSellerCardAccount());
		buyerExpense.setAction(ActionType.INCOME.getCode());
		buyerExpense.setAmount(Math.abs(buyerExpenseStream.getAmount()));
		buyerExpense.setCardNo(weighingBill.getSellerCardNo());
		buyerExpense.setHoldName(weighingBill.getSellerCardHolderName());
		buyerExpense.setCustomerId(weighingBill.getSellerId());
		buyerExpense.setCustomerName(weighingBill.getSellerName());
		buyerExpense.setCustomerNo(weighingBill.getSellerCode());
		buyerExpense.setStartBalance(sellerBalance);
		buyerExpense.setEndBalance(sellerBalance + buyerExpenseStream.getAmount());
		buyerExpense.setFirmId(firmId);
		buyerExpense.setFundItem(FundItem.TRANSFER.getCode());
		buyerExpense.setFundItemName(FundItem.TRANSFER.getName());
		buyerExpense.setOperateTime(tradeResponse.getWhen());
		buyerExpense.setOperatorId(operatorId);
		buyerExpense.setOperatorName(operator.getRealName());
		buyerExpense.setOperatorNo(operator.getUserName());
		buyerExpense.setNotes(String.format("撤销，买方，结算单号%s", ws.getSerialNo()));
		srList.add(buyerExpense);

		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordSellerCreditWithdrawAccountFlow(Long operatorId, WeighingStatement ws, PaymentTradeCommitResponseDto tradeResponse, WeighingBill weighingBill) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		Long firmId = this.getMarketIdByOperatorId(operatorId);
		User operator = this.getUserById(operatorId);
		Integer tradeType = PaymentTradeType.TRADE.getValue();
		if (ws.getFrozenAmount() != null && ws.getFrozenAmount() > 0) {
			tradeType = PaymentTradeType.TRADE.getValue();
		}

		// 卖家退款
		PaymentStream sellerExpenseStream = tradeResponse.getStreams().get(0);
		Long sellerBalance = sellerExpenseStream.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance());
		SerialRecordDo sellerExpense = new SerialRecordDo();
		sellerExpense.setTradeNo(ws.getSellerPoundagePayOrderNo());
		sellerExpense.setTradeType(tradeType);
		sellerExpense.setSerialNo(ws.getSerialNo());
		sellerExpense.setCustomerType(weighingBill.getSellerType());
		sellerExpense.setAccountId(weighingBill.getSellerCardAccount());
		sellerExpense.setAction(ActionType.INCOME.getCode());
		sellerExpense.setAmount(Math.abs(sellerExpenseStream.getAmount()));
		sellerExpense.setCardNo(weighingBill.getSellerCardNo());
		sellerExpense.setHoldName(weighingBill.getSellerCardHolderName());
		sellerExpense.setCustomerId(weighingBill.getSellerId());
		sellerExpense.setCustomerName(weighingBill.getSellerName());
		sellerExpense.setCustomerNo(weighingBill.getSellerCode());
		sellerExpense.setStartBalance(sellerBalance);
		sellerExpense.setEndBalance(sellerBalance + sellerExpenseStream.getAmount());
		sellerExpense.setFirmId(firmId);
		sellerExpense.setFundItem(FundItem.TRANSFER.getCode());
		sellerExpense.setFundItemName(FundItem.TRANSFER.getName());
		sellerExpense.setOperateTime(tradeResponse.getWhen());
		sellerExpense.setOperatorId(operatorId);
		sellerExpense.setOperatorName(operator.getRealName());
		sellerExpense.setOperatorNo(operator.getUserName());
		sellerExpense.setNotes(String.format("撤销，卖方，结算单号%s", ws.getSerialNo()));
		srList.add(sellerExpense);

		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	@Override
	public PageOutput<List<WeighingBillListPageDto>> listPage(WeighingBillQueryDto query) {
		query.setTradingBillType(TradingBillType.PROPRIETARY.getValue());
		return super.listPage(query);
	}

	@Override
	public List<WeighingBillClientListDto> listByExampleModified(WeighingBillQueryDto weighingBill) {
		weighingBill.setTradingBillType(TradingBillType.PROPRIETARY.getValue());
		return super.listByExampleModified(weighingBill);
	}

}
