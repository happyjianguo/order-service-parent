package com.dili.orders.rpc;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dili.orders.dto.AccountSimpleResponseDto;
import com.dili.ss.domain.BaseOutput;

/**
 * 根据卡号获取账户信息
 */
@FeignClient(name = "account-service", contextId = "card")
public interface CardRpc {
    /**
     * 根据卡号获取账户信息
     *
     * @param cardNo
     * @return
     */
    @GetMapping("/api/account/simpleInfo")
    BaseOutput<AccountSimpleResponseDto> getOneAccountCard(@RequestParam(value = "cardNo") String cardNo);
}
