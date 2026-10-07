package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Complaint;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.SysUser;
import com.smartproperty.service.ComplaintService;
import com.smartproperty.service.OwnerService;
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
 * 投诉建议管理。
 */
@Controller
@RequestMapping("/complaint")
public class ComplaintController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String type,
                       HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        Integer ownerId = null;
        if (loginUser.isOwner()) {
            Owner owner = ownerService.findByUserId(loginUser.getId());
            ownerId = owner == null ? -1 : owner.getId();
            keyword = null;
            model.addAttribute("owner", owner);
        } else {
            model.addAttribute("owners", ownerService.findAll());
        }
        PageResult<Complaint> page = complaintService.findPage(keyword, status, type, ownerId,
                pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        // 业主端只看自己的诉求统计
        if (loginUser.isOwner()) {
            model.addAttribute("pendingCount", complaintService.countByStatusAndOwner("待处理", ownerId));
            model.addAttribute("repliedCount", complaintService.countByStatusAndOwner("已回复", ownerId));
        } else {
            model.addAttribute("pendingCount", complaintService.countByStatus("待处理"));
            model.addAttribute("repliedCount", complaintService.countByStatus("已回复"));
        }
        return "complaint/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Integer id, HttpSession session, Model model) {
        Complaint complaint = complaintService.findById(id);
        if (complaint == null) {
            return "redirect:/complaint/list";
        }
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            Owner owner = ownerService.findByUserId(loginUser.getId());
            if (owner == null || !owner.getId().equals(complaint.getOwnerId())) {
                return "redirect:/complaint/list";
            }
        }
        model.addAttribute("complaint", complaint);
        return "complaint/detail";
    }

    @PostMapping("/save")
    public String save(Complaint complaint, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        String error = complaintService.save(complaint, loginUser);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "提交成功，物业服务中心将尽快处理并回复");
        }
        return "redirect:/complaint/list";
    }

    @PostMapping("/reply")
    public String reply(@RequestParam Integer id,
                        @RequestParam String replyContent,
                        HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权回复");
            return "redirect:/complaint/list";
        }
        String error = complaintService.reply(id, replyContent, loginUser.getId());
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "回复成功");
        }
        return "redirect:/complaint/detail?id=" + id;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权删除记录");
            return "redirect:/complaint/list";
        }
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除投诉建议记录仅系统管理员可操作");
            return "redirect:/complaint/list";
        }
        String error = complaintService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "记录已删除");
        }
        return "redirect:/complaint/list";
    }
}
