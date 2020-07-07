package com.dili.orders.api;

import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
    @RequestMapping(value = "/listPage", method = {RequestMethod.POST})
    public String listPage(@RequestBody TransitionDepartureApply transitionDepartureApply) throws Exception {
        return transitionDepartureApplyService.listEasyuiPageByExample(transitionDepartureApply, true).toString();
    }

    /**
     * 根据参数查询数据
     *
     * @param transitionDepartureApply
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listByQueryParams", method = {RequestMethod.POST})
    public PageOutput<List<TransitionDepartureApply>> listByQueryParams(@RequestBody TransitionDepartureApply transitionDepartureApply) throws Exception {
        return transitionDepartureApplyService.listByQueryParams(transitionDepartureApply);
    }

    /**
     * 新增TransitionDepartureApply
     *
     * @param transitionDepartureApply
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureApply> insert(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            if (transitionDepartureApply.getOriginatorTime() == null) {
                transitionDepartureApply.setOriginatorTime(LocalDateTime.now());
            }
            transitionDepartureApplyService.insertSelective(transitionDepartureApply);
            return BaseOutput.successData(transitionDepartureApply);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("新增失败" + e.getMessage());
        }
    }

    /**
     * 修改TransitionDepartureApply
     *
     * @param transitionDepartureApply
     * @return BaseOutput
     */
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public BaseOutput update(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
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
     * @param id
     * @return
     */

    @RequestMapping(value = "/getOneByID/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureApply> getOneByID(@PathVariable(value = "id") Long id) {
        try {
            if (id == null) {
                return BaseOutput.failure("查询失败,id不能为空");
            }
            return BaseOutput.successData(transitionDepartureApplyService.get(id));
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
    @RequestMapping(value = "/getOneByCustomerCardNo", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureApply> getOneByCustomerCardNo(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        try {
            return BaseOutput.successData(transitionDepartureApplyService.getOneByCustomerID(transitionDepartureApply));
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("查询失败" + e.getMessage());
        }
    }


}