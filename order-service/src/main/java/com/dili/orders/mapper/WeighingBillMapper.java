package com.dili.orders.mapper;

import java.util.List;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.ss.base.MyMapper;

public interface WeighingBillMapper extends MyMapper<WeighingBill> {

	List<WeighingBillListPageDto> listPage(WeighingBillQueryDto query);
	
	WeighingBillDetailDto selectDetailById(Long id);
}