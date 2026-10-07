package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Owner;
import com.smartproperty.entity.Room;
import com.smartproperty.service.BuildingService;
import com.smartproperty.service.OwnerService;
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
import java.util.List;

/**
 * 业主管理（含业主自助「我的房屋」页面）。
 */
@Controller
@RequestMapping("/owner")
public class OwnerController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private SysUserService userService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer buildingId,
                       Model model) {
        PageResult<Owner> page = ownerService.findPage(keyword, buildingId, pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("buildingId", buildingId);
        model.addAttribute("buildings", buildingService.findAll());
        // 可绑定房屋：空置房屋 + 当前被业主占用的房屋（编辑时用）
        List<Room> rooms = roomService.findOptions(false);
        model.addAttribute("rooms", rooms);
        model.addAttribute("ownerUsers", userService.findOwners());
        return "owner/list";
    }

    /** 业主自助：我的房屋 */
    @GetMapping("/my")
    public String my(HttpSession session, Model model) {
        com.smartproperty.entity.SysUser loginUser =
                (com.smartproperty.entity.SysUser) session.getAttribute("loginUser");
        Owner owner = ownerService.findByUserId(loginUser.getId());
        model.addAttribute("owner", owner);
        if (owner != null) {
            model.addAttribute("myRooms", roomService.findByOwnerId(owner.getId()));
        }
        return "owner/my";
    }

    @PostMapping("/save")
    public String save(Owner owner, RedirectAttributes ra) {
        String error = ownerService.save(owner);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", owner.getId() == null ? "业主档案新增成功" : "业主档案已更新");
        }
        return "redirect:/owner/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        com.smartproperty.entity.SysUser loginUser =
                (com.smartproperty.entity.SysUser) session.getAttribute("loginUser");
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除业主档案仅系统管理员可操作，物业员工可进行登记与修改");
            return "redirect:/owner/list";
        }
        String error = ownerService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "业主档案已删除，房屋状态已恢复为空置");
        }
        return "redirect:/owner/list";
    }
}
