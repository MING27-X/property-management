package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.SysUser;
import com.smartproperty.entity.Visitor;
import com.smartproperty.service.RoomService;
import com.smartproperty.service.VisitorService;
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
 * 访客登记管理。
 */
@Controller
@RequestMapping("/visitor")
public class VisitorController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       Model model) {
        PageResult<Visitor> page = visitorService.findPage(keyword, status, pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("rooms", roomService.findOptions(false));
        model.addAttribute("todayCount", visitorService.countToday());
        model.addAttribute("waitingCount", visitorService.countByStatus("待到访"));
        model.addAttribute("visitedCount", visitorService.countByStatus("已到访"));
        return "visitor/list";
    }

    @PostMapping("/save")
    public String save(Visitor visitor, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        String error = visitorService.save(visitor, loginUser.getId());
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", visitor.getId() == null ? "访客登记成功" : "访客信息已更新");
        }
        return "redirect:/visitor/list";
    }

    @PostMapping("/arrive")
    public String arrive(@RequestParam Integer id, RedirectAttributes ra) {
        String error = visitorService.arrive(id);
        ra.addFlashAttribute(error != null ? "error" : "msg", error != null ? error : "已确认访客到访");
        return "redirect:/visitor/list";
    }

    @PostMapping("/leave")
    public String leave(@RequestParam Integer id, RedirectAttributes ra) {
        String error = visitorService.leave(id);
        ra.addFlashAttribute(error != null ? "error" : "msg", error != null ? error : "已登记访客离开");
        return "redirect:/visitor/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, RedirectAttributes ra) {
        String error = visitorService.delete(id);
        ra.addFlashAttribute(error != null ? "error" : "msg", error != null ? error : "访客记录已删除");
        return "redirect:/visitor/list";
    }
}
