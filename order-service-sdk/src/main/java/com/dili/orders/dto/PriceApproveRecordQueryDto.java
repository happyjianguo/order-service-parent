package com.dili.orders.dto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.ss.domain.annotation.Operator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PriceApproveRecordQueryDto extends PriceApproveRecord {

	@Transient
	private String goodsSplitStr;
	@JsonIgnore
	@JSONField(serialize = false)
	@Operator(Operator.IN)
	@Column(name = "'goods_name'")
	private List<String> goodsNames;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`weighing_time`")
	private LocalDateTime weighingStartTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
