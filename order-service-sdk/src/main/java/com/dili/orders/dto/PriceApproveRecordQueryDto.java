package com.dili.orders.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.orders.domain.PriceApproveRecord;
import com.dili.ss.domain.annotation.Operator;
import com.fasterxml.jackson.annotation.JsonFormat;

public class PriceApproveRecordQueryDto extends PriceApproveRecord {

	@Operator(Operator.IN)
	@Column(name = "goods_id")
	private List<Long> goodsIds;
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
	@Operator(Operator.IN)
	@Column(name = "`process_instance_id`")
	private List<String> processInstanceIds;
	/**
	 * 过滤已关闭的过磅单的审批数据
	 */
	@Transient
	private Boolean filterClosedWeighingBill = true;

	public List<Long> getGoodsIds() {
		return goodsIds;
	}

	public void setGoodsIds(List<Long> goodsIds) {
		this.goodsIds = goodsIds;
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

	public List<String> getProcessInstanceIds() {
		return processInstanceIds;
	}

	public void setProcessInstanceIds(List<String> processInstanceIds) {
		this.processInstanceIds = processInstanceIds;
	}

	public Boolean getFilterClosedWeighingBill() {
		return filterClosedWeighingBill;
	}

	public void setFilterClosedWeighingBill(Boolean filterClosedWeighingBill) {
		this.filterClosedWeighingBill = filterClosedWeighingBill;
	}

}
