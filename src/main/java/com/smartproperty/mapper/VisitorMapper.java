package com.smartproperty.mapper;

import com.smartproperty.entity.Visitor;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VisitorMapper {

    List<Visitor> findPage(@Param("keyword") String keyword,
                           @Param("status") String status,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("status") String status);

    Visitor findById(@Param("id") Integer id);

    int insert(Visitor visitor);

    int update(Visitor visitor);

    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    int leave(@Param("id") Integer id);

    int deleteById(@Param("id") Integer id);

    long countByStatus(@Param("status") String status);

    long countToday();
}
