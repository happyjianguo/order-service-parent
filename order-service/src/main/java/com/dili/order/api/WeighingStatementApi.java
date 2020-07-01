package com.dili.order.api;

import com.dili.order.domain.WeighingStatement;
import com.dili.order.service.WeighingStatementService;
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
 * This file was generated on 2020-06-19 14:43:53.
 */
@Controller
@RequestMapping("/weighingStatement")
public class WeighingStatementApi {
    @Autowired
    WeighingStatementService weighingStatementService;

    /**
     * 跳转到WeighingStatement页面
     * @param modelMap
     * @return String
     */
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "weighingStatement/index";
    }

    /**
     * 分页查询WeighingStatement，返回easyui分页信息
     * @param weighingStatement
     * @return String
     * @throws Exception
     */
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(WeighingStatement weighingStatement) throws Exception {
        return weighingStatementService.listEasyuiPageByExample(weighingStatement, true).toString();
    }

    /**
     * 新增WeighingStatement
     * @param weighingStatement
     * @return BaseOutput
     */
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(WeighingStatement weighingStatement) {
        weighingStatementService.insertSelective(weighingStatement);
        return BaseOutput.success("新增成功");
    }

    /**
     * 修改WeighingStatement
     * @param weighingStatement
     * @return BaseOutput
     */
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(WeighingStatement weighingStatement) {
        weighingStatementService.updateSelective(weighingStatement);
        return BaseOutput.success("修改成功");
    }

    /**
     * 删除WeighingStatement
     * @param id
     * @return BaseOutput
     */
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        weighingStatementService.delete(id);
        return BaseOutput.success("删除成功");
    }
}