package com.dili.orders.mapper;

import java.util.List;

import com.dili.orders.domain.WeighingBill;
import com.dili.orders.domain.WeighingBillOperationRecord;
import com.dili.orders.dto.WeighingBillClientListDto;
import com.dili.orders.dto.WeighingBillDetailDto;
import com.dili.orders.dto.WeighingBillListPageDto;
import com.dili.orders.dto.WeighingBillListStatisticsDto;
import com.dili.orders.dto.WeighingBillQueryDto;
import com.dili.ss.base.MyMapper;

public interface WeighingBillMapper extends MyMapper<WeighingBill> {

    List<WeighingBillListPageDto> listPage(WeighingBillQueryDto query);

    WeighingBillDetailDto selectDetailById(Long id);

    List<WeighingBillClientListDto> selectByExampleModified(WeighingBillQueryDto weighingBill);

    /**
     * 查询最后一次过磅操作记录
     *
     * @param id 过磅单id
     * @return
     */
    WeighingBillOperationRecord selectLastWeighingOperationRecord(Long id);

    /**
     * 导出统计重量、金额合计
     *
     * @param query
     * @return
     */
    WeighingBillListStatisticsDto selectExportStatistics(WeighingBillQueryDto query);

    /**
     * 查询待同步检测值得数据
     *
     * @return
     */
    List<WeighingBillClientListDto> listSourceSyncData();
}