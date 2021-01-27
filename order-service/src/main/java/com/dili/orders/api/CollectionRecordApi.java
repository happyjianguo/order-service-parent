package com.dili.orders.api;

import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.CollectionRecord;
import com.dili.orders.service.CollectionRecordService;
import com.dili.orders.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
@RestController
@RequestMapping("/api/collectionRecord")
@Slf4j
public class CollectionRecordApi {

    @Autowired
    private CollectionRecordService service;

    /**
     * 根据参数查询数据
     *
     * @param collectionRecord
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listByQueryParams", method = {RequestMethod.POST})
    public PageOutput<List<CollectionRecord>> listByQueryParams(@RequestBody CollectionRecord collectionRecord) {
        return service.listByQueryParams(collectionRecord);
    }

    /**
     * 根据参数查询数据，日期查询对应过磅单，下钻使用
     *
     * @param collectionRecord
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/weighingBills", method = {RequestMethod.POST})
    public BaseOutput<List<CollectionRecord>> weighingBills(@RequestBody CollectionRecord collectionRecord) {
        //判断需要回款的日期是否为空
        if (CollectionUtils.isEmpty(collectionRecord.getBatchCollectionDate())) {
            return BaseOutput.failure("回款日期为空");
        }
        return null;
    }


    /**
     * 根据时间查询回款，并新增记录，转账
     *
     * @param collectionRecord
     * @return
     */
    @PostMapping("/insertAndPay")
    @BusinessLogger(businessType = "trading_orders", content = "回款单回款：${businessCode},结算单号：${statementSerialNo},所属市场id：${marketId}，操作员id:${operationId}", operationType = "edit", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput insertAndPay(CollectionRecord collectionRecord, HttpServletRequest request, String password) {
        //日志记录
        LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
        //判断需要回款的日期是否为空
        if (CollectionUtils.isEmpty(collectionRecord.getBatchCollectionDate())) {
            return BaseOutput.failure("回款日期为空");
        }
        //判断市场id是否为空
        if (Objects.isNull(collectionRecord.getMarketId())) {
            return BaseOutput.failure("市场id不能为空");
        }
        //判断买家id或买家卡账户是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getBuyerId()) && Objects.isNull(collectionRecord.getAccountBuyerId())) {
            return BaseOutput.failure("买家id或买家卡账户不能为空");
        }
        //判断卖家id和卖家卡账户是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getSellerId()) && Objects.isNull(collectionRecord.getAccountSellerId())) {
            return BaseOutput.failure("卖家id或卖家卡账户不能为空");
        }
        //判断数据权限是否为空，必须存在一个
        if (CollectionUtils.isEmpty(collectionRecord.getDepartmentIds())) {
            return BaseOutput.failure("数据权限部门不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return BaseOutput.failure("密码不能为空");
        }
        try {
            return service.insertAndPay(collectionRecord, password);
        } catch (Exception e) {
            log.error("/api/collectionRecord/insertAndPay-->" + e.getMessage(), e);
            return BaseOutput.failure("服务异常，请联系管理员");
        }
    }

    /**
     * 根据时间查询回款，
     *
     * @param collectionRecord
     * @return
     */
    @PostMapping("/listForDetail")
    public BaseOutput listForDetail(CollectionRecord collectionRecord) {
        //判断需要回款的日期是否为空
        if (CollectionUtils.isEmpty(collectionRecord.getBatchCollectionDate())) {
            return BaseOutput.failure("回款日期为空");
        }
        //判断市场id是否为空
        if (Objects.isNull(collectionRecord.getMarketId())) {
            return BaseOutput.failure("市场id不能为空");
        }
        //判断买家id或买家卡账户是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getBuyerId()) && Objects.isNull(collectionRecord.getAccountBuyerId())) {
            return BaseOutput.failure("买家id或买家卡账户不能为空");
        }
        //判断卖家id和卖家卡账户是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getSellerId()) && Objects.isNull(collectionRecord.getAccountSellerId())) {
            return BaseOutput.failure("卖家id或卖家卡账户不能为空");
        }
        //判断数据权限是否为空，必须存在一个
        if (CollectionUtils.isEmpty(collectionRecord.getDepartmentIds())) {
            return BaseOutput.failure("数据权限部门不能为空");
        }
        try {
            return service.listForDetail(collectionRecord);
        } catch (Exception e) {
            log.error("/api/collectionRecord/listForDetail-->" + e.getMessage(), e);
            return BaseOutput.failure("服务异常，请联系管理员");
        }
    }

    /**
     * 回款页面统计数据
     *
     * @param collectionRecord
     * @return
     */
    @PostMapping("/groupListForDetail")
    public BaseOutput<List<Map<String, String>>> groupListForDetail(@RequestBody CollectionRecord collectionRecord) {
        //判断市场id是否为空
        if (Objects.isNull(collectionRecord.getMarketId())) {
            return BaseOutput.failure("市场id不能为空");
        }
        //判断买家id或买家卡账户是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getBuyerId()) && Objects.isNull(collectionRecord.getAccountBuyerId())) {
            return BaseOutput.failure("买家id或买家卡账户不能为空");
        }
        //判断卖家id和卖家卡账户是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getSellerId()) && Objects.isNull(collectionRecord.getAccountSellerId())) {
            return BaseOutput.failure("卖家id或卖家卡账户不能为空");
        }
        //判断数据权限是否为空，必须存在一个
        if (CollectionUtils.isEmpty(collectionRecord.getDepartmentIds())) {
            return BaseOutput.failure("数据权限部门不能为空");
        }
        try {
            return service.groupListForDetail(collectionRecord);
        } catch (Exception e) {
            log.error("/api/collectionRecord/groupListForDetail-->" + e.getMessage(), e);
            return BaseOutput.failure("服务异常，请联系管理员");
        }
    }


}