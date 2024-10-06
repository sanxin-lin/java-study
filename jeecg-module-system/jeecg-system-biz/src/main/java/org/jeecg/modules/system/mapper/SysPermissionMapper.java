package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysPermission;

import java.util.List;

public interface SysPermissionMapper {
    /**
     * 根据用户查询用户权限
     * @param userId 用户ID
     * @return List<SysPermission>
     */
    public List<SysPermission> queryByUserId(@Param("userId") String userId);

    /**
     * 根据用户名称和test角色id查询权限
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysPermission> queryPermissionByTestRoleId();
}
