package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUserTenant;

import java.util.List;

public interface SysUserTenantMapper extends BaseMapper<SysUserTenant> {
    /**
     * 查询未被注销的租户
     * @param userId
     * @return
     */
    List<SysTenant> getTenantNoCancel(@Param("userId") String userId);

    /**
     * 根据用户id获取租户id，没有状态值(如获取租户已经存在，只不过是被拒绝或者审批中)
     * @param userId
     * @return
     */
    List<Integer> getTenantIdsNoStatus(@Param("userId") String userId);
}
