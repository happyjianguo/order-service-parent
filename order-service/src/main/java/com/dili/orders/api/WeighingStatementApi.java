package com.dili.orders.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.service.WeighingBillService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:43:53.
 */
@Controller
@RequestMapping("/weighingStatement")
public class WeighingStatementApi {

	@Autowired
	private WeighingBillService weighingBillService;

	/**
	 * 查询过磅单详情
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	public BaseOutput<WeighingBillDetailDto> detail(@RequestParam Long id) {
		WeighingBillDetailDto dto = this.weighingBillService.detail(id);
		return BaseOutput.success().setData(dto);
	}
}