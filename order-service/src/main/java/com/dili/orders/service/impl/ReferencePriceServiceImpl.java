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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Tyler
 * @date 2020年8月17日09:43:29
 */
@Service
public class ReferencePriceServiceImpl extends BaseServiceImpl<WeighingReferencePrice,Long> implements ReferencePriceService {

    private ReferencePriceMapper getActualDao(){return (ReferencePriceMapper)getDao();}

    @Value("${busin.transCount:30}")
    private int transCount;

    /**
     * 获取参考价逻辑
     * @param goodsId
     * @return Double
     */
    @Override
    public Long getReferencePriceByGoodsId(Long goodsId,Long marketId) {
        // 根据goodsId查询参考价规则表获取商品规则
        GoodsReferencePriceSetting ruleSetting = getActualDao().getGoodsRuleByGoodsId(goodsId,marketId);
        if (ruleSetting == null) {
            return null;
        }
        if (ruleSetting.getReferenceRule() == null) {
            return null;
        }
        if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_THREE) {
            return ruleSetting.getFixedPrice();
        }
        // 拼接查询条件
        Map<String,Object> map = new HashMap<>(4);
        map.put("goodsId",goodsId);
        map.put("todayStartDate",getStartTime(0));
        map.put("todayEndDate",getEndTime(0));
        map.put("marketId",marketId);

        WeighingReferencePrice todayReferencePrice = getActualDao().getReferencePriceByGoodsId(map);
        if (todayReferencePrice == null) {
            return null;
        }

        map.put("todayStartDate",getStartTime(-1));
        map.put("todayEndDate",getEndTime(-1));
        // 根据商品获取最近的参考价信息
        WeighingReferencePrice yesReferencePrice = getActualDao().getReferencePriceByGoodsId(map);

        if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_ONE) {
            // 取上一日的参考价数据
            if (yesReferencePrice == null) {
                return null;
            }
            if (todayReferencePrice.getTransCount() > transCount && todayReferencePrice.getTransPriceCount() > ReferencePriceDto.TRANS_PRICE_COUNT) {
                return yesReferencePrice.getPartAvgCount();
            } else {
                return yesReferencePrice.getTotalAvgCount();
            }
        } else if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_TWO) {
            if (todayReferencePrice.getTransPriceCount() > ReferencePriceDto.TRANS_PRICE_COUNT) {
                return todayReferencePrice.getPartAvgCount();
            } else {
                return todayReferencePrice.getTotalAvgCount();
            }
        }

        return null;
    }

    /**
     * 根据商品计算参考价规则
     * @param jsonStr
     */
    @Override
    public void calcReferencePrice(String jsonStr) {
        // 将传入的JSON串转换为中间表对象添加到中间表
        WeighingSettlementBillTemp weighingSettlementBill = JSONObject.parseObject(jsonStr,WeighingSettlementBillTemp.class);
        // 根据条件获取交易数据
        Map<String,Object> map = new HashMap<>(4);
        map.put("goodsId",weighingSettlementBill.getGoodsId());
        map.put("todayStartDate",getStartTime(0));
        map.put("todayEndDate",getEndTime(0));
        map.put("marketId",weighingSettlementBill.getMarketId());
        getCumulativePrice(weighingSettlementBill,map);
        // 查询该市场下该商品当天的交易数据
        WeighingTransCalcDto transData = getActualDao().getTransDataByGoodsId(map);

        // 若当前交易笔数小于配置的交易笔数 则不执行计算
        if (transData.getTradeCount() <= transCount) {
            return;
        }
        if (transData.getTradePriceCount() <= ReferencePriceDto.TRANS_PRICE_COUNT) {
            return;
        }

        WeighingReferencePrice referencePrice = new WeighingReferencePrice();

        // 取总平均值
        double totalPrice = transData.getTotalTradeAmount();
        double totalWeight = transData.getTotalTradeWeight();
        double totalAvgPrice = totalPrice/(totalWeight*2/100);
        totalAvgPrice=Math.round(totalAvgPrice);

        referencePrice.setTotalAvgCount(Long.valueOf(replace(String.valueOf(totalAvgPrice))));
        referencePrice.setGoodsId(weighingSettlementBill.getGoodsId());
        referencePrice.setMarketId(weighingSettlementBill.getMarketId());
        referencePrice.setSettlementTime(new Date());

        //获取交易价格数目
        referencePrice.setTransPriceCount(transData.getTradePriceCount());

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
        partPrice=Math.round(partPrice);
        referencePrice.setPartAvgCount(Long.valueOf(replace(String.valueOf(partPrice))));

        // 查询该商品是否存在参考价信息 若不存在 则添加 若存在 则更新
        int isExists =  getActualDao().getReferencePriceCountByGoodsIdIsExists(map);
        if (isExists > 0) {
            getActualDao().updateReferencePriceByGoods(referencePrice);
        } else {
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
        if ("2".equals(weighingSettlementBill.getMeasureType())) {
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
            weighingSettlementBill.setTradeCount(1);
            weighingSettlementBill.setTradePriceCount(1);
            getActualDao().addTransDataTempInfo(weighingSettlementBill);
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
        // 累加总交易额与交易量
        transData.setTotalTradeAmount(transData.getTotalTradeAmount()+weighingSettlementBill.getTradeAmount());
        transData.setTotalTradeWeight(transData.getTotalTradeWeight()+weighingSettlementBill.getNetWeight());
        transData.setSettlementTime(new Date());
        // 更新参考中间价单据表
        getActualDao().updateTransDataTempInfo(transData);
    }



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
