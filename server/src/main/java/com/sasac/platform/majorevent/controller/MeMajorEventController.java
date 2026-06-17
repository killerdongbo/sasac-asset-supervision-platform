package com.sasac.platform.majorevent.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.majorevent.dto.EventDTO;
import com.sasac.platform.majorevent.dto.GuaranteeDTO;
import com.sasac.platform.majorevent.dto.LawsuitDTO;
import com.sasac.platform.majorevent.entity.MeEvent;
import com.sasac.platform.majorevent.entity.MeGuarantee;
import com.sasac.platform.majorevent.entity.MeLawsuit;
import com.sasac.platform.majorevent.service.MeMajorEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for major event management operations.
 */
@RestController
@RequestMapping("/api/major-events")
@RequiredArgsConstructor
public class MeMajorEventController {

    private final MeMajorEventService meMajorEventService;

    // ===== Events =====

    @GetMapping("/events")
    public ApiResponse<List<MeEvent>> listEvents(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(meMajorEventService.listEvents(tenantId, eventType, status));
    }

    @GetMapping("/events/{id}")
    public ApiResponse<MeEvent> getEvent(@PathVariable Long id) {
        return ApiResponse.success(meMajorEventService.getEvent(id));
    }

    @PostMapping("/events")
    public ApiResponse<MeEvent> reportEvent(@Valid @RequestBody EventDTO dto) {
        return ApiResponse.success(meMajorEventService.report(dto));
    }

    @PutMapping("/events/{id}")
    public ApiResponse<MeEvent> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO dto) {
        return ApiResponse.success(meMajorEventService.updateEvent(id, dto));
    }

    @PostMapping("/events/{id}/approve")
    public ApiResponse<MeEvent> approveEvent(@PathVariable Long id) {
        return ApiResponse.success(meMajorEventService.approve(id));
    }

    @PutMapping("/events/{id}/track")
    public ApiResponse<MeEvent> trackEvent(@PathVariable Long id, @RequestParam String progress) {
        return ApiResponse.success(meMajorEventService.track(id, progress));
    }

    @PutMapping("/events/{id}/resolve")
    public ApiResponse<MeEvent> resolveEvent(@PathVariable Long id) {
        return ApiResponse.success(meMajorEventService.resolve(id));
    }

    @DeleteMapping("/events/{id}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long id) {
        meMajorEventService.deleteEvent(id);
        return ApiResponse.success(null);
    }

    // ===== Lawsuits =====

    @GetMapping("/lawsuits")
    public ApiResponse<List<MeLawsuit>> listLawsuits(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) String status) {
        return ApiResponse.success(meMajorEventService.listLawsuits(tenantId, eventId, status));
    }

    @GetMapping("/lawsuits/{id}")
    public ApiResponse<MeLawsuit> getLawsuit(@PathVariable Long id) {
        return ApiResponse.success(meMajorEventService.getLawsuit(id));
    }

    @PostMapping("/lawsuits")
    public ApiResponse<MeLawsuit> recordLawsuit(@Valid @RequestBody LawsuitDTO dto) {
        return ApiResponse.success(meMajorEventService.recordLawsuit(dto));
    }

    @PutMapping("/lawsuits/{id}")
    public ApiResponse<MeLawsuit> updateLawsuit(@PathVariable Long id, @Valid @RequestBody LawsuitDTO dto) {
        return ApiResponse.success(meMajorEventService.updateLawsuit(id, dto));
    }

    @PutMapping("/lawsuits/{id}/status")
    public ApiResponse<MeLawsuit> updateLawsuitStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String progress) {
        return ApiResponse.success(meMajorEventService.updateLawsuitStatus(id, status, progress));
    }

    @DeleteMapping("/lawsuits/{id}")
    public ApiResponse<Void> deleteLawsuit(@PathVariable Long id) {
        meMajorEventService.deleteLawsuit(id);
        return ApiResponse.success(null);
    }

    // ===== Guarantees =====

    @GetMapping("/guarantees")
    public ApiResponse<List<MeGuarantee>> listGuarantees(
            @RequestParam(required = false) Long tenantId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) String riskLevel) {
        return ApiResponse.success(meMajorEventService.listGuarantees(tenantId, eventId, riskLevel));
    }

    @GetMapping("/guarantees/{id}")
    public ApiResponse<MeGuarantee> getGuarantee(@PathVariable Long id) {
        return ApiResponse.success(meMajorEventService.getGuarantee(id));
    }

    @GetMapping("/guarantees/expiring")
    public ApiResponse<List<MeGuarantee>> findExpiringGuarantees(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "30") int days) {
        return ApiResponse.success(meMajorEventService.findExpiringGuarantees(tenantId, days));
    }

    @PostMapping("/guarantees")
    public ApiResponse<MeGuarantee> recordGuarantee(@Valid @RequestBody GuaranteeDTO dto) {
        return ApiResponse.success(meMajorEventService.recordGuarantee(dto));
    }

    @PutMapping("/guarantees/{id}")
    public ApiResponse<MeGuarantee> updateGuarantee(@PathVariable Long id, @Valid @RequestBody GuaranteeDTO dto) {
        return ApiResponse.success(meMajorEventService.updateGuarantee(id, dto));
    }

    @DeleteMapping("/guarantees/{id}")
    public ApiResponse<Void> deleteGuarantee(@PathVariable Long id) {
        meMajorEventService.deleteGuarantee(id);
        return ApiResponse.success(null);
    }
}
