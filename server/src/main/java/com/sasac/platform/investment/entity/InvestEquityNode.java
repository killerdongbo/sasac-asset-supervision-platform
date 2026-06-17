package com.sasac.platform.investment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sasac.platform.common.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Equity penetration tree node.
 * <p>
 * Maps to the {@code invest_equity_node} table. Supports hierarchical
 * equity structure via parent_id self-reference.
 */
@Getter
@Setter
@TableName("invest_equity_node")
public class InvestEquityNode extends BaseEntity {

    private Long tenantId;

    private Long projectId;

    private String companyName;

    private Long parentId;

    private BigDecimal equityPct;

    private Integer level;

    private Boolean isActualController;

    /** Non-persisted; used for building tree responses. */
    private List<InvestEquityNode> children;
}
