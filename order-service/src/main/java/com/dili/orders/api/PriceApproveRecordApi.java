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

	@RequestMapping("/listPage")
	public BaseOutput<Object> listPageByExample(PriceApproveRecordQueryDto query) {
		List<PriceApproveRecord> list = this.priceApproveRecordService.listByExample(query);
		return BaseOutput.success().setData(list);
	}

	@RequestMapping("/getById")
	public BaseOutput<Object> getById(@RequestParam Long id) {
		return BaseOutput.success().setData(this.priceApproveRecordService.get(id));
	}

}