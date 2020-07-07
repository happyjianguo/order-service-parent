package com.dili.orders.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.customer.sdk.domain.Customer;
import com.dili.customer.sdk.domain.dto.CustomerQueryInput;
import com.dili.customer.sdk.rpc.CustomerRpc;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.domain.WeighingBillState;
import com.dili.orders.domain.WeighingOperationType;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.domain.WeighingStatementState;
import com.dili.orders.dto.AccountBalanceDto;
import com.dili.orders.dto.AccountRequestDto;
import com.dili.orders.dto.FeeDto;
import com.dili.orders.dto.FeeType;
import com.dili.orders.dto.FeeUse;
import com.dili.orders.dto.PaymentTradeCommitDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;
import com.dili.orders.dto.PaymentTradePrepareDto;
import com.dili.orders.dto.PaymentTradeType;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.orders.dto.WeighingBillUpdateDto;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.mapper.WeighingBillOperationRecordMapper;
import com.dili.orders.mapper.WeighingStatementMapper;
import com.dili.orders.service.WeighingBillService;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.rpc.JmsfRpc;
import com.dili.orders.rpc.PayRpc;
import com.dili.orders.rpc.UidRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Transactional
@Service
public class WeighingBillServiceImpl extends BaseServiceImpl<WeighingBill, Long> implements WeighingBillService {

	@Autowired
	private AccountRpc accountRpc;
	@Autowired
	private JmsfRpc jsmfRpc;
	@Autowired
	private WeighingBillOperationRecordMapper wbrMapper;
	@Autowired
	private WeighingStatementMapper weighingStatementMapper;
	@Autowired
	private UidRpc uidRpc;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private PayRpc payRpc;
	@Autowired
	private CustomerRpc customerRpc;

	public WeighingBillMapper getActualDao() {
		return (WeighingBillMapper) getDao();
	}

	@Override
	public BaseOutput<String> addWeighingBill(WeighingBill weighingBill) {
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
		BaseOutput<UserAccountCardResponseDto> sellerOutput = this.accountRpc.getOneAccountCard(weighingBill.getBuyerCardNo());
		if (!sellerOutput.isSuccess()) {
			LOGGER.error(buyerOutput.getMessage());
			return BaseOutput.failure("查询卖家账户信息失败");
		}

		// 查询买家卖家姓名
		CustomerQueryInput customerQuery = new CustomerQueryInput();
		customerQuery.setIdList(Arrays.asList(buyerOutput.getData().getCustomerId(), sellerOutput.getData().getCustomerId()));
		BaseOutput<List<Customer>> customerOutput = this.customerRpc.list(customerQuery);
		if (!customerOutput.isSuccess()) {
			LOGGER.error(customerOutput.getMessage());
			return BaseOutput.failure("查询客户信息信息失败");
		}
		String buyerName = customerOutput.getData().stream().filter(c -> c.getId().equals(buyerOutput.getData().getCustomerId())).findFirst().get().getName();
		String sellerName = customerOutput.getData().stream().filter(c -> c.getId().equals(sellerOutput.getData().getCustomerId())).findFirst().get().getName();

		// 设置买家信息
		weighingBill.setBuyerAccount(buyerOutput.getData().getFundAccountId());
		weighingBill.setBuyerId(buyerOutput.getData().getCustomerId());
		weighingBill.setBuyerName(buyerName);

		// 设置卖家信息
		weighingBill.setSellerAccount(sellerOutput.getData().getFundAccountId());
		weighingBill.setSellerId(sellerOutput.getData().getCustomerId());
		weighingBill.setSellerName(sellerName);

		int rows = this.getActualDao().insertSelective(weighingBill);
		if (rows > 0 && StringUtils.isNotBlank(weighingBill.getTareBillNumber())) {
			// 删除皮重单
			this.jsmfRpc.removeTareNumber(Long.valueOf(weighingBill.getTareBillNumber()));
		}
		return rows > 0 ? BaseOutput.success().setData(output.getData()) : BaseOutput.failure("保存过磅单失败");
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
		return rows > 0 ? BaseOutput.success() : BaseOutput.failure("更新过磅单失败");
	}

	@Override
	public BaseOutput<Object> settle(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
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
			return null;
		}
		if (balanceOutput.getData().getAvailableAmount() < weighingStatement.getBuyerActualAmount()) {
			weighingBill.setState(WeighingBillState.FROZEN.getValue());
			return BaseOutput.failure("买方余额不足");
		}

		BaseOutput<?> output = null;
		if (weighingBill.getFrozenAmount() != null) {
			// 估计净重不为空，冻结交易
			output = this.freeze(weighingBill, weighingStatement);
		} else {
			output = this.trade(weighingBill, weighingStatement, buyerPassword);
		}

		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return BaseOutput.failure("交易失败");
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
		return BaseOutput.success();
	}

	private BaseOutput<?> trade(WeighingBill weighingBill, WeighingStatement weighingStatement, String password) {
		// 直接结算
		Long buyerAmount = weighingBill.getNetWeight() * weighingBill.getUnitPrice();
		// 创建支付订单
		PaymentTradePrepareDto prepareDto = new PaymentTradePrepareDto();
		prepareDto.setAccountId(weighingBill.getSellerAccount());
		prepareDto.setAmount(buyerAmount);
		prepareDto.setBusinessId(weighingBill.getBuyerCardAccount());
		prepareDto.setSerialNo(weighingBill.getSerialNo());
		prepareDto.setType(PaymentTradeType.TRADE.getValue());
		BaseOutput<String> paymentOutput = this.payRpc.prepare(prepareDto);
		if (!paymentOutput.isSuccess()) {
			return paymentOutput;
		}
		// 提交支付订单
		PaymentTradeCommitDto dto = new PaymentTradeCommitDto();
		dto.setAccountId(weighingBill.getSellerAccount());
		dto.setPassword(password);
		dto.setTradeId(paymentOutput.getData());
		dto.setBusinessId(weighingBill.getSellerCardAccount());
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
		sellerFee.setUseFor(FeeUse.BUYER.getValue());
		fees.add(sellerFee);
		dto.setFees(fees);
		BaseOutput<PaymentTradeCommitResponseDto> commitOutput = this.payRpc.commit(dto);
		return commitOutput;
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
		BaseOutput<String> paymentOutput = this.payRpc.prepare(prepareDto);
		if (paymentOutput.isSuccess()) {
			weighingStatement.setPayOrderNo(paymentOutput.getData());
		}
		return paymentOutput;
	}

	private WeighingStatement calculateBuyerAndSellerPoundage(WeighingBill weighingBill) {
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
			ws.setBuyerActualAmount(ws.getFrozenAmount() + 100L);
			ws.setSellerActualAmount(ws.getFrozenAmount() - 100L);
		} else {
			if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
				ws.setTradeAmount(weighingBill.getNetWeight() * weighingBill.getUnitPrice());
			} else {
				ws.setTradeAmount(weighingBill.getUnitAmount() * weighingBill.getUnitWeight() * weighingBill.getUnitPrice() / 100);
			}
			ws.setBuyerActualAmount(ws.getTradeAmount() + 100L);
			ws.setSellerActualAmount(ws.getTradeAmount() - 100L);
		}
		ws.setBuyerCardNo(weighingBill.getBuyerCardNo());
		ws.setBuyerId(weighingBill.getBuyerId());
		ws.setBuyerName(weighingBill.getBuyerName());
		ws.setBuyerPoundage(100L);
		ws.setSellerCardNo(weighingBill.getSellerCardNo());
		ws.setSellerId(weighingBill.getSellerId());
		ws.setSellerName(weighingBill.getSellerName());
		ws.setSellerPoundage(100L);
		return ws;

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
			return BaseOutput.failure("结算端不存在");
		}
		LocalDate todayDate = LocalDate.now();
		LocalDateTime opTime = weighingStatement.getModifiedTime() == null ? weighingStatement.getCreatedTime() : weighingStatement.getModifiedTime();
		if (!todayDate.equals(opTime.toLocalDate())) {
			return BaseOutput.failure("只能对当日的过磅交易进行撤销操作");
		}
		if (!weighingBill.getState().equals(WeighingBillState.SETTLED.getValue())) {
			return BaseOutput.failure("当前状态不能作废");
		}
//		BaseOutput<Boolean> output = this.accountRpc.validateAccountPassword(weighingBill.getBuyerAccount(), buyerPassword);
//		if (!output.isSuccess()) {
//			return BaseOutput.failure("验证买家密码请求失败");
//		}
//		if (!output.getData()) {
//			return BaseOutput.failure("买家密码不正确");
//		}
//		output = this.accountRpc.validateAccountPassword(weighingBill.getSellerAccount(), sellerPassword);
//		if (!output.isSuccess()) {
//			return BaseOutput.failure("验证卖家密码请求失败");
//		}
//		if (!output.getData()) {
//			return BaseOutput.failure("卖家密码不正确");
//		}
		// 此处支付系统未接入

		LocalDateTime now = LocalDateTime.now();
		weighingStatement.setState(WeighingStatementState.REFUNDED.getValue());
		weighingStatement.setModifiedTime(now);
		this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);

		weighingBill.setState(WeighingBillState.WITHDRAWN.getValue());
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
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
		return BaseOutput.success();
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
//		BaseOutput<Boolean> output = this.accountRpc.validateAccountPassword(weighingBill.getBuyerAccount(), buyerPassword);
//		if (!output.isSuccess()) {
//			return BaseOutput.failure("验证买家密码请求失败");
//		}
//		if (!output.getData()) {
//			return BaseOutput.failure("买家密码不正确");
//		}
//		output = this.accountRpc.validateAccountPassword(weighingBill.getSellerAccount(), sellerPassword);
//		if (!output.isSuccess()) {
//			return BaseOutput.failure("验证卖家密码请求失败");
//		}
//		if (!output.getData()) {
//			return BaseOutput.failure("卖家密码不正确");
//		}
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
}