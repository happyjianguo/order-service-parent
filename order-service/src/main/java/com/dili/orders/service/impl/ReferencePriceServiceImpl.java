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

    @Value("${busin.transCount}")
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
        // 根据商品获取最近的参考价信息
        WeighingReferencePrice referencePrice = getActualDao().getReferencePriceByGoodsId(goodsId,marketId);
        if (referencePrice == null) {
            return null;
        }
        if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_ONE) {
            if (referencePrice.getTransCount() > transCount && referencePrice.getTransPriceCount() > ReferencePriceDto.TRANS_PRICE_COUNT) {
                return referencePrice.getPartAvgCount();
            } else {
                return referencePrice.getTotalAvgCount();
            }
        } else if (ruleSetting.getReferenceRule() == ReferencePriceDto.RULE_TWO) {
            if (referencePrice.getTransPriceCount() > ReferencePriceDto.TRANS_PRICE_COUNT) {
                return referencePrice.getPartAvgCount();
            } else {
                return referencePrice.getTotalAvgCount();
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
        getActualDao().addTransDataTempInfo(weighingSettlementBill);
        // 根据条件获取交易数据
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> map = new HashMap<>(4);
        map.put("goodsId",weighingSettlementBill.getGoodsId());
        map.put("todayStartDate",sdf.format(getStartTime()));
        map.put("todayEndDate",sdf.format(getEndTime()));
        map.put("marketId",weighingSettlementBill.getMarketId());
        List<WeighingTransCalcDto> transList = getActualDao().getTransDataByGoodsId(map);
        int currentCount = 0;
        // 获取当前交易笔数
        for (WeighingTransCalcDto calcDto: transList) {
            currentCount+=calcDto.getCransCount();
        }
        // 若当前交易笔数小于配置的交易笔数 则不执行计算
        if (currentCount <= transCount) {
            return;
        }
        //将 元/件 的价格转换为 元/斤
        for (WeighingTransCalcDto calcDto: transList) {
            if ("2".equals(calcDto.getMeasureType())) {
                double unitPrice = calcDto.getUnitPrice();
                double unitWeight = calcDto.getUnitWeight();
                double netWeight = calcDto.getNetWeight();
                unitPrice = unitPrice/(unitWeight/100);
                double price = unitPrice*(netWeight*2/100);
                calcDto.setTradeAmount(Long.valueOf(replace(String.valueOf(price))));
            }
        }
        // 将笔数、金额、件数累加到对象中 以便后续计算
        WeighingTransCalcDto transCalcDto = new WeighingTransCalcDto();
        WeighingReferencePrice referencePrice = new WeighingReferencePrice();
        for (int i = 0;i < transList.size();i++) {
            WeighingTransCalcDto calcDto = transList.get(i);
            if (i == 0) {
                transCalcDto.setCransCount(calcDto.getCransCount());
                transCalcDto.setTradeAmount(calcDto.getTradeAmount());
                transCalcDto.setUnitAmount(calcDto.getUnitAmount());
                transCalcDto.setNetWeight(calcDto.getNetWeight());
            } else {
                transCalcDto.setCransCount(calcDto.getCransCount()+transCalcDto.getCransCount());
                transCalcDto.setTradeAmount(calcDto.getTradeAmount()+transCalcDto.getTradeAmount());
                transCalcDto.setUnitAmount(calcDto.getUnitAmount()+ transCalcDto.getUnitAmount());
                transCalcDto.setNetWeight(calcDto.getNetWeight()+transCalcDto.getNetWeight());
            }

        }
        // 取总平均值
        double totalPrice = transCalcDto.getTradeAmount();
        double totalWeight = transCalcDto.getNetWeight();
        double totalAvgPrice = totalPrice/(totalWeight*2/100);
        totalAvgPrice=Math.round(totalAvgPrice);

        referencePrice.setTotalAvgCount(Long.valueOf(replace(String.valueOf(totalAvgPrice))));
        referencePrice.setGoodsId(weighingSettlementBill.getGoodsId());
        referencePrice.setMarketId(weighingSettlementBill.getMarketId());
        referencePrice.setSettlementTime(new Date());
        //获取交易价格数目
        Set set = new HashSet();
        for (WeighingTransCalcDto calcDto: transList) {
            set.add(calcDto.getUnitPrice());
        }
        referencePrice.setTransPriceCount(set.size());

        //取交易笔数
        referencePrice.setTransCount(transCalcDto.getCransCount());

        //计算去掉最大值最小值的平均值
        WeighingTransCalcDto maxPrice = transList.stream().max(Comparator.comparing(WeighingTransCalcDto::getUnitPrice)).get();
        WeighingTransCalcDto minPrice = transList.stream().min(Comparator.comparing(WeighingTransCalcDto::getUnitPrice)).get();
        long totalMaxPrice = 0,totalMinPrice = 0;
        int delWeight = 0;
        for (WeighingTransCalcDto calcDto: transList) {
            if (maxPrice.getUnitPrice().equals(calcDto.getUnitPrice())) {
                totalMaxPrice += calcDto.getTradeAmount();
                delWeight += calcDto.getNetWeight();
            }
            if (minPrice.getUnitPrice().equals(calcDto.getUnitPrice())) {
                totalMinPrice += calcDto.getTradeAmount();
                delWeight += calcDto.getNetWeight();
            }
        }
        double partPrice = transCalcDto.getTradeAmount()-totalMaxPrice-totalMinPrice;
        double partWeight = transCalcDto.getNetWeight()-delWeight;
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

    private Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    private Date getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
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
