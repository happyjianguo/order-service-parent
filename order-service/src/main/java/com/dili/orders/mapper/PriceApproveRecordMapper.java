package com.dili.orders.mapper;

import java.util.List;

import com.dili.orders.domain.PriceApproveRecord;
import com.dili.orders.dto.PriceApproveRecordQueryDto;
import com.dili.ss.base.MyMapper;

public interface PriceApproveRecordMapper extends MyMapper<PriceApproveRecord> {

	List<PriceApproveRecord> listPageApp(PriceApproveRecordQueryDto query);
}