package com.dili.orders.rpc;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dili.assets.sdk.dto.CategoryDTO;
import com.dili.ss.domain.BaseOutput;

@FeignClient(name = "assets-service", contextId = "category")
public interface CategoryRpc {

	@RequestMapping(value = "/api/customCategory/getTree")
	BaseOutput<List<CategoryDTO>> getTree(CategoryDTO query);
}
