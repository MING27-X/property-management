package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Notice;
import com.smartproperty.entity.SysUser;
import com.smartproperty.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

/**
 * 公告通知管理。
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String type,
                       HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        // 业主只能看到已发布的公告
        boolean publishedOnly = loginUser.isOwner();
        PageResult<Notice> page = noticeService.findPage(keyword, type, publishedOnly,
                pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("noticeCount", noticeService.countAll());
        return "notice/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Integer id, HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        Notice notice = noticeService.findById(id);
        if (notice == null) {
            return "redirect:/notice/list";
        }
        // 先校验权限再统计浏览量，避免业主访问未发布公告也被计数
        if (loginUser.isOwner() && (notice.getStatus() == null || notice.getStatus() != 1)) {
            return "redirect:/notice/list";
        }
        notice = noticeService.openDetail(id);
        model.addAttribute("notice", notice);
        model.addAttribute("latestList", noticeService.findLatest(5));
        return "notice/detail";
    }

    @PostMapping("/save")
    public String save(Notice notice, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权发布公告");
            return "redirect:/notice/list";
        }
        String error = noticeService.save(notice, loginUser.getId());
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", notice.getId() == null ? "公告发布成功" : "公告内容已更新");
        }
        return "redirect:/notice/list";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权操作公告");
            return "redirect:/notice/list";
        }
        String error = noticeService.toggleStatus(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "公告状态已更新");
        }
        return "redirect:/notice/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权删除公告");
            return "redirect:/notice/list";
        }
        String error = noticeService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "公告已删除");
        }
        return "redirect:/notice/list";
    }
}
