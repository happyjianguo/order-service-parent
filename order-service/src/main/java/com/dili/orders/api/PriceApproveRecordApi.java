package com.dili.orders.api;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.dto.PriceApproveRecordQueryDto;
import com.dili.orders.service.PriceApproveRecordService;
import com.dili.orders.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;

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
	public PageOutput<Object> listPageByExample(@RequestBody PriceApproveRecordQueryDto query) {
		List<PriceApproveRecord> list = this.priceApproveRecordService.listByExample(query);
		Page<PriceApproveRecord> page = (Page<PriceApproveRecord>) list;
		return PageOutput.success().setData(page).setPageNum(page.getPageNum()).setTotal(page.getTotal());
	}

	/**
	 * app分页查询
	 * 
	 * @param query 查询条件
	 * @return
	 */
	@RequestMapping("/listPageApp")
	public PageOutput<Object> listPageApp(@RequestBody PriceApproveRecordQueryDto query) {
		List<PriceApproveRecord> list = this.priceApproveRecordService.listPageApp(query);
		Page<PriceApproveRecord> page = (Page<PriceApproveRecord>) list;
		return PageOutput.success().setData(page).setPageNum(page.getPageNum()).setTotal(page.getTotal());
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
	 * @param notes      说明
	 * @param taskId     流程实例id
	 * @param request
	 * @return
	 */
	@BusinessLogger(businessType = "trading_orders", content = "交易过磅价格审批通过，过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "price_approve", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping("/approveAccept")
	public BaseOutput<Object> approveAccept(@RequestParam Long id, @RequestParam String notes, @RequestParam Long approverId, @RequestParam String taskId, HttpServletRequest request) {
		LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
		return this.priceApproveRecordService.accept(id, approverId, notes, taskId);
	}

	/**
	 * 审批拒绝
	 * 
	 * @param id         id
	 * @param approverId 审批人id
	 * @param notes      说明
	 * @param taskId     流程实例id
	 * @param request
	 * @return
	 */
	@BusinessLogger(businessType = "trading_orders", content = "交易过磅价格审批拒绝，过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "price_approve", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping("/approveReject")
	public BaseOutput<Object> approveReject(@RequestParam Long id, @RequestParam String notes, @RequestParam Long approverId, @RequestParam String taskId, HttpServletRequest request) {
		LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
		return this.priceApproveRecordService.reject(id, approverId, notes, taskId);
	}

}