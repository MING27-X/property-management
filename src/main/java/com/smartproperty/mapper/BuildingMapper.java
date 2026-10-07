package com.smartproperty.mapper;

import com.smartproperty.entity.Building;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BuildingMapper {

    List<Building> findPage(@Param("keyword") String keyword,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword);

    List<Building> findAll();

    Building findById(@Param("id") Integer id);

    int countByBuildingNo(@Param("buildingNo") String buildingNo,
                          @Param("excludeId") Integer excludeId);

    int countRooms(@Param("buildingId") Integer buildingId);

    long countAll();

    int insert(Building building);

    int update(Building building);

    int deleteById(@Param("id") Integer id);
}
