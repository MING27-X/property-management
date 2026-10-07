package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.Building;
import com.smartproperty.service.BuildingService;
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
 * 楼栋管理。
 */
@Controller
@RequestMapping("/building")
public class BuildingController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private BuildingService buildingService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       Model model) {
        PageResult<Building> page = buildingService.findPage(keyword, pageNum, PAGE_SIZE);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        return "building/list";
    }

    @PostMapping("/save")
    public String save(Building building, RedirectAttributes ra) {
        String error = buildingService.save(building);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", building.getId() == null ? "楼栋新增成功" : "楼栋信息已更新");
        }
        return "redirect:/building/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (!loginUser.isAdmin()) {
            ra.addFlashAttribute("error", "删除楼栋仅系统管理员可操作，物业员工可进行新增与修改");
            return "redirect:/building/list";
        }
        String error = buildingService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "楼栋已删除");
        }
        return "redirect:/building/list";
    }
}
