package com.dili.orders.service.impl;

import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.ReferencePriceDto;
import com.dili.orders.dto.WeighingTransCalcDto;
import com.dili.orders.mapper.ReferencePriceMapper;
import com.dili.orders.service.ReferencePriceService;
import com.dili.orders.strategy.UpdateTransDataTempInfoStrategyContext;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 参考价服务实现类
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
    private final String TRANS_COUNT = "transCount";

    /**
     * 下浮幅度 Code
     */
    private final String DOWNWARD_RANGE = "downwardRange";

    /**
     * 商品id的字段名
     */
    private final String GOODS_ID_CONDITION_NAME = "goodsId";
    /**
     * 今天开始时间对应的查询条件名
     */
    private final String TODAY_START_DATE_CONDITION_NAME = "todayStartDate";
    /**
     * 今天结束时间对应的查询条件名
     */
    private final String TODAY_END_DATE_CONDITION_NAME = "todayEndDate";
    /**
     * 市场id对应的查询条件名
     */
    private final String MARKET_ID_CONDITION_NAME = "marketId";
    /**
     * 交易类型对应的查询条件名
     */
    private final String TRADE_TYPE_CONDITION_NAME = "tradeType";

    @Autowired
    private DataDictionaryRpc dataDictionaryRpc;

    /**
     * 获取参考价逻辑
     *
     * @param goodsId   商品Id
     * @param marketId  市场ID
     * @param tradeType 交易类型
     * @return Long
     */
    @Override
    public Long getReferencePriceByGoodsId(Long goodsId, Long marketId, String tradeType) {
        LOGGER.info("--------------开始获取到市场" + marketId + "下该商品" + goodsId + "，交易类型为" + tradeType + "的参考价-------------");
        int transCount = getTransCountByDictionary(marketId);
        // 根据goodsId查询参考价规则表获取商品规则
        GoodsReferencePriceSetting ruleSetting = getActualDao().getGoodsRuleByGoodsId(goodsId, marketId);
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
        // 拼接查询条件
        Map<String, Object> map = new HashMap<>(4);
        map.put(GOODS_ID_CONDITION_NAME, goodsId);
        map.put(TODAY_START_DATE_CONDITION_NAME, getStartTime(0));
        map.put(TODAY_END_DATE_CONDITION_NAME, getEndTime(0));
        map.put(MARKET_ID_CONDITION_NAME, marketId);
        map.put(TRADE_TYPE_CONDITION_NAME, tradeType);

        WeighingReferencePrice todayReferencePrice = getActualDao().getReferencePriceByGoodsId(map);

        map.put(TODAY_START_DATE_CONDITION_NAME, getStartTime(-1));
        map.put(TODAY_END_DATE_CONDITION_NAME, getEndTime(-1));
        // 根据商品获取最近的参考价信息
        WeighingReferencePrice yesReferencePrice = getActualDao().getReferencePriceByGoodsId(map);
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
     *
     * @param jsonStr 交易单据json
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculateReferencePrice(WeighingSettlementBillTemp weighingSettlementBill) {
        // 根据条件获取交易数据
        Map<String, Object> map = new HashMap<>(4);
        //商品id
        map.put(GOODS_ID_CONDITION_NAME, weighingSettlementBill.getGoodsId());
        //今天的最早时间 00:00:00
        map.put(TODAY_START_DATE_CONDITION_NAME, getStartTime(0));
        //今天的最晚时间 23:59:59
        map.put(TODAY_END_DATE_CONDITION_NAME, getEndTime(0));
        //市场id
        map.put(MARKET_ID_CONDITION_NAME, weighingSettlementBill.getMarketId());
        //交易类型
        map.put(TRADE_TYPE_CONDITION_NAME, weighingSettlementBill.getTradeType());

        //计算价格中间表数据
        this.getCumulativePrice(weighingSettlementBill, map);

        // 计算并更新商品参考价
        // 查询该市场下该商品当天的交易数据
        WeighingTransCalcDto transData = getActualDao().getTransDataByGoodsId(map);

        // 取总平均值
        BigDecimal totalPrice = new BigDecimal(transData.getTotalTradeAmount());
        BigDecimal totalWeight = new BigDecimal(transData.getTotalTradeWeight());
        BigDecimal totalAvgPrice = totalPrice.divide(totalWeight.multiply(new BigDecimal(2)).divide(new BigDecimal(100)));


        WeighingReferencePrice referencePrice = new WeighingReferencePrice();

        referencePrice.setTotalAvgCount(totalAvgPrice.longValue());
        referencePrice.setGoodsId(weighingSettlementBill.getGoodsId());
        referencePrice.setMarketId(weighingSettlementBill.getMarketId());
        referencePrice.setSettlementTime(new Date());
        //获取交易价格数目
        referencePrice.setTransPriceCount(transData.getTradePriceCount());
        // 获取交易类型
        referencePrice.setTradeType(transData.getTradeType());
        //取交易笔数
        referencePrice.setTransCount(transData.getTradeCount());
        //计算去掉最大值最小值的平均值
        Long partPrice = calculationAvg(transData.getMaxTradeAmount(), transData.getMinTradeAmount()
                , transData.getMaxTradeWeight(), transData.getMinTradeWeight()
                , transData.getTotalTradeAmount(), transData.getTotalTradeWeight());
        referencePrice.setPartAvgCount(partPrice);

        // 查询该商品是否存在参考价信息 若不存在 则添加 若存在 则更新
        int isExists = getActualDao().getReferencePriceCountByGoodsIdIsExists(map);
        if (isExists > 0) {
            LOGGER.info("--------------更新参考价表数据-----------------");
            getActualDao().updateReferencePriceByGoods(referencePrice);
        } else {
            LOGGER.info("--------------添加参考价表数据-----------------");
            try {
                getActualDao().insert(referencePrice);
            } catch (DuplicateKeyException e) {
                LOGGER.info("主键冲突，更新参考价数据");
                // 查询该市场下该商品当天的交易数据
                getActualDao().updateReferencePriceByGoods(referencePrice);
            }
        }
    }

    /**
     * 计算价格中间表数据
     */
    private void getCumulativePrice(WeighingSettlementBillTemp weighingSettlementBill, Map<String, Object> map) {
        // 查询该市场下该商品当天的交易数据
        //根据商品id、市场id、当天时间查询
        WeighingTransCalcDto transData = getActualDao().getTransDataByGoodsId(map);

        // 将元/件的单据转换为元/斤
        //如果计量方式的值为2
        if (ReferencePriceDto.MONTH_TWO.equals(weighingSettlementBill.getMeasureType())) {
            //转换单位得到最终价格
            Long lastPrice = unitConversionPrice(weighingSettlementBill.getUnitPrice(), weighingSettlementBill.getUnitWeight(), weighingSettlementBill.getNetWeight());
            //设置单笔交易额
            weighingSettlementBill.setTradeAmount(lastPrice);
        }

        //若查询出的list为空 则初始化数据进行添加
        //如果当天没有交易数据
        if (transData == null) {
            //将接收到的数据设置进去进行新增操作
            //设置数据
            setWeighingSettlementBillValue(weighingSettlementBill);
            try {
                //新增操作
                getActualDao().addTransDataTempInfo(weighingSettlementBill);
                LOGGER.info("--------------新增参考价中间表数据-----------------");
                return;
            } catch (DuplicateKeyException e) {
                LOGGER.info("主键冲突，重新查询参考价中间表数据");
                // 查询该市场下该商品当天的交易数据
                transData = getActualDao().getTransDataByGoodsId(map);
            }

        }

        //更新数据 找到对应的更新策略
        UpdateTransDataTempInfoStrategyContext updateTransDataTempInfoStrategyContext = new UpdateTransDataTempInfoStrategyContext(transData, weighingSettlementBill);
        //执行更新数据
        transData = updateTransDataTempInfoStrategyContext.executeStrategy(transData, weighingSettlementBill);

        // 交易笔数加一
        transData.setTradeCount(transData.getTradeCount() + 1);
        // 累加该商品总交易额与交易量
        transData.setTotalTradeAmount(transData.getTotalTradeAmount() + weighingSettlementBill.getTradeAmount());
        transData.setTotalTradeWeight(transData.getTotalTradeWeight() + weighingSettlementBill.getNetWeight());
        transData.setSettlementTime(new Date());
        // 更新参考中间价单据表
        LOGGER.info("--------------更新参考价中间表数据-----------------");
        getActualDao().updateTransDataTempInfo(transData);
    }

    /**
     * 设置参考价临时表数据
     *
     * @param weighingSettlementBill
     */
    private void setWeighingSettlementBillValue(WeighingSettlementBillTemp weighingSettlementBill) {
        weighingSettlementBill.setMaxPrice(weighingSettlementBill.getUnitPrice());
        weighingSettlementBill.setMinPrice(weighingSettlementBill.getUnitPrice());
        weighingSettlementBill.setMaxTradeAmount(weighingSettlementBill.getTradeAmount());
        weighingSettlementBill.setMinTradeAmount(weighingSettlementBill.getTradeAmount());
        weighingSettlementBill.setMaxTradeWeight(weighingSettlementBill.getNetWeight());
        weighingSettlementBill.setMinTradeWeight(weighingSettlementBill.getNetWeight());
        weighingSettlementBill.setTotalTradeAmount(weighingSettlementBill.getTradeAmount());
        weighingSettlementBill.setTotalTradeWeight(weighingSettlementBill.getNetWeight());
        weighingSettlementBill.setTradeCount(ReferencePriceDto.MONTH_ONE);
        weighingSettlementBill.setTradePriceCount(ReferencePriceDto.MONTH_ONE);
        weighingSettlementBill.setTradeType(weighingSettlementBill.getTradeType());
    }


    /**
     * 计算去掉最大值最小值的平均值
     *
     * @param MaxPrice         最大交易价格
     * @param MinPrice         最小交易价格
     * @param MaxWeight        最大交易量
     * @param MinWeight        最小交易量
     * @param TotalTradeAmount 总交易金额
     * @param TotalTradeWeight 总交易重量
     */
    private Long calculationAvg(Long MaxPrice, Long MinPrice, Integer MaxWeight, Integer MinWeight,
                                Long TotalTradeAmount, Integer TotalTradeWeight) {
        BigDecimal maxPrice = new BigDecimal(MaxPrice);
        BigDecimal minPrice = new BigDecimal(MinPrice);
        BigDecimal maxWeight = new BigDecimal(MaxWeight);
        BigDecimal minWeight = new BigDecimal(MinWeight);
        BigDecimal totalTradeAmount = new BigDecimal(TotalTradeAmount);
        BigDecimal totalTradeWeight = new BigDecimal(TotalTradeWeight);

        //总交易额-最大价格-最小价格
        BigDecimal partPrice = totalTradeAmount.subtract(maxPrice).subtract(minPrice);
        //总交易量-最大交易量-最小交易量
        BigDecimal partWeight = totalTradeWeight.subtract(maxWeight).subtract(minWeight);
        //总交易额/(总交易量*2)/100
        partPrice = partPrice.divide(partWeight.multiply(new BigDecimal(2)).divide(new BigDecimal(100)));
        return partPrice.longValue();
    }


    /**
     * 计算将元/件的转换为元/斤后的价格
     *
     * @param UnitPrice  单价
     * @param UnitWeight 单件重量
     * @param NetWeight  订单重量
     * @return
     */
    private Long unitConversionPrice(Long UnitPrice, Integer UnitWeight, Integer NetWeight) {
        //获取单价
        BigDecimal unitPrice = new BigDecimal(UnitPrice);
        //获取件重量
        BigDecimal unitWeight = new BigDecimal(UnitWeight);
        //获取总重量
        BigDecimal netWeight = new BigDecimal(NetWeight);
        //计算多少元一斤
        //重新计算单价 =单价/(单件重量/100)    除以100得到的单位：市斤，所以得到的单价是：元/斤
        unitPrice = unitPrice.divide(unitWeight.divide(new BigDecimal(100)));
        //计算单笔交易额 = (单价*重量*2)/100
        BigDecimal price = unitPrice.multiply(netWeight.multiply(new BigDecimal(2)).divide(new BigDecimal(100)));
        //得到转化后单笔交易额
        return price.longValue();
    }


    /**
     * 从数据字典中获取下浮幅度
     *
     * @param marketId 市场编号
     * @return
     */
    private double getDownwardRangeByDictionary(Long marketId) {
        DataDictionaryValue value = DTOUtils.newInstance(DataDictionaryValue.class);
        value.setDdCode(DOWNWARD_RANGE);
        value.setFirmId(marketId);
        BaseOutput<List<DataDictionaryValue>> transCountOutput = dataDictionaryRpc.listDataDictionaryValue(value);
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
     *
     * @param referencePrice 参考价
     * @param marketId       市场编号
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
        BigDecimal range = new BigDecimal(1).subtract(new BigDecimal(downwardRange));
        BigDecimal price = new BigDecimal(referencePrice);
        price = range.multiply(price);
        return price.longValue();
    }

    /**
     * 从数据字典中获取交易笔数
     *
     * @param marketId
     * @return
     */
    private int getTransCountByDictionary(Long marketId) {
        DataDictionaryValue value = DTOUtils.newInstance(DataDictionaryValue.class);
        value.setDdCode(TRANS_COUNT);
        value.setFirmId(marketId);
        BaseOutput<List<DataDictionaryValue>> transCountOutput = dataDictionaryRpc.listDataDictionaryValue(value);
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


    /**
     * 获取当天的开始时间
     *
     * @param day 天数
     * @return
     */
    private String getStartTime(int day) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar calendar = Calendar.getInstance();
        // 时
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        // 分
        calendar.set(Calendar.MINUTE, 0);
        // 秒
        calendar.set(Calendar.SECOND, 0);
        // 毫秒
        calendar.set(Calendar.MILLISECOND, 0);
        if (day != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, day);
        }
        Date time = calendar.getTime();
        String dateStr = df.format(time);
        return dateStr;
    }

    /**
     * 获取当天的结束时间
     *
     * @param day 天数
     * @return
     */
    private String getEndTime(int day) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        // 时
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        // 分
        calendar.set(Calendar.MINUTE, 59);
        // 秒
        calendar.set(Calendar.SECOND, 59);
        // 毫秒
        calendar.set(Calendar.MILLISECOND, 999);
        if (day != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, day);
        }
        Date time = calendar.getTime();
        String dateStr = df.format(time);
        return dateStr;
    }

    /**
     * 去除小数点后多余的0
     *
     * @param s 需要去除的数
     * @return
     */
    public String replace(String s) {
        if (null != s && s.indexOf(".") > 0) {
            //去掉多余的0
            s = s.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }
}
