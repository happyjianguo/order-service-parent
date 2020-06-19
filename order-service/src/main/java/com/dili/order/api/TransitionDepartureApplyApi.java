package com.dili.order.api;

import com.alibaba.fastjson.JSON;
import com.dili.assets.sdk.dto.CarTypeDTO;
import com.dili.assets.sdk.dto.CarTypePublicDTO;
import com.dili.order.domain.Orders;
import com.dili.order.domain.TransitionDepartureApply;
import com.dili.order.rpc.AssetsRpc;
import com.dili.order.service.TransitionDepartureApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
@RestController
@RequestMapping("/api/transitionDepartureApply")
public class TransitionDepartureApplyApi {
    @Autowired
    TransitionDepartureApplyService transitionDepartureApplyService;

    /**
     * 分页查询TransitionDepartureApply，返回easyui分页信息
     *
     * @param transitionDepartureApply
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<List<TransitionDepartureApply>> listPage(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            return BaseOutput.successData(transitionDepartureApplyService.listByQueryParams(transitionDepartureApply));
        } catch (Exception e) {
            return BaseOutput.failure("查询失败" + e.getMessage());
        }
    }

    /**
     * 新增TransitionDepartureApply
     *
     * @param transitionDepartureApply
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput insert(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            if (transitionDepartureApply.getOriginatorTime() == null) {
                transitionDepartureApply.setOriginatorTime(LocalDateTime.now());
            }
            transitionDepartureApplyService.insertSelective(transitionDepartureApply);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("新增失败" + e.getMessage());
        }
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改TransitionDepartureApply
     *
     * @param transitionDepartureApply
     * @return BaseOutput
     */
    @RequestMapping(value = "/update", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput update(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            if (transitionDepartureApply.getModifyTime() == null) {
                transitionDepartureApply.setModifyTime(LocalDateTime.now());
            }
            transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("修改失败" + e.getMessage());
        }
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除TransitionDepartureApply
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
            transitionDepartureApplyService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("删除失败" + e.getMessage());
        }
        return BaseOutput.success("删除成功");
    }

    /**
     * 根据id查询出对应申请单,包含需要使用的provider，申请单详细页面
     *
     * @param transitionDepartureApply
     * @return
     */

    @RequestMapping(value = "/getOneByID", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureApply> getOneByID(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            return BaseOutput.successData(transitionDepartureApplyService.get(transitionDepartureApply.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("查询失败" + e.getMessage());
        }
    }

    /**
     * 根据客户id查询客户最新审批通过该的审批单，如果是未结算的，那带出结算单的相关信息，如果是已撤销，那就不带出
     *
     * @param transitionDepartureApply
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getOneByCustomerID", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureApply> getOneByCustomerID(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            return BaseOutput.successData(transitionDepartureApplyService.getOneByCustomerID(transitionDepartureApply));
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("查询失败" + e.getMessage());
        }
    }

}