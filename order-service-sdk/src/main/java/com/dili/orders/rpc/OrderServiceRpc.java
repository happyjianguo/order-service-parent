package com.dili.orders.rpc;

import com.dili.orders.config.FeignHeaderConfig;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "order-service", contextId = "orders", url = "${orderService.url:}")
public interface OrderServiceRpc {
	/**
	 * 根据客户id查询客户最新审批通过该的审批单，如果是未结算的，那带出结算单的相关信息，如果是已撤销，那就不带出
	 *
	 * @param transitionDepartureApply
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/transitionDepartureApply/getOneByCustomerID", method = { RequestMethod.POST })
	BaseOutput<TransitionDepartureApply> getOneByCustomerID(@RequestBody TransitionDepartureApply transitionDepartureApply);

	/**
	 * 朔源系统同步接口
	 *
	 * @param id 从大于id（不包含）的数据开始同步
	 * @param rows 条数
	 * @return
	 */
	@RequestMapping("/sourceSync")
	BaseOutput<List<WeighingBillClientListDto>> sourceSync(@RequestParam Long id, @RequestParam Integer rows);

}
