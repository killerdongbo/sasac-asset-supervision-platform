package com.sasac.platform.report.service;

import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.report.entity.Report;
import com.sasac.platform.report.mapper.ReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for the report submission-review-accept workflow.
 * <p>
 * Implements the state machine: DRAFT -> SUBMITTED -> REVIEWED -> ACCEPTED.
 * REJECTED reports can be re-submitted back to SUBMITTED.
 */
@Service
@RequiredArgsConstructor
public class ReportSubmissionService {

    private final ReportMapper reportMapper;

    /**
     * Submits a report for review.
     * <p>
     * Only reports in DRAFT or REJECTED status can be submitted.
     *
     * @param reportId   the report ID
     * @param operatorId the ID of the submitting operator
     * @return the updated Report with SUBMITTED status
     * @throws BusinessException if the report is not found or not in a submittable state
     */
    @Transactional
    public Report submit(Long reportId, Long operatorId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"DRAFT".equals(report.getSubmitStatus()) && !"REJECTED".equals(report.getSubmitStatus())) {
            throw new BusinessException("当前状态不允许提交");
        }
        report.setSubmitStatus("SUBMITTED");
        reportMapper.updateById(report);
        return report;
    }

    /**
     * Reviews a submitted report.
     * <p>
     * Only reports in SUBMITTED status can be reviewed.
     *
     * @param reportId   the report ID
     * @param approved   true to approve (-> REVIEWED), false to reject (-> REJECTED)
     * @param remark     review remarks
     * @param reviewerId the ID of the reviewer
     * @return the updated Report
     * @throws BusinessException if the report is not found or not in SUBMITTED state
     */
    @Transactional
    public Report review(Long reportId, boolean approved, String remark, Long reviewerId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"SUBMITTED".equals(report.getSubmitStatus())) {
            throw new BusinessException("当前状态不允许审核");
        }
        report.setSubmitStatus(approved ? "REVIEWED" : "REJECTED");
        report.setReviewerId(String.valueOf(reviewerId));
        report.setReviewRemark(remark);
        reportMapper.updateById(report);
        return report;
    }

    /**
     * Accepts a reviewed report.
     * <p>
     * Only reports in REVIEWED status can be accepted.
     *
     * @param reportId the report ID
     * @return the updated Report with ACCEPTED status
     * @throws BusinessException if the report is not found or not in REVIEWED state
     */
    @Transactional
    public Report accept(Long reportId) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("报告不存在");
        }
        if (!"REVIEWED".equals(report.getSubmitStatus())) {
            throw new BusinessException("当前状态不允许接收");
        }
        report.setSubmitStatus("ACCEPTED");
        reportMapper.updateById(report);
        return report;
    }
}
