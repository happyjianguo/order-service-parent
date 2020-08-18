package com.dili.orders.service.impl;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.mapper.GoodsReferencePriceSettingMapper;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsReferencePriceSettingServiceImpl extends BaseServiceImpl<GoodsReferencePriceSetting,Long> implements GoodsReferencePriceSettingService {

    @Autowired
    private UidRpc uidRpc;

    public GoodsReferencePriceSettingMapper getActualDao(){return (GoodsReferencePriceSettingMapper)getDao();}

    @Override
    public List<GoodsReferencePriceSetting> getAllGoods(GoodsReferencePriceSetting goodsReferencePriceSetting) {
        List<GoodsReferencePriceSetting> list = getActualDao().listByQueryParams(goodsReferencePriceSetting);
        return list;
    }
}
