package com.dili.orders.mapper;

import com.dili.orders.domain.CollectionRecord;
import com.dili.orders.domain.WeighingStatement;
import com.dili.orders.dto.WeighingCollectionStatementDto;
import com.dili.orders.dto.WeighingStatementAppletDto;
import com.dili.orders.dto.WeighingStatementAppletQuery;
import com.dili.orders.dto.WeighingStatementAppletStateCountDto;
import com.dili.ss.base.MyMapper;

import java.util.List;
import java.util.Map;

public interface WeighingStatementMapper extends MyMapper<WeighingStatement> {

    List<WeighingStatementAppletDto> listApplet(WeighingStatementAppletQuery query);

    List<WeighingStatementAppletStateCountDto> selectStateCount(WeighingStatementAppletQuery query);

    List<WeighingCollectionStatementDto> listByDates(CollectionRecord collectionRecord);

    List<Map<String, String>> groupListForDetail(CollectionRecord collectionRecord);
}