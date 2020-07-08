package com.dili.orders.api;

import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.service.TransitionDepartureSettlementService;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/transitionDepartureSettlement")
public class TransitionDepartureSettlementApi {

    @Autowired
    TransitionDepartureSettlementService transitionDepartureSettlementService;

    @Autowired
    private ChargeRuleRpc chargeRuleRpc;


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
    public PageOutput<List<TransitionDepartureSettlement>> listByQueryParams(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        return transitionDepartureSettlementService.listByQueryParams(transitionDepartureSettlement);
    }

    /**
     * 根据id查询结算单信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOneById/{id}", method = {RequestMethod.GET})
    BaseOutput<TransitionDepartureSettlement> getOneById(@PathVariable(value = "id") Long id) {
        return BaseOutput.successData(transitionDepartureSettlementService.get(id));
    }

    /**
     * 新增TransitionDepartureSettlement
     *
     * @param transitionDepartureSettlement
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> insert(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        try {
            if (transitionDepartureSettlement.getCreateTime() == null) {
                transitionDepartureSettlement.setCreateTime(LocalDateTime.now());
            }
            transitionDepartureSettlementService.insertSelective(transitionDepartureSettlement);
            return BaseOutput.successData(transitionDepartureSettlement);
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
            return BaseOutput.successData(transitionDepartureSettlement);
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
     * @return
     */
    @RequestMapping(value = "/scheduleUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public void scheduleUpdate() {
        try {
            transitionDepartureSettlementService.scheduleUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增一个结算单
     *
     * @param transitionDepartureSettlement
     * @return
     */
    @RequestMapping(value = "/insertTransitionDepartureSettlement", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> insertTransitionDepartureSettlement(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        return transitionDepartureSettlementService.insertTransitionDepartureSettlement(transitionDepartureSettlement);
    }

    /**
     * 结算单支付
     *
     * @return
     */
    @RequestMapping(value = "/pay", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> pay(@RequestParam(value = "id") Long id, @RequestParam(value = "password") String password) {
        return transitionDepartureSettlementService.pay(id, password);
    }

    /**
     * 撤销交易
     *
     * @return
     */
    @RequestMapping(value = "/revocator", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<TransitionDepartureSettlement> revocator(@RequestBody TransitionDepartureSettlement transitionDepartureSettlement) {
        return transitionDepartureSettlementService.revocator(transitionDepartureSettlement);
    }


    /**
     * 获取计费规则所得到的的金额
     *
     * @return
     */
    @RequestMapping(value = "/fee", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput getFee(BigDecimal netWeight, Long marketId, Long departmentId) {
        QueryFeeInput queryFeeInput = new QueryFeeInput();
        Map<String, Object> map = new HashMap<>();
        //设置市场id
        queryFeeInput.setMarketId(marketId);
        //设置业务类型
        queryFeeInput.setBusinessType("ZLC_PAY");
        //设置收费项id
        queryFeeInput.setChargeItem(32L);
        map.put("weight", netWeight);
        queryFeeInput.setCalcParams(map);
        //构建指标
        Map<String, Object> map2 = new HashMap();
        map2.put("id", departmentId);
        map2.put("marketId", marketId);
        queryFeeInput.setConditionParams(map2);
        return chargeRuleRpc.queryFee(queryFeeInput);
    }
}