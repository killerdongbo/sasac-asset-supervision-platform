package com.sasac.platform.financial.service;

import com.sasac.platform.financial.adapter.FinancialAdapter;
import com.sasac.platform.financial.adapter.FinancialAdapter.AccountBalance;
import com.sasac.platform.financial.adapter.FinancialAdapter.FixedAssetCard;
import com.sasac.platform.financial.adapter.FinancialAdapter.SingleFinancialReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Facade over all registered {@link FinancialAdapter} beans.
 * <p>
 * Spring auto-discovers every {@code @Component} implementation
 * and injects them into the constructor list.
 */
@Service
@RequiredArgsConstructor
public class FinancialService {

    private final List<FinancialAdapter> adapters;

    /**
     * Returns the display names of all available adapters.
     */
    public List<String> getAvailableAdapters() {
        return adapters.stream()
                .map(FinancialAdapter::getSystemName)
                .toList();
    }

    /**
     * Tests the connection for the named adapter.
     *
     * @param adapterName the adapter display name (e.g., "用友NC")
     * @param config      connection parameters
     * @return true if the connection succeeds
     * @throws RuntimeException if no adapter matches
     */
    public boolean testConnection(String adapterName, Map<String, String> config) {
        return findAdapter(adapterName).testConnection(config);
    }

    /**
     * Fetches asset cards from the named adapter.
     */
    public List<FixedAssetCard> fetchAssets(String adapterName, Map<String, String> config) {
        return findAdapter(adapterName).fetchAssetCards(config);
    }

    /**
     * Fetches account balances from the named adapter.
     */
    public List<AccountBalance> fetchBalances(String adapterName,
                                               Map<String, String> config,
                                               String period) {
        return findAdapter(adapterName).fetchAccountBalances(config, period);
    }

    /**
     * Fetches single-entity reports from the named adapter.
     */
    public List<SingleFinancialReport> fetchReports(String adapterName,
                                                     Map<String, String> config,
                                                     String period) {
        return findAdapter(adapterName).fetchSingleReports(config, period);
    }

    // ---- private helpers ----

    private FinancialAdapter findAdapter(String name) {
        return adapters.stream()
                .filter(a -> a.getSystemName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No financial adapter found for: " + name));
    }
}
