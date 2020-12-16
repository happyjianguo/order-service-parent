package com.dili.orders.api;

import com.dili.assets.sdk.dto.BusinessChargeItemDto;
import com.dili.assets.sdk.rpc.BusinessChargeItemRpc;
import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.orders.constants.OrdersConstant;
import com.dili.orders.domain.ComprehensiveFee;
import com.dili.orders.service.ComprehensiveFeeService;
import com.dili.orders.utils.WebUtil;
import com.dili.rule.sdk.domain.input.QueryFeeInput;
import com.dili.rule.sdk.domain.output.QueryFeeOutput;
import com.dili.rule.sdk.rpc.ChargeRuleRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static tk.mybatis.spring.annotation.MapperScannerRegistrar.LOGGER;

/**
 * 检测收费服务接口
 *
 * @author  Henry.Huang
 * @date  2020/08/20
 */

@RestController
@RequestMapping("/api/comprehensiveFee")
public class ComprehensiveFeeApi {

    /** 检查收费计费规则型号  */
    private static final String TEST_FEE = "TEST_FEE";

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
        return comprehensiveFeeService.listByQueryParams(comprehensiveFee);
    }

    /**
     * 根据参数查询总交易数和总交易金额
     *
     * @param comprehensiveFee
     * @return String
     * @throws Exception
     */
    @RequestMapping(value = "/selectCountAndTotal", method = {RequestMethod.POST})
    public BaseOutput<ComprehensiveFee> selectCountAndTotal(@RequestBody ComprehensiveFee comprehensiveFee) {
        return comprehensiveFeeService.selectCountAndTotal(comprehensiveFee);
    }

    /**
     * 新增comprehensiveFee
     *
     * @param comprehensiveFee
     * @return BaseOutput
     */
    @RequestMapping(value = "/insert", method = {RequestMethod.POST})
    @BusinessLogger(businessType = "trading_orders", content = "${businessName}单新增，${businessName}单号：${businessCode}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "add", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput<ComprehensiveFee> insert(@RequestBody ComprehensiveFee comprehensiveFee, HttpServletRequest request) throws Exception{
        try {
            LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
            comprehensiveFeeService.insertComprehensiveFee(comprehensiveFee);
            return BaseOutput.successData(comprehensiveFee);
        } catch (AppException e) {
            return BaseOutput.failure("新增失败:" + e.getMessage());
        }
    }
    /**
     * 撤销操作
     * @param comprehensiveFee 检测收费
     * @param operatorId 操作人ID
     * @param operatorPassword 操作人密码
     * @param realName 操作人真实名字
     * @param userName 操作人账户名
     * @return
     *
     */
    @RequestMapping(value = "/revocator", method = {RequestMethod.POST})
    @BusinessLogger(businessType = "trading_orders", content = "检测收费结算单撤销，检查收费单号：${businessCode}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "weighing_withdraw", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput<ComprehensiveFee> revocator(@RequestBody ComprehensiveFee comprehensiveFee, Long operatorId, String realName, String operatorPassword, String userName, HttpServletRequest request) throws Exception{
        try{
            LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
            return this.comprehensiveFeeService.revocator(comprehensiveFee, operatorId, realName, operatorPassword, userName);
        }catch (AppException e) {
            return BaseOutput.failure(e.getMessage());
        }

    }

    /**
     * 根据id查询结算单信息
     *
     * @param id 检查收费id
     * @return
     */
    @RequestMapping(value = "/getOneById/{id}", method = {RequestMethod.GET})
    BaseOutput<ComprehensiveFee> getOneById(@PathVariable(value = "id") Long id) {
        return BaseOutput.successData(comprehensiveFeeService.get(id));
    }

    /**
     * 检测收费单支付
     *
     * @param id 检查收费ID
     * @param marketId 市场ID
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @param operatorUserName 操作人账户
     * @param password 支付密码
     * @return
     */
    @RequestMapping(value = "/pay", method = {RequestMethod.POST})
    @BusinessLogger(businessType = "trading_orders", content = "${businessName}单支付，${businessName}单号：${businessCode}，所属市场id：${marketId}，操作员id:${operatorId}", operationType = "pay", systemCode = OrdersConstant.SYSTEM_CODE)
    public BaseOutput<ComprehensiveFee> pay(@RequestParam(value = "id") Long id, @RequestParam(value = "password") String password, @RequestParam(value = "marketId") Long marketId, @RequestParam(value = "operatorId") Long operatorId, @RequestParam(value = "operatorName") String operatorName, @RequestParam(value = "operatorUserName") String operatorUserName, HttpServletRequest request) throws Exception{
        try {
            LoggerContext.put(LoggerConstant.LOG_REMOTE_IP_KEY, WebUtil.getClientIP(request));
            return comprehensiveFeeService.pay(id, password, marketId, operatorId, operatorName, operatorUserName);
        } catch (AppException e) {
            return BaseOutput.failure(e.getMessage());
        }
    }

    /**
     * 定时任务，每天凌晨12点更新当天为结算的单子，支付状态更改为已关闭状态
     *
     * @return
     */
    @RequestMapping(value = "/scheduleUpdate", method = {RequestMethod.GET, RequestMethod.POST})
    public BaseOutput<String> scheduleUpdate() throws Exception{
        try {
            return comprehensiveFeeService.scheduleUpdate();
        } catch (AppException e) {
            return BaseOutput.failure("综合收费将前一天未结算单据关闭定时任务执行失败");
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
    public BaseOutput<?> getFee( Long marketId, Long customerId, String type) {
        //根据业务类型获取收费项
        BusinessChargeItemDto businessChargeItemDto = new BusinessChargeItemDto();
        //业务类型
        businessChargeItemDto.setBusinessType(TEST_FEE);
        //市场id
        businessChargeItemDto.setMarketId(marketId);
        BaseOutput<List<BusinessChargeItemDto>> listBaseOutput = businessChargeItemRpc.listByExample(businessChargeItemDto);
        //判断是否成功
        if (!listBaseOutput.isSuccess()) {
            return listBaseOutput;
        }
        //设置收费项id
        List<QueryFeeInput> queryFeeInputList = new ArrayList<QueryFeeInput>();
        if (listBaseOutput.getData() != null && listBaseOutput.getData().size() > 0) {
            for (BusinessChargeItemDto bcDto : listBaseOutput.getData()) {
                QueryFeeInput queryFeeInput = new QueryFeeInput();
                //设置市场id
                queryFeeInput.setMarketId(marketId);
                //设置业务类型
                queryFeeInput.setBusinessType(TEST_FEE);
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
        BaseOutput<List<QueryFeeOutput>> result=chargeRuleRpc.batchQueryFee(queryFeeInputList);
        if(result.isSuccess()){
            List<QueryFeeOutput> datas=result.getData();
            StringBuffer infoSb=new StringBuffer("市场[ID:");
            infoSb.append(marketId);
            infoSb.append("]的操作员[ID:");
            infoSb.append(listBaseOutput.getData().get(0).getOperatorId());
            infoSb.append("],获取顾客[ID:");
            infoSb.append(customerId);
            infoSb.append("]的检查收费计费规则为：");
            for(int i=0;i<datas.size();i++){
                infoSb.append("规则[ID:");
                infoSb.append(datas.get(i).getRuleId());
                infoSb.append("]：");
                infoSb.append(datas.get(i).getTotalFee());
                infoSb.append("(元),");
            }
            LOGGER.info(infoSb.substring(0,infoSb.length()-1)+"。");
        }
        return result;
    }
}