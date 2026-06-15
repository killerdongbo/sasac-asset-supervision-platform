package com.sasac.platform.notification.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.notification.entity.Notification;
import com.sasac.platform.notification.entity.NotificationTemplate;
import com.sasac.platform.notification.mapper.NotificationMapper;
import com.sasac.platform.notification.mapper.NotificationTemplateMapper;
import com.sasac.platform.notification.ws.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationMapper notifMapper;
    private final NotificationTemplateMapper tplMapper;
    private final NotificationWebSocketHandler wsHandler;

    // ---- 查询 ----

    public Page<Notification> listByUser(Long userId, Integer isRead, int page, int size) {
        return notifMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(isRead != null, Notification::getIsRead, isRead)
                        .orderByDesc(Notification::getCreatedAt));
    }

    public long getUnreadCount(Long userId) {
        return notifMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0));
    }

    // ---- 发送 ----

    @Transactional
    public Notification send(Long tenantId, Long userId, String title, String content,
                             String type, String level, String bizType, Long bizId) {
        Notification n = new Notification();
        n.setTenantId(tenantId);
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        n.setType(type != null ? type : "SYSTEM");
        n.setLevel(level != null ? level : "INFO");
        n.setBizType(bizType);
        n.setBizId(bizId);
        notifMapper.insert(n);

        pushToUser(userId, n);
        return n;
    }

    @Transactional
    public Notification sendByTemplate(Long tenantId, Long userId, String templateCode,
                                        Map<String, String> params) {
        NotificationTemplate tpl = tplMapper.selectOne(
                new LambdaQueryWrapper<NotificationTemplate>()
                        .eq(NotificationTemplate::getTemplateCode, templateCode)
                        .eq(NotificationTemplate::getStatus, 1));
        if (tpl == null) {
            log.warn("模板不存在或已禁用: {}", templateCode);
            return null;
        }

        String title = replaceParams(tpl.getTitleTpl(), params);
        String content = replaceParams(tpl.getContentTpl(), params);
        return send(tenantId, userId, title, content, "SYSTEM", "INFO", null, null);
    }

    @Transactional
    public void broadcastToTenant(Long tenantId, String title, String content, String level) {
        // 实际项目中需要查询该租户下所有在线用户ID
        // 这里简化处理
        log.info("Broadcast to tenant {}: {}", tenantId, title);
    }

    private void pushToUser(Long userId, Notification n) {
        try {
            Map<String, Object> payload = Map.of(
                    "type", "notification",
                    "id", n.getId(),
                    "title", n.getTitle(),
                    "level", n.getLevel()
            );
            wsHandler.push(userId, payload);
        } catch (Exception e) {
            log.debug("WS push skipped for user {}: {}", userId, e.getMessage());
        }
    }

    // ---- 标记已读 ----

    @Transactional
    public void markRead(Long notificationId, Long userId) {
        Notification n = notifMapper.selectById(notificationId);
        if (n == null || !n.getUserId().equals(userId)) throw new BusinessException("通知不存在");
        n.setIsRead(1);
        n.setReadAt(LocalDateTime.now());
        notifMapper.updateById(n);
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> unread = notifMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0));
        for (Notification n : unread) {
            n.setIsRead(1);
            n.setReadAt(LocalDateTime.now());
            notifMapper.updateById(n);
        }
    }

    // ---- 模板管理 ----

    public List<NotificationTemplate> listTemplates() {
        return tplMapper.selectList(null);
    }

    private String replaceParams(String tpl, Map<String, String> params) {
        String result = tpl;
        for (Map.Entry<String, String> e : params.entrySet()) {
            result = result.replace("{" + e.getKey() + "}", e.getValue());
        }
        return result;
    }
}
