package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.Repair;
import com.smartproperty.entity.Room;
import com.smartproperty.entity.SysUser;
import com.smartproperty.service.OwnerService;
import com.smartproperty.service.RepairService;
import com.smartproperty.service.RoomService;
import com.smartproperty.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报修工单管理（业主报修 + 物业派单处理）。
 */
@Controller
@RequestMapping("/repair")
public class RepairController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private RepairService repairService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private SysUserService userService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String category,
                       HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        Integer ownerId = null;
        if (loginUser.isOwner()) {
            Owner owner = ownerService.findByUserId(loginUser.getId());
            ownerId = owner == null ? -1 : owner.getId();
            keyword = null;
            model.addAttribute("myRooms", owner == null ? null : roomService.findByOwnerId(owner.getId()));
            model.addAttribute("owner", owner);
        } else {
            model.addAttribute("rooms", roomService.findOptions(true));
        }

        PageResult<Repair> page = repairService.findPage(keyword, status, category, ownerId,
                pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("category", category);
        // 业主端只看自己的工单统计，物业端看全小区统计
        if (loginUser.isOwner()) {
            Map<String, Long> myStat = new HashMap<>();
            myStat.put("待处理", repairService.countByStatusAndOwner("待处理", ownerId));
            myStat.put("处理中", repairService.countByStatusAndOwner("处理中", ownerId));
            myStat.put("已完成", repairService.countByStatusAndOwner("已完成", ownerId));
            model.addAttribute("stat", myStat);
        } else {
            model.addAttribute("stat", repairService.countGroupByStatus());
        }
        return "repair/list";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam Integer id, HttpSession session, Model model) {
        Repair repair = repairService.findById(id);
        if (repair == null) {
            return "redirect:/repair/list";
        }
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        // 业主只能查看本人的工单
        if (loginUser.isOwner()) {
            Owner owner = ownerService.findByUserId(loginUser.getId());
            if (owner == null || !owner.getId().equals(repair.getOwnerId())) {
                return "redirect:/repair/list";
            }
        } else {
            model.addAttribute("staffList", userService.findStaff());
        }
        model.addAttribute("repair", repair);
        return "repair/detail";
    }

    @PostMapping("/save")
    public String save(Repair repair, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        String error = repairService.save(repair, loginUser);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "报修工单提交成功，物业将尽快安排处理");
        }
        return "redirect:/repair/list";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam Integer id, @RequestParam Integer handlerId,
                         HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权派单");
            return "redirect:/repair/list";
        }
        String error = repairService.assign(id, handlerId);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "派单成功，工单状态已更新为处理中");
        }
        return "redirect:/repair/detail?id=" + id;
    }

    @PostMapping("/finish")
    public String finish(@RequestParam Integer id,
                         @RequestParam(required = false) Integer handlerId,
                         @RequestParam(required = false) String handleRemark,
                         HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权处理工单");
            return "redirect:/repair/list";
        }
        String error = repairService.finish(id, handlerId != null ? handlerId : loginUser.getId(),
                handleRemark);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "工单已处理完成");
        }
        return "redirect:/repair/detail?id=" + id;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (loginUser.isOwner()) {
            ra.addFlashAttribute("error", "业主账号无权删除工单");
            return "redirect:/repair/list";
        }
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除报修工单仅系统管理员可操作");
            return "redirect:/repair/list";
        }
        String error = repairService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "工单已删除");
        }
        return "redirect:/repair/list";
    }
}
