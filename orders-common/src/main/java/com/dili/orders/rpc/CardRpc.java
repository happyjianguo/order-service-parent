package com.dili.orders.rpc;


import com.alibaba.fastjson.JSONObject;
import com.dili.orders.dto.AccountSimpleResponseDto;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    /**
     * 根据客户id查询卡号
     *
     * @param jsonObject
     * @return
     */
    @PostMapping("/api/account/getList")
    BaseOutput<List<UserAccountCardResponseDto>> getList(@RequestBody JSONObject jsonObject);
}
