package com.dili.orders.api;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:52:43.
 */
@RestController
@RequestMapping("/api/comprehensiveFee")
public class ComprehensiveFeeApi {

    @Autowired
    ComprehensiveFeeService comprehensiveFeeService;


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