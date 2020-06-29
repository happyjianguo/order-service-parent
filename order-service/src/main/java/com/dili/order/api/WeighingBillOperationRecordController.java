package com.dili.order.api;

import com.dili.order.domain.WeighingBillOperationRecord;
import com.dili.order.service.WeighingBillOperationRecordService;
import com.dili.ss.domain.BaseOutput;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-06-19 14:44:24.
 */
@Controller
@RequestMapping("/weighingBillOperationRecord")
public class WeighingBillOperationRecordController {
    @Autowired
    WeighingBillOperationRecordService weighingBillOperationRecordService;

    /**
     * 跳转到WeighingBillOperationRecord页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "weighingBillOperationRecord/index";
    }

    /**
     * 分页查询WeighingBillOperationRecord，返回easyui分页信息
     * @param weighingBillOperationRecord
     * @return String
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(WeighingBillOperationRecord weighingBillOperationRecord) throws Exception {
        return weighingBillOperationRecordService.listEasyuiPageByExample(weighingBillOperationRecord, true).toString();
    }

    /**
     * 新增WeighingBillOperationRecord
     * @param weighingBillOperationRecord
     * @return BaseOutput
     */
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(WeighingBillOperationRecord weighingBillOperationRecord) {
        weighingBillOperationRecordService.insertSelective(weighingBillOperationRecord);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改WeighingBillOperationRecord
     * @param weighingBillOperationRecord
     * @return BaseOutput
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(WeighingBillOperationRecord weighingBillOperationRecord) {
        weighingBillOperationRecordService.updateSelective(weighingBillOperationRecord);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除WeighingBillOperationRecord
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        weighingBillOperationRecordService.delete(id);
        return BaseOutput.success("删除成功");
    }
}