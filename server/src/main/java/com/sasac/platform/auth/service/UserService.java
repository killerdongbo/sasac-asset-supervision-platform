package com.sasac.platform.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.auth.entity.User;
import com.sasac.platform.auth.mapper.UserMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permService;

    public Page<User> listUsers(Long tenantId, String keyword, Integer status, int page, int size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, tenantId)
                .and(keyword != null && !keyword.isBlank(), q ->
                        q.like(User::getUsername, keyword).or().like(User::getRealName, keyword))
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreatedAt);
        return userMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public User getUser(Long id) {
        return userMapper.selectById(id);
    }

    @Transactional
    public User createUser(User user) {
        User exist = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (exist != null) throw new BusinessException("用户名已存在");
        user.setPassword(passwordEncoder.encode(user.getPassword() != null ? user.getPassword() : "123456"));
        if (user.getStatus() == null) user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    @Transactional
    public User updateUser(Long id, User update) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setRealName(update.getRealName());
        user.setPhone(update.getPhone());
        user.setOrgId(update.getOrgId());
        user.setRoleCode(update.getRoleCode());
        user.setStatus(update.getStatus());
        userMapper.updateById(user);
        return user;
    }

    @Transactional
    public void resetPassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(passwordEncoder.encode(newPassword != null ? newPassword : "123456"));
        userMapper.updateById(user);
    }

    @Transactional
    public void toggleStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Transactional
    public void assignRoles(Long userId, java.util.List<Long> roleIds) {
        permService.assignUserRoles(userId, roleIds);
    }
}
