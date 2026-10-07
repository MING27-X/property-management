package com.smartproperty.config;

import com.smartproperty.entity.SysUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 全局控制器增强：
 * 1. 所有页面自动携带当前登录用户，方便侧边栏与顶栏渲染；
 * 2. 统一异常处理，避免异常堆栈直接暴露给用户。
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addLoginUser(Model model, HttpSession session) {
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        ModelAndView mv = new ModelAndView("error/500");
        mv.addObject("message", e.getMessage());
        mv.addObject("url", request.getRequestURI());
        return mv;
    }

    /**
     * 表单数据格式错误（例如日期填成 abc）时给出友好提示并回到对应列表页，
     * 避免用户看到「系统出现异常」页面。
     */
    @ExceptionHandler({BindException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public String handleBindingError(Exception e, HttpServletRequest request,
                                     RedirectAttributes ra) {
        ra.addFlashAttribute("error",
                "输入的数据格式不正确，请检查后重新提交（日期示例 2026-03-19，时间示例 2026-03-19 10:00）");
        return "redirect:" + moduleListUrl(request);
    }

    /** 由请求路径推导出所属模块的列表页，例如 /visitor/save -> /visitor/list */
    private String moduleListUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        int index = uri.indexOf('/', 1);
        String module = index > 0 ? uri.substring(0, index) : uri;
        if ("/profile".equals(module) || "/login".equals(module)) {
            return module;
        }
        return module + "/list";
    }

    @RequestMapping("/denied")
    public String denied() {
        return "error/denied";
    }
}
