package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.Repair;
import com.smartproperty.entity.Room;
import com.smartproperty.entity.SysUser;
import com.smartproperty.mapper.RepairMapper;
import com.smartproperty.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报修工单业务。
 * <p>
 * 流程：业主提交报修（待处理）→ 物业派单（处理中）→ 处理完成（已完成）。
 */
@Service
public class RepairService {

    @Autowired
    private RepairMapper repairMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private OwnerService ownerService;

    public PageResult<Repair> findPage(String keyword, String status, String category,
                                       Integer ownerId, int pageNum, int pageSize) {
        PageResult<Repair> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(repairMapper.countByCondition(keyword, status, category, ownerId));
        page.setList(repairMapper.findPage(keyword, status, category, ownerId,
                page.getOffset(), pageSize));
        return page;
    }

    public Repair findById(Integer id) {
        return repairMapper.findById(id);
    }

    public List<Repair> findLatest(int limit) {
        return repairMapper.findLatest(limit);
    }

    public Map<String, Long> countGroupByStatus() {
        Map<String, Long> result = new HashMap<>();
        result.put("待处理", 0L);
        result.put("处理中", 0L);
        result.put("已完成", 0L);
        for (Map<String, Object> row : repairMapper.countGroupByStatus()) {
            result.put(String.valueOf(row.get("status")), ((Number) row.get("num")).longValue());
        }
        return result;
    }

    public long countAll() {
        return repairMapper.countAll();
    }

    public long countByStatus(String status) {
        return repairMapper.countByStatus(status);
    }

    /** 统计某位业主自己某个状态的工单数量（业主端统计卡使用） */
    public long countByStatusAndOwner(String status, Integer ownerId) {
        return repairMapper.countByCondition(null, status, null, ownerId);
    }

    /**
     * 业主提交报修 / 物业代报。
     */
    @Transactional
    public String save(Repair repair, SysUser loginUser) {
        if (repair.getTitle() == null || repair.getTitle().trim().isEmpty()) {
            return "报修标题不能为空";
        }
        if (repair.getRoomId() == null) {
            return "请选择报修房屋";
        }
        Room room = roomMapper.findById(repair.getRoomId());
        if (room == null) {
            return "房屋不存在";
        }
        if (room.getOwnerId() == null) {
            return "该房屋尚未登记业主，无法提交报修";
        }
        if (loginUser.isOwner()) {
            Owner owner = ownerService.findByUserId(loginUser.getId());
            if (owner == null || !owner.getId().equals(room.getOwnerId())) {
                return "只能为本人名下房屋提交报修";
            }
        }
        repair.setOwnerId(room.getOwnerId());
        if (repair.getCategory() == null || repair.getCategory().isEmpty()) {
            repair.setCategory("其他");
        }
        if (repair.getUrgency() == null || repair.getUrgency().isEmpty()) {
            repair.setUrgency("普通");
        }
        repair.setStatus("待处理");
        repair.setHandlerId(null);
        repair.setHandleTime(null);
        repairMapper.insert(repair);
        return null;
    }

    /** 物业派单：指定处理人并进入处理中 */
    @Transactional
    public String assign(Integer id, Integer handlerId) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            return "工单不存在";
        }
        if ("已完成".equals(repair.getStatus())) {
            return "已完成的工单不能重新派单";
        }
        if (handlerId == null) {
            return "请选择处理人员";
        }
        repairMapper.assign(id, handlerId, "处理中");
        return null;
    }

    /** 处理完成 */
    @Transactional
    public String finish(Integer id, Integer handlerId, String handleRemark) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            return "工单不存在";
        }
        if ("已完成".equals(repair.getStatus())) {
            return "该工单已处理完成";
        }
        if (handleRemark == null || handleRemark.trim().isEmpty()) {
            return "请填写处理说明";
        }
        Integer handler = handlerId != null ? handlerId : repair.getHandlerId();
        repairMapper.finish(id, handler, handleRemark.trim());
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        Repair repair = repairMapper.findById(id);
        if (repair == null) {
            return "工单不存在";
        }
        repairMapper.deleteById(id);
        return null;
    }
}
