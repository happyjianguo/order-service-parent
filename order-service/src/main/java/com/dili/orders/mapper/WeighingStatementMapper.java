package com.dili.orders.mapper;

import java.util.List;

import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.WeighingStatementAppletDto;
import com.dili.orders.dto.WeighingStatementAppletQuery;
import com.dili.ss.base.MyMapper;

public interface WeighingStatementMapper extends MyMapper<WeighingStatement> {

	List<WeighingStatementAppletDto> listApplet(WeighingStatementAppletQuery query);
}