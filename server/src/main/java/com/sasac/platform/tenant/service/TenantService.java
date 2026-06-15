package com.sasac.platform.tenant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.asset.mapper.AssetMapper;
import com.sasac.platform.auth.mapper.UserMapper;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.tenant.entity.Tenant;
import com.sasac.platform.tenant.entity.TenantConfig;
import com.sasac.platform.tenant.entity.TenantUsage;
import com.sasac.platform.tenant.mapper.TenantConfigMapper;
import com.sasac.platform.tenant.mapper.TenantMapper;
import com.sasac.platform.tenant.mapper.TenantUsageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantMapper tenantMapper;
    private final TenantConfigMapper configMapper;
    private final TenantUsageMapper usageMapper;
    private final UserMapper userMapper;
    private final AssetMapper assetMapper;

    public Page<Tenant> listTenants(String keyword, Integer status, int page, int size) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<Tenant>()
                .like(keyword != null && !keyword.isBlank(), Tenant::getTenantName, keyword)
                .eq(status != null, Tenant::getStatus, status)
                .orderByDesc(Tenant::getCreatedAt);
        return tenantMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Tenant getTenant(Long id) {
        return tenantMapper.selectById(id);
    }

    public Tenant getByCode(String code) {
        return tenantMapper.selectOne(
                new LambdaQueryWrapper<Tenant>().eq(Tenant::getTenantCode, code)
        );
    }

    @Transactional
    public Tenant createTenant(Tenant tenant) {
        Tenant existing = getByCode(tenant.getTenantCode());
        if (existing != null) throw new BusinessException("租户编码已存在");
        tenantMapper.insert(tenant);

        TenantUsage usage = new TenantUsage();
        usage.setTenantId(tenant.getId());
        usageMapper.insert(usage);

        return tenant;
    }

    @Transactional
    public Tenant updateTenant(Long id, Tenant update) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) throw new BusinessException("租户不存在");

        tenant.setTenantName(update.getTenantName());
        tenant.setContactPerson(update.getContactPerson());
        tenant.setContactPhone(update.getContactPhone());
        tenant.setContactEmail(update.getContactEmail());
        tenant.setExpireTime(update.getExpireTime());
        tenant.setMaxUsers(update.getMaxUsers());
        tenant.setMaxAssets(update.getMaxAssets());
        tenant.setEdition(update.getEdition());
        tenant.setLogoUrl(update.getLogoUrl());
        tenant.setDomain(update.getDomain());
        tenant.setRemark(update.getRemark());
        tenantMapper.updateById(tenant);
        return tenant;
    }

    @Transactional
    public void toggleStatus(Long id, Integer status) {
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) throw new BusinessException("租户不存在");
        tenant.setStatus(status);
        tenantMapper.updateById(tenant);
    }

    public List<TenantConfig> getConfigs(Long tenantId) {
        return configMapper.selectList(
                new LambdaQueryWrapper<TenantConfig>().eq(TenantConfig::getTenantId, tenantId)
        );
    }

    @Transactional
    public void saveConfig(Long tenantId, String key, String value, String description) {
        TenantConfig existing = configMapper.selectOne(
                new LambdaQueryWrapper<TenantConfig>()
                        .eq(TenantConfig::getTenantId, tenantId)
                        .eq(TenantConfig::getConfigKey, key)
        );
        if (existing != null) {
            existing.setConfigValue(value);
            existing.setDescription(description);
            configMapper.updateById(existing);
        } else {
            TenantConfig config = new TenantConfig();
            config.setTenantId(tenantId);
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setDescription(description);
            configMapper.insert(config);
        }
    }

    public TenantUsage getUsage(Long tenantId) {
        return usageMapper.selectOne(
                new LambdaQueryWrapper<TenantUsage>().eq(TenantUsage::getTenantId, tenantId)
        );
    }

    @Transactional
    public TenantUsage refreshUsage(Long tenantId) {
        TenantUsage usage = getUsage(tenantId);
        if (usage == null) {
            usage = new TenantUsage();
            usage.setTenantId(tenantId);
            usageMapper.insert(usage);
        }

        long userCount = userMapper.selectCount(
                new LambdaQueryWrapper<com.sasac.platform.auth.entity.User>()
                        .eq(com.sasac.platform.auth.entity.User::getTenantId, tenantId)
        );
        long assetCount = assetMapper.selectCount(
                new LambdaQueryWrapper<com.sasac.platform.asset.entity.Asset>()
                        .eq(com.sasac.platform.asset.entity.Asset::getTenantId, tenantId)
        );

        usage.setUserCount((int) userCount);
        usage.setAssetCount((int) assetCount);
        usageMapper.updateById(usage);
        return usage;
    }

    public List<Tenant> listAll() {
        return tenantMapper.selectList(
                new LambdaQueryWrapper<Tenant>().eq(Tenant::getStatus, 1).orderByAsc(Tenant::getTenantCode)
        );
    }
}
