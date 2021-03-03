package com.dili.orders.api;

import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatementState;
import com.dili.orders.dto.PrintTemplateDataDto;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillPrintDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.orders.dto.WeighingStatementPrintDto;
import com.dili.orders.service.FarmerWeghingBillService;
import com.dili.orders.service.WeighingBillService;
import com.dili.orders.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2020-06-19 14:20:28.
 */
@RestController
@RequestMapping("/api/farmerWeighingBill")
public class FarmerWeighingBillApi {
	@Autowired
	FarmerWeghingBillService weighingBillService;

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
	 * 打印列表
	 * 
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/printList", method = { RequestMethod.POST })
	public BaseOutput<?> printList(@RequestBody WeighingBillQueryDto query) {
		try {
			return this.weighingBillService.printList(query);
		} catch (Exception e) {
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
		List<WeighingBillClientListDto> list = weighingBillService.listByExampleModified(weighingBill);
		return BaseOutput.success().setData(list);
	}

	/**
	 * 新增WeighingBill
	 *
	 * @param weighingBill
	 * @return BaseOutput
	 */
	@BusinessLogger(businessType = "trading_orders", content = "新增过磅，过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "add", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@RequestBody WeighingBill weighingBill, HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
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
	@BusinessLogger(businessType = "trading_orders", content = "修改过磅,过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "edit", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@RequestBody WeighingBill weighingBill, HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
			return weighingBillService.updateWeighingBill(weighingBill);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * 结算
	 *
	 * @param request
	 *
	 * @param
	 * @return BaseOutput
	 */
	@BusinessLogger(businessType = "trading_orders", content = "交易过磅结算,过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/settle", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput settle(@RequestParam Long id, @RequestParam(required = false) String buyerPassword, @RequestParam Long operatorId, @RequestParam Long marketId,
			HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
			return weighingBillService.settle(id, buyerPassword, operatorId, marketId);
		} catch (AppException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	/**
	 * @param serialNo
	 * @param buyerPassword
	 * @param sellerPassword
	 * @param request
	 * @return
	 */
	@BusinessLogger(businessType = "trading_orders", content = "司磅员操作交易过磅撤销,过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "weighing_withdraw", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/withdraw", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> withdraw(@RequestParam Long id, @RequestParam String buyerPassword, @RequestParam String sellerPassword, @RequestParam Long operatorId, HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
			return this.weighingBillService.withdraw(id, buyerPassword, sellerPassword, operatorId);
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
	 * @param request
	 * @return
	 */
	@BusinessLogger(businessType = "trading_orders", content = "司磅员操作交易过磅作废,过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "invalidate", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/invalidate", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> invalidate(@RequestParam Long id, @RequestParam String buyerPassword, @RequestParam String sellerPassword, Long operatorId, HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
			return this.weighingBillService.invalidate(id, buyerPassword, sellerPassword, operatorId);
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
	@BusinessLogger(businessType = "trading_orders", content = "执行交易过磅定时任务，关闭未结算过磅单，执行时间:${operationTime}", systemCode = OrdersConstant.SYSTEM_CODE)
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
	 * @param request
	 * @param operatorPassword
	 * @return
	 */
	@BusinessLogger(businessType = "trading_orders", content = "后台操作员操作交易过磅作废，过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "invalidate", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/operatorInvalidate")
	public BaseOutput<Object> operatorInvalidate(@RequestParam Long id, @RequestParam Long operatorId, HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
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
	@BusinessLogger(businessType = "trading_orders", content = "后台操作员操作交易过磅撤销，过磅单号：${businessCode}，结算单号：${statementSerialNo}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "weighing_withdraw", systemCode = OrdersConstant.SYSTEM_CODE)
	@RequestMapping(value = "/operatorWithdraw")
	public BaseOutput<Object> operatorWithdraw(@RequestParam Long id, @RequestParam Long operatorId, HttpServletRequest request) {
		try {
			LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
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
	public BaseOutput<Object> getWeighingBillPrintData(@RequestParam Long id) {
		PrintTemplateDataDto<WeighingBillPrintDto> dto = this.weighingBillService.getWeighingBillPrintData(id);
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

	/**
	 * 朔源系统同步接口
	 *
	 * @param id   从大于id（不包含）的数据开始同步
	 * @param rows 条数
	 * @return
	 */
	@RequestMapping("/sourceSync")
	public BaseOutput<List<WeighingBillClientListDto>> sourceSync(@RequestParam Long id, @RequestParam Integer rows) {
		WeighingBillQueryDto wbQuery = new WeighingBillQueryDto();
		wbQuery.setIdStart(id);
		wbQuery.setRows(rows);
		wbQuery.setStatementStates(Arrays.asList(WeighingStatementState.PAID.getValue()));
		List<WeighingBillClientListDto> list = this.weighingBillService.listByExampleModifiedPage(wbQuery);
		return BaseOutput.successData(list);
	}
}
