package com.sasac.platform.quotation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sasac.platform.common.exception.BusinessException;
import com.sasac.platform.quotation.entity.Inquiry;
import com.sasac.platform.quotation.entity.PriceHistory;
import com.sasac.platform.quotation.entity.Quotation;
import com.sasac.platform.quotation.mapper.InquiryMapper;
import com.sasac.platform.quotation.mapper.PriceHistoryMapper;
import com.sasac.platform.quotation.mapper.QuotationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final InquiryMapper inquiryMapper;
    private final QuotationMapper quotationMapper;
    private final PriceHistoryMapper priceHistoryMapper;

    public Page<Inquiry> listInquiries(Long tenantId, String status, int page, int size) {
        LambdaQueryWrapper<Inquiry> wrapper = new LambdaQueryWrapper<Inquiry>()
                .eq(Inquiry::getTenantId, tenantId)
                .eq(status != null && !status.isBlank(), Inquiry::getStatus, status)
                .orderByDesc(Inquiry::getCreatedAt);
        return inquiryMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Inquiry getInquiry(Long id) {
        return inquiryMapper.selectById(id);
    }

    @Transactional
    public Inquiry createInquiry(Inquiry inquiry) {
        inquiry.setStatus("DRAFT");
        inquiryMapper.insert(inquiry);
        return inquiry;
    }

    @Transactional
    public Inquiry publishInquiry(Long id) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null) throw new BusinessException("询价单不存在");
        if (!"DRAFT".equals(inquiry.getStatus())) throw new BusinessException("只有草稿状态可以发布");
        inquiry.setStatus("OPEN");
        inquiryMapper.updateById(inquiry);
        return inquiry;
    }

    @Transactional
    public Inquiry closeInquiry(Long id) {
        Inquiry inquiry = inquiryMapper.selectById(id);
        if (inquiry == null) throw new BusinessException("询价单不存在");
        inquiry.setStatus("CLOSED");
        inquiryMapper.updateById(inquiry);
        return inquiry;
    }

    public List<Quotation> listQuotations(Long inquiryId) {
        return quotationMapper.selectList(
                new LambdaQueryWrapper<Quotation>()
                        .eq(Quotation::getInquiryId, inquiryId)
                        .orderByAsc(Quotation::getUnitPrice)
        );
    }

    @Transactional
    public Quotation submitQuotation(Quotation quotation) {
        Inquiry inquiry = inquiryMapper.selectById(quotation.getInquiryId());
        if (inquiry == null) throw new BusinessException("询价单不存在");
        if (!"OPEN".equals(inquiry.getStatus())) throw new BusinessException("询价单未开放");

        quotation.setQuotedAt(LocalDateTime.now());
        if (quotation.getTotalPrice() == null && inquiry.getQuantity() != null) {
            quotation.setTotalPrice(quotation.getUnitPrice().multiply(new java.math.BigDecimal(inquiry.getQuantity())));
        }
        quotationMapper.insert(quotation);

        PriceHistory history = new PriceHistory();
        history.setTenantId(quotation.getTenantId());
        history.setCategory(inquiry.getCategory());
        history.setSpecification(inquiry.getSpecification());
        history.setSupplierId(quotation.getSupplierId());
        history.setSupplierName(quotation.getSupplierName());
        history.setUnitPrice(quotation.getUnitPrice());
        history.setSourceType("QUOTATION");
        history.setSourceId(quotation.getId());
        history.setRecordDate(LocalDate.now());
        priceHistoryMapper.insert(history);

        return quotation;
    }

    @Transactional
    public void selectQuotation(Long quotationId) {
        Quotation q = quotationMapper.selectById(quotationId);
        if (q == null) throw new BusinessException("报价不存在");

        quotationMapper.selectList(
                new LambdaQueryWrapper<Quotation>().eq(Quotation::getInquiryId, q.getInquiryId())
        ).forEach(item -> {
            item.setIsSelected(item.getId().equals(quotationId) ? 1 : 0);
            quotationMapper.updateById(item);
        });
    }

    public List<PriceHistory> getPriceTrend(Long tenantId, String category, String startDate, String endDate) {
        LambdaQueryWrapper<PriceHistory> wrapper = new LambdaQueryWrapper<PriceHistory>()
                .eq(PriceHistory::getTenantId, tenantId)
                .eq(PriceHistory::getCategory, category)
                .orderByAsc(PriceHistory::getRecordDate);

        if (startDate != null && !startDate.isBlank()) {
            wrapper.ge(PriceHistory::getRecordDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            wrapper.le(PriceHistory::getRecordDate, LocalDate.parse(endDate));
        }
        return priceHistoryMapper.selectList(wrapper);
    }

    public Map<String, Object> getPriceAnalysis(Long tenantId, String category) {
        List<PriceHistory> records = priceHistoryMapper.selectList(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getTenantId, tenantId)
                        .eq(PriceHistory::getCategory, category)
                        .orderByDesc(PriceHistory::getRecordDate)
        );

        if (records.isEmpty()) {
            return Map.of("avgPrice", 0, "minPrice", 0, "maxPrice", 0, "recordCount", 0);
        }

        var prices = records.stream().map(PriceHistory::getUnitPrice).toList();
        var avg = prices.stream().reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
                .divide(java.math.BigDecimal.valueOf(prices.size()), 4, java.math.RoundingMode.HALF_UP);
        var min = prices.stream().min(java.math.BigDecimal::compareTo).orElse(java.math.BigDecimal.ZERO);
        var max = prices.stream().max(java.math.BigDecimal::compareTo).orElse(java.math.BigDecimal.ZERO);

        return Map.of("avgPrice", avg, "minPrice", min, "maxPrice", max, "recordCount", records.size());
    }
}
