package com.smartproperty.service;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.SysUser;

import java.util.Map;

/**
 * 系统用户业务接口。
 */
public interface SysUserService {

    /** 登录校验，成功返回用户对象，失败返回 null */
    SysUser login(String username, String password);

    /** 按账号查询用户（用于区分「账号或密码错误」与「账号已被禁用」） */
    SysUser findByUsername(String username);

    /** 校验明文密码是否正确 */
    boolean checkPassword(SysUser user, String rawPassword);

    SysUser findById(Integer id);

    PageResult<SysUser> findPage(String keyword, String role, int pageNum, int pageSize);

    /** 新增或修改用户 */
    String save(SysUser user);

    /** 重置密码为 123456 */
    void resetPassword(Integer id);

    /** 修改密码，返回是否成功 */
    boolean changePassword(Integer id, String oldPassword, String newPassword);

    /** 删除用户 */
    String delete(Integer id);

    /** 启用 / 禁用账号，返回是否成功 */
    boolean toggleStatus(Integer id);

    /** 各角色人数统计 */
    Map<String, Integer> countByRole();

    /** 业主账号列表 */
    java.util.List<SysUser> findOwners();

    /** 物业员工列表（含管理员），用于派单 */
    java.util.List<SysUser> findStaff();
}
