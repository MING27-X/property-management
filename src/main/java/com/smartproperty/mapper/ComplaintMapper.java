package com.smartproperty.mapper;

import com.smartproperty.entity.Complaint;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ComplaintMapper {

    List<Complaint> findPage(@Param("keyword") String keyword,
                             @Param("status") String status,
                             @Param("type") String type,
                             @Param("ownerId") Integer ownerId,
                             @Param("offset") int offset,
                             @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("status") String status,
                          @Param("type") String type,
                          @Param("ownerId") Integer ownerId);

    Complaint findById(@Param("id") Integer id);

    int insert(Complaint complaint);

    int reply(@Param("id") Integer id,
              @Param("replyContent") String replyContent,
              @Param("replyBy") Integer replyBy);

    int deleteById(@Param("id") Integer id);

    long countByStatus(@Param("status") String status);

    List<Map<String, Object>> countGroupByType();
}
