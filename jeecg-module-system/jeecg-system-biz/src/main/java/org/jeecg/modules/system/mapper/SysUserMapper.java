package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysUser;

public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 根据用户名设置部门ID
     * @param username
     * @param orgCode
     */
    void updateUserDepart(@Param("username") String username, @Param("orgCode") String orgCode, @Param("loginTenantId") Integer loginTenantId);

    /**
     * 通过用户账号查询用户信息
     * @param username
     * @return
     */
    public SysUser getUserByName(@Param("username") String username);
}
