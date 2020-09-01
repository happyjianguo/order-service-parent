package com.dili.orders.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.dto.PriceApproveRecordQueryDto;
import com.dili.orders.service.PriceApproveRecordService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-08-30 14:29:35.
 */
@RestController
@RequestMapping("/api/priceApproveRecord")
public class PriceApproveRecordApi {
	@Autowired
	PriceApproveRecordService priceApproveRecordService;

	/**
	 * 分页查询
	 * 
	 * @param query 查询条件
	 * @return
	 */
	@RequestMapping("/listPage")
	public BaseOutput<Object> listPageByExample(PriceApproveRecordQueryDto query) {
		List<PriceApproveRecord> list = this.priceApproveRecordService.listByExample(query);
		return BaseOutput.success().setData(list);
	}

	/**
	 * 根据id查询
	 * 
	 * @param id 审批记录id
	 * @return
	 */
	@RequestMapping("/getById")
	public BaseOutput<Object> getById(@RequestParam Long id) {
		return BaseOutput.success().setData(this.priceApproveRecordService.get(id));
	}

	/**
	 * 审批通过
	 * 
	 * @param id         id
	 * @param approverId 审批人id
	 * @param taskId     流程实例id
	 * @return
	 */
	@RequestMapping("/approveAccept")
	public BaseOutput<Object> approveAccept(@RequestParam Long id, @RequestParam Long approverId, @RequestParam String taskId) {
		return this.priceApproveRecordService.accept(id, approverId, taskId);
	}
	
	/**
	 * 审批拒绝
	 * 
	 * @param id         id
	 * @param approverId 审批人id
	 * @param taskId     流程实例id
	 * @return
	 */
	@RequestMapping("/approveReject")
	public BaseOutput<Object> approveReject(@RequestParam Long id, @RequestParam Long approverId, @RequestParam String taskId) {
		return this.priceApproveRecordService.reject(id, approverId, taskId);
	}

}