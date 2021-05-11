package com.dili.orders.rpc;

import com.dili.orders.dto.TraceTradeBillResponseDto;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.ss.domain.BaseOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/11/6 14:16
 * @Description: 溯源rpc
 */
@FeignClient(name = "quality-trace", contextId = "qualityTraceRpc", url = "http://10.28.1.5")
public interface QualityTraceRpc {

    /**
     * @author miaoguoxin
     * @date 2020/11/6
     */
    @PostMapping(value = "/api/qualityTraceTradeBillApi/queryByOrderIdList.api")
    BaseOutput<List<TraceTradeBillResponseDto>> queryByOrderIdList(List<Long> orderIds);

    /**
     * 朔源商品检测接口
     *
     * @param data
     * @return
     */
    @PostMapping(value = "/api/qualityTraceTradeBillApi/receiveTradeBillDataList.api")
    BaseOutput<Object> orderGoodsDetection(List<WeighingBillClientListDto> data);
}
