package com.smartproperty.mapper;

import com.smartproperty.entity.Parking;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ParkingMapper {

    List<Parking> findPage(@Param("keyword") String keyword,
                           @Param("status") String status,
                           @Param("type") String type,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("status") String status,
                          @Param("type") String type);

    Parking findById(@Param("id") Integer id);

    int countBySpaceNo(@Param("spaceNo") String spaceNo,
                       @Param("excludeId") Integer excludeId);

    int insert(Parking parking);

    int update(Parking parking);

    int rent(@Param("id") Integer id,
             @Param("ownerId") Integer ownerId,
             @Param("plateNo") String plateNo);

    int release(@Param("id") Integer id);

    int deleteById(@Param("id") Integer id);

    long countByStatus(@Param("status") String status);

    long countAll();

    /** 已租用车位的月租金合计 */
    BigDecimal sumRentedFee();

    /** 某业主名下（租用）的车位数量 */
    long countByOwner(@Param("ownerId") Integer ownerId);
}
