package com.sasac.platform.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Asset category entity representing the classification hierarchy of assets.
 * <p>
 * Supports multiple levels of categories with default depreciation settings
 * that can be inherited by assets in that category.
 */
@Getter
@Setter
@TableName("asset_category")
public class AssetCategory extends BaseEntity {

    /**
     * Category business code, unique.
     */
    @NotBlank(message = "分类编码不能为空")
    private String code;

    /**
     * Category display name.
     */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /**
     * Parent category ID. Null for root-level categories.
     */
    private Long parentId;

    /**
     * Category level in the hierarchy. Defaults to 1 for root categories.
     */
    private Integer level = 1;

    /**
     * Default depreciation method for assets in this category.
     * Defaults to STRAIGHT_LINE (straight-line depreciation).
     */
    private String depreciationMethod = "STRAIGHT_LINE";

    /**
     * Default useful life in months for assets in this category.
     */
    private Integer defaultUsefulLife;

    /**
     * Default residual rate for assets in this category. Defaults to 0.05 (5%).
     */
    private BigDecimal defaultResidualRate = BigDecimal.valueOf(0.05);
}
