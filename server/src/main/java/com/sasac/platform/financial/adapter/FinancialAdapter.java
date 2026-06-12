package com.sasac.platform.financial.adapter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contract for financial system adapters.
 * <p>
 * Each adapter encapsulates the connection logic and data-mapping
 * for a specific financial ERP (e.g., UFIDA NC, Kingdee, SAP).
 */
public interface FinancialAdapter {

    /**
     * Returns a human-readable name for the financial system.
     */
    String getSystemName();

    /**
     * Tests the connection to the financial system with the given configuration.
     *
     * @param config connection parameters (URL, credentials, etc.)
     * @return true if the connection is established successfully
     */
    boolean testConnection(Map<String, String> config);

    /**
     * Fetches fixed asset cards from the financial system.
     *
     * @param config connection parameters
     * @return list of fixed asset cards
     */
    List<FixedAssetCard> fetchAssetCards(Map<String, String> config);

    /**
     * Fetches account balances for a given accounting period.
     *
     * @param config connection parameters
     * @param period the accounting period (e.g., "2025-01")
     * @return list of account balances
     */
    List<AccountBalance> fetchAccountBalances(Map<String, String> config, String period);

    /**
     * Fetches single-entity financial reports for a given period.
     *
     * @param config connection parameters
     * @param period the reporting period (e.g., "2025-Q1")
     * @return list of single financial reports
     */
    List<SingleFinancialReport> fetchSingleReports(Map<String, String> config, String period);

    // ---- value objects ----

    /**
     * Fixed asset card record from the financial system.
     */
    record FixedAssetCard(
            String assetCode,
            String name,
            String category,
            BigDecimal originalValue,
            BigDecimal accumulatedDepreciation,
            LocalDate purchaseDate,
            String department
    ) {}

    /**
     * General ledger account balance record.
     */
    record AccountBalance(
            String accountCode,
            String accountName,
            BigDecimal debitAmount,
            BigDecimal creditAmount,
            BigDecimal balance
    ) {}

    /**
     * Single-entity financial report with named line items.
     */
    record SingleFinancialReport(
            String companyCode,
            String companyName,
            String period,
            Map<String, BigDecimal> items
    ) {}
}
