package com.dili.orders.api;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.enums.BusinessChargeItemEnum;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.customer.sdk.rpc.CustomerRpc;
import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.TransitionDepartureSettlement;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/comprehensiveFee")
public class ComprehensiveFeeApi {

    @Autowired
    ComprehensiveFeeService comprehensiveFeeService;

    @Autowired
    private ChargeRuleRpc chargeRuleRpc;

    @Autowired
    private BusinessChargeItemRpc businessChargeItemRpc;


    /**
     * @param comprehensiveFee
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listPage", method = {RequestMethod.POST})
    public String listPage(@RequestBody ComprehensiveFee comprehensiveFee) throws Exception {

        return comprehensiveFeeService.listEasyuiPageByExample(comprehensiveFee, true).toString();

    }


    /**
     * 根据参数查询数据
     *
     * @param comprehensiveFee
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/listByQueryParams", method = {RequestMethod.POST})
    public PageOutput<List<ComprehensiveFee>> listByQueryParams(@RequestBody ComprehensiveFee comprehensiveFee) {
        //如果没有传入时间范围，那默认展示当天的数据
        //设置开始时间
        if (Objects.isNull(comprehensiveFee.getOperatorTimeStart())) {
            comprehensiveFee.setOperatorTimeStart(getBeginDate());
        }
        //设置结束时间
        if (Objects.isNull(comprehensiveFee.getOperatorTimeEnd())) {
            comprehensiveFee.setOperatorTimeEnd(getEndDate());
        }
        return comprehensiveFeeService.listByQueryParams(comprehensiveFee);
    }

    /**
     * 新增comprehensiveFee
     *
     * @param comprehensiveFee
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    public BaseOutput<ComprehensiveFee> insert(@RequestBody ComprehensiveFee comprehensiveFee) {
        try {
            if (comprehensiveFee.getCreatedTime() == null) {
                comprehensiveFee.setCreatedTime(LocalDateTime.now());
            }
            comprehensiveFeeService.insertComprehensiveFee(comprehensiveFee);
            return BaseOutput.successData(comprehensiveFee);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure("新增失败" + e.getMessage());
        }
    }

    /**
     * 根据id查询结算单信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getOneById/{id}", method = {RequestMethod.GET})
    BaseOutput<ComprehensiveFee> getOneById(@PathVariable(value = "id") Long id) {
        return BaseOutput.successData(comprehensiveFeeService.get(id));
    }

    /**
     * 检测收费单支付
     *
     * @return
     */
    @RequestMapping(value = "/pay", method = {RequestMethod.POST})
    public BaseOutput<ComprehensiveFee> pay(@RequestParam(value = "id") Long id, @RequestParam(value = "password") String password, @RequestParam(value = "marketId") Long marketId, @RequestParam(value = "departmentId") Long departmentId, @RequestParam(value = "operatorCode") String operatorCode, @RequestParam(value = "operatorId") Long operatorId, @RequestParam(value = "operatorName") String operatorName, @RequestParam(value = "operatorUserName") String operatorUserName) {
        try {
            return comprehensiveFeeService.pay(id, password, marketId, departmentId, operatorCode, operatorId, operatorName, operatorUserName);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseOutput.failure(e.getMessage());
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
            comprehensiveFeeService.scheduleUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取检测收费费用
     *
     * @param marketId 市场ID
     * @param customerId 顾客ID
     * @param type 顾客身份类型
     * @return
     */
    @RequestMapping(value = "/fee", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput getFee( Long marketId, Long customerId, String type) {
        //根据业务类型获取收费项
        BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
        //业务类型
        businessChargeItemDto.setBusinessType("TEST_FEE");
        //市场id
        businessChargeItemDto.setMarketId(marketId);
        BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
        //判断是否成功
        if (!listBaseOutput.isSuccess()) {
            return listBaseOutput;
        }
        //设置收费项id
        List<QueryFeeInput> queryFeeInputList=new ArrayList<QueryFeeInput>();
        if (listBaseOutput.getData()!=null&&listBaseOutput.getData().size()>0) {
            for (BusinessChargeItemDto bcDto:listBaseOutput.getData()) {
                QueryFeeInput queryFeeInput = new QueryFeeInput();
                //设置市场id
                queryFeeInput.setMarketId(marketId);
                //设置业务类型
                queryFeeInput.setBusinessType("TEST_FEE");
                //设置收费项ID
                queryFeeInput.setChargeItem(bcDto.getId());
                //条件指标
                Map<String, Object> map = new HashMap<>(4);
                //设置部门信息
                map.put("customerId", customerId);
                //设置市场信息
                map.put("customerType", type);

                queryFeeInput.setConditionParams(map);

                queryFeeInputList.add(queryFeeInput);
            }
        }
        return chargeRuleRpc.batchQueryFee(queryFeeInputList);
    }

    /**
     * 获取当天开始时间
     *
     * @return
     */
    private Date getBeginDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return zero;
    }

    /**
     * 获取当天结束时间
     *
     * @return
     */
    private Date getEndDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date zero = calendar.getTime();
        return zero;
    }

}