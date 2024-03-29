package com.dili.orders.api;

import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.CollectionRecord;
import com.dili.orders.domain.PaymentWays;
import com.dili.orders.dto.WeighingCollectionStatementDto;
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
     * 根据参数查询数据，根据id精确查询，下钻使用
     *
     * @param collectionRecord
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/weighingBills", method = {RequestMethod.POST})
    public BaseOutput<List<WeighingCollectionStatementDto>> weighingBills(@RequestBody CollectionRecord collectionRecord) {
        if (CollectionUtils.isEmpty(collectionRecord.getCollectionRecordIds())) {
            return BaseOutput.failure("交易过磅结算单id不能为空");
        }
        try {
            return service.listForDetail(collectionRecord);
        } catch (Exception e) {
            log.error("/api/collectionRecord/weighingBills-->" + e.getMessage(), e);
            return BaseOutput.failure("服务异常，请联系管理员");
        }
    }


    /**
     * 根据时间查询回款，并新增记录，转账
     *
     * @param collectionRecord
     * @return
     */
    @PostMapping("/insertAndPay")
    @BusinessLogger(businessType = "trading_orders", content = "回款单回款：${businessCode},结算单号：${statementSerialNo},所属市场id：${marketId}，操作员id:${operationId}", operationType = "edit", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput insertAndPay(@RequestBody CollectionRecord collectionRecord, HttpServletRequest request, @RequestParam(value = "password") String password) {
        //日志记录
        LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
//        //判断需要回款的日期是否为空
//        if (CollectionUtils.isEmpty(collectionRecord.getBatchCollectionDate())) {
//            return BaseOutput.failure("回款日期为空");
//        }
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
        if (Objects.isNull(collectionRecord.getOperationDepartmentId())) {
            return BaseOutput.failure("操作员所属部门不能为空");
        }
        //判断数据权限是否为空，必须存在一个
        if (Objects.isNull(collectionRecord.getOperationDepartmentName())) {
            return BaseOutput.failure("操作员所属部门名称不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return BaseOutput.failure("密码不能为空");
        }
        //判断如果是代付的话，代付卡号是否是买家或者卖家卡，如果是则返回错误信息
        if (Objects.equals(collectionRecord.getPaymentWays(), PaymentWays.REFUSED.getCode())) {
            if (Objects.isNull(collectionRecord.getPaymentCardNumber())) {
                return BaseOutput.failure("代付的情况下，代付卡号不能为空");
            }
            if (Objects.equals(collectionRecord.getPaymentCardNumber(), collectionRecord.getBuyerCardNo()) || Objects.equals(collectionRecord.getPaymentCardNumber(), collectionRecord.getSellerCardNo())) {
                return BaseOutput.failure("代付卡号不能为卖家卡或买家卡");
            }
        }
        //判断实回金额是否大于应回金额，并且实回金额大于0
        if (collectionRecord.getAmountActually().longValue() < 0L) {
            return BaseOutput.failure("实回款金额不能小于0");
        }
        //实回金额不能大于应回金额
        if (collectionRecord.getAmountActually().longValue() > collectionRecord.getAmountReceivables().longValue()) {
            return BaseOutput.failure("实回金额不能大于应回金额");
        }
        try {
            return service.insertAndPay(collectionRecord, password);
        } catch (Exception e) {
            log.error("/api/collectionRecord/insertAndPay-->" + e.getMessage(), e);
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