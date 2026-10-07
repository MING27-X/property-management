package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Room;
import com.smartproperty.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房屋管理业务。
 */
@Service
public class RoomService {

    @Autowired
    private RoomMapper roomMapper;

    public PageResult<Room> findPage(String keyword, Integer buildingId, String status,
                                     int pageNum, int pageSize) {
        PageResult<Room> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(roomMapper.countByCondition(keyword, buildingId, status));
        page.setList(roomMapper.findPage(keyword, buildingId, status, page.getOffset(), pageSize));
        return page;
    }

    public Room findById(Integer id) {
        return roomMapper.findById(id);
    }

    public List<Room> findOptions(boolean onlyHasOwner) {
        return roomMapper.findOptions(onlyHasOwner);
    }

    public List<Room> findByOwnerId(Integer ownerId) {
        return roomMapper.findByOwnerId(ownerId);
    }

    public long countAll() {
        return roomMapper.countAll();
    }

    public long countByStatus(String status) {
        return roomMapper.countByStatus(status);
    }

    @Transactional
    public String save(Room room) {
        if (room.getBuildingId() == null) {
            return "请选择所属楼栋";
        }
        if (room.getRoomNo() == null || room.getRoomNo().trim().isEmpty()) {
            return "房号不能为空";
        }
        room.setRoomNo(room.getRoomNo().trim());
        if (roomMapper.countByRoomNo(room.getBuildingId(), room.getRoomNo(), room.getId()) > 0) {
            return "该楼栋下房号【" + room.getRoomNo() + "】已存在";
        }
        if (room.getFloor() == null) {
            room.setFloor(1);
        }
        if (room.getId() == null) {
            roomMapper.insert(room);
        } else {
            Room old = roomMapper.findById(room.getId());
            if (old == null) {
                return "房屋不存在";
            }
            roomMapper.update(room);
            // 状态置为「空置」时自动解除业主绑定：仅清空房屋侧的 owner_id，
            // 房屋列表的业主/电话列随之显示为空；业主档案与其登录账号保留不动。
            if ("空置".equals(room.getStatus()) && old.getOwnerId() != null) {
                roomMapper.updateOwner(room.getId(), null);
            }
        }
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        Room room = roomMapper.findById(id);
        if (room == null) {
            return "房屋不存在";
        }
        if (room.getOwnerId() != null) {
            return "该房屋已绑定业主，请先解除业主绑定";
        }
        // 完整性校验：存在关联业务数据时不允许删除，避免出现孤儿数据
        long bills = roomMapper.countBills(id);
        long repairs = roomMapper.countRepairs(id);
        long visitors = roomMapper.countVisitors(id);
        if (bills + repairs + visitors > 0) {
            return String.format("该房屋存在关联数据（账单 %d 条、报修 %d 条、访客记录 %d 条），请先处理后再删除",
                    bills, repairs, visitors);
        }
        roomMapper.deleteById(id);
        return null;
    }
}
