package com.dili.orders.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dili.orders.config.FeignHeaderConfig;
import com.dili.orders.dto.AccountBalanceDto;
import com.dili.orders.dto.AccountPasswordValidateDto;
import com.dili.orders.dto.AccountRequestDto;
import com.dili.orders.dto.CreateTradeResponseDto;
import com.dili.orders.dto.PaymentTradeCancelDto;
import com.dili.orders.dto.PaymentTradeCommitDto;
import com.dili.orders.dto.PaymentTradeCommitResponseDto;
import com.dili.orders.dto.PaymentTradeConfirmDto;
import com.dili.orders.dto.PaymentTradePrepareDto;
import com.dili.ss.domain.BaseOutput;

@FeignClient(name = "pay-service", contextId = "pay", configuration = FeignHeaderConfig.class)
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
    BaseOutput<CreateTradeResponseDto> prepareTrade(@RequestBody PaymentTradePrepareDto dto);

    /**
     * 撤销预授权交易
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:cancel", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> cancelTrade(@RequestBody PaymentTradeCancelDto dto);

    /**
     * 提交即时交易或提交预授权交易
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> commitTrade(@RequestBody PaymentTradeCommitDto dto);

    /**
     * 确认预授权
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:confirm", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> confirm(@RequestBody PaymentTradeCommitDto dto);

    /**
     * 缴费
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> pay(@RequestBody PaymentTradeCommitDto dto);

    /**
     * 撤销
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:cancel", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> cancel(@RequestBody PaymentTradeCommitDto dto);

    /**
     * 校验资金账户密码
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.permission.service:password", method = RequestMethod.POST)
    BaseOutput<Object> validateAccountPassword(@RequestBody AccountPasswordValidateDto dto);

    /**
     * 转账
     *
     * @param dto
     * @return
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> commit6(@RequestBody PaymentTradeCommitDto dto);
    
    /**
     * 缴费
     *
     * @param dto
     * @return 交易号
     */
    @RequestMapping(value = "/payment/api/gateway.do?service=payment.trade.service:commit4", method = RequestMethod.POST)
    BaseOutput<PaymentTradeCommitResponseDto> commit4(@RequestBody PaymentTradeCommitDto dto);
}
