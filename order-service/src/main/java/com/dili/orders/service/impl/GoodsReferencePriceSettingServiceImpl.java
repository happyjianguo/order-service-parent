package com.dili.orders.service.impl;


import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.mapper.GoodsReferencePriceSettingMapper;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
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

    @Override
    public BaseOutput<GoodsReferencePriceSetting> detail(GoodsReferencePriceSetting goodsReferencePriceSetting) {
        return BaseOutput.successData(this.getActualDao().selectDetailById(goodsReferencePriceSetting));
    }

    @Override
    public BaseOutput<GoodsReferencePriceSetting> insertGoodsReferencePriceSetting(GoodsReferencePriceSetting goodsReferencePriceSetting) {
        //设置默认版本号为0
        goodsReferencePriceSetting.setVersion(0);
        int insert = getActualDao().insert(goodsReferencePriceSetting);
        if (insert <= 0) {
            throw new RuntimeException("品类参考价新增-->创建品类参考价失败");
        }
        return BaseOutput.successData(goodsReferencePriceSetting);
    }

    @Override
    public BaseOutput<GoodsReferencePriceSetting> updateGoodsReferencePriceSetting(GoodsReferencePriceSetting goodsReferencePriceSetting) {
        goodsReferencePriceSetting.setVersion(goodsReferencePriceSetting.getVersion() + 1);
        int update = getActualDao().updateByPrimaryKey(goodsReferencePriceSetting);
        if (update <= 0) {
            throw new RuntimeException("品类参考价修改-->修改品类参考价失败");
        }
        return BaseOutput.successData(goodsReferencePriceSetting);
    }
}
