package com.dili.orders.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.PrintTemplateDataDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillPrintDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingStatementPrintDto;
import com.dili.orders.service.WeighingBillService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.exception.AppException;

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
	 * @param
	 * @return String
	 * @throws Exception
	 */
	@RequestMapping(value = "/listPage", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<?> listPage(@RequestBody WeighingBillQueryDto query) throws Exception {
		try {
			return weighingBillService.listPage(query);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}

	}

	/**
	 * 根据条件查询过磅单
	 *
	 * @param weighingBill
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listByExample", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<List<WeighingBillListPageDto>> listByExample(@RequestBody WeighingBillQueryDto weighingBill) throws Exception {
		List<WeighingBillListPageDto> list = weighingBillService.listByExampleModified(weighingBill);
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
		try {
			return weighingBillService.addWeighingBill(weighingBill);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 修改WeighingBill
	 *
	 * @param weighingBill
	 * @return BaseOutput
	 */
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@RequestBody WeighingBill weighingBill) {
		try {
			return weighingBillService.updateWeighingBill(weighingBill);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 结算
	 *
	 * @param
	 * @return BaseOutput
	 */
	@RequestMapping(value = "/settle", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput settle(@RequestParam String serialNo, @RequestParam String buyerPassword, @RequestParam Long operatorId, @RequestParam Long marketId) {
		try {
			return weighingBillService.settle(serialNo, buyerPassword, operatorId, marketId);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * @param serialNo
	 * @param buyerPassword
	 * @param sellerPassword
	 * @return
	 */
	@RequestMapping(value = "/withdraw", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> withdraw(@RequestParam String serialNo, @RequestParam String buyerPassword, @RequestParam String sellerPassword, @RequestParam Long operatorId) {
		try {
			return this.weighingBillService.withdraw(serialNo, buyerPassword, sellerPassword, operatorId);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
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
	public BaseOutput<Object> invalidate(@RequestParam String serialNo, @RequestParam String buyerPassword, @RequestParam String sellerPassword, Long operatorId) {
		try {
			return this.weighingBillService.invalidate(serialNo, buyerPassword, sellerPassword, operatorId);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 关闭
	 *
	 * @param serialNo
	 * @return
	 */
	@RequestMapping(value = "/autoClose", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> autoClose() {
		try {
			return this.weighingBillService.autoClose();
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

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
	 * 操作员作废过磅单
	 *
	 * @param id
	 * @param operatorId
	 * @param operatorPassword
	 * @return
	 */
	@RequestMapping(value = "/operatorInvalidate")
	public BaseOutput<Object> operatorInvalidate(@RequestParam Long id, @RequestParam Long operatorId) {
		try {
			return this.weighingBillService.operatorInvalidate(id, operatorId);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 操作员作废过磅单
	 *
	 * @param id
	 * @param operatorId
	 * @param operatorPassword
	 * @return
	 */
	@RequestMapping(value = "/operatorWithdraw")
	public BaseOutput<Object> operatorWithdraw(@RequestParam Long id, @RequestParam Long operatorId) {
		try {
			return this.weighingBillService.operatorWithdraw(id, operatorId);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 获取过磅单打印数据
	 * 
	 * @param serialNo 过磅单编号
	 * @return
	 */
	@RequestMapping("/getWeighingBillPrintData")
	public BaseOutput<Object> getWeighingBillPrintData(@RequestParam String serialNo) {
		PrintTemplateDataDto<WeighingBillPrintDto> dto = this.weighingBillService.getWeighingBillPrintData(serialNo);
		return BaseOutput.success().setData(dto);
	}

	/**
	 * 获取结算单打印数据
	 * 
	 * @param serialNo 结算单号
	 * @return
	 */
	@RequestMapping("/getWeighingStatementPrintData")
	public BaseOutput<Object> getWeighingStatementPrintData(@RequestParam String serialNo) {
		PrintTemplateDataDto<WeighingStatementPrintDto> dto = this.weighingBillService.getWeighingStatementPrintData(serialNo);
		return BaseOutput.success().setData(dto);
	}
}