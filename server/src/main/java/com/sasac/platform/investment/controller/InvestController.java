package com.sasac.platform.investment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.investment.dto.InvestDealDTO;
import com.sasac.platform.investment.dto.InvestExitDTO;
import com.sasac.platform.investment.dto.InvestPlanCreateDTO;
import com.sasac.platform.investment.dto.InvestPostDTO;
import com.sasac.platform.investment.dto.InvestProjectCreateDTO;
import com.sasac.platform.investment.dto.InvestProjectQueryDTO;
import com.sasac.platform.investment.entity.InvestDD;
import com.sasac.platform.investment.entity.InvestDeal;
import com.sasac.platform.investment.entity.InvestEquityNode;
import com.sasac.platform.investment.entity.InvestExit;
import com.sasac.platform.investment.entity.InvestPlan;
import com.sasac.platform.investment.entity.InvestPost;
import com.sasac.platform.investment.entity.InvestProject;
import com.sasac.platform.investment.service.InvestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for investment management CRUD and lifecycle operations.
 */
@RestController
@RequestMapping("/api/investment")
@RequiredArgsConstructor
public class InvestController {

    private final InvestService investService;

    /**
     * Creates a new annual investment plan.
     */
    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<InvestPlan>> createPlan(@Valid @RequestBody InvestPlanCreateDTO dto) {
        InvestPlan plan = investService.createPlan(dto);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }

    /**
     * Creates a new investment project.
     */
    @PostMapping("/projects")
    public ResponseEntity<ApiResponse<InvestProject>> createProject(@Valid @RequestBody InvestProjectCreateDTO dto) {
        InvestProject project = investService.createProject(dto);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    /**
     * Queries investment projects with filters and pagination.
     */
    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<InvestProject>>> queryProjects(@ModelAttribute InvestProjectQueryDTO query) {
        Page<InvestProject> page = investService.queryProjects(query);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(page.getTotal())
                .page((int) page.getCurrent())
                .limit((int) page.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(page.getRecords(), meta));
    }

    /**
     * Retrieves an investment project by ID.
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<ApiResponse<InvestProject>> getProject(@PathVariable Long id) {
        InvestProject project = investService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    /**
     * Updates an investment project.
     */
    @PutMapping("/projects/{id}")
    public ResponseEntity<ApiResponse<InvestProject>> updateProject(@PathVariable Long id,
                                                                    @RequestBody InvestProject update) {
        InvestProject updated = investService.updateProject(id, update);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Soft-deletes an investment project by ID.
     */
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        investService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Records a due diligence report for a project.
     */
    @PostMapping("/projects/{id}/dd")
    public ResponseEntity<ApiResponse<InvestDD>> recordDD(@PathVariable Long id,
                                                          @Valid @RequestBody InvestDD dd) {
        dd.setProjectId(id);
        InvestDD created = investService.recordDD(dd);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves DD records for a project.
     */
    @GetMapping("/projects/{id}/dd")
    public ResponseEntity<ApiResponse<List<InvestDD>>> getDDRecords(@PathVariable Long id,
                                                                    @RequestParam Long tenantId) {
        List<InvestDD> records = investService.getDDRecords(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Records a deal / transaction for a project.
     */
    @PostMapping("/projects/{id}/deals")
    public ResponseEntity<ApiResponse<InvestDeal>> recordDeal(@PathVariable Long id,
                                                              @Valid @RequestBody InvestDealDTO dto) {
        dto.setProjectId(id);
        InvestDeal deal = investService.recordDeal(dto);
        return ResponseEntity.ok(ApiResponse.success(deal));
    }

    /**
     * Retrieves deal records for a project.
     */
    @GetMapping("/projects/{id}/deals")
    public ResponseEntity<ApiResponse<List<InvestDeal>>> getDealRecords(@PathVariable Long id,
                                                                        @RequestParam Long tenantId) {
        List<InvestDeal> records = investService.getDealRecords(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Records a post-investment monitoring snapshot.
     */
    @PostMapping("/projects/{id}/post")
    public ResponseEntity<ApiResponse<InvestPost>> recordPost(@PathVariable Long id,
                                                              @Valid @RequestBody InvestPostDTO dto) {
        dto.setProjectId(id);
        InvestPost post = investService.recordPost(dto);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    /**
     * Retrieves post-investment records for a project.
     */
    @GetMapping("/projects/{id}/post")
    public ResponseEntity<ApiResponse<List<InvestPost>>> getPostRecords(@PathVariable Long id,
                                                                        @RequestParam Long tenantId) {
        List<InvestPost> records = investService.getPostRecords(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Records an investment exit.
     */
    @PostMapping("/projects/{id}/exit")
    public ResponseEntity<ApiResponse<InvestExit>> recordExit(@PathVariable Long id,
                                                              @Valid @RequestBody InvestExitDTO dto) {
        dto.setProjectId(id);
        InvestExit exit = investService.recordExit(dto);
        return ResponseEntity.ok(ApiResponse.success(exit));
    }

    /**
     * Retrieves exit records for a project.
     */
    @GetMapping("/projects/{id}/exit")
    public ResponseEntity<ApiResponse<List<InvestExit>>> getExitRecords(@PathVariable Long id,
                                                                        @RequestParam Long tenantId) {
        List<InvestExit> records = investService.getExitRecords(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * Builds the equity penetration tree for a project.
     */
    @GetMapping("/projects/{id}/equity-tree")
    public ResponseEntity<ApiResponse<InvestEquityNode>> buildEquityTree(@PathVariable Long id,
                                                                         @RequestParam Long tenantId) {
        InvestEquityNode tree = investService.buildEquityTree(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(tree));
    }
}
