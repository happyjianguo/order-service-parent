package com.dili.orders.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingBillUpdateDto;
import com.dili.orders.service.WeighingBillService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.github.pagehelper.Page;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@RestController
@RequestMapping("/api/weighingBill")
public class WeighingBillApi {
	@Autowired
	WeighingBillService weighingBillService;

	/**
	 * 分页查询WeighingBill，返回easyui分页信息
	 * 
	 * @param weighingBill
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/listPage", method = { RequestMethod.POST })
	public @ResponseBody PageOutput<List<WeighingBillListPageDto>> listPage(@RequestBody WeighingBillQueryDto query) throws Exception {
		return weighingBillService.listPage(query);

	}

	/**
	 * 根据条件查询过磅单
	 * 
	 * @param weighingBill
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listByExample", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<List<WeighingBill>> listByExample(@RequestBody WeighingBillQueryDto weighingBill) throws Exception {
		List<WeighingBill> list = weighingBillService.listByExample(weighingBill);
		return BaseOutput.success().setData(list);
	}

	/**
	 * 新增WeighingBill
	 * 
	 * @param weighingBill
	 * @return BaseOutput
	 */
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@RequestBody WeighingBill weighingBill) {
		BaseOutput<WeighingStatement> output = weighingBillService.addWeighingBill(weighingBill);
		return output;
	}

	/**
	 * 修改WeighingBill
	 * 
	 * @param weighingBill
	 * @return BaseOutput
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@RequestBody WeighingBillUpdateDto weighingBill) {
		BaseOutput<Object> output = weighingBillService.updateWeighingBill(weighingBill);
		return output;
	}

	/**
	 * 结算
	 * 
	 * @param weighingBill
	 * @return BaseOutput
	 */
	@RequestMapping(value = "/settle", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput settle(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
		BaseOutput<Object> output = weighingBillService.settle(serialNo, buyerPassword, operatorId);
		return output;
	}

	/**
	 * 
	 * @param serialNo
	 * @param buyerPassword
	 * @param sellerPassword
	 * @return
	 */
	@RequestMapping(value = "/withdraw", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> withdraw(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
		return this.weighingBillService.withdraw(serialNo, buyerPassword, sellerPassword, operatorId);
	}

	/**
	 * 作废
	 * 
	 * @param serialNo
	 * @param buyerPassword
	 * @param sellerPassword
	 * @param operatorId
	 * @return
	 */
	@RequestMapping(value = "/invalidate", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> invalidate(String serialNo, String buyerPassword, String sellerPassword, Long operatorId) {
		return this.weighingBillService.invalidate(serialNo, buyerPassword, sellerPassword, operatorId);
	}

	/**
	 * 关闭
	 * 
	 * @param serialNo
	 * @return
	 */
	@RequestMapping(value = "/close", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> close(String serialNo) {
		return this.weighingBillService.close(serialNo);
	}

	/**
	 * 查询过磅单详情
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail")
	public BaseOutput<WeighingBillDetailDto> detail(Long id) {
		WeighingBillDetailDto dto = this.weighingBillService.detail(id);
		return BaseOutput.success().setData(dto);
	}

}