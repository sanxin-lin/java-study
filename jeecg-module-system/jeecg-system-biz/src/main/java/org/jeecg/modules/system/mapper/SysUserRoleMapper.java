package org.jeecg.modules.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户角色表 Mapper 接口
 * </p>
 *
 * @Author Sunshine_Lin
 */
public interface SysUserRoleMapper {
    /**
     * 通过用户账号查询角色集合
     * @param userId 用户id
     * @return List<String>
     */
    @Select("select role_code from sys_role where id in (select role_id from sys_user_role where user_id = #{userId})")
    List<String> getRoleCodeByUserId(@Param("userId") String userId);
}
