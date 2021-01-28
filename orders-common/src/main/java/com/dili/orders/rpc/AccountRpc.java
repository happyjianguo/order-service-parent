package com.dili.orders.rpc;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dili.orders.dto.AccountQueryDto;
import com.dili.orders.dto.AccountQueryResponseDto;
import com.dili.orders.dto.CardQueryDto;
import com.dili.orders.dto.SerialDto;
import com.dili.orders.dto.UserAccountCardResponseDto;
import com.dili.ss.domain.BaseOutput;

/**
 * 根据卡号查询账户信息
 */

@FeignClient(name = "account-service", contextId = "accountRpc")
public interface AccountRpc {

	@RequestMapping(value = "/api/account/getSingle", method = RequestMethod.POST)
	BaseOutput<UserAccountCardResponseDto> getSingle(@RequestBody CardQueryDto dto);

	/**
	 * 获取列表
	 */
	@RequestMapping(value = "/api/serial/batchSave", method = RequestMethod.POST)
	BaseOutput<Object> batchSave(@RequestBody SerialDto serialDto);
	
	/**
	 * 获取列表
	 */
	@RequestMapping(value = "/api/account/getList", method = RequestMethod.POST)
	BaseOutput<List<AccountQueryResponseDto>> getList(@RequestBody AccountQueryDto dto);

}
