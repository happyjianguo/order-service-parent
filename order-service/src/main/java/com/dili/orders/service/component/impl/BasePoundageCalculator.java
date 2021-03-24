package com.dili.orders.service.component.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.enums.BusinessChargeItemEnum;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.commons.glossary.YesOrNoEnum;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.MeasureType;
import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.service.component.FeeCalculator;
import com.dili.orders.service.util.DomainUtil;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.exception.AppException;
import com.dili.ss.util.MoneyUtils;

public class BasePoundageCalculator<T extends PoundageCalculateParam> implements FeeCalculator<T> {

	protected ChargeRuleRpc chargeRuleRpc;
	protected BusinessChargeItemRpc businessChargeItemRpc;

	public BasePoundageCalculator(ChargeRuleRpc chargeRuleRpc, BusinessChargeItemRpc businessChargeItemRpc) {
		super();
		this.chargeRuleRpc = chargeRuleRpc;
		this.businessChargeItemRpc = businessChargeItemRpc;
	}

	@Override
	public Long calculate(T param) {
		String businessType = param.getBusinessType();
		WeighingBill weighingBill = param.getWeighingBill();
		WeighingStatement statement = param.getWeighingStatement();
		List<Long> chargeItemIds = this.getBuyerSellerRuleId(businessType, weighingBill.getMarketId());
		List<QueryFeeInput> queryFeeInputList = new ArrayList<QueryFeeInput>(chargeItemIds.size());
		chargeItemIds.forEach(c -> {
			QueryFeeInput queryFeeInput = new QueryFeeInput();
			Map<String, Object> map = new HashMap<>();
			// 设置市场id
			queryFeeInput.setMarketId(weighingBill.getMarketId());
			// 设置业务类型
			queryFeeInput.setBusinessType(businessType);
			queryFeeInput.setChargeItem(c);
			if (businessType.equals(OrdersConstant.WEIGHING_BILL_BUYER_POUNDAGE_BUSINESS_TYPE)) {
				map.put("customerType", weighingBill.getBuyerType());
				map.put("customerId", weighingBill.getBuyerId());
			} else {
				map.put("customerType", weighingBill.getSellerType());
				map.put("customerId", weighingBill.getSellerId());
			}
			map.put("goodsId", weighingBill.getGoodsId());
			LocalDateTime tradeTime = statement.getCreatedTime();
			if (tradeTime == null) {
				tradeTime = LocalDateTime.now();
			}
			map.put("tradeTime", tradeTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			if (weighingBill.getMeasureType().equals(MeasureType.WEIGHT.getValue())) {
				// 单价转换为元/公斤
				map.put("unitPrice", new BigDecimal(MoneyUtils.centToYuan(new BigDecimal(weighingBill.getUnitPrice() * 2).longValue())));
				map.put("totalWeight", new BigDecimal(MoneyUtils.centToYuan(weighingBill.getNetWeight())));
			} else {
				// 斤转换为公斤
				map.put("unitPrice", new BigDecimal(DomainUtil.getConvertDoubleUnitPrice(weighingBill)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
				map.put("totalWeight", new BigDecimal(weighingBill.getUnitAmount() * weighingBill.getUnitWeight()).divide(new BigDecimal(200)).setScale(2, RoundingMode.HALF_UP));
			}
			map.put("tradeTypeId", weighingBill.getTradeTypeId());
			map.put("tradeAmount", new BigDecimal(MoneyUtils.centToYuan(statement.getTradeAmount())));
			map.put("tradingBillType", weighingBill.getTradingBillType());
			queryFeeInput.setCalcParams(map);
			queryFeeInput.setConditionParams(map);
			queryFeeInputList.add(queryFeeInput);
		});
		BaseOutput<List<QueryFeeOutput>> buyerFeeOutput = chargeRuleRpc.batchQueryFee(queryFeeInputList);
		if (!buyerFeeOutput.isSuccess()) {
			throw new AppException(buyerFeeOutput.getMessage());
		}
		if (CollectionUtils.isEmpty(buyerFeeOutput.getData())) {
			throw new AppException("未匹配到计费规则，请联系管理员");
		}
		BigDecimal buyerTotalFee = new BigDecimal(0);
		for (QueryFeeOutput qfo : buyerFeeOutput.getData()) {
			if (qfo.getTotalFee() != null) {
				buyerTotalFee = buyerTotalFee.add(qfo.getTotalFee().setScale(2, RoundingMode.HALF_UP));
			}
		}
		return buyerTotalFee.multiply(new BigDecimal(100)).longValue();
	}

	public List<Long> getBuyerSellerRuleId(String businessType, Long marketId) {
		BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
		// 业务类型
		businessChargeItemDto.setBusinessType(businessType);
		// 是否必须
		businessChargeItemDto.setIsRequired(YesOrNoEnum.YES.getCode());
		businessChargeItemDto.setIsEnable(YesOrNoEnum.YES.getCode());
		// 收费
		businessChargeItemDto.setChargeType(BusinessChargeItemEnum.ChargeType.收费.getCode());
		// 市场id
		businessChargeItemDto.setMarketId(marketId);
		BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
		// 判断是否成功
		if (!listBaseOutput.isSuccess()) {
			throw new AppException(listBaseOutput.getMessage());
		}
		if (CollectionUtils.isEmpty(listBaseOutput.getData())) {
			return null;
		}
		List<Long> ruleIds = new ArrayList<Long>(listBaseOutput.getData().size());
		listBaseOutput.getData().forEach(bci -> ruleIds.add(bci.getId()));
		return ruleIds;
	}

}