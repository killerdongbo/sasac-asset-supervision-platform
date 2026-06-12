package com.sasac.platform.asset.inventory.controller;

import com.sasac.platform.asset.inventory.entity.InventoryDiff;
import com.sasac.platform.asset.inventory.entity.InventoryRecord;
import com.sasac.platform.asset.inventory.entity.InventoryTask;
import com.sasac.platform.asset.inventory.service.InventoryService;
import com.sasac.platform.common.response.ApiResponse;
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
 * REST controller for inventory management.
 * <p>
 * Endpoints for inventory task CRUD, physical inventory recording,
 * and discrepancy querying.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // ---- Inventory Task ----

    /**
     * Creates a new inventory task.
     */
    @PostMapping("/inventory-tasks")
    public ResponseEntity<ApiResponse<InventoryTask>> createTask(
            @Valid @RequestBody InventoryTask task) {
        InventoryTask created = inventoryService.createTask(task);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Retrieves tasks assigned to the current user.
     *
     * @param assigneeId the user ID of the assignee
     */
    @GetMapping("/inventory-tasks/my")
    public ResponseEntity<ApiResponse<List<InventoryTask>>> getMyTasks(
            @RequestParam Long assigneeId) {
        List<InventoryTask> tasks = inventoryService.getMyTasks(assigneeId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    // ---- Inventory Record ----

    /**
     * Records the physical inventory result for a single asset.
     */
    @PostMapping("/inventory-records")
    public ResponseEntity<ApiResponse<Void>> recordInventory(
            @Valid @RequestBody InventoryRecord record) {
        inventoryService.recordInventory(record);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * Retrieves all inventory records for a task.
     */
    @GetMapping("/inventory-tasks/{taskId}/records")
    public ResponseEntity<ApiResponse<List<InventoryRecord>>> getRecords(
            @PathVariable Long taskId) {
        List<InventoryRecord> records = inventoryService.getRecords(taskId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    // ---- Inventory Diff ----

    /**
     * Retrieves all inventory diffs (discrepancies) for a task.
     */
    @GetMapping("/inventory-tasks/{taskId}/diffs")
    public ResponseEntity<ApiResponse<List<InventoryDiff>>> getDiffs(
            @PathVariable Long taskId) {
        List<InventoryDiff> diffs = inventoryService.getDiffs(taskId);
        return ResponseEntity.ok(ApiResponse.success(diffs));
    }
}
