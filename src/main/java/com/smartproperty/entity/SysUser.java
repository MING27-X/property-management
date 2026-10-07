package com.smartproperty.entity;

import java.util.Date;

/**
 * 系统用户（管理员 / 物业员工 / 业主）
 */
public class SysUser {

    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    /** ADMIN 系统管理员 / STAFF 物业员工 / OWNER 业主 */
    private String role;
    private Integer status;
    private Date createTime;

    /** 角色中文名称（页面展示用） */
    public String getRoleName() {
        if ("ADMIN".equals(role)) {
            return "系统管理员";
        }
        if ("STAFF".equals(role)) {
            return "物业员工";
        }
        if ("OWNER".equals(role)) {
            return "业主";
        }
        return "未知";
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isOwner() {
        return "OWNER".equals(role);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
