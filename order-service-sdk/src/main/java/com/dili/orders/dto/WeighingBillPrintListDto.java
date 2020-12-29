package com.dili.orders.dto;

import com.github.pagehelper.Page;

public class WeighingBillPrintListDto {

	private Page<WeighingBillListPageDto> pageList;
	private WeighingBillListStatisticsDto statisticsDto;

	public Page<WeighingBillListPageDto> getPageList() {
		return pageList;
	}

	public void setPageList(Page<WeighingBillListPageDto> pageList) {
		this.pageList = pageList;
	}

	public WeighingBillListStatisticsDto getStatisticsDto() {
		return statisticsDto;
	}

	public void setStatisticsDto(WeighingBillListStatisticsDto statisticsDto) {
		this.statisticsDto = statisticsDto;
	}

}
