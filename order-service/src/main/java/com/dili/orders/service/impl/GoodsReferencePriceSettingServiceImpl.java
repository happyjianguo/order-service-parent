package com.dili.orders.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.dto.ReferencePriceSettingItemDto;
import com.dili.orders.dto.ReferencePriceSettingRequestDto;
import com.dili.orders.mapper.GoodsReferencePriceSettingMapper;
import com.dili.orders.service.GoodsReferencePriceSettingService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 品类参考价接口实现类
 *
 * @date: 2020/8/21
 * @author: Seabert.Zhan
 */
@Service
public class GoodsReferencePriceSettingServiceImpl extends BaseServiceImpl<GoodsReferencePriceSetting, Long> implements GoodsReferencePriceSettingService {

    public GoodsReferencePriceSettingMapper getActualDao() {
        return (GoodsReferencePriceSettingMapper) getDao();
    }

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
            throw new AppException("品类参考价新增-->创建品类参考价失败");
        }
        return BaseOutput.successData(goodsReferencePriceSetting);
    }

    @Override
    public BaseOutput<GoodsReferencePriceSetting> updateGoodsReferencePriceSetting(GoodsReferencePriceSetting goodsReferencePriceSetting) {
        int update = getActualDao().updateByPrimaryKey(goodsReferencePriceSetting);
        if (update <= 0) {
            throw new AppException("品类参考价修改-->修改品类参考价失败");
        }
        return BaseOutput.successData(goodsReferencePriceSetting);
    }

    @Override
    public void saveOrEdit(ReferencePriceSettingRequestDto requestDto) {
        List<ReferencePriceSettingItemDto> items = requestDto.getItems();
        GoodsReferencePriceSetting queryParam = new GoodsReferencePriceSetting();
        queryParam.setGoodsId(requestDto.getGoodsId());
        List<GoodsReferencePriceSetting> list = this.getActualDao().listByQueryParams(queryParam);
        if (CollectionUtil.isEmpty(list)) {
            List<GoodsReferencePriceSetting> collect = items.stream().map(itemDto -> this.buildSettingEntity(requestDto, itemDto)).collect(Collectors.toList());
            this.getActualDao().insertList(collect);
            return;
        }

        Map<Integer, GoodsReferencePriceSetting> tradeTypeSettingMap = list.stream()
                .collect(Collectors.toMap(GoodsReferencePriceSetting::getTradeType,
                        a -> a, (k1, k2) -> k1));
        for (ReferencePriceSettingItemDto item : items) {
            GoodsReferencePriceSetting setting = tradeTypeSettingMap.get(item.getTradeType());
            if (setting == null) {
                GoodsReferencePriceSetting settingEntity = this.buildSettingEntity(requestDto, item);
                this.getActualDao().insert(settingEntity);
            } else {
                this.updateSettingEntity(setting,item);
                this.getActualDao().updateByPrimaryKey(setting);
            }
        }
    }

    private void updateSettingEntity(GoodsReferencePriceSetting setting, ReferencePriceSettingItemDto item) {
        setting.setModifiedTime(LocalDateTime.now());
        setting.setTradeType(item.getTradeType());
        setting.setFixedPrice(item.getFixedPrice());
        setting.setReferenceRule(item.getReferenceRule());
        setting.setModifierId(item.getModifierId());
    }

    private GoodsReferencePriceSetting buildSettingEntity(ReferencePriceSettingRequestDto requestDto, ReferencePriceSettingItemDto item) {
        GoodsReferencePriceSetting setting = new GoodsReferencePriceSetting();
        setting.setMarketId(requestDto.getMarketId());
        setting.setGoodsId(requestDto.getGoodsId());
        setting.setParentGoodsId(requestDto.getParentGoodsId());
        setting.setGoodsName(requestDto.getGoodsName());
        setting.setCreatedTime(LocalDateTime.now());
        setting.setModifiedTime(LocalDateTime.now());
        setting.setVersion(0);

        setting.setTradeType(item.getTradeType());
        setting.setReferenceRule(item.getReferenceRule());
        setting.setFixedPrice(item.getFixedPrice());
        setting.setCreatorId(item.getCreatorId());
        setting.setModifierId(item.getModifierId());
        return setting;
    }
}
