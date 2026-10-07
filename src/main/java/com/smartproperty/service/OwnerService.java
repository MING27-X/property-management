package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Owner;
import com.smartproperty.mapper.OwnerMapper;
import com.smartproperty.mapper.ParkingMapper;
import com.smartproperty.mapper.RoomMapper;
import com.smartproperty.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业主档案业务。业主与房屋是一对一绑定关系，
 * 绑定/解绑时会同步维护房屋的入住状态。
 */
@Service
public class OwnerService {

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private ParkingMapper parkingMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    public PageResult<Owner> findPage(String keyword, Integer buildingId, int pageNum, int pageSize) {
        PageResult<Owner> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(ownerMapper.countByCondition(keyword, buildingId));
        page.setList(ownerMapper.findPage(keyword, buildingId, page.getOffset(), pageSize));
        return page;
    }

    public List<Owner> findAll() {
        return ownerMapper.findAll();
    }

    public Owner findById(Integer id) {
        return ownerMapper.findById(id);
    }

    public Owner findByUserId(Integer userId) {
        return ownerMapper.findByUserId(userId);
    }

    public long countAll() {
        return ownerMapper.countAll();
    }

    @Transactional
    public String save(Owner owner) {
        if (owner.getName() == null || owner.getName().trim().isEmpty()) {
            return "业主姓名不能为空";
        }
        if (owner.getPhone() == null || owner.getPhone().trim().isEmpty()) {
            return "联系电话不能为空";
        }
        if (owner.getRoomId() == null) {
            return "请选择业主房屋";
        }
        if (ownerMapper.countByRoomId(owner.getRoomId(), owner.getId()) > 0) {
            return "该房屋已登记其他业主，请先解除原业主绑定";
        }
        if (owner.getFamilyCount() == null) {
            owner.setFamilyCount(1);
        }

        if (owner.getId() == null) {
            ownerMapper.insert(owner);
            roomMapper.updateStatusAndOwner(owner.getRoomId(), owner.getId(), "已入住");
        } else {
            Owner old = ownerMapper.findById(owner.getId());
            ownerMapper.update(owner);
            // 更换房屋时解除原房屋的绑定
            if (old != null && old.getRoomId() != null && !old.getRoomId().equals(owner.getRoomId())) {
                roomMapper.updateStatusAndOwner(old.getRoomId(), null, "空置");
            }
            roomMapper.updateStatusAndOwner(owner.getRoomId(), owner.getId(), "已入住");
        }
        // 姓名双向同步：业主档案姓名变更时，同步更新其关联登录账号的姓名
        if (owner.getUserId() != null) {
            sysUserMapper.updateRealName(owner.getUserId(), owner.getName());
        }
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        Owner owner = ownerMapper.findById(id);
        if (owner == null) {
            return "业主不存在";
        }
        // 完整性校验：存在关联业务数据时不允许删除，避免出现孤儿数据
        long bills = ownerMapper.countBills(id);
        long repairs = ownerMapper.countRepairs(id);
        long complaints = ownerMapper.countComplaints(id);
        long parkings = parkingMapper.countByOwner(id);
        if (bills + repairs + complaints + parkings > 0) {
            return String.format("该业主存在关联数据（账单 %d 条、报修 %d 条、投诉建议 %d 条、车位 %d 个），请先处理后再删除",
                    bills, repairs, complaints, parkings);
        }
        if (owner.getRoomId() != null) {
            roomMapper.updateStatusAndOwner(owner.getRoomId(), null, "空置");
        }
        ownerMapper.deleteById(id);
        return null;
    }
}
