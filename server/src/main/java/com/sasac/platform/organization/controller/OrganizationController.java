package com.sasac.platform.organization.controller;

import com.sasac.platform.common.response.ApiResponse;
import com.sasac.platform.organization.entity.Organization;
import com.sasac.platform.organization.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for organization tree management.
 * <p>
 * Provides endpoints for CRUD operations on the organization hierarchy,
 * including direct children listing and full subtree retrieval.
 */
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * Lists all organizations.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<Organization>>> list() {
        return ResponseEntity.ok(ApiResponse.success(organizationService.listAll()));
    }

    /**
     * Creates a new organization node.
     *
     * @param org the organization to create
     * @return API response with the created organization
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Organization>> create(@RequestBody Organization org) {
        Organization created = organizationService.create(org);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    /**
     * Updates an existing organization.
     * <p>
     * Performs cycle detection if parentId is changed.
     *
     * @param id  the organization ID
     * @param org the updated organization fields
     * @return API response with the updated organization
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Organization>> update(@PathVariable Long id,
                                                            @RequestBody Organization org) {
        org.setId(id);
        Organization updated = organizationService.update(org);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Retrieves a single organization by ID.
     *
     * @param id the organization ID
     * @return API response with the organization
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Organization>> getById(@PathVariable Long id) {
        Organization org = organizationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(org));
    }

    /**
     * Lists direct children of an organization.
     *
     * @param id the parent organization ID
     * @return API response with the list of direct children
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<Organization>>> getChildren(@PathVariable Long id) {
        List<Organization> children = organizationService.getChildren(id);
        return ResponseEntity.ok(ApiResponse.success(children));
    }

    /**
     * Returns the complete organization tree as nested structure (with children).
     *
     * @return root organizations with nested children for tree display
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFullTree() {
        List<Organization> all = organizationService.listAll();

        // Build parentId -> children map
        Map<Long, List<Map<String, Object>>> childrenMap = new LinkedHashMap<>();
        List<Map<String, Object>> allNodes = new ArrayList<>();

        for (Organization org : all) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", org.getId().toString());
            node.put("name", org.getName());
            node.put("orgType", org.getOrgType());
            node.put("orgCode", org.getOrgCode());
            node.put("parentId", org.getParentId() != null ? org.getParentId().toString() : null);
            node.put("children", new ArrayList<>());
            allNodes.add(node);

            Long parentId = org.getParentId() != null ? org.getParentId() : 0L;
            childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(node);
        }

        // Attach children to parents
        for (Map<String, Object> node : allNodes) {
            Long nodeId = Long.valueOf((String) node.get("id"));
            List<Map<String, Object>> children = childrenMap.getOrDefault(nodeId, List.of());
            node.put("children", children);
        }

        // Return root nodes (parentId is null)
        List<Map<String, Object>> roots = childrenMap.getOrDefault(0L, List.of());
        return ResponseEntity.ok(ApiResponse.success(roots));
    }

    /**
     * Retrieves the full subtree (all descendants) of an organization.
     *
     * @param id the root organization ID of the subtree
     * @return API response with the list of all descendants
     */
    @GetMapping("/{id}/tree")
    public ResponseEntity<ApiResponse<List<Organization>>> getTree(@PathVariable Long id) {
        List<Organization> descendants = organizationService.getDescendants(id);
        return ResponseEntity.ok(ApiResponse.success(descendants));
    }

    /**
     * Deletes an organization if it has no children.
     *
     * @param id the organization ID to delete
     * @return API response indicating success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        organizationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
