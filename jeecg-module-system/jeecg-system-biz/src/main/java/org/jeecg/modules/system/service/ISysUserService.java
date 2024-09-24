/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-23 22:38
 **/

package org.jeecg.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 根据用户名设置部门ID
     * @param username
     * @param orgCode
     */
    void updateUserDepart(String username,String orgCode,Integer loginTenantId);

    /**
     * 校验用户是否有效
     * @param sysUser
     * @return
     */
    Result checkUserIsEffective(SysUser sysUser);

    /**
     * 设置登录租户
     * @param sysUser
     * @return
     */
    Result<JSONObject>  setLoginTenant(SysUser sysUser, JSONObject obj, String username, Result<JSONObject> result);
}
