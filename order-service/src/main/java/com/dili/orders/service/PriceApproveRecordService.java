package com.dili.orders.service;

import com.dili.orders.domain.PriceApproveRecord;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-08-30 14:29:35.
 */
public interface PriceApproveRecordService extends BaseService<PriceApproveRecord, Long> {

	/**
	 * 审批通过
	 * 
	 * @param id         id
	 * @param approverId 审批人id
	 * @param taskId     流程任务id
	 * @return
	 */
	BaseOutput<Object> accept(Long id, Long approverId, String taskId);
	
	/**
	 * 审批拒绝
	 * 
	 * @param id         id
	 * @param approverId 审批人id
	 * @param taskId     流程任务id
	 * @return
	 */
	BaseOutput<Object> reject(Long id, Long approverId, String taskId);
}