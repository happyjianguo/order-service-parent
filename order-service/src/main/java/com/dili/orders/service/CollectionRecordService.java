package com.dili.orders.service;

import com.dili.orders.domain.CollectionRecord;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
public interface CollectionRecordService extends BaseService<CollectionRecord, Long> {

    /**
     * 根据参数获取列表
     *
     * @param collectionRecord
     * @return
     */
    PageOutput<List<CollectionRecord>> listByQueryParams(CollectionRecord collectionRecord);

    /**
     * 插入数据和支付
     *
     * @param collectionRecord
     */
    BaseOutput insertAndPay(CollectionRecord collectionRecord, String password);

    /**
     * 统计数据
     *
     * @param collectionRecord
     */
    BaseOutput groupListForDetail(CollectionRecord collectionRecord);

    /**
     * 下钻查询数据
     *
     * @param collectionRecord
     * @return
     */

    BaseOutput listForDetail(CollectionRecord collectionRecord);
}