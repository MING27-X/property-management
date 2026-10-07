package com.smartproperty.mapper;

import com.smartproperty.entity.Owner;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OwnerMapper {

    List<Owner> findPage(@Param("keyword") String keyword,
                         @Param("buildingId") Integer buildingId,
                         @Param("offset") int offset,
                         @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("buildingId") Integer buildingId);

    List<Owner> findAll();

    Owner findById(@Param("id") Integer id);

    Owner findByUserId(@Param("userId") Integer userId);

    int countByRoomId(@Param("roomId") Integer roomId,
                      @Param("excludeId") Integer excludeId);

    long countAll();

    /** 关联数据统计：用于删除前的完整性校验 */
    long countBills(@Param("ownerId") Integer ownerId);

    long countRepairs(@Param("ownerId") Integer ownerId);

    long countComplaints(@Param("ownerId") Integer ownerId);

    /** 解除业主与登录账号的绑定（删除系统账号时调用） */
    int clearUserLink(@Param("userId") Integer userId);

    /** 按关联账号同步业主档案姓名（账号姓名变更时调用） */
    int updateNameByUserId(@Param("userId") Integer userId, @Param("name") String name);

    int insert(Owner owner);

    int update(Owner owner);

    int deleteById(@Param("id") Integer id);
}
