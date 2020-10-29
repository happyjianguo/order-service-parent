package com.dili.orders.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.domain.PriceState;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillState;
import com.dili.orders.dto.PriceApproveRecordQueryDto;
import com.dili.orders.mapper.PriceApproveRecordMapper;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.service.PriceApproveRecordService;
import com.dili.orders.service.WeighingBillService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import com.github.pagehelper.PageHelper;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-08-30 14:29:35.
 */
@Service
public class PriceApproveRecordServiceImpl extends BaseServiceImpl<PriceApproveRecord, Long> implements PriceApproveRecordService {

	@Autowired
	private UserRpc userRpc;
	@Autowired
	private TaskRpc taskRpc;
	@Autowired
	private WeighingBillMapper weighingBillMapper;
	@Autowired
	private WeighingBillService weighingBillService;

	public PriceApproveRecordMapper getActualDao() {
		return (PriceApproveRecordMapper) getDao();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> accept(Long id, Long approverId, String notes, String taskId) {
		PriceApproveRecord approve = this.getActualDao().selectByPrimaryKey(id);
		if (approve == null) {
			return BaseOutput.failure("审批记录不存在");
		}
		if (!approve.getState().equals(PriceState.APPROVING.getValue())) {
			return BaseOutput.failure("当前状态不能审批");
		}
		WeighingBill wb = this.weighingBillMapper.selectByPrimaryKey(approve.getWeighingBillId());
		if (wb == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!wb.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !wb.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前过磅单状态不能进行审批操作");
		}
		approve.setState(PriceState.ACCEPTED.getValue());
		BaseOutput<User> userOutput = this.userRpc.get(approverId);
		if (!userOutput.isSuccess()) {
			LOGGER.error(userOutput.getMessage());
			return BaseOutput.failure("查询用户信息失败");
		}
		approve.setApproverId(approverId);
		approve.setNotes(notes);
		approve.setApproverName(userOutput.getData().getRealName());
		approve.setApproveTime(LocalDateTime.now());
		int rows = this.getActualDao().updateByPrimaryKeySelective(approve);
		if (rows <= 0) {
			return BaseOutput.failure("更新审批记录失败");
		}
		wb.setPriceState(PriceState.ACCEPTED.getValue());
		rows = this.weighingBillMapper.updateByPrimaryKeySelective(wb);
		if (rows <= 0) {
			throw new AppException("更新过磅单价格状态失败");
		}
		BaseOutput<String> output = this.taskRpc.complete(taskId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("流程实例执行失败");
		}

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, approve.getWeighingBillSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, approve.getWeighingBillId());
		LoggerContext.put("approveId", approve.getId());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, approverId);
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, approve.getMarketId());

		return BaseOutput.success();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public BaseOutput<Object> reject(Long id, Long approverId, String notes, String taskId) {
		PriceApproveRecord approve = this.getActualDao().selectByPrimaryKey(id);
		if (approve == null) {
			return BaseOutput.failure("审批记录不存在");
		}
		if (!approve.getState().equals(PriceState.APPROVING.getValue())) {
			return BaseOutput.failure("当前状态不能审批");
		}
		WeighingBill wb = this.weighingBillMapper.selectByPrimaryKey(approve.getWeighingBillId());
		if (wb == null) {
			return BaseOutput.failure("过磅单不存在");
		}
		if (!wb.getState().equals(WeighingBillState.NO_SETTLEMENT.getValue()) && !wb.getState().equals(WeighingBillState.FROZEN.getValue())) {
			return BaseOutput.failure("当前过磅单状态不能进行审批操作");
		}
		approve.setState(PriceState.REJECTED.getValue());
		BaseOutput<User> userOutput = this.userRpc.get(approverId);
		if (!userOutput.isSuccess()) {
			LOGGER.error(userOutput.getMessage());
			return BaseOutput.failure("查询用户信息失败");
		}
		approve.setApproverId(approverId);
		approve.setNotes(notes);
		approve.setApproverName(userOutput.getData().getRealName());
		approve.setApproveTime(LocalDateTime.now());
		int rows = this.getActualDao().updateByPrimaryKeySelective(approve);
		if (rows <= 0) {
			return BaseOutput.failure("更新审批记录失败");
		}
		wb.setPriceState(PriceState.REJECTED.getValue());
		rows = this.weighingBillMapper.updateByPrimaryKeySelective(wb);
		if (rows <= 0) {
			throw new AppException("更新过磅单价格状态失败");
		}
		BaseOutput<?> output = this.weighingBillService.operatorInvalidate(approve.getWeighingBillId(), approverId);
		if (!output.isSuccess()) {
			throw new AppException("作废过磅单失败");
		}
		output = this.taskRpc.complete(taskId);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			throw new AppException("流程实例执行失败");
		}

		// 记录日志系统
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, approve.getWeighingBillSerialNo());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, approve.getWeighingBillId());
		LoggerContext.put("approveId", approve.getId());
		LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, approverId);
		LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, approve.getMarketId());

		return BaseOutput.success();
	}

	@Override
	public List<PriceApproveRecord> listPageApp(PriceApproveRecordQueryDto query) {
		// 设置分页信息
		Integer page = query.getPage();
		page = (page == null) ? Integer.valueOf(1) : page;
		if (query.getRows() != null && query.getRows() >= 1) {
			// 为了线程安全,请勿改动下面两行代码的顺序
			PageHelper.startPage(page, query.getRows());
		}
		return this.getActualDao().listPageApp(query);
	}
}