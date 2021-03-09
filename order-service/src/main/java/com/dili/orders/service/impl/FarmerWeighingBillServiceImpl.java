package com.dili.orders.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.customer.sdk.domain.dto.CustomerExtendDto;
import com.dili.jmsf.microservice.sdk.dto.TruckDTO;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.MeasureType;
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
import com.dili.orders.dto.FundItem;
import com.dili.orders.dto.PaymentErrorCode;
import com.dili.orders.dto.PaymentTradeCommitDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;
import com.dili.orders.dto.PaymentTradePrepareDto;
import com.dili.orders.dto.PaymentTradeType;
import com.dili.orders.dto.PrintTemplateDataDto;
import com.dili.orders.dto.SerialRecordDo;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillPrintDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingStatementPrintDto;
import com.dili.orders.service.FarmerWeghingBillService;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.exception.AppException;
import com.dili.ss.util.BeanConver;
import com.dili.ss.util.MoneyUtils;
import com.dili.uap.sdk.domain.User;

import io.seata.spring.annotation.GlobalTransactional;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Service
public class FarmerWeighingBillServiceImpl extends WeighingBillServiceImpl implements FarmerWeghingBillService {

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> addWeighingBill(WeighingBill bill) {
		if (bill.getTradingBillType() == null) {
			return BaseOutput.failure("单据类型不能为空");
		}
		if (!bill.getTradingBillType().equals(TradingBillType.FARMER.getValue())) {
			return BaseOutput.failure("单据类型不正确");
		}
		return super.addWeighingBill(bill);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> updateWeighingBill(WeighingBill bill) {
		LOGGER.info("修改交易过磅接收参数：{}", JSON.toJSONString(bill));
		if (bill.getTradingBillType() == null) {
			return BaseOutput.failure("单据类型不能为空");
		}
		if (!bill.getTradingBillType().equals(TradingBillType.FARMER.getValue())) {
			return BaseOutput.failure("单据类型不正确");
		}
		WeighingBill weighingBill = this.getWeighingBillById(bill.getId());
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能修改过磅单");
		}
		this.updateWeihingBillInfo(weighingBill, bill);
		// 查询未结算单
		WeighingStatement ws = this.getNoSettlementWeighingStatementByWeighingBillId(weighingBill.getId());
		if (ws == null) {
			return BaseOutput.failure("未找到结算单");
		}

		// 判断重复冻结
		if (this.isRepeatFreeze(bill, ws)) {
			return BaseOutput.failure("不能重复冻结");
		}

		ws.setPaymentType(bill.getPaymentType());

		Long marketId = this.getMarketIdByOperatorId(bill.getModifierId());

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

		User operator = this.getUserById(weighingBill.getModifierId());
		LocalDateTime now = LocalDateTime.now();
		ws.setModifierId(bill.getModifierId());
		ws.setModifiedTime(now);

		// 判断是否是未结算单，否则记录过磅时间
		if (weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {

			// 更新操作人信息
			ws.setLastOperationTime(now);
			ws.setLastOperatorId(bill.getModifierId());
			ws.setLastOperatorName(operator.getRealName());
			ws.setLastOperatorUserName(operator.getUserName());

			// 插入一条过磅信息
			WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, ws, operator, WeighingOperationType.WEIGH, now);
			rows = this.wbrMapper.insertSelective(wbor);
			if (rows <= 0) {
				throw new AppException("保存操作记录失败");
			}
		}
		rows = this.weighingStatementMapper.updateByPrimaryKey(ws);
		if (rows <= 0) {
			throw new AppException("更新过磅单失败");
		}

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, weighingBill.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, weighingBill.getId());
		LoggerContext.put("statementId", ws.getId());
		LoggerContext.put("statementSerialNo", ws.getSerialNo());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, weighingBill.getModifierId());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_NAME_KEY, operator.getRealName());
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, weighingBill.getMarketId());

		return BaseOutput.successData(ws);
	}

	@Override
	protected void updateWeihingBillInfo(WeighingBill weighingBill, WeighingBill dto) {
		super.updateWeihingBillInfo(weighingBill, dto);
		weighingBill.setPaymentType(dto.getPaymentType());
	}

	@Override
	protected WeighingStatement buildWeighingStatement(WeighingBill weighingBill, Long marketId) {
		WeighingStatement ws = super.buildWeighingStatement(weighingBill, marketId);
		ws.setPaymentType(weighingBill.getPaymentType());
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

	@GlobalTransactional
	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<WeighingStatement> settle(Long id, String buyerPassword, Long operatorId, Long marketId) {
		WeighingBill weighingBill = this.getWeighingBillById(id);

		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getTradingBillType().equals(TradingBillType.FARMER.getValue())) {
			throw new AppException("单据类型不正确");
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

		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			// 判断余额是否足够
			AccountRequestDto balanceQuery = new AccountRequestDto();
			balanceQuery.setAccountId(weighingBill.getBuyerAccount());
			BaseOutput<AccountBalanceDto> balanceOutput = this.payRpc.queryAccountBalance(balanceQuery);
			if (!balanceOutput.isSuccess()) {
				LOGGER.error(String.format("交易过磅结算调用支付系统查询买方余额失败:code=%s,message=%s", balanceOutput.getCode(), balanceOutput.getMessage()));
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
							weighingBill.getTradeType().toString(), weighingBill.getTradingBillType());
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
		} else {
			weighingBill.setPaymentState(PaymentState.UNRECEIVED.getValue());
			weighingStatement.setPaymentState(PaymentState.UNRECEIVED.getValue());
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
		BaseOutput<PaymentTradeCommitResponseDto> unfreezeOutput = null;
		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			weighingBill.setPaymentState(PaymentState.RECEIVED.getValue());
			weighingStatement.setPaymentState(PaymentState.RECEIVED.getValue());
			if (originalState.equals(WeighingBillState.FROZEN.getValue())) {
				if (weighingStatement.getPayOrderNo() == null) {
					BaseOutput<?> output = this.prepareTrade(weighingBill, weighingStatement);
					if (!output.isSuccess()) {
						LOGGER.error(String.format("交易过磅结算调用支付系统创建支付订单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
						throw new AppException(output.getMessage());
					}
					paymentOutput = this.commitTrade(weighingBill, weighingStatement, buyerPassword);
					if (paymentOutput == null) {
						throw new AppException("调用支付系统无响应");
					}
					if (!paymentOutput.isSuccess()) {
						LOGGER.error(String.format("交易过磅结算调用支付系统确认交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
						throw new AppException(paymentOutput.getMessage());
					}
				} else {
					paymentOutput = this.confirmTrade(weighingBill, weighingStatement, buyerPassword);
					if (paymentOutput == null) {
						throw new AppException("调用支付系统无响应");
					}
					if (!paymentOutput.isSuccess()) {
						LOGGER.error(String.format("交易过磅结算调用支付系统确认交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
						throw new AppException(paymentOutput.getMessage());
					}
				}
			} else {
				if (weighingStatement.getPayOrderNo() == null) {
					BaseOutput<?> output = this.prepareTrade(weighingBill, weighingStatement);
					if (!output.isSuccess()) {
						LOGGER.error(String.format("交易过磅结算调用支付系统创建支付订单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
						throw new AppException(output.getMessage());
					}
				}
				paymentOutput = this.commitTrade(weighingBill, weighingStatement, buyerPassword);
				if (paymentOutput == null) {
					throw new AppException("调用支付系统无响应");
				}
				if (!paymentOutput.isSuccess()) {
					LOGGER.error(String.format("交易过磅结算调用支付系统确认交易失败:code=%s,message=%s", paymentOutput.getCode(), paymentOutput.getMessage()));
					throw new AppException(paymentOutput.getMessage());
				}
			}
		} else {
			// 如果是冻结单园区卡转赊销需要解冻
			if (originalState.equals(WeighingBillState.FROZEN.getValue()) && StringUtils.isNotBlank(weighingStatement.getPayOrderNo())) {
				PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
				dto.setTradeId(weighingStatement.getPayOrderNo());
				unfreezeOutput = this.payRpc.cancel(dto);
				if (unfreezeOutput == null) {
					throw new AppException("解冻金额调用支付系统无响应");
				}
				if (!unfreezeOutput.isSuccess()) {
					LOGGER.error(String.format("交易过磅解冻调用支付系统确认交易失败:code=%s,message=%s", unfreezeOutput.getCode(), unfreezeOutput.getMessage()));
					throw new AppException(unfreezeOutput.getMessage());
				}
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
		}
		if (unfreezeOutput != null) {
			this.recordUnfreezeAccountFlow(operatorId, weighingBill, weighingStatement, unfreezeOutput.getData());
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
		if (weighingBill.getPaymentType().equals(PaymentType.CARD.getValue())) {
			LoggerContext.put(LoggerConstant.LOG_OPERATION_TYPE_KEY, "settle");
		} else {
			LoggerContext.put(LoggerConstant.LOG_OPERATION_TYPE_KEY, "credit");
		}

		return BaseOutput.successData(weighingStatement);
	}

	@Override
	protected void setWeighingStatementFrozenAmount(WeighingBill weighingBill, WeighingStatement ws) {
		if (weighingBill.getPaymentType().equals(PaymentType.CREDIT.getValue())) {
			ws.setBuyerActualAmount(null);
			ws.setBuyerPoundage(null);
			ws.setSellerActualAmount(null);
			ws.setSellerPoundage(null);
			ws.setTradeAmount(null);
			ws.setFrozenAmount(0L);
		} else {
			super.setWeighingStatementFrozenAmount(weighingBill, ws);
		}
	}

	@Override
	protected BaseOutput<List<QueryFeeOutput>> calculatePoundage(WeighingBill weighingBill, WeighingStatement statement, Long marketId, String businessType) {
		// 无需查询计费规则，返回空
		BaseOutput<List<QueryFeeOutput>> output = BaseOutput.success();
		output.setData(new ArrayList<QueryFeeOutput>(0));
		return output;
	}

	@Override
	protected void setWeighingStatementBuyerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		BaseOutput<List<QueryFeeOutput>> buyerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE);
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException(buyerFeeOutput.getMessage());
		}
		BigDecimal buyerTotalFee = new BigDecimal(0L);
//		if (CollectionUtils.isEmpty(buyerFeeOutput.getData())) {
//			throw new AppException("未匹配到计费规则，请联系管理员");
//		}
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
//		if (CollectionUtils.isEmpty(sellerFeeOutput.getData())) {
//			throw new AppException("未匹配到计费规则，请联系管理员");
//		}
		for (QueryFeeOutput qfo : sellerFeeOutput.getData()) {
			if (qfo.getTotalFee() != null) {
				sellerTotalFee = sellerTotalFee.add(qfo.getTotalFee().setScale(2, RoundingMode.HALF_UP));
			}
		}
		if (!isFreeze(weighingBill)) {
			ws.setSellerActualAmount(ws.getTradeAmount() - MoneyUtils.yuanToCent(sellerTotalFee.doubleValue()));
			ws.setSellerPoundage(MoneyUtils.yuanToCent(sellerTotalFee.doubleValue()));
		}
		ws.setSellerCardNo(weighingBill.getSellerCardNo());
		ws.setSellerId(weighingBill.getSellerId());
		ws.setSellerName(weighingBill.getSellerName());
	}

	@Override
	public BaseOutput<Object> withdraw(Long id, String buyerPassword, String sellerPassword, Long operatorId) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getTradingBillType().equals(TradingBillType.FARMER.getValue())) {
			return BaseOutput.failure("单据类型不正确");
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

		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = null;
		// 退款
		if (weighingStatement.getPayOrderNo() != null) {
			PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
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

		// 重新生成一条过磅单
		PaymentTradeCommitResponseDto dtoToWhen = new PaymentTradeCommitResponseDto();
		dtoToWhen.setWhen(now);
		this.rebuildWeighingBill(paymentOutput != null ? paymentOutput.getData() : dtoToWhen, weighingBill, operatorId);

		User operator = this.getUserById(operatorId);
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		weighingStatement.setLastOperationTime(now);
		weighingStatement.setLastOperatorId(operatorId);
		weighingStatement.setLastOperatorName(operator.getRealName());
		weighingStatement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		// 记录操作流水
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.WITHDRAW, now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		if (paymentOutput != null) {
			// 记录撤销交易流水
			this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill, weighingStatement);
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

	@Override
	protected WeighingBill rebuildWeighingBill(PaymentTradeCommitResponseDto paymentOutput, WeighingBill weighingBill, Long operatorId) {
		WeighingBill wb = new WeighingBill();
		BeanUtils.copyProperties(weighingBill, wb, "id", "buyerAgentId", "buyerAgentName", "sellerAgentId", "sellerAgentName", "state", "createdTime", "modifiedTime", "modifierId", "settlementTime",
				"version","paymentState");
		wb.setState(WeighingBillState.NO_SETTLEMENT.getValue());

		int rows = this.getActualDao().insertSelective(wb);
		if (rows <= 0) {
			throw new AppException("保存过磅单失败");
		}

		User operator = this.getUserById(wb.getCreatorId());
		WeighingStatement statement = this.buildWeighingStatement(wb, weighingBill.getMarketId());
		statement.setCreatorId(operatorId);
		statement.setLastOperationTime(paymentOutput.getWhen());
		statement.setLastOperatorId(operatorId);
		statement.setLastOperatorName(operator.getRealName());
		statement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.insertSelective(statement);
		if (rows <= 0) {
			throw new AppException("保存结算单失败");
		}

		WeighingBillOperationRecord wbor = this.getActualDao().selectLastWeighingOperationRecord(weighingBill.getId());
		if (wbor == null) {
			throw new AppException("未查询到过磅记录");
		}

		WeighingBillOperationRecord newRecord = this.buildOperationRecord(wb, statement, operator, WeighingOperationType.WEIGH, wbor.getOperationTime());
		rows = this.wbrMapper.insertSelective(newRecord);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return wb;
	}

	@Override
	public BaseOutput<Object> operatorWithdraw(Long id, Long operatorId) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getTradingBillType().equals(TradingBillType.FARMER.getValue())) {
			return BaseOutput.failure("单据类型不正确");
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

		if (weighingBill.getPaymentState().equals(PaymentState.RECEIVED.getValue())) {
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
				LOGGER.error(String.format("操作员撤销过磅单调用进门收费恢复皮重单失败:code=%s,message=%s", output.getCode(), output.getMessage()));
				throw new AppException(output.getMessage());
			}
		}

		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = null;
		// 退款
		if (weighingStatement.getPayOrderNo() != null) {
			PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
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

		// 重新生成一条过磅单
		PaymentTradeCommitResponseDto dtoToWhen = new PaymentTradeCommitResponseDto();
		dtoToWhen.setWhen(now);
		this.rebuildWeighingBill(paymentOutput != null ? paymentOutput.getData() : dtoToWhen, weighingBill, operatorId);

		User operator = this.getUserById(operatorId);
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifierId(operatorId);
		weighingStatement.setModifiedTime(now);
		weighingStatement.setLastOperationTime(paymentOutput != null ? paymentOutput.getData().getWhen() : now);
		weighingStatement.setLastOperatorId(operatorId);
		weighingStatement.setLastOperatorName(operator.getRealName());
		weighingStatement.setLastOperatorUserName(operator.getUserName());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		// 记录操作流水
		WeighingBillOperationRecord wbor = this.buildOperationRecord(weighingBill, weighingStatement, operator, WeighingOperationType.WITHDRAW, now);
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		if (paymentOutput != null) {
			// 记录撤销交易流水
			this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill, weighingStatement);
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

	@Override
	public PageOutput<List<WeighingBillListPageDto>> listPage(WeighingBillQueryDto query) {
		query.setTradingBillType(TradingBillType.FARMER.getValue());
		return super.listPage(query);
	}

	@Override
	public List<WeighingBillClientListDto> listByExampleModified(WeighingBillQueryDto weighingBill) {
		weighingBill.setTradingBillType(TradingBillType.FARMER.getValue());
		return super.listByExampleModified(weighingBill);
	}

	@Override
	public PrintTemplateDataDto<WeighingBillPrintDto> getWeighingBillPrintData(Long id) {
		PrintTemplateDataDto<WeighingBillPrintDto> result = super.getWeighingBillPrintData(id);
		result.setTemplate("ProprietaryTradingDocument");
		return result;
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
		dto.setTradeType(wb.getTradeType());
		dto.setSubtractionRate(wb.getSubtractionRate());
		dto.setSubtractionWeight(wb.getSubtractionWeight());
		dto.setPackingTypeName(wb.getPackingTypeName());

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
			LOGGER.error(String.format("打印结算单调用支付系统查询买方余额失败:code=%s,message=%s", balanceOutput.getCode(), balanceOutput.getMessage()));
			return null;
		}
		dto.setBuyerBalance(balanceOutput.getData().getAvailableAmount());
		String tempName = wb.getMeasureType().equals(MeasureType.WEIGHT.getValue()) ? "ProprietarySettlementDocument" : "ProprietarySettlementPieceDocument";
		return new PrintTemplateDataDto<WeighingStatementPrintDto>(tempName, dto);
	}

}
