package com.sasac.platform.asset.basic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for BasicDataController covering CRUD operations
 * on all four basic-data entities: Locations, Suppliers, MaintenanceProviders,
 * and AssetCategories.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class BasicDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;
    private static Long locationId;
    private static Long supplierId;
    private static Long providerId;

    // ---------------------------------------------------------------
    // Shared: login
    // ---------------------------------------------------------------

    @Test
    @Order(1)
    void step1_login() throws Exception {
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

    // ---------------------------------------------------------------
    // Location CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(2)
    void step2_createLocation() throws Exception {
        String body = """
                {"name":"市委大楼","address":"湛江市赤坎区","parentId":null,"tenantId":100,"sortOrder":1,"status":1}
                """;

        String resp = mockMvc.perform(post("/api/basic-data/locations")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.name").value("市委大楼"))
                .andReturn().getResponse().getContentAsString();

        locationId = objectMapper.readTree(resp).get("data").get("id").asLong();
    }

    @Test
    @Order(3)
    void step3_getLocation() throws Exception {
        mockMvc.perform(get("/api/basic-data/locations/" + locationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("市委大楼"))
                .andExpect(jsonPath("$.data.address").value("湛江市赤坎区"));
    }

    @Test
    @Order(4)
    void step4_updateLocation() throws Exception {
        String body = """
                {"name":"市委大楼-更新","address":"湛江市霞山区"}
                """;

        mockMvc.perform(put("/api/basic-data/locations/" + locationId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("市委大楼-更新"));
    }

    @Test
    @Order(5)
    void step5_deleteLocation() throws Exception {
        mockMvc.perform(delete("/api/basic-data/locations/" + locationId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(6)
    void step6_listLocations_emptyAfterDelete() throws Exception {
        mockMvc.perform(get("/api/basic-data/locations")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ---------------------------------------------------------------
    // Supplier CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(7)
    void step7_createSupplier() throws Exception {
        String body = """
                {"name":"湛江物资有限公司","contact":"张三","phone":"13800138000","address":"湛江市开发区","businessScope":"建材销售","tenantId":100,"status":1}
                """;

        String resp = mockMvc.perform(post("/api/basic-data/suppliers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.name").value("湛江物资有限公司"))
                .andReturn().getResponse().getContentAsString();

        supplierId = objectMapper.readTree(resp).get("data").get("id").asLong();
    }

    @Test
    @Order(8)
    void step8_getSupplier() throws Exception {
        mockMvc.perform(get("/api/basic-data/suppliers/" + supplierId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("湛江物资有限公司"))
                .andExpect(jsonPath("$.data.contact").value("张三"));
    }

    @Test
    @Order(9)
    void step9_deleteSupplier() throws Exception {
        mockMvc.perform(delete("/api/basic-data/suppliers/" + supplierId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ---------------------------------------------------------------
    // MaintenanceProvider CRUD
    // ---------------------------------------------------------------

    @Test
    @Order(10)
    void step10_createMaintenanceProvider() throws Exception {
        String body = """
                {"name":"湛江设备维修中心","contact":"李四","phone":"13900139000","serviceTypes":"空调维修,电梯维护","tenantId":100,"status":1}
                """;

        String resp = mockMvc.perform(post("/api/basic-data/maintenance-providers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.name").value("湛江设备维修中心"))
                .andReturn().getResponse().getContentAsString();

        providerId = objectMapper.readTree(resp).get("data").get("id").asLong();
    }

    @Test
    @Order(11)
    void step11_getMaintenanceProvider() throws Exception {
        mockMvc.perform(get("/api/basic-data/maintenance-providers/" + providerId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("湛江设备维修中心"))
                .andExpect(jsonPath("$.data.serviceTypes").value("空调维修,电梯维护"));
    }

    @Test
    @Order(12)
    void step12_updateMaintenanceProvider() throws Exception {
        String body = """
                {"name":"湛江设备维修中心-更新","serviceTypes":"空调维修,电梯维护,消防维护"}
                """;

        mockMvc.perform(put("/api/basic-data/maintenance-providers/" + providerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("湛江设备维修中心-更新"))
                .andExpect(jsonPath("$.data.serviceTypes").value("空调维修,电梯维护,消防维护"));
    }

    @Test
    @Order(13)
    void step13_deleteMaintenanceProvider() throws Exception {
        mockMvc.perform(delete("/api/basic-data/maintenance-providers/" + providerId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ---------------------------------------------------------------
    // AssetCategory (read-only, seeded by V3 migration)
    // ---------------------------------------------------------------

    @Test
    @Order(14)
    void step14_listAssetCategories() throws Exception {
        mockMvc.perform(get("/api/basic-data/asset-categories")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(15)
    void step15_getAssetCategory() throws Exception {
        mockMvc.perform(get("/api/basic-data/asset-categories/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
