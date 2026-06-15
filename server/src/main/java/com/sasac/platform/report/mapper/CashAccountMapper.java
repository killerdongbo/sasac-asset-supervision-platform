package com.sasac.platform.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.report.entity.CashAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * MyBatis-Plus mapper for CashAccount entity.
 * <p>
 * Inherits built-in CRUD methods from {@link BaseMapper}.
 */
@Mapper
public interface CashAccountMapper extends BaseMapper<CashAccount> {
}
