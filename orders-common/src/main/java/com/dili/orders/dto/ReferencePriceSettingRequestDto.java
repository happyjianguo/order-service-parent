package com.dili.orders.dto;

import com.dili.orders.validator.ConstantValidator;
import com.dili.ss.domain.BaseDomain;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Auther: miaoguoxin
 * @Date: 2021/2/1 14:09
 * @Description: 参考价设置请求参数
 */
public class ReferencePriceSettingRequestDto extends BaseDomain {
    /**市场id*/
    private Long marketId;
    /**商品id*/
    @NotNull(message = "商品id不能为空", groups = ConstantValidator.Insert.class)
    private Long goodsId;
    /**品类名称*/
    @NotBlank(message = "商品名称不能为空", groups = ConstantValidator.Insert.class)
    private String goodsName;
    /**父级商品id*/
    private Long parentGoodsId;
    /**详情信息*/
    @Valid
    @NotNull(message = "规则细则不能为空", groups = ConstantValidator.Insert.class)
    private List<ReferencePriceSettingItemDto> items;

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getParentGoodsId() {
        return parentGoodsId;
    }

    public void setParentGoodsId(Long parentGoodsId) {
        this.parentGoodsId = parentGoodsId;
    }


    public List<ReferencePriceSettingItemDto> getItems() {
        return items;
    }

    public void setItems(List<ReferencePriceSettingItemDto> items) {
        this.items = items;
    }
}
