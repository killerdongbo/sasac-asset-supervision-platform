package com.sasac.platform.permission.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.permission.annotation.RequirePermission;
import com.sasac.platform.permission.entity.Permission;
import com.sasac.platform.permission.entity.Role;
import com.sasac.platform.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permService;

    // ===== 角色 =====

    @GetMapping("/roles")
    public ApiResponse<List<Role>> listRoles(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId) {
        return ApiResponse.success(permService.listRoles(tenantId));
    }

    @GetMapping("/roles/{id}")
    public ApiResponse<Role> getRole(@PathVariable Long id) {
        return ApiResponse.success(permService.getRole(id));
    }

    @PostMapping("/roles")
    @RequirePermission(roles = "SYSTEM_ADMIN")
    public ApiResponse<Role> createRole(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestBody Role role) {
        role.setTenantId(tenantId);
        return ApiResponse.success(permService.createRole(role));
    }

    @PutMapping("/roles/{id}")
    @RequirePermission(roles = "SYSTEM_ADMIN")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        return ApiResponse.success(permService.updateRole(id, role));
    }

    @DeleteMapping("/roles/{id}")
    @RequirePermission(roles = "SYSTEM_ADMIN")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        permService.deleteRole(id);
        return ApiResponse.success(null);
    }

    // ===== 权限列表 =====

    @GetMapping
    public ApiResponse<List<Permission>> listPermissions() {
        return ApiResponse.success(permService.listAllPermissions());
    }

    @GetMapping("/roles/{roleId}/permissions")
    public ApiResponse<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        return ApiResponse.success(permService.getRolePermissions(roleId));
    }

    @PutMapping("/roles/{roleId}/permissions")
    @RequirePermission(roles = "SYSTEM_ADMIN")
    public ApiResponse<Void> assignRolePermissions(
            @PathVariable Long roleId, @RequestBody List<Long> permIds) {
        permService.assignPermissions(roleId, permIds);
        return ApiResponse.success(null);
    }

    // ===== 用户角色 =====

    @GetMapping("/users/{userId}/roles")
    public ApiResponse<List<Role>> getUserRoles(@PathVariable Long userId) {
        return ApiResponse.success(permService.getUserRoles(userId));
    }

    @PutMapping("/users/{userId}/roles")
    @RequirePermission(roles = "SYSTEM_ADMIN")
    public ApiResponse<Void> assignUserRoles(
            @PathVariable Long userId, @RequestBody List<Long> roleIds) {
        permService.assignUserRoles(userId, roleIds);
        return ApiResponse.success(null);
    }

    @GetMapping("/users/{userId}/codes")
    public ApiResponse<Set<String>> getUserPermissionCodes(@PathVariable Long userId) {
        return ApiResponse.success(permService.getUserPermissionCodes(userId));
    }
}
