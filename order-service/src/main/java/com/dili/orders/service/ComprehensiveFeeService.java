package com.dili.orders.service;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;

import java.text.ParseException;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
public interface ComprehensiveFeeService extends BaseService<ComprehensiveFee, Long> {

	/**
	 * 根据页面条件查询
	 * 
	 * @param comprehensiveFee
	 * @return
	 */
	PageOutput<List<ComprehensiveFee>> listByQueryParams(ComprehensiveFee comprehensiveFee);

	/**
	 * 新增
	 */
	BaseOutput<ComprehensiveFee> insertComprehensiveFee(ComprehensiveFee comprehensiveFee);

	/**
	 * 支付
	 */
	BaseOutput pay(Long id, String password, Long marketId, Long departmentId, String operatorCode, Long operatorId, String operatorName, String operatorUserName);

	/**
	 * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
	 */
	void scheduleUpdate() throws ParseException;
}