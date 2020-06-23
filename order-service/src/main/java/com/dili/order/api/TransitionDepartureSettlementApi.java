package com.dili.order.api;

import com.dili.order.domain.TransitionDepartureSettlement;
import com.dili.order.service.TransitionDepartureSettlementService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/transitionDepartureSettlement")
public class TransitionDepartureSettlementApi {

    @Autowired
    TransitionDepartureSettlementService transitionDepartureSettlementService;


    /**
     * @param transitionDepartureSettlement
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public String listPage(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) throws Exception {
        return transitionDepartureSettlementService.listEasyuiPageByExample(transitionDepartureSettlement, true).toString();

    }


    /**
     * 根据参数查询数据
     *
     * @param transitionDepartureSettlement
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listByQueryParams", method = {RequestMethod.GET, RequestMethod.POST})
    public String listByQueryParams(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) throws Exception {
        List<TransitionDepartureSettlement> transitionDepartureSettlementList = transitionDepartureSettlementService.listByQueryParams(transitionDepartureSettlement);
        return new EasyuiPageOutput(transitionDepartureSettlementList.size(), ValueProviderUtils.buildDataByProvider(transitionDepartureSettlement, transitionDepartureSettlementList)).toString();
    }


    /**
     * 新增TransitionDepartureSettlement
     *
     * @param transitionDepartureSettlement
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput insert(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        try {
            if (transitionDepartureSettlement.getCreateTime() == null) {
                transitionDepartureSettlement.setCreateTime(LocalDateTime.now());
            }
            transitionDepartureSettlementService.insertSelective(transitionDepartureSettlement);
            return BaseOutput.success("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("新增失败" + e.getMessage());
        }
    }

    /**
     * 修改TransitionDepartureSettlement
     *
     * @param transitionDepartureSettlement
     * @return BaseOutput
     */
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput update(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        try {
            transitionDepartureSettlementService.updateSelective(transitionDepartureSettlement);
            return BaseOutput.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("修改失败" + e.getMessage());
        }

    }

    /**
     * 删除TransitionDepartureSettlement
     *
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput delete(Long id) {
        if (id == null) {
            return BaseOutput.failure("删除失败，id不能为空");
        }
        try {
            transitionDepartureSettlementService.delete(id);
            return BaseOutput.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("删除失败" + e.getMessage());
        }
    }

    /**
     * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
     *
     * @param transitionDepartureSettlement
     * @return
     */
    @RequestMapping(value = "/scheduleUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput scheduleUpdate(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        try {
            transitionDepartureSettlementService.scheduleUpdate(transitionDepartureSettlement);
            return BaseOutput.success();
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("操作失败" + e.getMessage());
        }
    }
}