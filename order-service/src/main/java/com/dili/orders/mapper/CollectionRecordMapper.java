package com.dili.orders.mapper;

import com.dili.orders.domain.CollectionRecord;
import com.dili.ss.base.MyMapper;

import java.util.List;

public interface CollectionRecordMapper extends MyMapper<CollectionRecord> {

    List<CollectionRecord> listByQueryParams(CollectionRecord collectionRecord);

}