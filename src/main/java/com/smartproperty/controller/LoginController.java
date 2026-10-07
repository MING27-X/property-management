package com.smartproperty.controller;

import com.smartproperty.entity.SysUser;
import com.smartproperty.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * 登录 / 注销。
 */
@Controller
public class LoginController {

    @Autowired
    private SysUserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session, Model model) {
        SysUser user = userService.findByUsername(username);
        String error = null;
        if (user == null || !userService.checkPassword(user, password)) {
            error = "账号或密码错误，请重新输入";
        } else if (user.getStatus() != null && user.getStatus() == 0) {
            error = "该账号已被禁用，请联系系统管理员";
        }
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("username", username);
            return "login";
        }
        session.setAttribute("loginUser", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/error/404")
    public String notFound() {
        return "error/404";
    }

    @GetMapping("/error/500")
    public String serverError() {
        return "error/500";
    }
}
