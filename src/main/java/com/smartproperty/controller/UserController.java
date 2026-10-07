package com.smartproperty.controller;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.SysUser;
import com.smartproperty.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * 系统用户管理（仅系统管理员，访问权限由拦截器控制）。
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private SysUserService userService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String role,
                       Model model) {
        PageResult<SysUser> page = userService.findPage(keyword, role, pageNum, PAGE_SIZE);
        Map<String, Integer> roleStat = userService.countByRole();
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("adminCount", roleStat.containsKey("ADMIN") ? roleStat.get("ADMIN") : 0);
        model.addAttribute("staffCount", roleStat.containsKey("STAFF") ? roleStat.get("STAFF") : 0);
        model.addAttribute("ownerCount", roleStat.containsKey("OWNER") ? roleStat.get("OWNER") : 0);
        return "user/list";
    }

    @PostMapping("/save")
    public String save(SysUser user, RedirectAttributes ra) {
        String error = userService.save(user);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", user.getId() == null
                    ? "账号新增成功，初始密码为 123456"
                    : "账号信息已更新");
        }
        return "redirect:/user/list";
    }

    @PostMapping("/reset")
    public String reset(@RequestParam Integer id, RedirectAttributes ra) {
        userService.resetPassword(id);
        ra.addFlashAttribute("msg", "密码已重置为 123456");
        return "redirect:/user/list";
    }

    @PostMapping("/toggle")
    public String toggle(@RequestParam Integer id, RedirectAttributes ra) {
        if (userService.toggleStatus(id)) {
            ra.addFlashAttribute("msg", "账号状态已更新");
        } else {
            ra.addFlashAttribute("error", "系统内置管理员账号不允许禁用");
        }
        return "redirect:/user/list";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Integer id, RedirectAttributes ra) {
        String error = userService.delete(id);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            ra.addFlashAttribute("msg", "账号已删除");
        }
        return "redirect:/user/list";
    }
}
