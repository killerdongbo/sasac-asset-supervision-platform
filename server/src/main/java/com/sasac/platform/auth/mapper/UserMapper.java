package com.sasac.platform.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sasac.platform.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * MyBatis-Plus mapper for {@link User} entity.
 * <p>
 * Provides CRUD operations via BaseMapper and custom queries.
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * Finds an active (non-deleted) user by username.
     *
     * @param username the username to look up
     * @return the matching user, or {@code null} if not found
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
}
