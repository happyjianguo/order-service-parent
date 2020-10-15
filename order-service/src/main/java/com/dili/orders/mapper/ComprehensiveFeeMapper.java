package com.dili.orders.mapper;

import com.dili.orders.domain.ComprehensiveFee;
import com.dili.ss.base.MyMapper;
import com.dili.ss.domain.BaseOutput;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;

/**
 * 检测收费Mapper
 *@author  Henry.Huang
 *@date  2020/08/20
 *
 */
public interface ComprehensiveFeeMapper extends MyMapper<ComprehensiveFee> {

    List<ComprehensiveFee> listByQueryParams(ComprehensiveFee comprehensiveFee);

    List<ComprehensiveFee> scheduleUpdateSelect(ComprehensiveFee comprehensiveFee);

    void scheduleUpdate(@Param("set") HashSet<Long> cfIds);

    Integer updateByIdAndVersion(ComprehensiveFee comprehensiveFee);

    ComprehensiveFee selectCountAndTotal(ComprehensiveFee comprehensiveFee);
}