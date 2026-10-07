package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Room;
import com.smartproperty.service.BuildingService;
import com.smartproperty.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartproperty.entity.SysUser;
import javax.servlet.http.HttpSession;

/**
 * 房屋管理。
 */
@Controller
@RequestMapping("/room")
public class RoomController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Integer buildingId,
                       @RequestParam(required = false) String status,
                       Model model) {
        PageResult<Room> page = roomService.findPage(keyword, buildingId, status, pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("buildingId", buildingId);
        model.addAttribute("status", status);
        model.addAttribute("buildings", buildingService.findAll());
        return "room/list";
    }

    @PostMapping("/save")
    public String save(Room room, RedirectAttributes ra) {
        String error = roomService.save(room);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", room.getId() == null ? "房屋新增成功" : "房屋信息已更新");
        }
        return "redirect:/room/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除房屋仅系统管理员可操作，物业员工可进行新增与修改");
            return "redirect:/room/list";
        }
        String error = roomService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "房屋已删除");
        }
        return "redirect:/room/list";
    }
}
