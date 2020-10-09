package com.dili.orders.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.domain.PriceState;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.mapper.PriceApproveRecordMapper;
import com.dili.orders.mapper.WeighingBillMapper;
import com.dili.orders.service.PriceApproveRecordService;
import com.dili.orders.service.WeighingBillService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;

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
		WeighingBill wb = this.weighingBillMapper.selectByPrimaryKey(approve.getWeighingBillId());
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
		WeighingBill wb = this.weighingBillMapper.selectByPrimaryKey(approve.getWeighingBillId());
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
		return BaseOutput.success();
	}
}