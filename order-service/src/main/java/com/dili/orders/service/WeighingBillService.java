package com.dili.orders.service;

import java.util.List;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingBillUpdateDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
public interface WeighingBillService extends BaseService<WeighingBill, Long> {

	/**
	 * 新增过磅单
	 * 
	 * @param weighingBill
	 * @return
	 */
	BaseOutput<WeighingStatement> addWeighingBill(WeighingBill weighingBill);

	/**
	 * 修改过磅单
	 * 
	 * @param weighingBill
	 * @return
	 */
	BaseOutput<Object> updateWeighingBill(WeighingBillUpdateDto weighingBill);

	/**
	 * 结算
	 * 
	 * @param serialNo      过磅单号
	 * @param buyerPassword 买家交易密码
	 * @param operatorId    TODO
	 * @return
	 */
	BaseOutput<Object> settle(String serialNo, String buyerPassword, Long operatorId);

	/**
	 * 撤销过磅单
	 * 
	 * @param serialNo       过磅单号
	 * @param buyerPassword  买家交易密码
	 * @param sellerPassword 卖家交易密码
	 * @param operatorId     TODO
	 * @return
	 */
	BaseOutput<Object> withdraw(String serialNo, String buyerPassword, String sellerPassword, Long operatorId);

	/**
	 * 作废过磅单
	 * 
	 * @param serialNo       过磅单号
	 * @param buyerPassword  买家交易密码
	 * @param sellerPassword 卖家交易密码
	 * @param operatorId     操作员id
	 * @return
	 */
	BaseOutput<Object> invalidate(String serialNo, String buyerPassword, String sellerPassword, Long operatorId);

	/**
	 * 操作员作废过磅单
	 * 
	 * @param id               过磅id
	 * @param operatorId       操作员id
	 * @param operatorPassword 操作员登录密码
	 * @return
	 */
	BaseOutput<Object> operatorInvalidate(Long id, Long operatorId, String operatorPassword);

	/**
	 * 操作员撤销过磅单
	 * 
	 * @param id               过磅id
	 * @param operatorId       操作员id
	 * @param operatorPassword 操作员登录密码
	 * @return
	 */
	BaseOutput<Object> operatorWithdraw(Long id, Long operatorId, String operatorPassword);

	/**
	 * 关闭
	 * 
	 * @param serialNo 过磅单号
	 * @return
	 */
	BaseOutput<Object> close(String serialNo);

	/**
	 * 分页查询
	 * 
	 * @param query
	 * @return
	 */
	PageOutput<List<WeighingBillListPageDto>> listPage(WeighingBillQueryDto query);

	/**
	 * 过磅单详情
	 * 
	 * @param id
	 * @return
	 */
	WeighingBillDetailDto detail(Long id);

}