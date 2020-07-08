package com.dili.orders.rpc;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dili.jmsf.microservice.sdk.dto.VehicleAccessDTO;
import com.dili.ss.domain.BaseOutput;

@FeignClient(name = "jmsf-service")
public interface JmsfRpc {

	/**
	 * 转离场保存
	 */
	@RequestMapping(value = "/api/vehicleAccess/add", method = RequestMethod.POST)
	BaseOutput<VehicleAccessDTO> add(@RequestBody VehicleAccessDTO accessDTO);

	/**
	 * 删除皮重单
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/api/truck/deleteById")
	BaseOutput<Object> removeTareNumber(@RequestParam Long id);

}
