package com.sasac.platform.asset.basic.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sasac.platform.asset.basic.entity.Location;
import com.sasac.platform.asset.basic.entity.MaintenanceProvider;
import com.sasac.platform.asset.basic.entity.Supplier;
import com.sasac.platform.asset.basic.mapper.AssetCategoryMapper;
import com.sasac.platform.asset.basic.mapper.LocationMapper;
import com.sasac.platform.asset.basic.mapper.MaintenanceProviderMapper;
import com.sasac.platform.asset.basic.mapper.SupplierMapper;
import com.sasac.platform.asset.entity.AssetCategory;
import com.sasac.platform.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service consolidating CRUD for all basic-data dictionary entities.
 */
@Service
@RequiredArgsConstructor
public class BasicDataService {

    private final LocationMapper locationMapper;
    private final SupplierMapper supplierMapper;
    private final MaintenanceProviderMapper maintenanceProviderMapper;
    private final AssetCategoryMapper assetCategoryMapper;

    // ---------------------------------------------------------------
    // Location
    // ---------------------------------------------------------------

    @Transactional
    public Location createLocation(Location location) {
        locationMapper.insert(location);
        return location;
    }

    public Location getLocation(Long id) {
        Location loc = locationMapper.selectById(id);
        if (loc == null) {
            throw new BusinessException("位置不存在");
        }
        return loc;
    }

    public List<Location> listLocations() {
        return locationMapper.selectList(
                new LambdaQueryWrapper<Location>().orderByAsc(Location::getSortOrder));
    }

    @Transactional
    public Location updateLocation(Long id, Location update) {
        Location existing = getLocation(id);
        Location merged = new Location();
        merged.setId(id);
        merged.setName(update.getName() != null ? update.getName() : existing.getName());
        merged.setAddress(update.getAddress() != null ? update.getAddress() : existing.getAddress());
        merged.setParentId(update.getParentId() != null ? update.getParentId() : existing.getParentId());
        merged.setSortOrder(update.getSortOrder() != null ? update.getSortOrder() : existing.getSortOrder());
        merged.setStatus(update.getStatus() != null ? update.getStatus() : existing.getStatus());
        merged.setTenantId(update.getTenantId() != null ? update.getTenantId() : existing.getTenantId());
        merged.setCreatedAt(existing.getCreatedAt());
        merged.setDeleted(existing.getDeleted());
        locationMapper.updateById(merged);
        return locationMapper.selectById(id);
    }

    @Transactional
    public void deleteLocation(Long id) {
        getLocation(id);
        locationMapper.deleteById(id);
    }

    // ---------------------------------------------------------------
    // Supplier
    // ---------------------------------------------------------------

    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        supplierMapper.insert(supplier);
        return supplier;
    }

    public Supplier getSupplier(Long id) {
        Supplier s = supplierMapper.selectById(id);
        if (s == null) {
            throw new BusinessException("供应商不存在");
        }
        return s;
    }

    public List<Supplier> listSuppliers() {
        return supplierMapper.selectList(
                new LambdaQueryWrapper<Supplier>().orderByDesc(Supplier::getId));
    }

    @Transactional
    public Supplier updateSupplier(Long id, Supplier update) {
        Supplier existing = getSupplier(id);
        Supplier merged = new Supplier();
        merged.setId(id);
        merged.setName(update.getName() != null ? update.getName() : existing.getName());
        merged.setContact(update.getContact() != null ? update.getContact() : existing.getContact());
        merged.setPhone(update.getPhone() != null ? update.getPhone() : existing.getPhone());
        merged.setAddress(update.getAddress() != null ? update.getAddress() : existing.getAddress());
        merged.setBusinessScope(update.getBusinessScope() != null ? update.getBusinessScope() : existing.getBusinessScope());
        merged.setStatus(update.getStatus() != null ? update.getStatus() : existing.getStatus());
        merged.setCreatedAt(existing.getCreatedAt());
        merged.setDeleted(existing.getDeleted());
        supplierMapper.updateById(merged);
        return supplierMapper.selectById(id);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        getSupplier(id);
        supplierMapper.deleteById(id);
    }

    // ---------------------------------------------------------------
    // MaintenanceProvider
    // ---------------------------------------------------------------

    @Transactional
    public MaintenanceProvider createMaintenanceProvider(MaintenanceProvider provider) {
        maintenanceProviderMapper.insert(provider);
        return provider;
    }

    public MaintenanceProvider getMaintenanceProvider(Long id) {
        MaintenanceProvider p = maintenanceProviderMapper.selectById(id);
        if (p == null) {
            throw new BusinessException("维保单位不存在");
        }
        return p;
    }

    public List<MaintenanceProvider> listMaintenanceProviders() {
        return maintenanceProviderMapper.selectList(
                new LambdaQueryWrapper<MaintenanceProvider>().orderByDesc(MaintenanceProvider::getId));
    }

    @Transactional
    public MaintenanceProvider updateMaintenanceProvider(Long id, MaintenanceProvider update) {
        MaintenanceProvider existing = getMaintenanceProvider(id);
        MaintenanceProvider merged = new MaintenanceProvider();
        merged.setId(id);
        merged.setName(update.getName() != null ? update.getName() : existing.getName());
        merged.setContact(update.getContact() != null ? update.getContact() : existing.getContact());
        merged.setPhone(update.getPhone() != null ? update.getPhone() : existing.getPhone());
        merged.setServiceTypes(update.getServiceTypes() != null ? update.getServiceTypes() : existing.getServiceTypes());
        merged.setStatus(update.getStatus() != null ? update.getStatus() : existing.getStatus());
        merged.setCreatedAt(existing.getCreatedAt());
        merged.setDeleted(existing.getDeleted());
        maintenanceProviderMapper.updateById(merged);
        return maintenanceProviderMapper.selectById(id);
    }

    @Transactional
    public void deleteMaintenanceProvider(Long id) {
        getMaintenanceProvider(id);
        maintenanceProviderMapper.deleteById(id);
    }

    // ---------------------------------------------------------------
    // AssetCategory
    // ---------------------------------------------------------------

    @Transactional
    public AssetCategory createCategory(AssetCategory category) {
        assetCategoryMapper.insert(category);
        return category;
    }

    public List<AssetCategory> listAssetCategories() {
        return assetCategoryMapper.selectList(
                new LambdaQueryWrapper<AssetCategory>().orderByAsc(AssetCategory::getLevel));
    }

    public AssetCategory getAssetCategory(Long id) {
        AssetCategory c = assetCategoryMapper.selectById(id);
        if (c == null) {
            throw new BusinessException("资产分类不存在");
        }
        return c;
    }

    @Transactional
    public AssetCategory updateCategory(Long id, AssetCategory update) {
        AssetCategory existing = getAssetCategory(id);
        AssetCategory merged = new AssetCategory();
        merged.setId(id);
        merged.setCode(update.getCode() != null ? update.getCode() : existing.getCode());
        merged.setName(update.getName() != null ? update.getName() : existing.getName());
        merged.setParentId(update.getParentId() != null ? update.getParentId() : existing.getParentId());
        merged.setLevel(update.getLevel() != null ? update.getLevel() : existing.getLevel());
        merged.setDepreciationMethod(update.getDepreciationMethod() != null ? update.getDepreciationMethod() : existing.getDepreciationMethod());
        merged.setDefaultUsefulLife(update.getDefaultUsefulLife() != null ? update.getDefaultUsefulLife() : existing.getDefaultUsefulLife());
        merged.setDefaultResidualRate(update.getDefaultResidualRate() != null ? update.getDefaultResidualRate() : existing.getDefaultResidualRate());
        merged.setCreatedAt(existing.getCreatedAt());
        merged.setDeleted(existing.getDeleted());
        assetCategoryMapper.updateById(merged);
        return assetCategoryMapper.selectById(id);
    }

    @Transactional
    public void deleteCategory(Long id) {
        getAssetCategory(id);
        assetCategoryMapper.deleteById(id);
    }
}
