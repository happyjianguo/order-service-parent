package com.dili.order.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dili.order.domain.WeighingBill;
import com.dili.order.dto.WeighingBillQueryDto;
import com.dili.order.dto.WeighingBillUpdateDto;
import com.dili.order.service.WeighingBillService;
import com.dili.ss.domain.BaseOutput;

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
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(WeighingBillQueryDto weighingBill) throws Exception {
		return weighingBillService.listEasyuiPageByExample(weighingBill, true).toString();
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
		BaseOutput<String> output = weighingBillService.addWeighingBill(weighingBill);
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
		BaseOutput<Object> output = weighingBillService.settle(serialNo, buyerPassword, sellerPassword, operatorId);
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
	 * 删除WeighingBill
	 * 
	 * @param id
	 * @return BaseOutput
	 */
	@RequestMapping(value = "/delete.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		weighingBillService.delete(id);
		return BaseOutput.success("删除成功");
	}
}