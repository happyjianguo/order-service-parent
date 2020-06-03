package com.dili.order.api;

import com.dili.order.domain.Orders;
import com.dili.order.service.OrdersService;
import com.dili.ss.domain.BaseOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-03 09:37:03.
 */
@RestController
@RequestMapping("/api/orders")
public class OrdersApi {
    @Autowired
    OrdersService ordersService;

    /**
     * 分页查询Orders，返回easyui分页信息
     * @param orders
     * @return String
     * @throws Exception
     */
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public String listPage(Orders orders) throws Exception {
        return ordersService.listEasyuiPageByExample(orders, true).toString();
    }

    /**
     * 新增Orders
     * @param orders
     * @return BaseOutput
     */
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput insert(Orders orders) {
        ordersService.insertSelective(orders);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改Orders
     * @param orders
     * @return BaseOutput
     */
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput update(Orders orders) {
        ordersService.updateSelective(orders);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除Orders
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput delete(Long id) {
        ordersService.delete(id);
        return BaseOutput.success("删除成功");
    }
}