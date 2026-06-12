package com.sasac.platform.asset.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AssetTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @Test
    void validAsset_shouldPassValidation() {
        // Arrange
        Asset asset = new Asset();
        asset.setName("办公服务器");
        asset.setAssetCode("ZC-2024-001");
        asset.setCategory("IT_EQUIPMENT");
        asset.setOrgId(1L);
        asset.setOriginalValue(new BigDecimal("50000.00"));

        // Act
        Set<ConstraintViolation<Asset>> violations = validator.validate(asset);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void assetWithMissingName_shouldFailValidation() {
        // Arrange
        Asset asset = new Asset();
        asset.setAssetCode("ZC-2024-002");
        asset.setCategory("FURNITURE");
        asset.setOrgId(2L);

        // Act
        Set<ConstraintViolation<Asset>> violations = validator.validate(asset);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }
}
