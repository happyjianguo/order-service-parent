package com.dili.orders.service;

import java.util.List;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.WeighingStatementAppletDto;
import com.dili.orders.dto.WeighingStatementAppletQuery;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.PageOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:43:53.
 */
public interface WeighingStatementService extends BaseService<WeighingStatement, Long> {

	/**
	 * 微信小程序查询列表
	 * 
	 * @param query
	 * @return
	 */
	PageOutput<List<WeighingStatementAppletDto>> listApplet(WeighingStatementAppletQuery query);
}