package com.smartproperty.mapper;

import com.smartproperty.entity.Room;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoomMapper {

    List<Room> findPage(@Param("keyword") String keyword,
                        @Param("buildingId") Integer buildingId,
                        @Param("status") String status,
                        @Param("offset") int offset,
                        @Param("pageSize") int pageSize);

    long countByCondition(@Param("keyword") String keyword,
                          @Param("buildingId") Integer buildingId,
                          @Param("status") String status);

    Room findById(@Param("id") Integer id);

    List<Room> findOptions(@Param("onlyHasOwner") boolean onlyHasOwner);

    List<Room> findByOwnerId(@Param("ownerId") Integer ownerId);

    int countByStatus(@Param("status") String status);

    int countByBuildingId(@Param("buildingId") Integer buildingId);

    long countAll();

    /** 关联数据统计：用于删除前的完整性校验 */
    long countBills(@Param("roomId") Integer roomId);

    long countRepairs(@Param("roomId") Integer roomId);

    long countVisitors(@Param("roomId") Integer roomId);

    int countByRoomNo(@Param("buildingId") Integer buildingId,
                      @Param("roomNo") String roomNo,
                      @Param("excludeId") Integer excludeId);

    int insert(Room room);

    int update(Room room);

    int updateOwner(@Param("id") Integer id, @Param("ownerId") Integer ownerId);

    int updateStatusAndOwner(@Param("id") Integer id,
                             @Param("ownerId") Integer ownerId,
                             @Param("status") String status);

    int deleteById(@Param("id") Integer id);
}
