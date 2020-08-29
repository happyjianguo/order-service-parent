package com.dili.orders.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.enums.BusinessChargeItemEnum;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.commons.rabbitmq.RabbitMQMessageService;
import com.dili.customer.sdk.rpc.CustomerRpc;
import com.dili.jmsf.microservice.sdk.dto.TruckDTO;
import com.dili.orders.config.RabbitMQConfig;
import com.dili.orders.config.WeighingBillMQConfig;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;
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
import com.dili.orders.dto.SerialRecordDo;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillPrintDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingStatementPrintDto;
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
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.util.BeanConver;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Transactional(rollbackFor = Exception.class)
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
	private ReferencePriceService referencePriceService;
	@Autowired
	private UidRpc uidRpc;
	@Autowired
	private UserRpc userRpc;

	@Autowired
	private WeighingBillOperationRecordMapper wbrMapper;

	@Autowired
	private WeighingStatementMapper weighingStatementMapper;

	@Override
	public BaseOutput<WeighingStatement> addWeighingBill(WeighingBill weighingBill) {
		BaseOutput<String> output = this.uidRpc.getCode();
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return BaseOutput.failure("生成过磅单编号失败");
		}
		weighingBill.setSerialNo(output.getData());
		weighingBill.setState(WeighingBillState.NO_SETTLEMENT.getValue());

		// 根据卡号查询账户信息
		// 查询买家账户信息
		this.setWeighingBillBuyerInfo(weighingBill);
		// 查询卖家账户信息
		this.setWeighingBillSellerInfo(weighingBill);

		int rows = this.getActualDao().insertSelective(weighingBill);
		if (rows <= 0) {
			throw new AppException("保存过磅单失败");
		}

		Long firmId = this.getMarketIdByOperatorId(weighingBill.getCreatorId());
		WeighingStatement ws = this.buildWeighingStatement(weighingBill, firmId);
		rows = this.weighingStatementMapper.insertSelective(ws);
		if (rows <= 0) {
			throw new AppException("保存结算单失败");
		}

		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.WEIGH.getValue());
		wbor.setOperationTypeName(WeighingOperationType.WEIGH.getName());
		wbor.setOperatorId(weighingBill.getCreatorId());
		wbor.setOperatorName(this.getUserRealNameById(weighingBill.getCreatorId()));
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return rows > 0 ? BaseOutput.success().setData(output.getData()) : BaseOutput.failure("保存过磅单失败");
	}

	@Override
	public BaseOutput<Object> close(String serialNo) {
		WeighingBill query = DTOUtils.newInstance(WeighingBill.class);
		WeighingBill weighingBill = this.getActualDao().selectOne(query);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			return BaseOutput.failure("当前状态不能关闭");
		}
		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.CLOSED.getValue());
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		return rows > 0 ? BaseOutput.success() : BaseOutput.failure("更新过磅单状态失败");
	}

	@Override
	public WeighingBillDetailDto detail(Long id) {
		return this.getActualDao().selectDetailById(id);
	}

	@Override
	public BaseOutput<Object> freeze(String serialNo, String buyerPassword, Long operatorId) {
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
			LOGGER.error(balanceOutput.getMessage());
			return BaseOutput.failure("查询买方余额失败");
		}
		if (balanceOutput.getData().getAvailableAmount() < weighingStatement.getFrozenAmount()) {
			return BaseOutput.failure("买方余额不足");
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
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		weighingStatement.setModifiedTime(now);
		weighingStatement.setState(WeighingStatementState.FROZEN.getValue());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.FREEZE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.FREEZE.getName());
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(this.getUserRealNameById(operatorId));
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
		srDto.setAccountId(weighingBill.getBuyerAccount());
		srDto.setAction(ActionType.EXPENSE.getCode());
		srDto.setAmount(data.getFrozenAmount());
		srDto.setCardNo(weighingBill.getBuyerCardNo());
		srDto.setCustomerId(weighingBill.getBuyerId());
		srDto.setCustomerName(weighingBill.getBuyerName());
		srDto.setCustomerNo(weighingBill.getBuyerCode());
		srDto.setEndBalance(data.getBalance() - (data.getFrozenBalance() + data.getFrozenAmount()));
		srDto.setFirmId(this.getMarketIdByOperatorId(operatorId));
		srDto.setFundItem(FundItem.TRADE_FREEZE.getCode());
		srDto.setFundItemName(FundItem.TRADE_FREEZE.getName());
		srDto.setOperateTime(now);
		srDto.setOperatorId(operatorId);
		srDto.setOperatorName(this.getUserRealNameById(operatorId));
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(Arrays.asList(srDto)));

		return BaseOutput.success();
	}

	public WeighingBillMapper getActualDao() {
		return (WeighingBillMapper) getDao();
	}

	@Override
	public WeighingBillPrintDto getWeighingBillPrintData(String serialNo) {
		WeighingBill weighingBill = this.getWeighingBillBySerialNo(serialNo);
		if (weighingBill == null) {
			return null;
		}
		WeighingBillPrintDto dto = BeanConver.copyBean(weighingBill, WeighingBillPrintDto.class);
		if (weighingBill.getModifierId() != null) {
			BaseOutput<User> output = this.userRpc.get(weighingBill.getModifierId());
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				throw new AppException("查询用户信息失败");
			}
			dto.setWeighingOperatorUserName(output.getData().getUserName());
			dto.setWeighingOperatorName(output.getData().getRealName());
		} else {
			BaseOutput<User> output = this.userRpc.get(weighingBill.getCreatorId());
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				throw new AppException("查询用户信息失败");
			}
			dto.setWeighingOperatorUserName(output.getData().getUserName());
			dto.setWeighingOperatorName(output.getData().getRealName());
		}
		return dto;
	}

	@Override
	public WeighingStatementPrintDto getWeighingStatementPrintData(String serialNo) {
		WeighingStatement weighingStatement = this.getWeighingStatementBySerialNo(serialNo);
		if (weighingStatement == null) {
			return null;
		}
		WeighingStatementPrintDto dto = BeanConver.copyBean(weighingStatement, WeighingStatementPrintDto.class);
		WeighingBillOperationRecord wborQuery = new WeighingBillOperationRecord();
		WeighingOperationType opType = WEIGHING_STATEMENT_STATE_MAPPING_OPERATION_TYPE_CONFIG.get(WeighingStatementState.valueOf(weighingStatement.getState()));
		wborQuery.setOperationType(opType.getValue());
		wborQuery.setWeighingBillId(weighingStatement.getWeighingBillId());
		List<WeighingBillOperationRecord> opList = this.wbrMapper.select(wborQuery);
		BaseOutput<User> output = this.userRpc.get(opList.get(0).getOperatorId());
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		dto.setWeighingOperatorUserName(output.getData().getUserName());
		dto.setWeighingOperatorName(output.getData().getRealName());
		return dto;
	}

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
			return BaseOutput.failure("买方密码错误");
		}

		AccountPasswordValidateDto sellerPwdDto = new AccountPasswordValidateDto();
		sellerPwdDto.setAccountId(weighingBill.getSellerAccount());
		sellerPwdDto.setPassword(sellerPassword);
		pwdOutput = this.payRpc.validateAccountPassword(sellerPwdDto);
		if (!pwdOutput.isSuccess()) {
			return BaseOutput.failure("卖方密码错误");
		}

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.INVALIDATED.getValue());
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}
		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.INVALIDATE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.INVALIDATE.getName());
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(this.getUserRealNameById(operatorId));
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
			this.recordUnfreezeAccountFlow(operatorId, weighingBill, paymentOutput.getData());
		}

		return BaseOutput.success();
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<WeighingBillListPageDto> listByExampleModified(WeighingBillQueryDto weighingBill) {
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

	@Override
	public BaseOutput<Object> operatorInvalidate(Long id, Long operatorId, String operatorPassword) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		// 校验操作员密码
		BaseOutput<Object> userOutput = this.userRpc.validatePassword(operatorId, operatorPassword);
		if (!userOutput.isSuccess()) {
			return BaseOutput.failure("操作员密码错误");
		}

		Integer beforeUpdateState = weighingBill.getState();

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.INVALIDATED.getValue());
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}
		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.INVALIDATE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.INVALIDATE.getName());
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(this.getUserRealNameById(operatorId));
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
			this.recordUnfreezeAccountFlow(operatorId, weighingBill, paymentOutput.getData());
		}
		return BaseOutput.success();
	}

	/**
	 * json 修改，再调用方法buildWeighingStatement的时候，重新创建了一个结算单，并且设置了PayOrderNo
	 * 在调用recordWithdrawAccountFlow的时候，卖家信息，在第一个streams中获取，买家信息在relation.streams中获取，按照刚哥的说法。
	 * 不知道是否存在问题，目前是可以撤销成功
	 * 
	 * @param id               过磅id
	 * @param operatorId       操作员id
	 * @param operatorPassword 操作员登录密码
	 * @return
	 */
	@Override
	public BaseOutput<Object> operatorWithdraw(Long id, Long operatorId, String operatorPassword) {
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
			return BaseOutput.failure("当前状态不能撤销");
		}

		// 校验操作员密码
		BaseOutput<Object> pwdOutput = this.userRpc.validatePassword(operatorId, operatorPassword);
		if (!pwdOutput.isSuccess()) {
			return pwdOutput;
		}

		LocalDateTime now = LocalDateTime.now();
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifiedTime(now);
		int rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		weighingBill.setState(WeighingBillState.WITHDRAWN.getValue());
		weighingBill.setModifiedTime(now);
		rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.WITHDRAW.getValue());
		wbor.setOperationTypeName(WeighingOperationType.WITHDRAW.getName());
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(this.getUserRealNameById(operatorId));
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 重新生成一条结算单
		WeighingStatement newWeighingStatement = this.buildWeighingStatement(weighingBill, this.getMarketIdByOperatorId(operatorId));
		newWeighingStatement.setPayOrderNo(weighingStatement.getPayOrderNo());
		rows = this.weighingStatementMapper.insertSelective(newWeighingStatement);
		if (rows <= 0) {
			throw new AppException("创建结算单失败");
		}

		// 退款
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		cancelDto.setTradeId(weighingStatement.getPayOrderNo());
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
		if (!paymentOutput.isSuccess()) {
			LOGGER.error(paymentOutput.getMessage());
			throw new AppException("退款失败");
		}

		// 记录交易流水
		this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill);

		return BaseOutput.success();
	}

	@Override
	public BaseOutput<Object> settle(String serialNo, String buyerPassword, Long operatorId, Long marketId) {
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
			LOGGER.error(balanceOutput.getMessage());
			return BaseOutput.failure("查询买方余额失败");
		}
		if (balanceOutput.getData().getAvailableAmount() < weighingStatement.getBuyerActualAmount()) {
			return BaseOutput.failure("买方余额不足");
		}

		if (weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			BaseOutput<?> output = this.prepareTrade(weighingBill, weighingStatement);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return BaseOutput.failure("交易失败");
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
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		weighingStatement.setModifiedTime(now);
		weighingStatement.setState(WeighingStatementState.PAID.getValue());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.SETTLE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.SETTLE.getName());
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(this.getUserRealNameById(operatorId));
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
		this.recordSettlementAccountFlow(weighingBill, paymentOutput.getData(), operatorId);
		// 发送mq通知中间价计算模块计算中间价
		this.sendCalculateReferencePriceMessage(weighingBill, marketId, weighingStatement.getTradeAmount());
		return BaseOutput.success();
	}

	@Override
	public BaseOutput<Object> updateWeighingBill(WeighingBill dto) {
		WeighingBill weighingBill = this.getWeighingBillBySerialNo(dto.getSerialNo());
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能修改过磅单");
		}
		this.updateWeihingBillInfo(weighingBill, dto);
		// 查询未结算单
		Example example = new Example(WeighingStatement.class);
		example.createCriteria().andIn("state", Arrays.asList(WeighingStatementState.FROZEN.getValue(), WeighingStatementState.UNPAID.getValue())).andEqualTo("weighingSerialNo",
				weighingBill.getSerialNo());
		WeighingStatement ws = this.weighingStatementMapper.selectOneByExample(example);
		if (ws == null) {
			return BaseOutput.failure("未找到结算单");
		}

		// 判断重复冻结
		if (this.isRepeatFreeze(weighingBill, ws)) {
			return BaseOutput.failure("不能重复冻结");
		}

		Long marketId = this.getMarketIdByOperatorId(dto.getModifierId());

		if (ws.getState().equals(WeighingStatementState.UNPAID.getValue())) {
			// 修改状态是“未结算”单据时，卖方信息不可修改，其它均可修改；->买方信息可以修改
			this.setWeighingBillBuyerInfo(weighingBill);
		} else {
			// 修改状态是“已冻结”单据时，“买方、卖方、毛重”不能修改；其它可以修改；->判断下毛重是否被修改
			if (this.isWeighingBillRoughWeightUpdated(weighingBill, dto)) {
				return BaseOutput.failure("冻结单毛重不能修改");
			}
		}
		// 更新结算单买卖家信息，重新算费用
		this.setWeighingStatementBuyerInfo(weighingBill, ws, marketId);
		this.setWeighingStatementSellerInfo(weighingBill, ws, marketId);

		int rows = this.getActualDao().updateByPrimaryKey(weighingBill);
		if (rows <= 0) {
			throw new AppException("更新过磅单失败");
		}
		ws.setModifiedTime(LocalDateTime.now());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(ws);

		// 插入一条过磅信息
		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.SETTLE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.SETTLE.getName());
		wbor.setOperatorId(dto.getModifierId());
		wbor.setOperatorName(this.getUserRealNameById(dto.getModifierId()));
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		return rows > 0 ? BaseOutput.success() : BaseOutput.failure("更新过磅单失败");
	}

	private boolean isWeighingBillRoughWeightUpdated(WeighingBill weighingBill, WeighingBill dto) {
		if (weighingBill.getRoughWeight() == null && dto.getRoughWeight() != null) {
			return true;
		}
		return !weighingBill.getRoughWeight().equals(dto.getRoughWeight());
	}

	private boolean isRepeatFreeze(WeighingBill weighingBill, WeighingStatement ws) {
		return this.isFreeze(weighingBill) && ws.getState().equals(WeighingStatementState.FROZEN.getValue());
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
		weighingBill.setUnitAmount(dto.getUnitAmount());
		weighingBill.setUnitPrice(dto.getUnitPrice());
		weighingBill.setUnitWeight(dto.getUnitWeight());
	}

	private void setWeighingStatementBuyerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		this.setWeighingStatementTradeAmount(weighingBill, ws);
		BaseOutput<QueryFeeOutput> buyerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE);
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException("计算买家手续费失败");
		}
		ws.setBuyerActualAmount(ws.getTradeAmount() - buyerFeeOutput.getData().getTotalFee().longValue());
		ws.setBuyerPoundage(buyerFeeOutput.getData().getTotalFee().longValue());
		ws.setBuyerCardNo(weighingBill.getBuyerCardNo());
		ws.setBuyerId(weighingBill.getBuyerId());
		ws.setBuyerName(weighingBill.getBuyerName());
	}

	private void setWeighingStatementTradeAmount(WeighingBill weighingBill, WeighingStatement ws) {
		if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			ws.setTradeAmount(weighingBill.getNetWeight() * weighingBill.getUnitPrice() * 2 / 100);
		} else {
			ws.setTradeAmount(weighingBill.getUnitAmount() * weighingBill.getUnitPrice() / 100);
		}
	}

	private void setWeighingStatementFrozenAmount(WeighingBill weighingBill, WeighingStatement ws) {
		ws.setFrozenAmount(weighingBill.getEstimatedNetWeight() * weighingBill.getUnitPrice() / 100);
	}

	private void setWeighingBillBuyerInfo(WeighingBill weighingBill) {
		// 根据卡号查询账户信息
		// 查询买家账户信息
		CardQueryDto buyerQueryDto = new CardQueryDto();
		buyerQueryDto.setCardNo(weighingBill.getBuyerCardNo());
		BaseOutput<UserAccountCardResponseDto> buyerOutput = this.accountRpc.getSingle(buyerQueryDto);
		if (!buyerOutput.isSuccess()) {
			LOGGER.error(buyerOutput.getMessage());
			throw new AppException("查询买家信息失败");
		}
		// 设置买家信息
		weighingBill.setBuyerAccount(buyerOutput.getData().getFundAccountId());
		weighingBill.setBuyerId(buyerOutput.getData().getCustomerId());
		weighingBill.setBuyerCode(buyerOutput.getData().getCustomerCode());
		weighingBill.setBuyerName(buyerOutput.getData().getCustomerName());
		weighingBill.setBuyerContact(buyerOutput.getData().getCustomerContactsPhone());
		weighingBill.setBuyerCardAccount(buyerOutput.getData().getAccountId());
		weighingBill.setBuyerType(buyerOutput.getData().getCustomerMarketType());
	}

	private void setWeighingStatementSellerInfo(WeighingBill weighingBill, WeighingStatement ws, Long marketId) {
		this.setWeighingStatementTradeAmount(weighingBill, ws);
		BaseOutput<QueryFeeOutput> sellerFeeOutput = this.calculatePoundage(weighingBill, ws, marketId, OrdersConstant.WEIGHING_BILL_SELLER_POUNDAGE_BUSINESS_TYPE);
		if (!sellerFeeOutput.isSuccess()) {
			throw new AppException("计算卖家手续费失败");
		}
		ws.setSellerActualAmount(ws.getTradeAmount() - sellerFeeOutput.getData().getTotalFee().longValue());
		ws.setSellerPoundage(sellerFeeOutput.getData().getTotalFee().longValue());
		ws.setSellerCardNo(weighingBill.getSellerCardNo());
		ws.setSellerId(weighingBill.getSellerId());
		ws.setSellerName(weighingBill.getSellerName());
	}

	private void setWeighingBillSellerInfo(WeighingBill weighingBill) {
		// 查询卖家账户信息
		CardQueryDto sellerQueryDto = new CardQueryDto();
		sellerQueryDto.setCardNo(weighingBill.getSellerCardNo());
		BaseOutput<UserAccountCardResponseDto> sellerOutput = this.accountRpc.getSingle(sellerQueryDto);
		if (!sellerOutput.isSuccess()) {
			LOGGER.error(sellerOutput.getMessage());
			throw new AppException("查询卖家账户信息失败");
		}
		// 设置卖家信息
		weighingBill.setSellerAccount(sellerOutput.getData().getFundAccountId());
		weighingBill.setSellerId(sellerOutput.getData().getCustomerId());
		weighingBill.setSellerCode(sellerOutput.getData().getCustomerCode());
		weighingBill.setSellerName(sellerOutput.getData().getCustomerName());
		weighingBill.setSellerContact(sellerOutput.getData().getCustomerContactsPhone());
		weighingBill.setSellerCardAccount(sellerOutput.getData().getAccountId());
		weighingBill.setSellerType(sellerOutput.getData().getCustomerMarketType());
	}

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
			return BaseOutput.failure("买方密码错误");
		}

		AccountPasswordValidateDto sellerPwdDto = new AccountPasswordValidateDto();
		sellerPwdDto.setAccountId(weighingBill.getSellerAccount());
		sellerPwdDto.setPassword(sellerPassword);
		pwdOutput = this.payRpc.validateAccountPassword(sellerPwdDto);
		if (!pwdOutput.isSuccess()) {
			return BaseOutput.failure("卖方密码错误");
		}

		LocalDateTime now = LocalDateTime.now();
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifiedTime(now);
		int rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}

		weighingBill.setState(WeighingBillState.NO_SETTLEMENT.getValue());
		weighingBill.setModifiedTime(now);
		rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}
		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.WITHDRAW.getValue());
		wbor.setOperationTypeName(WeighingOperationType.WITHDRAW.getName());
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(this.getUserRealNameById(operatorId));
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 重新生成一条结算单
		WeighingStatement newWs = this.buildWeighingStatement(weighingBill, this.getMarketIdByOperatorId(operatorId));
		newWs.setState(WeighingStatementState.UNPAID.getValue());
		rows = this.weighingStatementMapper.insertSelective(newWs);
		if (rows <= 0) {
			throw new AppException("创建结算单失败");
		}

		// 退款
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		cancelDto.setTradeId(weighingStatement.getPayOrderNo());
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
		if (!paymentOutput.isSuccess()) {
			LOGGER.error(paymentOutput.getMessage());
			throw new AppException("退款失败");
		}

		// 记录撤销交易流水
		this.recordWithdrawAccountFlow(operatorId, paymentOutput.getData(), weighingBill);

		return BaseOutput.success();
	}

	private WeighingStatement buildWeighingStatement(WeighingBill weighingBill, Long marketId) {
		BaseOutput<String> output = this.uidRpc.getCode();
		if (!output.isSuccess()) {
			throw new AppException(output.getMessage());
		}
		WeighingStatement ws = new WeighingStatement();
		ws.setSerialNo(output.getData());
		ws.setWeighingBillId(weighingBill.getId());
		ws.setWeighingSerialNo(weighingBill.getSerialNo());

		if (this.isFreeze(weighingBill)) {
			this.setWeighingStatementFrozenAmount(weighingBill, ws);
		}
		this.setWeighingStatementBuyerInfo(weighingBill, ws, marketId);
		this.setWeighingStatementSellerInfo(weighingBill, ws, marketId);
		ws.setState(WeighingStatementState.UNPAID.getValue());
		return ws;

	}

	private BaseOutput<QueryFeeOutput> calculatePoundage(WeighingBill weighingBill, WeighingStatement statement, Long marketId, String businessType) {
		QueryFeeInput queryFeeInput = new QueryFeeInput();
		Map<String, Object> map = new HashMap<>();
		// 设置市场id
		queryFeeInput.setMarketId(marketId);
		// 设置业务类型
		queryFeeInput.setBusinessType(businessType);
		Long chargeItemId = this.getBuyerSellerRuleId(businessType, marketId);
		queryFeeInput.setChargeItem(chargeItemId);
		if (businessType.equals(OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE)) {
			map.put("customerType", weighingBill.getBuyerType());
		} else {
			map.put("customerType", weighingBill.getSellerType());
		}
		LocalDateTime tradeTime = statement.getCreatedTime();
		if (tradeTime == null) {
			tradeTime = LocalDateTime.now();
		}
		map.put("tradeTime", tradeTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		map.put("unitPrice", weighingBill.getUnitPrice());
		if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
			map.put("totalWeight", weighingBill.getNetWeight());
		} else {
			map.put("totalWeight", weighingBill.getUnitAmount() * weighingBill.getUnitWeight());
		}
		map.put("tradeType", weighingBill.getTradeType());
		map.put("tradeAmount", statement.getTradeAmount());
		queryFeeInput.setCalcParams(map);
		queryFeeInput.setConditionParams(map);
		return chargeRuleRpc.queryFee(queryFeeInput);
	}

	private boolean checkPrice(WeighingBill wb) {
		if (!wb.getCheckPrice()) {
			return false;
		}
		if (wb.getPriceState() != null) {
			return false;
		}
//		this.referencePriceService.getReferencePriceByGoodsId(wb.getGoodsId(), wb)
		return false;
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

	private Long getBuyerSellerRuleId(String businessType, Long marketId) {
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
			LOGGER.error(listBaseOutput.getMessage());
			throw new AppException("查询买家和卖家手续费规则失败");
		}
		if (CollectionUtils.isEmpty(listBaseOutput.getData())) {
			return null;
		}
		return listBaseOutput.getData().get(0).getId();
	}

	private Long getMarketIdByOperatorId(Long operatorId) {
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		BaseOutput<Firm> firmOutput = this.firmRpc.getByCode(output.getData().getFirmCode());
		if (!firmOutput.isSuccess()) {
			throw new AppException("获取市场id失败");
		}
		return firmOutput.getData().getId();
	}

	private WeighingStatement getNoSettlementWeighingStatementByWeighingBillId(Long weighingBillId) {
		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setWeighingBillId(weighingBillId);
		wsQuery.setState(WeighingStatementState.UNPAID.getValue());
		WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
		return weighingStatement;
	}

	private String getUserRealNameById(Long operatorId) {
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		return output.getData().getRealName();
	}

	private WeighingBill getWeighingBillBySerialNo(String serialNo) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		return this.getActualDao().selectOne(query);
	}

	private WeighingStatement getWeighingStatementBySerialNo(String serialNo) {
		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setSerialNo(serialNo);
		return this.weighingStatementMapper.selectOne(wsQuery);
	}

	// 判断是否需要走冻结流程
	private boolean isFreeze(WeighingBill weighingBill) {
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

	private BaseOutput<?> prepareTrade(WeighingBill weighingBill, WeighingStatement ws) {
		// 创建支付订单
		PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
		prepareDto.setAccountId(weighingBill.getSellerAccount());
		prepareDto.setAmount(ws.getTradeAmount());
		prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
		prepareDto.setSerialNo(weighingBill.getSerialNo());
		prepareDto.setType(PaymentTradeType.TRADE.getValue());
		BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
		if (!paymentOutput.isSuccess()) {
			return paymentOutput;
		}
		ws.setPayOrderNo(paymentOutput.getData().getTradeId());
		ws.setState(WeighingStatementState.PAID.getValue());
		return paymentOutput;
	}

	private void recordSettlementAccountFlow(WeighingBill weighingBill, PaymentTradeCommitResponseDto paymentResult, Long operatorId) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		// 买家入账
		// 解冻
		SerialRecordDo frozenRecord = new SerialRecordDo();
		frozenRecord.setAccountId(weighingBill.getBuyerAccount());
		frozenRecord.setAction(ActionType.INCOME.getCode());
		frozenRecord.setAmount(-paymentResult.getFrozenAmount());
		frozenRecord.setCardNo(weighingBill.getBuyerCardNo());
		frozenRecord.setCustomerId(weighingBill.getBuyerId());
		frozenRecord.setCustomerName(weighingBill.getBuyerName());
		frozenRecord.setCustomerNo(weighingBill.getBuyerCode());
		frozenRecord.setEndBalance(paymentResult.getBalance() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
		frozenRecord.setFirmId(this.getMarketIdByOperatorId(operatorId));
		frozenRecord.setFundItem(FundItem.TRADE_UNFREEZE.getCode());
		frozenRecord.setFundItemName(FundItem.TRADE_UNFREEZE.getName());
		LocalDateTime now = LocalDateTime.now();
		frozenRecord.setOperateTime(now);
		frozenRecord.setOperatorId(operatorId);
		frozenRecord.setOperatorName(this.getUserRealNameById(operatorId));
		frozenRecord.setNotes("交易解冻");
		srList.add(frozenRecord);
		// 买家支出
		SerialRecordDo buyerExpense = new SerialRecordDo();
		buyerExpense.setAccountId(weighingBill.getBuyerAccount());
		buyerExpense.setAction(ActionType.EXPENSE.getCode());
		buyerExpense.setAmount(-paymentResult.getStreams().get(0).getAmount());
		buyerExpense.setCardNo(weighingBill.getBuyerCardNo());
		buyerExpense.setCustomerId(weighingBill.getBuyerId());
		buyerExpense.setCustomerName(weighingBill.getBuyerName());
		buyerExpense.setCustomerNo(weighingBill.getBuyerCode());
		buyerExpense
				.setEndBalance(paymentResult.getStreams().get(0).getBalance() + paymentResult.getStreams().get(0).getAmount() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
		buyerExpense.setFirmId(this.getMarketIdByOperatorId(operatorId));
		buyerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		buyerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		buyerExpense.setOperateTime(now);
		buyerExpense.setOperatorId(operatorId);
		buyerExpense.setOperatorName(this.getUserRealNameById(operatorId));
		buyerExpense.setNotes("买家交易支出");
		srList.add(buyerExpense);
		// 买家手续费
		SerialRecordDo buyerPoundage = new SerialRecordDo();
		buyerPoundage.setAccountId(weighingBill.getBuyerAccount());
		buyerPoundage.setAction(ActionType.EXPENSE.getCode());
		buyerPoundage.setAmount(-paymentResult.getStreams().get(1).getAmount());
		buyerPoundage.setCardNo(weighingBill.getBuyerCardNo());
		buyerPoundage.setCustomerId(weighingBill.getBuyerId());
		buyerPoundage.setCustomerName(weighingBill.getBuyerName());
		buyerPoundage.setCustomerNo(weighingBill.getBuyerCode());
		buyerPoundage
				.setEndBalance(paymentResult.getStreams().get(1).getBalance() + paymentResult.getStreams().get(1).getAmount() - (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
		buyerPoundage.setFirmId(this.getMarketIdByOperatorId(operatorId));
		buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		buyerPoundage.setOperateTime(now);
		buyerPoundage.setOperatorId(operatorId);
		buyerPoundage.setOperatorName(this.getUserRealNameById(operatorId));
		buyerPoundage.setNotes("买家手续费");
		srList.add(buyerPoundage);
		// 卖家收入
		SerialRecordDo sellerIncome = new SerialRecordDo();
		sellerIncome.setAccountId(weighingBill.getSellerAccount());
		sellerIncome.setAction(ActionType.INCOME.getCode());
		sellerIncome.setAmount(paymentResult.getRelation().getStreams().get(0).getAmount());
		sellerIncome.setCardNo(weighingBill.getSellerCardNo());
		sellerIncome.setCustomerId(weighingBill.getSellerId());
		sellerIncome.setCustomerName(weighingBill.getSellerName());
		sellerIncome.setCustomerNo(weighingBill.getSellerCode());
		sellerIncome.setEndBalance(paymentResult.getRelation().getStreams().get(0).getBalance() + paymentResult.getRelation().getStreams().get(0).getAmount()
				- (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
		sellerIncome.setFirmId(this.getMarketIdByOperatorId(operatorId));
		sellerIncome.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		sellerIncome.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		sellerIncome.setOperateTime(now);
		sellerIncome.setOperatorId(operatorId);
		sellerIncome.setOperatorName(this.getUserRealNameById(operatorId));
		sellerIncome.setNotes("买家交易支出");
		srList.add(sellerIncome);
		// 卖家手续费
		SerialRecordDo sellerPoundage = new SerialRecordDo();
		sellerIncome.setAccountId(weighingBill.getSellerAccount());
		sellerPoundage.setAction(ActionType.EXPENSE.getCode());
		sellerPoundage.setAmount(-paymentResult.getStreams().get(1).getAmount());
		sellerPoundage.setCardNo(weighingBill.getSellerCardNo());
		sellerPoundage.setCustomerId(weighingBill.getSellerId());
		sellerPoundage.setCustomerName(weighingBill.getSellerName());
		sellerPoundage.setCustomerNo(weighingBill.getSellerCode());
		sellerPoundage.setEndBalance(paymentResult.getRelation().getStreams().get(1).getBalance() + paymentResult.getRelation().getStreams().get(1).getAmount()
				- (paymentResult.getFrozenAmount() + paymentResult.getFrozenBalance()));
		sellerPoundage.setFirmId(this.getMarketIdByOperatorId(operatorId));
		sellerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		sellerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		sellerPoundage.setOperateTime(now);
		sellerPoundage.setOperatorId(operatorId);
		sellerPoundage.setOperatorName(this.getUserRealNameById(operatorId));
		sellerPoundage.setNotes("卖家手续费");
		srList.add(sellerPoundage);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordUnfreezeAccountFlow(Long operatorId, WeighingBill weighingBill, PaymentTradeCommitResponseDto tradeResponse) {
		// 解冻
		LocalDateTime now = LocalDateTime.now();
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		SerialRecordDo frozenRecord = new SerialRecordDo();
		frozenRecord.setAccountId(weighingBill.getBuyerAccount());
		frozenRecord.setAction(ActionType.INCOME.getCode());
		frozenRecord.setAmount(-tradeResponse.getFrozenAmount());
		frozenRecord.setCardNo(weighingBill.getBuyerCardNo());
		frozenRecord.setCustomerId(weighingBill.getBuyerId());
		frozenRecord.setCustomerName(weighingBill.getBuyerName());
		frozenRecord.setCustomerNo(weighingBill.getBuyerCode());
		frozenRecord.setEndBalance(tradeResponse.getBalance() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance()));
		frozenRecord.setFirmId(this.getMarketIdByOperatorId(operatorId));
		frozenRecord.setFundItem(FundItem.TRADE_UNFREEZE.getCode());
		frozenRecord.setFundItemName(FundItem.TRADE_UNFREEZE.getName());
		frozenRecord.setOperateTime(now);
		frozenRecord.setOperatorId(operatorId);
		frozenRecord.setOperatorName(this.getUserRealNameById(operatorId));
		frozenRecord.setNotes("交易解冻");
		srList.add(frozenRecord);
		this.mqService.send(RabbitMQConfig.EXCHANGE_ACCOUNT_SERIAL, RabbitMQConfig.ROUTING_ACCOUNT_SERIAL, JSON.toJSONString(srList));
	}

	private void recordWithdrawAccountFlow(Long operatorId, PaymentTradeCommitResponseDto tradeResponse, WeighingBill weighingBill) {
		List<SerialRecordDo> srList = new ArrayList<SerialRecordDo>();
		Long firmId = this.getMarketIdByOperatorId(operatorId);
		String operatorName = this.getUserRealNameById(operatorId);
		LocalDateTime now = LocalDateTime.now();
		// 卖家手续费
//        PaymentStream sellerPoundageStream = tradeResponse.getRelation().getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
		PaymentStream sellerPoundageStream = tradeResponse.getStreams().stream().filter(s -> s.getType() == 2).findFirst().orElse(null);
		SerialRecordDo sellerRefound = new SerialRecordDo();
		sellerRefound.setAccountId(weighingBill.getSellerAccount());
		sellerRefound.setAction(ActionType.INCOME.getCode());
		sellerRefound.setAmount(sellerPoundageStream.getAmount());
		sellerRefound.setCardNo(weighingBill.getSellerCardNo());
		sellerRefound.setCustomerId(weighingBill.getSellerId());
		sellerRefound.setCustomerName(weighingBill.getSellerName());
		sellerRefound.setCustomerNo(weighingBill.getSellerCode());
		sellerRefound.setEndBalance(sellerPoundageStream.getBalance() + sellerPoundageStream.getAmount() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance()));
		sellerRefound.setFirmId(firmId);
		sellerRefound.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		sellerRefound.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		sellerRefound.setOperateTime(now);
		sellerRefound.setOperatorId(operatorId);
		sellerRefound.setOperatorName(operatorName);
		sellerRefound.setNotes("卖家手续费退款");
		srList.add(sellerRefound);
		// 卖家退款
//        PaymentStream sellerExpenseStream = tradeResponse.getRelation().getStreams().stream().filter(s -> s.getType() == 2).findFirst().orElse(null);
		PaymentStream sellerExpenseStream = tradeResponse.getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
		SerialRecordDo sellerExpense = new SerialRecordDo();
		sellerExpense.setAccountId(weighingBill.getSellerAccount());
		sellerExpense.setAction(ActionType.EXPENSE.getCode());
		sellerExpense.setAmount(-sellerExpenseStream.getAmount());
		sellerExpense.setCardNo(weighingBill.getSellerCardNo());
		sellerExpense.setCustomerId(weighingBill.getSellerId());
		sellerExpense.setCustomerName(weighingBill.getSellerName());
		sellerExpense.setCustomerNo(weighingBill.getSellerCode());
		sellerExpense.setEndBalance(sellerExpenseStream.getBalance() + sellerExpenseStream.getAmount() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance()));
		sellerExpense.setFirmId(firmId);
		sellerExpense.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		sellerExpense.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		sellerExpense.setOperateTime(now);
		sellerExpense.setOperatorId(operatorId);
		sellerExpense.setOperatorName(operatorName);
		sellerExpense.setNotes("卖家退款");
		srList.add(sellerExpense);

		// 买家手续费
		PaymentStream buyerPoundageStream = tradeResponse.getRelation().getStreams().stream().filter(s -> s.getType() == 1).findFirst().orElse(null);
		SerialRecordDo buyerPoundage = new SerialRecordDo();
		buyerPoundage.setAccountId(weighingBill.getBuyerAccount());
		buyerPoundage.setAction(ActionType.INCOME.getCode());
		buyerPoundage.setAmount(-buyerPoundageStream.getAmount());
		buyerPoundage.setCardNo(weighingBill.getBuyerCardNo());
		buyerPoundage.setCustomerId(weighingBill.getBuyerId());
		buyerPoundage.setCustomerName(weighingBill.getBuyerName());
		buyerPoundage.setCustomerNo(weighingBill.getBuyerCode());
		buyerPoundage.setEndBalance(buyerPoundageStream.getBalance() + buyerPoundageStream.getAmount() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance()));
		buyerPoundage.setFirmId(this.getMarketIdByOperatorId(operatorId));
		buyerPoundage.setFundItem(FundItem.TRADE_SERVICE_FEE.getCode());
		buyerPoundage.setFundItemName(FundItem.TRADE_SERVICE_FEE.getName());
		buyerPoundage.setOperateTime(now);
		buyerPoundage.setOperatorId(operatorId);
		buyerPoundage.setOperatorName(this.getUserRealNameById(operatorId));
		buyerPoundage.setNotes("买家手续费退款");
		srList.add(buyerPoundage);
		// 买家退款
		PaymentStream buyerRefundStream = tradeResponse.getRelation().getStreams().stream().filter(s -> s.getType() == 0).findFirst().orElse(null);
		SerialRecordDo buyerRefund = new SerialRecordDo();
		buyerRefund.setAccountId(weighingBill.getBuyerAccount());
		buyerRefund.setAction(ActionType.INCOME.getCode());
		buyerRefund.setAmount(-buyerRefundStream.getAmount());
		buyerRefund.setCardNo(weighingBill.getBuyerCardNo());
		buyerRefund.setCustomerId(weighingBill.getBuyerId());
		buyerRefund.setCustomerName(weighingBill.getBuyerName());
		buyerRefund.setCustomerNo(weighingBill.getBuyerCode());
		buyerRefund.setEndBalance(buyerRefundStream.getBalance() + buyerRefundStream.getAmount() - (tradeResponse.getFrozenAmount() + tradeResponse.getFrozenBalance()));
		buyerRefund.setFirmId(firmId);
		buyerRefund.setFundItem(FundItem.TRADE_PAYMENT.getCode());
		buyerRefund.setFundItemName(FundItem.TRADE_PAYMENT.getName());
		buyerRefund.setOperateTime(now);
		buyerRefund.setOperatorId(operatorId);
		buyerRefund.setOperatorName(operatorName);
		buyerRefund.setNotes("买家退款");
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
		// 设置重量
		map.put("netWeight", String.valueOf(weighingBill.getNetWeight()));
		// 设置结算日期
		map.put("settlementTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
		// 设置交易金额
		map.put("tradeAmount", String.valueOf(tradeAmount));
		// mq发送消息
		this.mqService.send(WeighingBillMQConfig.EXCHANGE_REFERENCE_PRICE_CHANGE, WeighingBillMQConfig.ROUTING_REFERENCE_PRICE_CHANGE, JSON.toJSONString(map));
	}

}