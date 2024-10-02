package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysRole;

import java.util.List;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 根据用户id查询用户拥有的角色Code
     *
     * @param userId
     * @param tenantId
     * @return
     */
    List<SysRole> getRoleCodeListByUserId(@Param("userId") String userId, @Param("tenantId") Integer tenantId);
}
