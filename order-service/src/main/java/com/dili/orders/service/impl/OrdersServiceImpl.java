package com.dili.orders.service.impl;

import com.dili.orders.domain.Orders;
import com.dili.orders.mapper.OrdersMapper;
import com.dili.orders.service.OrdersService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-03 09:37:03.
 */
@Service
public class OrdersServiceImpl extends BaseServiceImpl<Orders, Long> implements OrdersService {

    public OrdersMapper getActualDao() {
        return (OrdersMapper)getDao();
    }
}