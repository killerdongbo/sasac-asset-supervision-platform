package com.sasac.platform.notification.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.notification.entity.Notification;
import com.sasac.platform.notification.entity.NotificationTemplate;
import com.sasac.platform.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ApiResponse<Page<Notification>> list(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(notificationService.listByUser(userId, isRead, page, size));
    }

    @GetMapping("/notifications/unread-count")
    public ApiResponse<Long> unreadCount(@RequestParam Long userId) {
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/notifications/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.markRead(id, userId);
        return ApiResponse.success(null);
    }

    @PutMapping("/notifications/read-all")
    public ApiResponse<Void> markAllRead(@RequestParam Long userId) {
        notificationService.markAllRead(userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/notifications/send")
    public ApiResponse<Notification> send(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "0") Long tenantId,
            @RequestBody Map<String, Object> body) {
        return ApiResponse.success(notificationService.send(
                tenantId,
                Long.parseLong(body.get("userId").toString()),
                (String) body.get("title"),
                (String) body.get("content"),
                (String) body.getOrDefault("type", "SYSTEM"),
                (String) body.getOrDefault("level", "INFO"),
                (String) body.getOrDefault("bizType", null),
                body.get("bizId") != null ? Long.parseLong(body.get("bizId").toString()) : null));
    }

    @GetMapping("/notification-templates")
    public ApiResponse<List<NotificationTemplate>> listTemplates() {
        return ApiResponse.success(notificationService.listTemplates());
    }
}
