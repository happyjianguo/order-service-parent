package com.dili.orders.rpc;

import com.dili.orders.config.PayServiceFeignConfig;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.dto.*;
import com.dili.ss.domain.BaseOutput;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "pay-service", contextId = "pay", configuration = PayServiceFeignConfig.class)
public interface PayRpc {

    /**
     * 查询余额
     *
     * @param query
     * @return
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.fund.service:query", method = RequestMethod.POST)
    BaseOutput<AccountBalanceDto> queryAccountBalance(@RequestBody AccountRequestDto query);

    /**
     * 创建支付订单
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:prepare", method = RequestMethod.POST)
    BaseOutput<CreateTradeResponseDto> prepare(@RequestBody PaymentTradePrepareDto dto);

    /**
     * 提交预授权交易
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> confirm(@RequestBody PaymentTradeConfirmDto dto);

    /**
     * 撤销预授权交易
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:cancel", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> confirm(@RequestBody PaymentTradeCancelDto dto);

    /**
     * 提交即时交易
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> commit(@RequestBody PaymentTradeCommitDto dto);

    /**
     * 缴费
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> commit4(@RequestBody PaymentTradeCommitDto dto);

    /**
     * 撤销
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:confirm", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> cancel2(@RequestBody PaymentTradeCommitDto dto);
}
