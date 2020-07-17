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
    BaseOutput<Object> removeTareNumber(@RequestParam(value = "id") Long id);

    /**
     * 转离场获取单号，判断是否以及离场
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/vehicleAccess/get", method = RequestMethod.GET)
    BaseOutput<VehicleAccessDTO> getAccess(@RequestParam("id") Long id);

}
