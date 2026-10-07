package com.smartproperty.mapper;

import com.smartproperty.entity.FeeBill;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FeeBillMapper {

    List<FeeBill> findPage(@Param("keyword") String keyword,
                           @Param("status") String status,
                           @Param("feeType") String feeType,
                           @Param("period") String period,
                           @Param("ownerId") Integer ownerId,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("status") String status,
                          @Param("feeType") String feeType,
                          @Param("period") String period,
                          @Param("ownerId") Integer ownerId);

    FeeBill findById(@Param("id") Integer id);

    int insert(FeeBill bill);

    int update(FeeBill bill);

    int pay(@Param("id") Integer id,
            @Param("payMethod") String payMethod);

    int deleteById(@Param("id") Integer id);

    BigDecimal sumAmount(@Param("status") String status,
                         @Param("period") String period,
                         @Param("ownerId") Integer ownerId);

    long countByStatus(@Param("status") String status);

    long countUnpaidByOwner(@Param("ownerId") Integer ownerId);

    /** 按周期统计应收与实收，用于首页柱状图 */
    List<Map<String, Object>> sumByPeriod();
}
