package com.sasac.platform.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.permission.entity.*;
import com.sasac.platform.permission.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final RoleMapper roleMapper;
    private final PermissionMapper permMapper;
    private final RolePermissionMapper rpMapper;
    private final UserRoleMapper urMapper;

    // ===== 角色管理 =====

    public List<Role> listRoles(Long tenantId) {
        return roleMapper.selectList(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getTenantId, tenantId)
                        .or().eq(Role::getRoleType, "SYSTEM")
                        .orderByAsc(Role::getRoleCode)
        );
    }

    public Role getRole(Long id) {
        return roleMapper.selectById(id);
    }

    @Transactional
    public Role createRole(Role role) {
        Role exist = roleMapper.selectOne(
                new LambdaQueryWrapper<Role>()
                        .eq(Role::getTenantId, role.getTenantId())
                        .eq(Role::getRoleCode, role.getRoleCode())
        );
        if (exist != null) throw new BusinessException("角色编码已存在");
        role.setRoleType("CUSTOM");
        roleMapper.insert(role);
        return role;
    }

    @Transactional
    public Role updateRole(Long id, Role update) {
        Role role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException("角色不存在");
        if ("SYSTEM".equals(role.getRoleType())) throw new BusinessException("内置角色不可修改");

        role.setRoleName(update.getRoleName());
        role.setDataScope(update.getDataScope());
        role.setDescription(update.getDescription());
        roleMapper.updateById(role);
        return role;
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleMapper.selectById(id);
        if (role == null) throw new BusinessException("角色不存在");
        if ("SYSTEM".equals(role.getRoleType())) throw new BusinessException("内置角色不可删除");

        rpMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
        urMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId, id));
        roleMapper.deleteById(id);
    }

    // ===== 权限列表 =====

    public List<Permission> listAllPermissions() {
        return permMapper.selectList(
                new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getSortOrder)
        );
    }

    // ===== 角色权限分配 =====

    public List<Permission> getRolePermissions(Long roleId) {
        List<RolePermission> rps = rpMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId)
        );
        if (rps.isEmpty()) return List.of();

        return permMapper.selectList(
                new LambdaQueryWrapper<Permission>().in(Permission::getId,
                        rps.stream().map(RolePermission::getPermId).toList())
        );
    }

    @Transactional
    public void assignPermissions(Long roleId, List<Long> permIds) {
        rpMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        for (Long permId : permIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermId(permId);
            rpMapper.insert(rp);
        }
    }

    // ===== 用户角色分配 =====

    public List<Role> getUserRoles(Long userId) {
        List<UserRole> urs = urMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        if (urs.isEmpty()) return List.of();

        return roleMapper.selectList(
                new LambdaQueryWrapper<Role>().in(Role::getId,
                        urs.stream().map(UserRole::getRoleId).toList())
        );
    }

    @Transactional
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        urMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        for (Long rid : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(rid);
            urMapper.insert(ur);
        }
    }

    // ===== 获取用户所有权限码 =====

    public Set<String> getUserPermissionCodes(Long userId) {
        List<UserRole> urs = urMapper.selectList(
                new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId)
        );
        if (urs.isEmpty()) return Set.of();

        List<RolePermission> rps = rpMapper.selectList(
                new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId,
                        urs.stream().map(UserRole::getRoleId).toList())
        );
        if (rps.isEmpty()) return Set.of();

        List<Permission> perms = permMapper.selectList(
                new LambdaQueryWrapper<Permission>().in(Permission::getId,
                        rps.stream().map(RolePermission::getPermId).distinct().toList())
        );

        return perms.stream().map(Permission::getPermCode).collect(Collectors.toSet());
    }
}
