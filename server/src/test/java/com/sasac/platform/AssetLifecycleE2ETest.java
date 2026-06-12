package com.sasac.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sasac.platform.asset.dto.AssetCreateDTO;
import com.sasac.platform.auth.dto.LoginRequest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end integration test covering the full asset lifecycle.
 * <p>
 * Tests the complete flow: login -> create asset -> record change ->
 * generate report -> submit report -> dashboard overview.
 * Uses ordered test methods to simulate a real user session.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class AssetLifecycleE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static Long assetId;
    private static Long reportId;

    @Test
    @Order(1)
    void step1_loginAndGetToken() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("admin");
        req.setPassword("admin123");

        String resp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(resp).get("data").get("token").asText();
    }

    @Test
    @Order(2)
    void step2_createAsset() throws Exception {
        AssetCreateDTO dto = new AssetCreateDTO();
        dto.setName("E2E测试资产-市委大楼");
        dto.setAssetCode("E2E-" + System.currentTimeMillis());
        dto.setCategory("REAL_ESTATE");
        dto.setOrgId(100L);
        dto.setTenantId(100L);
        dto.setOriginalValue(new BigDecimal("5000000.00"));
        dto.setDepreciationMethod("STRAIGHT_LINE");
        dto.setUsefulLifeMonths(240);

        String resp = mockMvc.perform(post("/api/assets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        assetId = objectMapper.readTree(resp).get("data").get("id").asLong();
    }

    @Test
    @Order(3)
    void step3_recordTransferChange() throws Exception {
        String changeJson = String.format(
                "{\"changeType\":\"TRANSFER\",\"toOrgId\":200,\"reason\":\"调拨到港口公司\"}");

        mockMvc.perform(post("/api/assets/" + assetId + "/changes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.changeType").value("TRANSFER"));
    }

    @Test
    @Order(4)
    void step4_verifyAssetUpdated() throws Exception {
        // After the transfer change, verify the asset was updated
        mockMvc.perform(get("/api/assets/" + assetId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.currentValue").isNotEmpty());
    }

    @Test
    @Order(5)
    void step5_generateAndSubmitReport() throws Exception {
        // Generate a report
        String resp = mockMvc.perform(post("/api/reports/generate")
                        .header("Authorization", "Bearer " + token)
                        .param("reportType", "ASSET_SUMMARY")
                        .param("orgId", "100")
                        .param("period", "2026-06")
                        .param("tenantId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.submitStatus").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();

        reportId = objectMapper.readTree(resp).get("data").get("id").asLong();

        // Submit the report
        mockMvc.perform(post("/api/reports/" + reportId + "/submit")
                        .header("Authorization", "Bearer " + token)
                        .param("operatorId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.submitStatus").value("SUBMITTED"));
    }

    @Test
    @Order(6)
    void step6_dashboardOverview() throws Exception {
        mockMvc.perform(get("/api/dashboard/overview")
                        .header("Authorization", "Bearer " + token)
                        .header("X-Tenant-Id", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalAssets").value(greaterThan(0)));
    }
}
