package com.dili.orders.api;

import com.dili.assets.sdk.dto.CarTypeForBusinessDTO;
import com.dili.assets.sdk.dto.CategoryDTO;
import com.dili.assets.sdk.dto.TradeTypeDto;
import com.dili.assets.sdk.rpc.AssetsRpc;
import com.dili.assets.sdk.rpc.TradeTypeRpc;
import com.dili.orders.domain.TransitionDepartureApply;
import com.dili.orders.domain.UidStatic;
import com.dili.orders.dto.MyBusinessType;
import com.dili.orders.glossary.ApplyEnum;
import com.dili.orders.rpc.UidRpc;
import com.dili.orders.service.TransitionDepartureApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-17 08:51:33.
 */
@RestController
@RequestMapping("/api/transitionDepartureApply")
@Slf4j
public class TransitionDepartureApplyApi {
    @Autowired
    private TransitionDepartureApplyService transitionDepartureApplyService;

    @Autowired
    private AssetsRpc assetsRpc;

    @Autowired
    private UidRpc uidRpc;

    @Autowired
    private TradeTypeRpc tradeTypeRpc;

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
    public PageOutput<List<TransitionDepartureApply>> listByQueryParams(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        return transitionDepartureApplyService.listByQueryParams(transitionDepartureApply);
    }

    /**
     * 根据参数查询数据
     *
     * @param transitionDepartureApply
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/listByCustomerCardNo", method = {RequestMethod.POST, RequestMethod.GET})
    public BaseOutput<?> listByCustomerCardNo(@RequestBody TransitionDepartureApply transitionDepartureApply) {
        if (Objects.isNull(transitionDepartureApply.getCustomerCardNo())) {
            return BaseOutput.failure("客户卡号不能为空");
        }
        if (Objects.isNull(transitionDepartureApply.getMarketId())) {
            return BaseOutput.failure("市场id不能为空");
        }
        //设置当天时间，进行查询
        transitionDepartureApply.setBeginTime(LocalDate.now());
        transitionDepartureApply.setEndTime(LocalDate.now());
        return BaseOutput.successData(transitionDepartureApplyService.getListByCustomerId(transitionDepartureApply));
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
            CarTypeForBusinessDTO carTypeForJmsfDTO = new CarTypeForBusinessDTO();
            carTypeForJmsfDTO.setBusinessCode(MyBusinessType.KCJM.getCode());
            carTypeForJmsfDTO.setMarketId(transitionDepartureApply.getMarketId());
            carTypeForJmsfDTO.setId(transitionDepartureApply.getCarTypeId());
            BaseOutput<List<CarTypeForBusinessDTO>> listBaseOutput = assetsRpc.listCarTypePublicByBusiness(carTypeForJmsfDTO);
            if (!listBaseOutput.isSuccess()) {
                throw new RuntimeException("进门收费车型查询失败");
            }
//            BaseOutput<String> sg_zlc_apply = uidRpc.bizNumber("sg_zlc_apply");
            BaseOutput<String> sg_zlc_apply = uidRpc.bizNumber(UidStatic.SG_ZLC_APPLY_CODE);
            if (!sg_zlc_apply.isSuccess()) {
                return BaseOutput.failure(sg_zlc_apply.getMessage());
            }
            transitionDepartureApply.setCode(sg_zlc_apply.getData());
            transitionDepartureApply.setCarTypeName(listBaseOutput.getData().get(0).getCarTypeName());
            //插入的时候把车牌号变成大写
            transitionDepartureApply.setPlate(transitionDepartureApply.getPlate().toUpperCase());
            //设置交易类型名称
            BaseOutput<TradeTypeDto> tradeTypeDtoBaseOutput = this.tradeTypeRpc.get(Long.valueOf(transitionDepartureApply.getTransTypeId()));
            if (!tradeTypeDtoBaseOutput.isSuccess()) {
                return BaseOutput.failure(tradeTypeDtoBaseOutput.getMessage());
            }
            transitionDepartureApply.setTransTypeName(tradeTypeDtoBaseOutput.getData().getName());
            //设置商品名称
            BaseOutput<CategoryDTO> categoryDTOBaseOutput = assetsRpc.get(transitionDepartureApply.getCategoryId());
            if (!categoryDTOBaseOutput.isSuccess()) {
                return BaseOutput.failure(categoryDTOBaseOutput.getMessage());
            }
            transitionDepartureApply.setCategoryName(categoryDTOBaseOutput.getData().getName());
            //插入数据
            transitionDepartureApplyService.insertSelective(transitionDepartureApply);
            return BaseOutput.successData(transitionDepartureApply);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("新增失败");
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
            if (Objects.isNull(transitionDepartureApply.getId())) {
                return BaseOutput.failure("申请单id不能为空");
            }
            //判断当前的这个结算单是否是今天的
            TransitionDepartureApply transitionDepartureApply1 = transitionDepartureApplyService.get(transitionDepartureApply.getId());
            if (Objects.isNull(transitionDepartureApply1)) {
                return BaseOutput.failure("没有对应申请单信息");
            }
            //判断是否是待审批状态，如果是，则可以审批
            if (!Objects.equals(transitionDepartureApply1.getApprovalState(), ApplyEnum.TOBEREVIEWED.getCode())) {
                return BaseOutput.failure("该申请单状态不能再审批");
            }
            LocalDate createTime = transitionDepartureApply1.getOriginatorTime().toLocalDate();
            //如果为0，则表示为当天
            if (LocalDate.now().compareTo(createTime) != 0) {
                return BaseOutput.failure("只能审批当天申请单");
            }
            //乐观锁实现，需要先查询一次数据库，然后设置version
            transitionDepartureApply.setVersion(transitionDepartureApply1.getVersion());
            transitionDepartureApplyService.updateSelective(transitionDepartureApply);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("修改失败");
        }
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除TransitionDepartureApply
     *
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    public BaseOutput delete(Long id) {
        if (id == null) {
            return BaseOutput.failure("删除失败，id不能为空");
        }
        try {
            transitionDepartureApplyService.delete(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("删除失败");
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
            log.error(e.getMessage(), e);
            return BaseOutput.failure("查询失败");
        }
    }


    /**
     * 根据id查询出对应申请单,包含需要使用的provider，申请单详细页面
     *
     * @param
     * @return
     */

    @RequestMapping(value = "/getApplyAndSettleById", method = {RequestMethod.POST})
    public BaseOutput<TransitionDepartureApply> getApplyAndSettleById(@RequestBody TransitionDepartureApply transitionDepartureApply, @RequestParam(value = "marketId") Long marketId, @RequestParam(value = "departmentId") Long departmentId) {
        try {
            if (Objects.isNull(transitionDepartureApply.getId())) {
                return BaseOutput.failure("查询失败,id不能为空");
            }
            return BaseOutput.successData(transitionDepartureApplyService.getOneById(transitionDepartureApply.getId(), marketId, departmentId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("查询失败");
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
    public BaseOutput<TransitionDepartureApply> getOneByCustomerCardNo(@RequestBody TransitionDepartureApply transitionDepartureApply, Long marketId, Long departmentId) {
        try {
            return BaseOutput.successData(transitionDepartureApplyService.getOneByCustomerID(transitionDepartureApply, marketId, departmentId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseOutput.failure("查询失败");
        }
    }


}