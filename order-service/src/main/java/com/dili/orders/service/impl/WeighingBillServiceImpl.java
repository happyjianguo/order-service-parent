package com.dili.orders.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.customer.sdk.domain.Customer;
import com.dili.customer.sdk.domain.dto.CustomerQueryInput;
import com.dili.customer.sdk.rpc.CustomerRpc;
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
import com.dili.orders.dto.CreateTradeResponseDto;
import com.dili.orders.dto.FeeDto;
import com.dili.orders.dto.FeeType;
import com.dili.orders.dto.FeeUse;
import com.dili.orders.dto.PaymentTradeCommitDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;
import com.dili.orders.dto.PaymentTradePrepareDto;
import com.dili.orders.dto.PaymentTradeType;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingBillUpdateDto;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.mapper.WeighingBillOperationRecordMapper;
import com.dili.orders.mapper.WeighingStatementMapper;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.rpc.JmsfRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.WeighingBillService;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Transactional
@Service
public class WeighingBillServiceImpl extends BaseServiceImpl<WeighingBill, Long> implements WeighingBillService {

	@Autowired
	private AccountRpc accountRpc;
	@Autowired
	private CustomerRpc customerRpc;
	@Autowired
	private JmsfRpc jsmfRpc;
	@Autowired
	private PayRpc payRpc;
	@Autowired
	private UidRpc uidRpc;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private FirmRpc firmRpc;
	@Autowired
	private ChargeRuleRpc chargeRuleRpc;
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
		BaseOutput<UserAccountCardResponseDto> buyerOutput = this.accountRpc.getOneAccountCard(weighingBill.getBuyerCardNo());
		if (!buyerOutput.isSuccess()) {
			LOGGER.error(buyerOutput.getMessage());
			return BaseOutput.failure("查询买家账户信息失败");
		}
		// 查询卖家账户信息
		BaseOutput<UserAccountCardResponseDto> sellerOutput = this.accountRpc.getOneAccountCard(weighingBill.getSellerCardNo());
		if (!sellerOutput.isSuccess()) {
			LOGGER.error(buyerOutput.getMessage());
			return BaseOutput.failure("查询卖家账户信息失败");
		}

		// 查询买家卖家姓名
		CustomerQueryInput customerQuery = new CustomerQueryInput();
		customerQuery.setIdList(Arrays.asList(buyerOutput.getData().getCustomerId(), sellerOutput.getData().getCustomerId()));
		BaseOutput<Firm> firmOutput = this.firmRpc.getByCode(OrdersConstant.SHOUGUANG_FIRM_CODE);
		if (!firmOutput.isSuccess()) {
			return BaseOutput.failure("查询市场信息失败");
		}
		customerQuery.setMarketId(firmOutput.getData().getId());
		BaseOutput<List<Customer>> customerOutput = this.customerRpc.list(customerQuery);
		if (!customerOutput.isSuccess()) {
			LOGGER.error(customerOutput.getMessage());
			return BaseOutput.failure("查询客户信息信息失败");
		}
		Customer buyer = customerOutput.getData().stream().filter(c -> c.getId().equals(buyerOutput.getData().getCustomerId())).findFirst().get();
		Customer seller = customerOutput.getData().stream().filter(c -> c.getId().equals(sellerOutput.getData().getCustomerId())).findFirst().get();

		// 设置买家信息
		weighingBill.setBuyerAccount(buyerOutput.getData().getFundAccountId());
		weighingBill.setBuyerId(buyerOutput.getData().getCustomerId());
		weighingBill.setBuyerCode(buyer.getCode());
		weighingBill.setBuyerName(buyer.getName());
		weighingBill.setBuyerContact(buyer.getContactsPhone());
		weighingBill.setBuyerCardAccount(buyerOutput.getData().getAccountId());

		// 设置卖家信息
		weighingBill.setSellerAccount(sellerOutput.getData().getFundAccountId());
		weighingBill.setSellerId(sellerOutput.getData().getCustomerId());
		weighingBill.setSellerName(seller.getName());
		weighingBill.setSellerContact(seller.getContactsPhone());
		weighingBill.setSellerCardAccount(sellerOutput.getData().getAccountId());

		int rows = this.getActualDao().insertSelective(weighingBill);
		if (rows <= 0) {
			throw new AppException("保存过磅单失败");
		}

		WeighingStatement ws = this.buildWeighingStatement(weighingBill, firmOutput.getData().getId());
		ws.setState(WeighingStatementState.UNPAID.getValue());
		rows = this.weighingStatementMapper.insertSelective(ws);
		if (rows <= 0) {
			throw new AppException("保存结算单失败");
		}

		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.CREATE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.CREATE.getName());
		BaseOutput<User> opOutput = this.userRpc.get(weighingBill.getCreatorId());
		if (!opOutput.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		wbor.setOperatorId(opOutput.getData().getId());
		wbor.setOperatorName(opOutput.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return rows > 0 ? BaseOutput.success().setData(output.getData()) : BaseOutput.failure("保存过磅单失败");
	}

	private BaseOutput<QueryFeeOutput> calculaterPoundage(WeighingStatement statement, Long marketId, String businessType) {
		QueryFeeInput queryFeeInput = new QueryFeeInput();
		Map<String, Object> map = new HashMap<>();
		// 设置市场id
		queryFeeInput.setMarketId(marketId);
		// 设置业务类型
		queryFeeInput.setBusinessType(businessType);
		if (businessType.equals("WEIGHING_BILL_BUYER_POUNDAGE")) {
			queryFeeInput.setChargeItem(64L);
		} else {
			queryFeeInput.setChargeItem(65L);
		}
		map.put("tradeAmount", statement.getTradeAmount());
		queryFeeInput.setCalcParams(map);
		return chargeRuleRpc.queryFee(queryFeeInput);
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

	public WeighingBillMapper getActualDao() {
		return (WeighingBillMapper) getDao();
	}

	@Override
	public BaseOutput<Object> invalidate(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		WeighingBill weighingBill = this.getActualDao().selectOne(query);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
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
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		wbor.setOperatorId(output.getData().getId());
		wbor.setOperatorName(output.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return BaseOutput.success();
	}

	@Override
	public BaseOutput<Object> settle(String serialNo, String buyerPassword, Long operatorId) {
		WeighingBill query = new WeighingBill();
		query.setSerialNo(serialNo);
		WeighingBill weighingBill = this.getActualDao().selectOne(query);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}

		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前结算单状态不能进行支付操作");
		}

		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setWeighingBillId(weighingBill.getId());
		WeighingStatement weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
		if (weighingStatement == null) {
			return BaseOutput.failure("未查询到结算单信息");
		}

		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前状态不能进行结算操作");
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

		BaseOutput<?> output = null;
		if (weighingBill.getFrozenAmount() != null) {
			// 估计净重不为空，冻结交易
			output = this.freeze(weighingBill, weighingStatement);
		} else {
			output = this.prepareTrade(weighingBill, weighingStatement);
		}

		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return BaseOutput.failure("交易失败");
		}

		// 删除皮重单
		if (StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 删除皮重单
			BaseOutput<Object> jmsfOutput = this.jsmfRpc.removeTareNumber(Long.valueOf(weighingBill.getTareBillNumber()));
			if (!jmsfOutput.isSuccess()) {
				throw new AppException("删除过磅单失败");
			}
		}

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.SETTLED.getValue());
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		weighingStatement.setModifiedTime(now);
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}

		WeighingBillOperationRecord wbor = new WeighingBillOperationRecord();
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.SETTLE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.SETTLE.getName());
		BaseOutput<User> userOutput = this.userRpc.get(operatorId);
		if (!userOutput.isSuccess()) {
			LOGGER.error(userOutput.getMessage());
			throw new AppException("查询操作员信息失败");
		}
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(userOutput.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		BaseOutput<?> paymentOutput = this.commitTrade(weighingBill, weighingStatement, buyerPassword);
		if (!paymentOutput.isSuccess()) {
			LOGGER.error(paymentOutput.getMessage());
			throw new AppException("支付失败");
		}
		return BaseOutput.success();
	}

	@Override
	public BaseOutput<Object> updateWeighingBill(WeighingBillUpdateDto dto) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(dto.getId());
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			return BaseOutput.failure("当前状态不能修改过磅单");
		}
		BeanCopier copier = BeanCopier.create(WeighingBillUpdateDto.class, WeighingBill.class, false);
		copier.copy(dto, weighingBill, null);
		weighingBill.setModifiedTime(LocalDateTime.now());
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		BaseOutput<Firm> firmOutput = this.firmRpc.getByCode(OrdersConstant.SHOUGUANG_FIRM_CODE);
		if (!firmOutput.isSuccess()) {
			return BaseOutput.failure("查询市场信息失败");
		}
		WeighingStatement ws = this.buildWeighingStatement(weighingBill, firmOutput.getData().getId());
		rows = this.weighingStatementMapper.updateByPrimaryKeySelective(ws);

		return rows > 0 ? BaseOutput.success() : BaseOutput.failure("更新过磅单失败");
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
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		wbor.setOperatorId(output.getData().getId());
		wbor.setOperatorName(output.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 退款
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		cancelDto.setTradeId(weighingStatement.getPayOrderNo());
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
		if (!paymentOutput.isSuccess()) {
			LOGGER.error(paymentOutput.getMessage());
			throw new AppException("退款失败");
		}

		return BaseOutput.success();
	}

	private WeighingStatement buildWeighingStatement(WeighingBill weighingBill, Long marketId) {
		WeighingStatement wsQuery = new WeighingStatement();
		wsQuery.setWeighingBillId(weighingBill.getId());
		WeighingStatement ws = this.weighingStatementMapper.selectOne(wsQuery);
		if (ws == null) {
			ws = new WeighingStatement();
		}
		BaseOutput<String> output = this.uidRpc.getCode();
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		ws.setSerialNo(output.getData());
		ws.setWeighingBillId(weighingBill.getId());
		ws.setWeighingSerialNo(weighingBill.getSerialNo());

		if (weighingBill.getEstimatedNetWeight() != null) {
			ws.setFrozenAmount(weighingBill.getEstimatedNetWeight() * weighingBill.getUnitPrice());
		} else {
			if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
				ws.setTradeAmount(weighingBill.getNetWeight() * weighingBill.getUnitPrice() * 2 / 100);
			} else {
				ws.setTradeAmount(weighingBill.getUnitAmount() * weighingBill.getUnitPrice() / 100);
			}
		}
		BaseOutput<QueryFeeOutput> buyerFeeOutput = this.calculaterPoundage(ws, marketId, "WEIGHING_BILL_BUYER_POUNDAGE");
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException("计算买家手续费失败");
		}
		ws.setBuyerActualAmount(ws.getTradeAmount() + buyerFeeOutput.getData().getTotalFee().longValue());
		ws.setBuyerPoundage(buyerFeeOutput.getData().getTotalFee().longValue());
		BaseOutput<QueryFeeOutput> sellerFeeOutput = this.calculaterPoundage(ws, marketId, "WEIGHING_BILL_SELLER_POUNDAGE");
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException("计算卖家手续费失败");
		}
		ws.setSellerActualAmount(ws.getTradeAmount() - sellerFeeOutput.getData().getTotalFee().longValue());
		ws.setSellerPoundage(sellerFeeOutput.getData().getTotalFee().longValue());
		ws.setBuyerCardNo(weighingBill.getBuyerCardNo());
		ws.setBuyerId(weighingBill.getBuyerId());
		ws.setBuyerName(weighingBill.getBuyerName());
		ws.setSellerCardNo(weighingBill.getSellerCardNo());
		ws.setSellerId(weighingBill.getSellerId());
		ws.setSellerName(weighingBill.getSellerName());
		return ws;

	}

	// 冻结交易
	private BaseOutput<?> freeze(WeighingBill weighingBill, WeighingStatement weighingStatement) {
		Long buyerAmount = weighingBill.getEstimatedNetWeight() * weighingBill.getUnitPrice();
		// 创建支付订单
		PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
		prepareDto.setAccountId(weighingBill.getSellerAccount());
		prepareDto.setAmount(buyerAmount);
		prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
		prepareDto.setSerialNo(weighingBill.getSerialNo());
		prepareDto.setType(PaymentTradeType.PREAUTHORIZED.getValue());
		BaseOutput<CreateTradeResponseDto> paymentOutput = this.payRpc.prepareTrade(prepareDto);
		if (paymentOutput.isSuccess()) {
			weighingStatement.setPayOrderNo(paymentOutput.getData().getTradeId());
		}
		return paymentOutput;
	}

	private BaseOutput<?> prepareTrade(WeighingBill weighingBill, WeighingStatement ws) {
		// 直接结算
		Long buyerAmount = weighingBill.getNetWeight() * weighingBill.getUnitPrice();
		// 创建支付订单
		PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
		prepareDto.setAccountId(weighingBill.getSellerAccount());
		prepareDto.setAmount(buyerAmount);
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

	private BaseOutput<?> commitTrade(WeighingBill weighingBill, WeighingStatement weighingStatement, String password) {
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
		sellerFee.setAmount(weighingStatement.getBuyerPoundage());
		sellerFee.setType(FeeType.SELLER_POUNDAGE.getValue());
		sellerFee.setTypeName(FeeType.SELLER_POUNDAGE.getName());
		sellerFee.setUseFor(FeeUse.SELLER.getValue());
		fees.add(sellerFee);
		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.commitTrade(dto);
		return commitOutput;
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
	public WeighingBillDetailDto detail(Long id) {
		return this.getActualDao().selectDetailById(id);
	}

	@Override
	public BaseOutput<Object> operatorInvalidate(Long id, Long operatorId, String operatorPassword) {
		WeighingBill weighingBill = this.getActualDao().selectByPrimaryKey(id);
		if (weighingBill == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}

		// 校验操作员密码
		BaseOutput<Object> userOutput = this.userRpc.validatePassword(operatorId, operatorPassword);
		if (!userOutput.isSuccess()) {
			return BaseOutput.failure("操作员密码错误");
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
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		wbor.setOperatorId(output.getData().getId());
		wbor.setOperatorName(output.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return BaseOutput.success();
	}

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
			return BaseOutput.failure("当前状态不能作废");
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
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询用户信息失败");
		}
		wbor.setOperatorId(output.getData().getId());
		wbor.setOperatorName(output.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}

		// 退款
		PaymentTradeCommitDto cancelDto = new PaymentTradeCommitDto();
		cancelDto.setTradeId(weighingStatement.getPayOrderNo());
		BaseOutput<PaymentTradeCommitResponseDto> paymentOutput = this.payRpc.cancel(cancelDto);
		if (!paymentOutput.isSuccess()) {
			LOGGER.error(paymentOutput.getMessage());
			throw new AppException("退款失败");
		}

		return BaseOutput.success();
	}
}