package com.smartproperty.controller;

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

import javax.servlet.http.HttpSession;

/**
 * 个人中心：查看本人信息、修改登录密码。
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private SysUserService userService;

    @GetMapping
    public String index(HttpSession session, Model model) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        model.addAttribute("user", userService.findById(loginUser.getId()));
        return "profile";
    }

    @PostMapping("/info")
    public String updateInfo(@RequestParam String realName,
                             @RequestParam(required = false) String phone,
                             HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        SysUser user = userService.findById(loginUser.getId());
        user.setRealName(realName);
        user.setPhone(phone);
        String error = userService.save(user);
        if (error != null) {
            ra.addFlashAttribute("error", error);
        } else {
            session.setAttribute("loginUser", userService.findById(loginUser.getId()));
            ra.addFlashAttribute("msg", "个人信息已更新");
        }
        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session, RedirectAttributes ra) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        if (newPassword == null || newPassword.trim().length() < 6) {
            ra.addFlashAttribute("error", "新密码长度不能少于 6 位");
            return "redirect:/profile";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "两次输入的新密码不一致");
            return "redirect:/profile";
        }
        if (userService.changePassword(loginUser.getId(), oldPassword, newPassword)) {
            ra.addFlashAttribute("msg", "密码修改成功，请使用新密码登录");
        } else {
            ra.addFlashAttribute("error", "原密码不正确");
        }
        return "redirect:/profile";
    }
}
