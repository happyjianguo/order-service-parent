package com.dili.orders.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.orders.domain.GoodsReferencePriceSetting;
import com.dili.orders.domain.WeighingReferencePrice;
import com.dili.orders.domain.WeighingSettlementBillTemp;
import com.dili.orders.dto.ReferencePriceDto;
import com.dili.orders.dto.WeighingTransCalcDto;
import com.dili.orders.mapper.ReferencePriceMapper;
import com.dili.orders.service.ReferencePriceService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.domain.dto.UapDataDictionaryDto;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Tyler
 * @date 2020年8月17日09:43:29
 */
@Service
public class ReferencePriceServiceImpl extends BaseServiceImpl<WeighingReferencePrice,Long> implements ReferencePriceService {

    private ReferencePriceMapper getActualDao(){return (ReferencePriceMapper)getDao();}

    /**
     * 交易笔数 Code
     */
    private final String TRANS_COUNT = "transCount";

    /**
     * 下浮幅度 Code
     */
    private final String DOWNWARD_RANGE = "downwardRange";

    @Autowired
    private DataDictionaryRpc dataDictionaryRpc;

    /**
     * 获取参考价逻辑
     * @param goodsId 商品Id
     * @param marketId 市场ID
     * @param tradeType 交易类型
     * @return Long
     */
    @Override
    public Long getReferencePriceByGoodsId(Long goodsId, Long marketId, String tradeType) {
        LOGGER.info("--------------开始获取到市场"+marketId+"下该商品"+goodsId+"，交易类型为"+tradeType+"的参考价-------------");
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
        Map<String,Object> map = new HashMap<>(4);
        map.put("goodsId", goodsId);
        map.put("todayStartDate", getStartTime(0));
        map.put("todayEndDate", getEndTime(0));
        map.put("marketId", marketId);
        map.put("tradeType",tradeType);

        WeighingReferencePrice todayReferencePrice = getActualDao().getReferencePriceByGoodsId(map);

        map.put("todayStartDate", getStartTime(-1));
        map.put("todayEndDate", getEndTime(-1));
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
            } else{
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
                return calculateReferencePriceByDownwardRange(todayReferencePrice.getTotalAvgCount(),marketId);
            } else {
                // 取上一日的参考价数据
                if (yesReferencePrice == null) {
                    LOGGER.info("--------------上一日参考价中间表无商品数据-------------");
                    return null;
                }
                return calculateReferencePriceByDownwardRange(yesReferencePrice.getTotalAvgCount(),marketId);
            }
        }
        LOGGER.info("--------------未获取到市场"+marketId+"下商品"+goodsId+"，交易类型为"+tradeType+"所匹配的参考价-------------");
        return null;
    }

    /**
     * 根据商品计算参考价规则
     * @param jsonStr 交易单据json
     */
    @Override
    @Transactional
    public void calculateReferencePrice(String jsonStr) {
        // 将传入的JSON串转换为中间表对象添加到中间表
        WeighingSettlementBillTemp weighingSettlementBill = JSONObject.parseObject(jsonStr,WeighingSettlementBillTemp.class);
        if (weighingSettlementBill == null) {
            LOGGER.info("格式转换错误，传入的消息无法正确进行转换");
            return;
        }
        // 根据条件获取交易数据
        Map<String,Object> map = new HashMap<>(4);
        map.put("goodsId", weighingSettlementBill.getGoodsId());
        map.put("todayStartDate", getStartTime(0));
        map.put("todayEndDate", getEndTime(0));
        map.put("marketId", weighingSettlementBill.getMarketId());
        map.put("tradeType", weighingSettlementBill.getTradeType());
        getCumulativePrice(weighingSettlementBill,map);
        // 查询该市场下该商品当天的交易数据
        WeighingTransCalcDto transData = getActualDao().getTransDataByGoodsId(map);

        //从数据字典获取配置的交易笔数
        int transCount = getTransCountByDictionary(weighingSettlementBill.getMarketId());

        // 若当前交易笔数小于配置的交易笔数 则不执行计算
        if (transData.getTradeCount() <= transCount) {
            LOGGER.info("--------------当前商品交易笔数小于或等于配置的交易笔数-----------------");
            return;
        }

        WeighingReferencePrice referencePrice = new WeighingReferencePrice();

        // 取总平均值
        double totalPrice = transData.getTotalTradeAmount();
        double totalWeight = transData.getTotalTradeWeight();
        double totalAvgPrice = totalPrice/(totalWeight*2/100);
        totalAvgPrice = Math.round(totalAvgPrice);

        referencePrice.setTotalAvgCount(Long.valueOf(replace(String.valueOf(totalAvgPrice))));
        referencePrice.setGoodsId(weighingSettlementBill.getGoodsId());
        referencePrice.setMarketId(weighingSettlementBill.getMarketId());
        referencePrice.setSettlementTime(new Date());

        //获取交易价格数目
        referencePrice.setTransPriceCount(transData.getTradePriceCount());

        // 获取交易类型
        referencePrice.setTardeType(transData.getTradeType());

        //取交易笔数
        referencePrice.setTransCount(transData.getTradeCount());

        //计算去掉最大值最小值的平均值
        long totalMaxPrice = transData.getMaxTradeAmount();
        long totalMinPrice = transData.getMinTradeAmount();
        int maxWeight = transData.getMaxTradeWeight();
        int minWeight = transData.getMinTradeWeight();

        double partPrice = transData.getTotalTradeAmount()-totalMaxPrice-totalMinPrice;
        double partWeight = transData.getTotalTradeWeight()-maxWeight-minWeight;
        partPrice = partPrice/(partWeight*2/100);
        partPrice = Math.round(partPrice);
        referencePrice.setPartAvgCount(Long.valueOf(replace(String.valueOf(partPrice))));

        // 查询该商品是否存在参考价信息 若不存在 则添加 若存在 则更新
        int isExists =  getActualDao().getReferencePriceCountByGoodsIdIsExists(map);
        if (isExists > 0) {
            LOGGER.info("--------------更新参考价表数据-----------------");
            getActualDao().updateReferencePriceByGoods(referencePrice);
        } else {
            LOGGER.info("--------------添加参考价表数据-----------------");
            getActualDao().insert(referencePrice);
        }
    }

    /**
     * 计算价格中间表数据
     */
    private void getCumulativePrice(WeighingSettlementBillTemp weighingSettlementBill,Map<String,Object> map) {
        // 查询该市场下该商品当天的交易数据
        WeighingTransCalcDto transData = getActualDao().getTransDataByGoodsId(map);

        // 将元/件的单据转换为元/斤
        if (ReferencePriceDto.MONTH_TWO.equals(weighingSettlementBill.getMeasureType())) {
            double unitPrice = weighingSettlementBill.getUnitPrice();
            double unitWeight = weighingSettlementBill.getUnitWeight();
            double netWeight = weighingSettlementBill.getNetWeight();
            unitPrice = unitPrice/(unitWeight/100);
            double price = unitPrice*(netWeight*2/100);
            price=Math.round(price);
            weighingSettlementBill.setTradeAmount(Long.valueOf(replace(String.valueOf(price))));
        }

        //若查询出的list为空 则初始化数据进行添加
        if (transData == null) {
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
            getActualDao().addTransDataTempInfo(weighingSettlementBill);
            LOGGER.info("--------------新增参考价中间表数据-----------------");
            return;
        }

        if (weighingSettlementBill.getUnitPrice() > transData.getMaxPrice()) {
            // 若接收到的单据中单价大于最高单价
            // 更新最大价格、最大交易额、最大交易量、交易价格数
            transData.setMaxPrice(weighingSettlementBill.getUnitPrice());
            transData.setMaxTradeAmount(weighingSettlementBill.getTradeAmount());
            transData.setMaxTradeWeight(weighingSettlementBill.getNetWeight());
            transData.setTradePriceCount(transData.getTradePriceCount()+1);
        } else if (weighingSettlementBill.getUnitPrice() < transData.getMinPrice()) {
            // 若接收到的单据中单价小于最低价格
            // 更新最小价格、最小交易额、最小交易量、交易价格数
            transData.setMinPrice(weighingSettlementBill.getUnitPrice());
            transData.setMinTradeAmount(weighingSettlementBill.getTradeAmount());
            transData.setMinTradeWeight(weighingSettlementBill.getNetWeight());
            transData.setTradePriceCount(transData.getTradePriceCount()+1);
        } else if (transData.getMaxPrice().equals(weighingSettlementBill.getUnitPrice())) {
            // 若等于最大价格 则更新最大交易额、最大交易量
            transData.setMaxTradeAmount(transData.getMaxTradeAmount()+weighingSettlementBill.getTradeAmount());
            transData.setMaxTradeWeight(transData.getMaxTradeWeight()+weighingSettlementBill.getNetWeight());
        } else if (transData.getMinPrice().equals(weighingSettlementBill.getUnitPrice())) {
            // 若等于最小价格 则更新最小交易额、最小交易量
            transData.setMinTradeAmount(transData.getMinTradeAmount()+weighingSettlementBill.getTradeAmount());
            transData.setMinTradeWeight(transData.getMinTradeWeight()+weighingSettlementBill.getNetWeight());
        } else {
            // 若小于最大值 大于最小值 更新交易价格数
            transData.setTradePriceCount(transData.getTradePriceCount()+1);
        }

        // 交易笔数加一
        transData.setTradeCount(transData.getTradeCount()+1);
        // 累加该商品总交易额与交易量
        transData.setTotalTradeAmount(transData.getTotalTradeAmount()+weighingSettlementBill.getTradeAmount());
        transData.setTotalTradeWeight(transData.getTotalTradeWeight()+weighingSettlementBill.getNetWeight());
        transData.setSettlementTime(new Date());
        // 更新参考中间价单据表
        LOGGER.info("--------------更新参考价中间表数据-----------------");
        getActualDao().updateTransDataTempInfo(transData);
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
        List<DataDictionaryValue> valueList = transCountOutput.getData();
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (DataDictionaryValue obj : valueList ) {
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
    private Long calculateReferencePriceByDownwardRange(Long referencePrice,Long marketId) {
        double downwardRange = getDownwardRangeByDictionary(marketId);
        if (referencePrice == null || referencePrice == 0) {
            return 0L;
        }
        if (downwardRange == 0) {
            return referencePrice;
        }
        double range = 1-downwardRange;
        double price = referencePrice;
        price = price*range;
        price = Math.round(price);
        return Long.valueOf(replace(String.valueOf(price)));
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
        List<DataDictionaryValue> valueList = transCountOutput.getData();
        if (CollectionUtils.isNotEmpty(valueList)) {
            for (DataDictionaryValue obj : valueList ) {
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
     * @param s 需要去除的数
     * @return
     */
    public String replace(String s){
        if(null != s && s.indexOf(".") > 0){
            //去掉多余的0
            s = s.replaceAll("0+?$", "");
            //如最后一位是.则去掉
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }
}
