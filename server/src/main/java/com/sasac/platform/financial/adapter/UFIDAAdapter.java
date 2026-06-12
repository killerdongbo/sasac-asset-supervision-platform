package com.sasac.platform.financial.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Stub UFIDA NC adapter for MVP demonstration.
 * <p>
 * Returns mock / hard-coded data and logs all method invocations.
 * Will be replaced with real UFIDA NC SOAP/REST integration later.
 */
@Slf4j
@Component
public class UFIDAAdapter implements FinancialAdapter {

    @Override
    public String getSystemName() {
        return "用友NC";
    }

    @Override
    public boolean testConnection(Map<String, String> config) {
        log.info("Testing connection to 用友NC with config: {}", config);
        // MVP stub — always succeeds
        return true;
    }

    @Override
    public List<FixedAssetCard> fetchAssetCards(Map<String, String> config) {
        log.info("Fetching asset cards from 用友NC with config: {}", config);
        FixedAssetCard sample = new FixedAssetCard(
                "NC-ZC-001",
                "办公楼A",
                "房屋建筑物",
                new BigDecimal("5000000.00"),
                new BigDecimal("500000.00"),
                LocalDate.of(2020, 6, 1),
                "行政部"
        );
        return List.of(sample);
    }

    @Override
    public List<AccountBalance> fetchAccountBalances(Map<String, String> config, String period) {
        log.info("Fetching account balances from 用友NC for period {} with config: {}", period, config);
        AccountBalance sample = new AccountBalance(
                "1601",
                "固定资产",
                new BigDecimal("100000.00"),
                new BigDecimal("0.00"),
                new BigDecimal("100000.00")
        );
        return List.of(sample);
    }

    @Override
    public List<SingleFinancialReport> fetchSingleReports(Map<String, String> config, String period) {
        log.info("Fetching single reports from 用友NC for period {} with config: {}", period, config);
        // MVP stub — returns empty list
        return List.of();
    }
}
