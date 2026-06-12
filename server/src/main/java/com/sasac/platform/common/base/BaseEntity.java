package com.sasac.platform.common.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Abstract base entity for MyBatis-Plus mapped domain objects.
 * <p>
 * Every persistent entity in the application extends this class,
 * inheriting common fields such as id, audit timestamps, and
 * soft-delete support.
 */
@Getter
@Setter
public abstract class BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
