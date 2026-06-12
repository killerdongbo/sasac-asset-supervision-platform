package com.sasac.platform.financial.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class FinancialServiceTest {

    @Autowired
    FinancialService service;

    @Test
    void shouldListAvailableAdapters() {
        List<String> adapters = service.getAvailableAdapters();
        assertThat(adapters).contains("用友NC");
    }

    @Test
    void shouldTestConnection() {
        boolean connected = service.testConnection("用友NC", Map.of("serverUrl", "http://test"));
        assertThat(connected).isTrue();
    }

    @Test
    void shouldFetchAssetCards() {
        var cards = service.fetchAssets("用友NC", Map.of());
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).assetCode()).isEqualTo("NC-ZC-001");
    }

    @Test
    void shouldThrowForUnknownAdapter() {
        assertThrows(RuntimeException.class, () ->
                service.testConnection("不存在的系统", Map.of()));
    }
}
