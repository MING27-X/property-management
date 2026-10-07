package com.smartproperty.mapper;

import com.smartproperty.entity.Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeMapper {

    List<Notice> findPage(@Param("keyword") String keyword,
                          @Param("type") String type,
                          @Param("publishedOnly") boolean publishedOnly,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("type") String type,
                          @Param("publishedOnly") boolean publishedOnly);

    Notice findById(@Param("id") Integer id);

    List<Notice> findLatest(@Param("limit") int limit);

    int insert(Notice notice);

    int update(Notice notice);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int increaseView(@Param("id") Integer id);

    int deleteById(@Param("id") Integer id);

    long countAll();
}
