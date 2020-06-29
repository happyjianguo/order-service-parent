package com.dili.order.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.order.domain.WeighingBill;
import com.dili.order.domain.WeighingBillOperationRecord;
import com.dili.order.domain.WeighingBillState;
import com.dili.order.domain.WeighingOperationType;
import com.dili.order.domain.WeighingStatement;
import com.dili.order.domain.WeighingStatementState;
import com.dili.order.dto.WeighingBillUpdateDto;
import com.dili.order.mapper.WeighingBillMapper;
import com.dili.order.mapper.WeighingBillOperationRecordMapper;
import com.dili.order.mapper.WeighingStatementMapper;
import com.dili.order.rpc.AccountRpc;
import com.dili.order.rpc.JmsfRpc;
import com.dili.order.rpc.UidRpc;
import com.dili.order.service.WeighingBillService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@Transactional
@Service
public class WeighingBillServiceImpl extends BaseServiceImpl<WeighingBill, Long> implements WeighingBillService {

//	@Autowired
//	private AccountRpc accountRpc;
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
		weighingBill.setBuyerId(1L);
		weighingBill.setBuyerAccount("123456789");
		weighingBill.setBuyerName("张三");
		weighingBill.setSellerId(2L);
		weighingBill.setSellerAccount("987654321");
		weighingBill.setSellerName("李四");
		int rows = this.getActualDao().insertSelective(weighingBill);
		if (rows > 0) {
			// 删除过磅单
//			this.jsmfRpc.removeTareNumber(weighingBill.getTareBillNumber());
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

		WeighingStatement weighingStatement = null;

		if (weighingBill.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue())) {
			// 创建结算单
			weighingStatement = this.buildWeighingStatement(weighingBill);
			// 请求支付系统
			BaseOutput<Object> output = this.payByWeighingStatement(weighingStatement, buyerPassword, sellerPassword);
			if (!output.isSuccess()) {
				return output;
			}
		} else if (weighingBill.getState().equals(WeighingBillState.FROZEN.getValue())) {
			// 创建结算单
			WeighingStatement wsQuery = new WeighingStatement();
			wsQuery.setWeighingBillId(weighingBill.getId());
			weighingStatement = this.weighingStatementMapper.selectOne(wsQuery);
			if (weighingStatement == null) {
				return BaseOutput.failure("查询结算单失败");
			}
			// 请求支付系统
			BaseOutput<Object> output = this.payByWeighingStatement(weighingStatement, buyerPassword, sellerPassword);
			if (!output.isSuccess()) {
				return output;
			}
		} else {
			return BaseOutput.failure("当前结算单状态不能进行支付操作");
		}

		LocalDateTime now = LocalDateTime.now();
		weighingBill.setState(WeighingBillState.SETTLED.getValue());
		weighingBill.setModifiedTime(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(weighingBill);
		if (rows <= 0) {
			return BaseOutput.failure("更新过磅单状态失败");
		}
		WeighingBillOperationRecord wbor = DTOUtils.newInstance(WeighingBillOperationRecord.class);
		wbor.setWeighingBillId(weighingBill.getId());
		wbor.setOperationType(WeighingOperationType.SETTLE.getValue());
		wbor.setOperationTypeName(WeighingOperationType.SETTLE.getName());
		BaseOutput<User> output = this.userRpc.get(operatorId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("查询操作员信息失败");
		}
		wbor.setOperatorId(operatorId);
		wbor.setOperatorName(output.getData().getRealName());
		rows = this.wbrMapper.insertSelective(wbor);
		if (rows <= 0) {
			throw new AppException("保存操作记录失败");
		}
		return BaseOutput.success();
	}

	private BaseOutput<Object> payByWeighingStatement(WeighingStatement weighingStatement, String buyerPassword, String sellerPassword) {
		// 支付系统未接入
		weighingStatement.setState(WeighingStatementState.PAID.getValue());
		int rows = 0;
		if (weighingStatement.getId() == null) {
			rows = this.weighingStatementMapper.insertSelective(weighingStatement);
		} else {
			rows = this.weighingStatementMapper.updateByPrimaryKeySelective(weighingStatement);
		}
		if (rows <= 0) {
			return BaseOutput.failure("更新结算单状态失败");
		}
		return BaseOutput.success();
	}

	private WeighingStatement buildWeighingStatement(WeighingBill weighingBill) {
		WeighingStatement ws = new WeighingStatement();
		BaseOutput<String> output = this.uidRpc.getCode();
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		ws.setSerialNo(output.getData());
		ws.setWeighingBillId(weighingBill.getId());
		ws.setWeighingSerialNo(weighingBill.getSerialNo());
		ws.setBuyerActualAmount(10000L);
		ws.setBuyerCardNo(weighingBill.getBuyerCardNo());
		ws.setBuyerId(weighingBill.getBuyerId());
		ws.setBuyerName(weighingBill.getBuyerName());
		ws.setBuyerPoundage(100L);
		ws.setPayOrderNo("12345678910");
		ws.setSellerActualAmount(10000L);
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