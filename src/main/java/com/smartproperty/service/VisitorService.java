package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Visitor;
import com.smartproperty.mapper.VisitorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 访客登记业务。
 */
@Service
public class VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;

    public PageResult<Visitor> findPage(String keyword, String status, int pageNum, int pageSize) {
        PageResult<Visitor> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(visitorMapper.countByCondition(keyword, status));
        page.setList(visitorMapper.findPage(keyword, status, page.getOffset(), pageSize));
        return page;
    }

    public long countByStatus(String status) {
        return visitorMapper.countByStatus(status);
    }

    public long countToday() {
        return visitorMapper.countToday();
    }

    @Transactional
    public String save(Visitor visitor, Integer registerBy) {
        if (visitor.getVisitorName() == null || visitor.getVisitorName().trim().isEmpty()) {
            return "访客姓名不能为空";
        }
        if (visitor.getRoomId() == null) {
            return "请选择访问房屋";
        }
        if (visitor.getVisitTime() == null) {
            return "请填写到访时间";
        }
        if (visitor.getStatus() == null || visitor.getStatus().isEmpty()) {
            visitor.setStatus("待到访");
        }
        if (visitor.getId() == null) {
            visitor.setRegisterBy(registerBy);
            visitorMapper.insert(visitor);
        } else {
            // 编辑表单不含离开时间，这里按状态维护，避免编辑后离开时间丢失
            Visitor old = visitorMapper.findById(visitor.getId());
            if ("已离开".equals(visitor.getStatus())) {
                visitor.setLeaveTime(old != null && old.getLeaveTime() != null
                        ? old.getLeaveTime() : new Date());
            } else {
                visitor.setLeaveTime(null);
            }
            visitorMapper.update(visitor);
        }
        return null;
    }

    /** 确认到访 */
    @Transactional
    public String arrive(Integer id) {
        Visitor visitor = visitorMapper.findById(id);
        if (visitor == null) {
            return "访客记录不存在";
        }
        if ("已离开".equals(visitor.getStatus())) {
            return "该访客已离开，无需重复确认";
        }
        visitorMapper.updateStatus(id, "已到访");
        return null;
    }

    /** 登记离开 */
    @Transactional
    public String leave(Integer id) {
        Visitor visitor = visitorMapper.findById(id);
        if (visitor == null) {
            return "访客记录不存在";
        }
        if ("已离开".equals(visitor.getStatus())) {
            return "该访客已登记离开";
        }
        visitorMapper.leave(id);
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        if (visitorMapper.findById(id) == null) {
            return "访客记录不存在";
        }
        visitorMapper.deleteById(id);
        return null;
    }
}
