package com.dili.orders.strategy;

import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.WeighingTransCalcDto;

/**
 * @Description: 更新参考价中间表部分信息策略
 * @Author Tab.Xie
 * @Date 2020/9/24
 * @Version V1.0
 **/
public interface UpdateTransDataTempInfoStrategy {

    /**
     * 设置数据
     * @param transData
     * @param weighingSettlementBill
     */
    WeighingTransCalcDto setData(WeighingTransCalcDto transData, WeighingSettlementBillTemp weighingSettlementBill);
}