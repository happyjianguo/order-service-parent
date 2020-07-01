package com.dili.order.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dili.ss.domain.BaseOutput;

//@FeignClient(name = "account-service", contextId = "account")
public interface AccountRpc {

//	@RequestMapping(value = "/api/carType/listCarType", method = RequestMethod.POST)
	BaseOutput<Boolean> validateAccountPassword(String account, String password);
}
