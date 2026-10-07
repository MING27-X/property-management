package com.smartproperty.mapper;

import com.smartproperty.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 系统用户数据访问接口。
 */
public interface SysUserMapper {

    /** 根据账号查询用户（登录用） */
    SysUser findByUsername(@Param("username") String username);

    /** 根据主键查询 */
    SysUser findById(@Param("id") Integer id);

    /** 分页查询用户列表 */
    List<SysUser> findPage(@Param("keyword") String keyword,
                           @Param("role") String role,
                           @Param("offset") int offset,
                           @Param("pageSize") int pageSize);

    /** 统计满足条件的用户数量 */
    long countByCondition(@Param("keyword") String keyword,
                          @Param("role") String role);

    /** 统计各角色人数，用于首页统计 */
    List<Map<String, Object>> countByRole();

    /** 查询所有业主角色的账号，用于业主档案绑定登录账号 */
    List<SysUser> findOwners();

    /** 查询所有物业员工，用于报修派单 */
    List<SysUser> findStaff();

    int insert(SysUser user);

    int update(SysUser user);

    int updatePassword(@Param("id") Integer id, @Param("password") String password);

    /** 按主键同步账号姓名（业主档案姓名变更时调用） */
    int updateRealName(@Param("id") Integer id, @Param("realName") String realName);

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int deleteById(@Param("id") Integer id);

    int countByUsername(@Param("username") String username);
}
