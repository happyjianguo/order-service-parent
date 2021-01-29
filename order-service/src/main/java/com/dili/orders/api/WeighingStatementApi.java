package com.dili.orders.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dili.orders.dto.AccountQueryDto;
import com.dili.orders.dto.AccountQueryResponseDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingStatementAppletQuery;
import com.dili.orders.rpc.AccountRpc;
import com.dili.orders.service.WeighingBillService;
import com.dili.orders.service.WeighingStatementService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:43:53.
 */
@RestController
@RequestMapping("/api/weighingStatement")
public class WeighingStatementApi {

	@Autowired
	private WeighingBillService weighingBillService;
	@Autowired
	private WeighingStatementService weighingStatementService;
	@Autowired
	private AccountRpc accountRpc;

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

	/**
	 * 小程序分页查询
	 * 
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/listByApplet")
	public BaseOutput<?> listApplet(@RequestBody WeighingStatementAppletQuery query) {
		AccountQueryDto dto = new AccountQueryDto();
		dto.setParentAccountId(query.getAccountId());
		dto.setFirmId(query.getFirmId());
		BaseOutput<List<AccountQueryResponseDto>> output = this.accountRpc.getList(dto);
		if (output == null) {
			return BaseOutput.failure("查询账户信息失败");
		}
		if (!output.isSuccess()) {
			return BaseOutput.failure(output.getMessage());
		}
		List<Long> accountIds = new ArrayList<Long>(output.getData().size() + 1);
		accountIds.add(query.getAccountId());
		output.getData().forEach(a -> accountIds.add(a.getAccountId().longValue()));
		query.setAccountIds(accountIds);
		return this.weighingStatementService.listApplet(query);
	}

	/**
	 * 小程序状态统计
	 * 
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/stateCountStatistics")
	public BaseOutput<?> stateCountStatistics(@RequestBody WeighingStatementAppletQuery query) {
		AccountQueryDto dto = new AccountQueryDto();
		dto.setParentAccountId(query.getAccountId());
		dto.setFirmId(query.getFirmId());
		BaseOutput<List<AccountQueryResponseDto>> output = this.accountRpc.getList(dto);
		if (output == null) {
			return BaseOutput.failure("查询账户信息失败");
		}
		if (!output.isSuccess()) {
			return BaseOutput.failure(output.getMessage());
		}
		List<Long> accountIds = new ArrayList<Long>(output.getData().size() + 1);
		accountIds.add(query.getAccountId());
		output.getData().forEach(a -> accountIds.add(a.getAccountId().longValue()));
		query.setAccountIds(accountIds);
		return BaseOutput.successData(this.weighingStatementService.stateCountStatistics(query));
	}
}