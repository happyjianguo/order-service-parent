package com.dili.orders.service;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;

import java.text.ParseException;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 * @author  Henry.Huang
 * @date  2020/08/20
 */
public interface ComprehensiveFeeService extends BaseService<ComprehensiveFee, Long> {

	/**
	 * 根据页面条件查询
	 * 
	 * @param comprehensiveFee comprehensiveFee对象
	 * @return
	 */
	PageOutput<List<ComprehensiveFee>> listByQueryParams(ComprehensiveFee comprehensiveFee);

	/**
	 * 新增
	 * @param comprehensiveFee   comprehensiveFee对象
	 * @return
	 */
	BaseOutput<ComprehensiveFee> insertComprehensiveFee(ComprehensiveFee comprehensiveFee);
	/**
	 * 操作员撤销
	 *
	 * @param id               检测单id
	 * @param operatorId       操作员id
	 * @param operatorPassword 操作员登录密码
	 * @return
	 */
	BaseOutput<Object> revocator(Long id, Long operatorId,String userName, String operatorPassword);

	/**
	 * 支付
	 * @param id
	 * @param password
	 * @param marketId
	 * @param departmentId
	 * @param operatorCode
	 * @param operatorId
	 * @param operatorName
	 * @param operatorUserName
	 * @return
	 */
	BaseOutput pay(Long id, String password, Long marketId, Long departmentId, String operatorCode, Long operatorId, String operatorName, String operatorUserName);

	/**
	 * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
	 * @throws   ParseException
	 */
	void scheduleUpdate() throws ParseException;
}