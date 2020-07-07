package com.dili.order.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.GET;

@FeignClient(name = "jmsf-service", contextId = "jmsf")
public interface JmsfRpc {

	/**
	 * 删除皮重单
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/api/truck/deleteById")
	BaseOutput<Object> removeTareNumber(@RequestParam Long id);
}
