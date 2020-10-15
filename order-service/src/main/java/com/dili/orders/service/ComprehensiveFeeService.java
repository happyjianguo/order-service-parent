package com.dili.orders.service;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;

import java.text.ParseException;
import java.util.List;

/**
 * 检测收费服务
 *
 * @author  Henry.Huang
 * @date  2020/08/20
 */
public interface ComprehensiveFeeService extends BaseService<ComprehensiveFee, Long> {

	/**
	 * 根据页面条件查询
	 * 
	 * @param comprehensiveFee 检测收费
	 * @return
	 */
	PageOutput<List<ComprehensiveFee>> listByQueryParams(ComprehensiveFee comprehensiveFee);

	/**
	 * 根据页面条件查询交易总数和交易总额
	 *
	 * @param comprehensiveFee 检测收费
	 * @return
	 */
	BaseOutput<ComprehensiveFee> selectCountAndTotal(ComprehensiveFee comprehensiveFee);

	/**
	 * 新增
	 * @param comprehensiveFee   检测收费
	 * @return
	 */
	BaseOutput<ComprehensiveFee> insertComprehensiveFee(ComprehensiveFee comprehensiveFee);

	/**
	 * 操作员撤销
	 *
	 * @param comprehensiveFee 检测收费
	 * @param operatorId       操作员id
	 * @param userName         用户真实名字
	 * @param operatorPassword 操作员登录密码
	 * @param operatorName     操作员登录名
	 * @return
	 */
	BaseOutput<ComprehensiveFee> revocator(ComprehensiveFee comprehensiveFee, Long operatorId, String userName, String operatorPassword, String operatorName);

	/**
	 * 支付
	 * @param id                 检测单id
	 * @param password           操作员登录密码
	 * @param marketId           市场id
	 * @param operatorId         操作员id
	 * @param operatorName       操作员账户名
	 * @param operatorUserName   操作员真实姓名
	 * @return
	 */
	BaseOutput<ComprehensiveFee> pay(Long id, String password, Long marketId, Long operatorId, String operatorName, String operatorUserName);

	/**
	 * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
	 * @throws   ParseException
	 */
	BaseOutput<String> scheduleUpdate() throws ParseException;
}