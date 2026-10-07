package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Notice;
import com.smartproperty.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公告通知业务。
 */
@Service
public class NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    public PageResult<Notice> findPage(String keyword, String type, boolean publishedOnly,
                                       int pageNum, int pageSize) {
        PageResult<Notice> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        page.setTotal(noticeMapper.countByCondition(keyword, type, publishedOnly));
        page.setList(noticeMapper.findPage(keyword, type, publishedOnly, page.getOffset(), pageSize));
        return page;
    }

    public Notice findById(Integer id) {
        return noticeMapper.findById(id);
    }

    public List<Notice> findLatest(int limit) {
        return noticeMapper.findLatest(limit);
    }

    public long countAll() {
        return noticeMapper.countAll();
    }

    /** 打开公告详情时累加浏览量 */
    @Transactional
    public Notice openDetail(Integer id) {
        Notice notice = noticeMapper.findById(id);
        if (notice != null) {
            noticeMapper.increaseView(id);
            notice.setViewCount(notice.getViewCount() == null ? 1 : notice.getViewCount() + 1);
        }
        return notice;
    }

    @Transactional
    public String save(Notice notice, Integer publisherId) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            return "公告标题不能为空";
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            return "公告内容不能为空";
        }
        if (notice.getType() == null || notice.getType().isEmpty()) {
            notice.setType("公告");
        }
        if (notice.getId() == null) {
            notice.setPublisherId(publisherId);
            notice.setStatus(notice.getStatus() == null ? 1 : notice.getStatus());
            noticeMapper.insert(notice);
        } else {
            noticeMapper.update(notice);
        }
        return null;
    }

    /** 发布 / 撤回公告 */
    @Transactional
    public String toggleStatus(Integer id) {
        Notice notice = noticeMapper.findById(id);
        if (notice == null) {
            return "公告不存在";
        }
        Integer status = notice.getStatus() != null && notice.getStatus() == 1 ? 0 : 1;
        noticeMapper.updateStatus(id, status);
        return null;
    }

    @Transactional
    public String delete(Integer id) {
        if (noticeMapper.findById(id) == null) {
            return "公告不存在";
        }
        noticeMapper.deleteById(id);
        return null;
    }
}
