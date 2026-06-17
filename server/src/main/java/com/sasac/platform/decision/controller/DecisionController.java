package com.sasac.platform.decision.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.decision.dto.DecisionItemCreateDTO;
import com.sasac.platform.decision.dto.MeetingCreateDTO;
import com.sasac.platform.decision.dto.ResolutionCreateDTO;
import com.sasac.platform.decision.dto.SupervisionCreateDTO;
import com.sasac.platform.decision.entity.DecisionItem;
import com.sasac.platform.decision.entity.DecisionMeeting;
import com.sasac.platform.decision.entity.DecisionResolution;
import com.sasac.platform.decision.entity.DecisionSupervision;
import com.sasac.platform.decision.service.DecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for 三重一大 decision management.
 */
@RestController
@RequestMapping("/api/decision")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    /**
     * Submits a new decision item.
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<DecisionItem>> submitItem(@Valid @RequestBody DecisionItemCreateDTO dto) {
        DecisionItem created = decisionService.submitItem(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Queries decision items with filters and pagination.
     */
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<DecisionItem>>> queryItems(
            @RequestParam Long tenantId,
            @RequestParam(required = false) String itemType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<DecisionItem> result = decisionService.queryItems(tenantId, itemType, status, page, limit);
        ApiResponse.PageMeta meta = ApiResponse.PageMeta.builder()
                .total(result.getTotal())
                .page((int) result.getCurrent())
                .limit((int) result.getSize())
                .build();
        return ResponseEntity.ok(ApiResponse.success(result.getRecords(), meta));
    }

    /**
     * Creates a new decision meeting.
     */
    @PostMapping("/meetings")
    public ResponseEntity<ApiResponse<DecisionMeeting>> createMeeting(@Valid @RequestBody MeetingCreateDTO dto) {
        DecisionMeeting created = decisionService.createMeeting(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Creates a resolution for a specific meeting.
     */
    @PostMapping("/meetings/{meetingId}/resolutions")
    public ResponseEntity<ApiResponse<DecisionResolution>> createResolution(
            @PathVariable Long meetingId,
            @Valid @RequestBody ResolutionCreateDTO dto) {
        DecisionResolution created = decisionService.createResolution(meetingId, dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Creates a supervision task for a specific resolution.
     */
    @PostMapping("/resolutions/{resolutionId}/supervisions")
    public ResponseEntity<ApiResponse<DecisionSupervision>> createSupervision(
            @PathVariable Long resolutionId,
            @Valid @RequestBody SupervisionCreateDTO dto) {
        DecisionSupervision created = decisionService.createSupervision(resolutionId, dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves all pending supervision tasks for a tenant.
     */
    @GetMapping("/supervisions/pending")
    public ResponseEntity<ApiResponse<List<DecisionSupervision>>> pendingSupervisions(@RequestParam Long tenantId) {
        List<DecisionSupervision> list = decisionService.queryPendingSupervisions(tenantId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
