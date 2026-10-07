package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Complaint;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.SysUser;
import com.smartproperty.mapper.ComplaintMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 投诉建议业务。
 */
@Service
public class ComplaintService {

    @Autowired
    private ComplaintMapper complaintMapper;

    @Autowired
    private OwnerService ownerService;

    public PageResult<Complaint> findPage(String keyword, String status, String type,
                                          Integer ownerId, int pageNum, int pageSize) {
        PageResult<Complaint> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(complaintMapper.countByCondition(keyword, status, type, ownerId));
        page.setList(complaintMapper.findPage(keyword, status, type, ownerId,
                page.getOffset(), pageSize));
        return page;
    }

    public Complaint findById(Integer id) {
        return complaintMapper.findById(id);
    }

    public long countByStatus(String status) {
        return complaintMapper.countByStatus(status);
    }

    /** 统计某位业主自己某个状态的诉求数量（业主端统计卡使用） */
    public long countByStatusAndOwner(String status, Integer ownerId) {
        return complaintMapper.countByCondition(null, status, null, ownerId);
    }

    /** 业主提交投诉或建议（物业员工可代业主录入） */
    @Transactional
    public String save(Complaint complaint, SysUser loginUser) {
        if (complaint.getTitle() == null || complaint.getTitle().trim().isEmpty()) {
            return "标题不能为空";
        }
        if (complaint.getContent() == null || complaint.getContent().trim().isEmpty()) {
            return "内容不能为空";
        }
        Owner owner;
        if (loginUser.isOwner()) {
            owner = ownerService.findByUserId(loginUser.getId());
        } else {
            owner = complaint.getOwnerId() == null ? null : ownerService.findById(complaint.getOwnerId());
        }
        if (owner == null) {
            return loginUser.isOwner()
                    ? "当前账号未关联业主档案，请联系物业服务中心"
                    : "请选择提交投诉建议的业主";
        }
        complaint.setOwnerId(owner.getId());
        if (complaint.getType() == null || complaint.getType().isEmpty()) {
            complaint.setType("投诉");
        }
        complaintMapper.insert(complaint);
        return null;
    }

    /** 物业回复 */
    @Transactional
    public String reply(Integer id, String replyContent, Integer replyBy) {
        Complaint complaint = complaintMapper.findById(id);
        if (complaint == null) {
            return "记录不存在";
        }
        if (replyContent == null || replyContent.trim().isEmpty()) {
            return "请填写回复内容";
        }
        complaintMapper.reply(id, replyContent.trim(), replyBy);
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        Complaint complaint = complaintMapper.findById(id);
        if (complaint == null) {
            return "记录不存在";
        }
        complaintMapper.deleteById(id);
        return null;
    }
}
