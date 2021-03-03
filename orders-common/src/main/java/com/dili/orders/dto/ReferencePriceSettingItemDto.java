package com.dili.orders.dto;

import com.dili.orders.domain.ReferenceRule;
import com.dili.orders.validator.ConstantValidator;
import com.dili.ss.util.MoneyUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Auther: miaoguoxin
 * @Date: 2021/2/1 14:13
 * @Description: 参考价设置细则参数
 */
public class ReferencePriceSettingItemDto implements Serializable {
    /** 交易类型 {@link com.dili.orders.domain.TradingBillType}*/
    @NotNull(message = "交易规则类型不能为空",groups = ConstantValidator.Insert.class)
    private Integer tradeType;
    /**参考价规则{@link com.dili.orders.domain.ReferenceRule}*/
    @NotNull(message = "规则不能为空",groups = ConstantValidator.Insert.class)
    private Integer referenceRule;
    /**固定价格*/
    private Long fixedPrice;
    /**创建人id*/
    private Long creatorId;
    /**修改人id*/
    private Long modifierId;

    public String getReferenceRuleText() {
        ReferenceRule enabledState = ReferenceRule.getEnabledState(referenceRule);
        if (enabledState == null){
            return null;
        }
        return enabledState.getName();
    }

    public String getFixedPriceText(){
        if (fixedPrice == null){
            return "";
        }
        return MoneyUtils.centToYuan(fixedPrice);
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getModifierId() {
        return modifierId;
    }

    public void setModifierId(Long modifierId) {
        this.modifierId = modifierId;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public Integer getReferenceRule() {
        return referenceRule;
    }

    public void setReferenceRule(Integer referenceRule) {
        this.referenceRule = referenceRule;
    }

    public Long getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(Long fixedPrice) {
        this.fixedPrice = fixedPrice;
    }
}
