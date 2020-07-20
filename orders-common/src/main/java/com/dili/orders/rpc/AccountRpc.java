package com.dili.orders.rpc;

import com.dili.orders.dto.SerialDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.ss.domain.BaseOutput;

/**
 * 根据卡号查询账户信息
 */

@FeignClient(name = "account-service", contextId = "accountRpc")
public interface AccountRpc {

    @GetMapping("/api/account/getOneAccountCard/{cardNo}")
    BaseOutput<UserAccountCardResponseDto> getOneAccountCard(@PathVariable(value = "cardNo") String cardNo);

    /**
     * 获取列表
     */
    @RequestMapping(value = "/api/serial/batchSave", method = RequestMethod.POST)
    BaseOutput<Object> batchSave(@RequestBody SerialDto serialDto);

}
