package com.sasac.platform.organization.service;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.organization.entity.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrganizationServiceTest {

    @Autowired
    private OrganizationService organizationService;

    @Test
    void shouldCreateAndRetrieveOrgTree() {
        // Create root SASAC
        Organization root = new Organization();
        root.setName("国资委");
        root.setOrgType("SASAC");
        root.setTenantId(0L);
        organizationService.create(root);

        // Create group under root
        Organization group = new Organization();
        group.setParentId(root.getId());
        group.setName("集团A");
        group.setOrgType("GROUP");
        group.setTenantId(0L);
        organizationService.create(group);

        // Create company under group
        Organization company = new Organization();
        company.setParentId(group.getId());
        company.setName("子公司A");
        company.setOrgType("ENTERPRISE");
        company.setTenantId(0L);
        organizationService.create(company);

        // Verify root has 1 direct child
        List<Organization> rootChildren = organizationService.getChildren(root.getId());
        assertThat(rootChildren).hasSize(1);
        assertThat(rootChildren.get(0).getName()).isEqualTo("集团A");

        // Verify root has 2 descendants (group + company)
        List<Organization> descendants = organizationService.getDescendants(root.getId());
        assertThat(descendants).hasSize(2);

        // Verify group has 1 child
        List<Organization> groupChildren = organizationService.getChildren(group.getId());
        assertThat(groupChildren).hasSize(1);
        assertThat(groupChildren.get(0).getName()).isEqualTo("子公司A");
    }

    @Test
    void shouldNotAllowCycleReference() {
        // Create A (no parent = root)
        Organization a = new Organization();
        a.setName("Org A");
        a.setOrgType("GROUP");
        a.setTenantId(0L);
        organizationService.create(a);

        // Create B with parent A
        Organization b = new Organization();
        b.setParentId(a.getId());
        b.setName("Org B");
        b.setOrgType("ENTERPRISE");
        b.setTenantId(0L);
        organizationService.create(b);

        // Try to set A's parent to B (would create cycle: A -> B -> A)
        a.setParentId(b.getId());
        BusinessException ex = assertThrows(BusinessException.class, () ->
                organizationService.update(a));
        assertThat(ex.getMessage()).contains("循环引用");
    }
}
