package com.dili.orders.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.dili.orders.domain.PriceApproveRecord;
import com.dili.ss.domain.annotation.Operator;

public class PriceApproveRecordQueryDto extends PriceApproveRecord {

	@Transient
	private String goodsSplitStr;
	@Operator(Operator.IN)
	@Column(name = "'goods_name'")
	private List<String> goodsNames;
	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`weighing_time`")
	private LocalDateTime weighingStartTime;
	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`weighing_time`")
	private LocalDateTime weighingEndTime;

	public String getGoodsSplitStr() {
		return goodsSplitStr;
	}

	public void setGoodsSplitStr(String goodsSplitStr) {
		this.goodsSplitStr = goodsSplitStr;
		if (StringUtils.isNotBlank(goodsSplitStr)) {
			this.goodsNames = Arrays.asList(goodsSplitStr.split("，"));
		}
	}

	public List<String> getGoodsNames() {
		return goodsNames;
	}

	public LocalDateTime getWeighingStartTime() {
		return weighingStartTime;
	}

	public void setWeighingStartTime(LocalDateTime weighingStartTime) {
		this.weighingStartTime = weighingStartTime;
	}

	public LocalDateTime getWeighingEndTime() {
		return weighingEndTime;
	}

	public void setWeighingEndTime(LocalDateTime weighingEndTime) {
		this.weighingEndTime = weighingEndTime;
	}

}
