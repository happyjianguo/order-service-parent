package com.dili.order.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dili.order.constants.OrdersConstant;
import com.dili.order.dto.AccountBalanceDto;
import com.dili.order.dto.AccountRequestDto;
import com.dili.order.dto.PaymentTradeCancelDto;
import com.dili.order.dto.PaymentTradeCommitDto;
import com.dili.order.dto.PaymentPreauthorizedTradeCommitDto;
import com.dili.order.dto.PaymentTradeCommitResponseDto;
import com.dili.order.dto.PaymentTradePrepareDto;
import com.dili.ss.domain.BaseOutput;

import feign.Headers;

@FeignClient(name = "pay-service", contextId = "pay")
public interface PayRpc {

	/**
	 * 查询余额
	 * 
	 * @param query
	 * @return
	 */
	@Headers({ "Content-Type:application/json", "appid:" + OrdersConstant.PAYMENT_APP_ID, "token:" + OrdersConstant.PAYMENT_TOKEN, "service:" + OrdersConstant.PAYMENT_FUND_SERVICE_QUERY })
	@RequestMapping(value = "/payment/api/gateway.do", method = RequestMethod.POST)
	BaseOutput<AccountBalanceDto> queryAccountBalance(@RequestBody AccountRequestDto query);

	/**
	 * 创建支付订单
	 * 
	 * @param dto
	 * @return 交易号
	 */
	@Headers({ "Content-Type:application/json", "appid:" + OrdersConstant.PAYMENT_APP_ID, "token:" + OrdersConstant.PAYMENT_TOKEN, "service:" + OrdersConstant.PAYMENT_TRADE_SERVICE_PREPARE })
	@RequestMapping(value = "/payment/api/gateway.do", method = RequestMethod.POST)
	BaseOutput<String> prepare(@RequestBody PaymentTradePrepareDto dto);

	/**
	 * 提交预授权交易
	 * 
	 * @param dto
	 * @return 交易号
	 */
	@Headers({ "Content-Type:application/json", "appid:" + OrdersConstant.PAYMENT_APP_ID, "token:" + OrdersConstant.PAYMENT_TOKEN, "service:" + OrdersConstant.PAYMENT_TRADE_SERVICE_CONFIRM })
	@RequestMapping(value = "/payment/api/gateway.do", method = RequestMethod.POST)
	BaseOutput<PaymentTradeCommitResponseDto> confirm(@RequestBody PaymentTradeConfirmDto dto);

	/**
	 * 撤销预授权交易
	 * 
	 * @param dto
	 * @return 交易号
	 */
	@Headers({ "Content-Type:application/json", "appid:" + OrdersConstant.PAYMENT_APP_ID, "token:" + OrdersConstant.PAYMENT_TOKEN, "service:" + OrdersConstant.PAYMENT_TRADE_SERVICE_CANCEL })
	@RequestMapping(value = "/payment/api/gateway.do", method = RequestMethod.POST)
	BaseOutput<PaymentTradeCommitResponseDto> confirm(@RequestBody PaymentTradeCancelDto dto);

	/**
	 * 提交即时交易
	 * 
	 * @param dto
	 * @return 交易号
	 */
	@Headers({ "Content-Type:application/json", "appid:" + OrdersConstant.PAYMENT_APP_ID, "token:" + OrdersConstant.PAYMENT_TOKEN, "service:" + OrdersConstant.PAYMENT_TRADE_SERVICE_COMMIT })
	@RequestMapping(value = "/payment/api/gateway.do", method = RequestMethod.POST)
	BaseOutput<PaymentTradeCommitResponseDto> commit(@RequestBody PaymentTradeCommitDto dto);
}
