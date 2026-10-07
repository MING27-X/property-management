package com.smartproperty.service.impl;

import com.smartproperty.common.PageResult;
import com.smartproperty.entity.SysUser;
import com.smartproperty.mapper.OwnerMapper;
import com.smartproperty.mapper.SysUserMapper;
import com.smartproperty.service.SysUserService;
import com.smartproperty.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {

    /** 默认密码 */
    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Override
    public SysUser findByUsername(String username) {
        return username == null ? null : userMapper.findByUsername(username.trim());
    }

    @Override
    public boolean checkPassword(SysUser user, String rawPassword) {
        return user != null && MD5Util.matches(rawPassword, user.getPassword());
    }

    @Override
    public SysUser login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        SysUser user = userMapper.findByUsername(username.trim());
        if (user == null) {
            return null;
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return MD5Util.matches(password, user.getPassword()) ? user : null;
    }

    @Override
    public SysUser findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public PageResult<SysUser> findPage(String keyword, String role, int pageNum, int pageSize) {
        PageResult<SysUser> page = new PageResult<>();
        page.setPageNum(pageNum < 1 ? 1 : pageNum);
        page.setPageSize(pageSize);
        long total = userMapper.countByCondition(keyword, role);
        page.setTotal(total);
        page.setList(userMapper.findPage(keyword, role, page.getOffset(), pageSize));
        return page;
    }

    @Override
    @Transactional
    public String save(SysUser user) {
        if (user.getId() == null) {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return "登录账号不能为空";
            }
            if (userMapper.countByUsername(user.getUsername().trim()) > 0) {
                return "账号【" + user.getUsername() + "】已存在";
            }
            user.setUsername(user.getUsername().trim());
            user.setPassword(MD5Util.encrypt(DEFAULT_PASSWORD));
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
            userMapper.insert(user);
            return null;
        }
        userMapper.update(user);
        // 姓名双向同步：账号姓名变更时，同步更新其关联的业主档案姓名
        if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            ownerMapper.updateNameByUserId(user.getId(), user.getRealName().trim());
        }
        return null;
    }

    @Override
    @Transactional
    public void resetPassword(Integer id) {
        userMapper.updatePassword(id, MD5Util.encrypt(DEFAULT_PASSWORD));
    }

    @Override
    @Transactional
    public boolean changePassword(Integer id, String oldPassword, String newPassword) {
        SysUser user = userMapper.findById(id);
        if (user == null || !MD5Util.matches(oldPassword, user.getPassword())) {
            return false;
        }
        userMapper.updatePassword(id, MD5Util.encrypt(newPassword));
        return true;
    }

    @Override
    @Transactional
    public String delete(Integer id) {
        SysUser user = userMapper.findById(id);
        if (user == null) {
            return "用户不存在";
        }
        if ("admin".equals(user.getUsername())) {
            return "系统内置管理员账号不允许删除";
        }
        // 解除与业主档案的绑定，避免业主档案里残留无效账号
        ownerMapper.clearUserLink(id);
        userMapper.deleteById(id);
        return null;
    }

    @Override
    public Map<String, Integer> countByRole() {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Object>> rows = userMapper.countByRole();
        for (Map<String, Object> row : rows) {
            result.put(String.valueOf(row.get("role")), ((Number) row.get("num")).intValue());
        }
        return result;
    }

    @Override
    public List<SysUser> findOwners() {
        return userMapper.findOwners();
    }

    @Override
    public List<SysUser> findStaff() {
        return userMapper.findStaff();
    }

    @Override
    @Transactional
    public boolean toggleStatus(Integer id) {
        SysUser user = userMapper.findById(id);
        if (user == null || "admin".equals(user.getUsername())) {
            return false;
        }
        int status = user.getStatus() != null && user.getStatus() == 1 ? 0 : 1;
        userMapper.updateStatus(id, status);
        return true;
    }
}
