package com.dili.orders.rpc;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dili.assets.sdk.dto.CarTypeForJmsfDTO;
import com.dili.assets.sdk.dto.CategoryDTO;
import com.dili.ss.domain.BaseOutput;

@FeignClient(name = "assets-service", contextId = "assets")
public interface AssetsRpc {
    /**
     * 获取车型
     */
    @RequestMapping(value = "/api/carTypePublic/listCarTypePublicByBusiness", method = RequestMethod.POST)
    BaseOutput<List<CarTypeForJmsfDTO>> listCarType(CarTypeForJmsfDTO carTypeForJmsfDTO);

    @RequestMapping(value = "/api/customCategory/getTree", method = RequestMethod.POST)
    BaseOutput<List<CategoryDTO>> list(CategoryDTO categoryDTO);


}
