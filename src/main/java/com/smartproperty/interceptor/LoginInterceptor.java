package com.smartproperty.interceptor;

import com.smartproperty.entity.SysUser;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录与权限拦截器。
 * <p>
 * 未登录用户统一跳转到登录页；已登录用户访问超出角色的功能时跳回首页。
 */
public class LoginInterceptor implements HandlerInterceptor {

    /** 路径前缀 -> 允许访问的角色，多个角色用逗号分隔 */
    private static final Map<String, String> ROLE_RULES = new HashMap<>();

    /** 所有已登录用户均可访问的路径（业主自助页面等） */
    private static final String[] OPEN_PATHS = {
            "/owner/my", "/dashboard", "/profile", "/fee/", "/repair/",
            "/complaint/", "/notice/"
    };

    static {
        // 用户管理仅系统管理员可用
        ROLE_RULES.put("/user/", "ADMIN");
        // 小区基础数据（楼栋、房屋、业主、停车位）由管理员和物业员工维护
        ROLE_RULES.put("/building/", "ADMIN,STAFF");
        ROLE_RULES.put("/room/", "ADMIN,STAFF");
        ROLE_RULES.put("/owner/", "ADMIN,STAFF");
        ROLE_RULES.put("/parking/", "ADMIN,STAFF");
        // 访客登记由物业前台办理，业主端不开放
        ROLE_RULES.put("/visitor/", "ADMIN,STAFF");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        HttpSession session = request.getSession();
        SysUser loginUser = (SysUser) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect(contextPath + "/login");
            return false;
        }

        // 业主自助页面与公共业务页面：登录即可访问，数据范围由各控制器控制
        for (String open : OPEN_PATHS) {
            if (path.startsWith(open)) {
                return true;
            }
        }

        for (Map.Entry<String, String> rule : ROLE_RULES.entrySet()) {
            if (!path.startsWith(rule.getKey())) {
                continue;
            }
            for (String role : rule.getValue().split(",")) {
                if (role.equals(loginUser.getRole())) {
                    return true;
                }
            }
            // 角色不匹配，跳转到无权限提示页
            response.sendRedirect(contextPath + "/denied");
            return false;
        }
        return true;
    }
}
