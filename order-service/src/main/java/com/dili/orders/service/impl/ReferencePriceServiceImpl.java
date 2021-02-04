package com.dili.orders.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.domain.WeighingSettlementBillDaily;
import com.dili.orders.dto.ReferencePriceDto;
import com.dili.orders.dto.ReferencePriceQueryDto;
import com.dili.orders.dto.WeighingSettlementDto;
import com.dili.orders.mapper.ReferencePriceMapper;
import com.dili.orders.service.ReferencePriceService;
import com.dili.orders.service.referenceprice.ReferencePriceCalculator;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.BusinessException;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Tyler
 * @date 2020年8月17日09:43:29
 */
@Service
public class ReferencePriceServiceImpl extends BaseServiceImpl<WeighingReferencePrice, Long> implements ReferencePriceService {

    private ReferencePriceMapper getActualDao() {
        return (ReferencePriceMapper) getDao();
    }

    /**
     * 交易笔数 Code
     */
    private static final String TRANS_COUNT = "transCount";

    /**
     * 下浮幅度 Code
     */
    private static final String DOWNWARD_RANGE = "downwardRange";

    @Autowired
    private DataDictionaryRpc dataDictionaryRpc;
    @Autowired
    private ReferencePriceCalculator referencePriceCalculator;

    /**
     * 获取参考价逻辑
     * @param goodsId 商品Id
     * @param marketId 市场ID
     * @param tradeType 交易类型
     * @return Long
     */
    @Override
    public Long getReferencePriceByGoodsId(Long goodsId, Long marketId, String tradeType, Integer type) {
        int transCount = getTransCountByDictionary(marketId);
        // 根据goodsId查询参考价规则表获取商品规则
        GoodsReferencePriceSetting ruleSetting = getActualDao().getGoodsRuleByGoodsIdAndTradeType(goodsId, marketId, type);
        if (ruleSetting == null) {
            LOGGER.info("--------------当前市场下的商品暂无配置商品规则-------------");
            return null;
        }

        if (ruleSetting.getReferenceRule() == null) {
            LOGGER.info("--------------当前市场下的商品暂无配置商品规则-------------");
            return null;
        }
        if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_THREE) {
            return ruleSetting.getFixedPrice();
        }

        //查询当日参考价信息
        ReferencePriceQueryDto queryParam = new ReferencePriceQueryDto();
        queryParam.setGoodsId(goodsId);
        queryParam.setMarketId(marketId);
        queryParam.setTradeType(tradeType);
        queryParam.setSettlementDay(DateUtil.today());
        WeighingReferencePrice todayReferencePrice = this.getActualDao().getReferencePriceByGoodsId(queryParam);

        // 查询昨日参考价信息
        queryParam.setSettlementDay(DateUtil.yesterday().toString("yyyy-MM-dd"));
        WeighingReferencePrice yesReferencePrice = getActualDao().getReferencePriceByGoodsId(queryParam);
        // 若为规则1
        if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_ONE) {
            // 判断当天是否有数据、并且交易笔数是否大于N
            if (todayReferencePrice != null && todayReferencePrice.getTransCount() > transCount) {
                // 交易金额数是否大于2
                if (todayReferencePrice.getTransPriceCount() > ReferencePriceDto.TRANS_PRICE_COUNT) {
                    return calculateReferencePriceByDownwardRange(todayReferencePrice.getPartAvgCount(), marketId);
                } else {
                    return calculateReferencePriceByDownwardRange(todayReferencePrice.getTotalAvgCount(), marketId);
                }
            } else {
                // 取上一日的参考价数据
                if (yesReferencePrice == null) {
                    LOGGER.info("--------------上一日参考价中间表无商品数据-------------");
                    return null;
                }
                // 判断上一日交易金额是否大于N 、 交易价格数是否大于2
                if (yesReferencePrice.getTransCount() > transCount && yesReferencePrice.getTransPriceCount() > ReferencePriceDto.TRANS_PRICE_COUNT) {
                    return calculateReferencePriceByDownwardRange(yesReferencePrice.getPartAvgCount(), marketId);
                } else {
                    return calculateReferencePriceByDownwardRange(yesReferencePrice.getTotalAvgCount(), marketId);
                }
            }
            // 若为规则二
        } else if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_TWO) {
            if (todayReferencePrice != null && todayReferencePrice.getTransCount() > transCount) {
                return calculateReferencePriceByDownwardRange(todayReferencePrice.getTotalAvgCount(), marketId);
            } else {
                // 取上一日的参考价数据
                if (yesReferencePrice == null) {
                    LOGGER.info("--------------上一日参考价中间表无商品数据-------------");
                    return null;
                }
                return calculateReferencePriceByDownwardRange(yesReferencePrice.getTotalAvgCount(), marketId);
            }
        }
        LOGGER.info("--------------未获取到市场" + marketId + "下商品" + goodsId + "，交易类型为" + tradeType + "所匹配的参考价-------------");
        return null;
    }


    /**
     * 根据商品计算参考价规则
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateReferencePrice(WeighingSettlementDto weighingSettlementBill) {
        // 根据条件获取交易数据
        ReferencePriceQueryDto queryParam = new ReferencePriceQueryDto();
        queryParam.setGoodsId(weighingSettlementBill.getGoodsId());
        queryParam.setMarketId(weighingSettlementBill.getMarketId());
        queryParam.setTradeType(weighingSettlementBill.getTradeType());
        queryParam.setSettlementDay(DateUtil.today());

        // 计算每日参考价数据
        WeighingSettlementBillDaily transData = this.getCumulativePrice(weighingSettlementBill, queryParam);
        //计算去掉最大值最小值的平均值
        Long partPrice = referencePriceCalculator.getReferenceAvgPrice(transData);
        //总平均值
        Long totalAvgPrice = referencePriceCalculator.getTotalAvgPrice(transData.getTotalTradeAmount(),
                transData.getTotalTradeWeight());

        WeighingReferencePrice referencePrice = this.getActualDao().getReferencePriceByGoodsId(queryParam);
        if (referencePrice != null) {
            LOGGER.info("--------------更新参考价表数据-----------------");
            referencePrice.copyFromDailyData(transData, totalAvgPrice, partPrice);
            this.getActualDao().updateReferencePrice(referencePrice);
        } else {
            LOGGER.info("--------------添加参考价表数据-----------------");
            referencePrice = new WeighingReferencePrice();
            referencePrice.copyFromDailyData(transData, totalAvgPrice, partPrice);
            this.getActualDao().insert(referencePrice);
        }
    }

    /**
     * 计算每日参考价数据
     */
    private WeighingSettlementBillDaily getCumulativePrice(WeighingSettlementDto weighingSettlementBill, ReferencePriceQueryDto queryParam) {
        // 查询当天的交易数据
        WeighingSettlementBillDaily transData = this.getActualDao().getTransDataByGoodsId(queryParam);
        //计算交易额
        Long tradeAmount = referencePriceCalculator.getTradeAmount(weighingSettlementBill);
        weighingSettlementBill.setTradeAmount(tradeAmount);
        //计算单价
        Long unitPrice = referencePriceCalculator.getUnitPrice(weighingSettlementBill);
        if (transData == null) {
            transData = WeighingSettlementBillDaily.create(weighingSettlementBill, unitPrice);
            getActualDao().insertDaily(transData);
            LOGGER.info("--------------新增每日参考价数据-----------------");
            return transData;
        }
        //比较价格并设置值
        referencePriceCalculator.comparePrice(transData, weighingSettlementBill);

        transData.setTradeCount(transData.getTradeCount() + 1);
        transData.setTotalTradeAmount(transData.getTotalTradeAmount() + weighingSettlementBill.getTradeAmount());
        transData.setTotalTradeWeight(transData.getTotalTradeWeight() + weighingSettlementBill.getNetWeight());
        // 更新参考价单据表
        LOGGER.info("--------------更新每日参考价数据-----------------");
        this.getActualDao().updateDaily(transData);
        return transData;
    }

    /**
     * 从数据字典中获取下浮幅度
     * @param marketId 市场编号
     * @return
     */
    private double getDownwardRangeByDictionary(Long marketId) {
        DataDictionaryValue value = DTOUtils.newInstance(DataDictionaryValue.class);
        value.setDdCode(DOWNWARD_RANGE);
        value.setFirmId(marketId);
        BaseOutput<List<DataDictionaryValue>> transCountOutput = dataDictionaryRpc.listDataDictionaryValue(value);
        if (!transCountOutput.getCode().equals(ResultCode.OK)) {
            LOGGER.error("获取下浮幅度配置失败:{}", JSON.toJSONString(transCountOutput));
            throw new BusinessException(ResultCode.DATA_ERROR, "获取下浮幅度配置失败");
        }
        List<DataDictionaryValue> valueList = transCountOutput.getData();
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (DataDictionaryValue obj : valueList) {
                if (DOWNWARD_RANGE.equals(obj.getCode())) {
                    return Double.valueOf(obj.getName());
                }
            }
        }
        LOGGER.info("--------------数据字典中未获取到下浮幅度-----------------");
        return 0;
    }

    /**
     * 根据下浮幅度计算最终参考价
     * @param referencePrice 参考价
     * @param marketId 市场编号
     * @return
     */
    private Long calculateReferencePriceByDownwardRange(Long referencePrice, Long marketId) {
        double downwardRange = getDownwardRangeByDictionary(marketId);
        if (referencePrice == null || referencePrice == 0) {
            return 0L;
        }
        if (downwardRange == 0) {
            return referencePrice;
        }
        return referencePriceCalculator.getDownwardRangeReferencePrice(downwardRange, referencePrice);
    }

    /**
     * 从数据字典中获取交易笔数
     * @param marketId
     * @return
     */
    private int getTransCountByDictionary(Long marketId) {
        DataDictionaryValue value = DTOUtils.newInstance(DataDictionaryValue.class);
        value.setDdCode(TRANS_COUNT);
        value.setFirmId(marketId);
        BaseOutput<List<DataDictionaryValue>> transCountOutput = dataDictionaryRpc.listDataDictionaryValue(value);
        if (!transCountOutput.getCode().equals(ResultCode.OK)) {
            LOGGER.error("获取交易笔数配置失败:{}", JSON.toJSONString(transCountOutput));
            throw new BusinessException(ResultCode.DATA_ERROR, "获取交易笔数配置失败");
        }
        List<DataDictionaryValue> valueList = transCountOutput.getData();
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (DataDictionaryValue obj : valueList) {
                if (TRANS_COUNT.equals(obj.getCode())) {
                    return Integer.valueOf(obj.getName());
                }
            }
        }
        LOGGER.info("--------------数据字典中未获取到交易笔数-----------------");
        return 0;
    }

}
