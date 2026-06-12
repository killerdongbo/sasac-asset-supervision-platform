package com.sasac.platform.common.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TenantContextTest {

    @AfterEach
    void clear() {
        TenantContext.clear();
    }

    @Test
    void shouldSetAndGetTenantId() {
        TenantContext.setCurrentTenant(1L);
        assertThat(TenantContext.getCurrentTenant()).isEqualTo(1L);
    }

    @Test
    void shouldClearTenantId() {
        TenantContext.setCurrentTenant(1L);
        TenantContext.clear();
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }

    @Test
    void shouldReturnNullWhenNotSet() {
        assertThat(TenantContext.getCurrentTenant()).isNull();
    }
}
