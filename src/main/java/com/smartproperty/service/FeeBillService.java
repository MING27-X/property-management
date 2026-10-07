package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.FeeBill;
import com.smartproperty.entity.Room;
import com.smartproperty.mapper.FeeBillMapper;
import com.smartproperty.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 物业收费业务。
 */
@Service
public class FeeBillService {

    @Autowired
    private FeeBillMapper feeBillMapper;

    @Autowired
    private RoomMapper roomMapper;

    public PageResult<FeeBill> findPage(String keyword, String status, String feeType,
                                        String period, Integer ownerId, int pageNum, int pageSize) {
        PageResult<FeeBill> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(feeBillMapper.countByCondition(keyword, status, feeType, period, ownerId));
        page.setList(feeBillMapper.findPage(keyword, status, feeType, period, ownerId,
                page.getOffset(), pageSize));
        return page;
    }

    public FeeBill findById(Integer id) {
        return feeBillMapper.findById(id);
    }

    public BigDecimal sumAmount(String status, String period, Integer ownerId) {
        BigDecimal value = feeBillMapper.sumAmount(status, period, ownerId);
        return value == null ? BigDecimal.ZERO : value;
    }

    public long countUnpaidByOwner(Integer ownerId) {
        return feeBillMapper.countUnpaidByOwner(ownerId);
    }

    public long countByStatus(String status) {
        return feeBillMapper.countByStatus(status);
    }

    public List<Map<String, Object>> sumByPeriod() {
        return feeBillMapper.sumByPeriod();
    }

    @Transactional
    public String save(FeeBill bill) {
        if (bill.getRoomId() == null) {
            return "请选择房屋";
        }
        if (bill.getAmount() == null) {
            return "请填写应缴金额";
        }
        if (bill.getPeriod() == null || bill.getPeriod().trim().isEmpty()) {
            return "请填写费用周期";
        }
        // 费用归属业主跟随房屋自动确定
        Room room = roomMapper.findById(bill.getRoomId());
        if (room == null) {
            return "房屋不存在";
        }
        if (room.getOwnerId() == null) {
            return "该房屋尚未登记业主，请先在业主管理中登记";
        }
        bill.setOwnerId(room.getOwnerId());

        if (bill.getFeeType() == null || bill.getFeeType().isEmpty()) {
            bill.setFeeType("物业费");
        }
        if (bill.getStatus() == null || bill.getStatus().isEmpty()) {
            bill.setStatus("未缴");
        }
        // 编辑已缴费账单时，表单不含缴费时间与方式，这里先取回原值（必须在下面的默认赋值之前）
        if (bill.getId() != null && "已缴".equals(bill.getStatus())) {
            FeeBill old = feeBillMapper.findById(bill.getId());
            if (old != null && "已缴".equals(old.getStatus())) {
                if (bill.getPayTime() == null) {
                    bill.setPayTime(old.getPayTime());
                }
                if (bill.getPayMethod() == null) {
                    bill.setPayMethod(old.getPayMethod());
                }
            }
        }
        if ("已缴".equals(bill.getStatus()) && bill.getPayTime() == null) {
            bill.setPayTime(new java.util.Date());
            if (bill.getPayMethod() == null) {
                bill.setPayMethod("线下缴费");
            }
        }
        if ("未缴".equals(bill.getStatus())) {
            bill.setPayTime(null);
            bill.setPayMethod(null);
        }

        if (bill.getId() == null) {
            feeBillMapper.insert(bill);
        } else {
            feeBillMapper.update(bill);
        }
        return null;
    }

    /** 缴费核销 */
    @Transactional
    public String pay(Integer id, String payMethod) {
        FeeBill bill = feeBillMapper.findById(id);
        if (bill == null) {
            return "账单不存在";
        }
        if ("已缴".equals(bill.getStatus())) {
            return "该账单已完成缴费";
        }
        feeBillMapper.pay(id, payMethod == null || payMethod.isEmpty() ? "线下缴费" : payMethod);
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        FeeBill bill = feeBillMapper.findById(id);
        if (bill == null) {
            return "账单不存在";
        }
        feeBillMapper.deleteById(id);
        return null;
    }
}
