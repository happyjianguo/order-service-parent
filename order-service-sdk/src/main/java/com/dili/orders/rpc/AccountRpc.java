package com.dili.orders.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dili.order.dto.UserAccountCardResponseDto;
import com.dili.ss.domain.BaseOutput;

/**
 * 根据卡号查询账户信息
 */

@FeignClient(name = "account-service", contextId = "accountRpc")
public interface AccountRpc {

    @GetMapping("/api/account/getOneAccountCard/{cardNo}")
    BaseOutput<UserAccountCardResponseDto> getOneAccountCard(@PathVariable(value = "cardNo") String cardNo);

}
