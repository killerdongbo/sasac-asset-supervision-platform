package com.sasac.platform.hr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Employee entity representing personnel in the state-owned assets supervision platform.
 * <p>
 * Maps to the {@code hr_employee} table and tracks the full lifecycle of each employee,
 * including basic info, employment details, and contact information.
 */
@Getter
@Setter
@TableName("hr_employee")
public class HrEmployee extends BaseEntity {

    /**
     * Tenant ID for multi-tenancy.
     */
    private Long tenantId;

    /**
     * Organization ID this employee belongs to.
     */
    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    /**
     * Unique employee number (business code).
     */
    @NotBlank(message = "工号不能为空")
    private String employeeNo;

    /**
     * Employee name.
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * Gender (MALE / FEMALE).
     */
    private String gender;

    /**
     * National ID card number.
     */
    private String idCard;

    /**
     * Date of birth.
     */
    private LocalDate birthDate;

    /**
     * Phone number.
     */
    private String phone;

    /**
     * Email address.
     */
    private String email;

    /**
     * Highest education level.
     */
    private String education;

    /**
     * Major / field of study.
     */
    private String major;

    /**
     * Graduate school name.
     */
    private String graduateSchool;

    /**
     * Date of entry / hire.
     */
    private LocalDate entryDate;

    /**
     * Total years of work experience.
     */
    private Integer workYears;

    /**
     * Department ID.
     */
    private Long deptId;

    /**
     * Position / job title name.
     */
    private String position;

    /**
     * Professional title (e.g., engineer, senior engineer).
     */
    private String title;

    /**
     * Employment type (FULL_TIME, PART_TIME, CONTRACT, etc.).
     */
    private String employmentType;

    /**
     * Employment status (ACTIVE, RESIGNED, RETIRED, etc.).
     */
    private String status;

    /**
     * URL of the employee avatar.
     */
    private String avatarUrl;

    /**
     * Emergency contact person.
     */
    private String emergencyContact;

    /**
     * Emergency contact phone number.
     */
    private String emergencyPhone;

    /**
     * Home address.
     */
    private String homeAddress;

    /**
     * Additional remarks.
     */
    private String remark;
}
