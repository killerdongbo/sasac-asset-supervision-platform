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
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getAddress() != null) existing.setAddress(update.getAddress());
        if (update.getParentId() != null) existing.setParentId(update.getParentId());
        if (update.getSortOrder() != null) existing.setSortOrder(update.getSortOrder());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        if (update.getTenantId() != null) existing.setTenantId(update.getTenantId());
        locationMapper.updateById(existing);
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
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getContact() != null) existing.setContact(update.getContact());
        if (update.getPhone() != null) existing.setPhone(update.getPhone());
        if (update.getAddress() != null) existing.setAddress(update.getAddress());
        if (update.getBusinessScope() != null) existing.setBusinessScope(update.getBusinessScope());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        supplierMapper.updateById(existing);
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
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getContact() != null) existing.setContact(update.getContact());
        if (update.getPhone() != null) existing.setPhone(update.getPhone());
        if (update.getServiceTypes() != null) existing.setServiceTypes(update.getServiceTypes());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        maintenanceProviderMapper.updateById(existing);
        return maintenanceProviderMapper.selectById(id);
    }

    @Transactional
    public void deleteMaintenanceProvider(Long id) {
        getMaintenanceProvider(id);
        maintenanceProviderMapper.deleteById(id);
    }

    // ---------------------------------------------------------------
    // AssetCategory (read-only)
    // ---------------------------------------------------------------

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
}
