package com.smartproperty.mapper;

import com.smartproperty.entity.Repair;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RepairMapper {

    List<Repair> findPage(@Param("keyword") String keyword,
                          @Param("status") String status,
                          @Param("category") String category,
                          @Param("ownerId") Integer ownerId,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("status") String status,
                          @Param("category") String category,
                          @Param("ownerId") Integer ownerId);

    Repair findById(@Param("id") Integer id);

    int insert(Repair repair);

    int update(Repair repair);

    int assign(@Param("id") Integer id,
               @Param("handlerId") Integer handlerId,
               @Param("status") String status);

    int finish(@Param("id") Integer id,
               @Param("handlerId") Integer handlerId,
               @Param("handleRemark") String handleRemark);

    int deleteById(@Param("id") Integer id);

    long countByStatus(@Param("status") String status);

    long countAll();

    /** 各状态工单数量，用于首页统计 */
    List<java.util.Map<String, Object>> countGroupByStatus();

    /** 最近的报修记录，用于首页时间线 */
    List<Repair> findLatest(@Param("limit") int limit);
}
