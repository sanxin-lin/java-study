/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-29 12:19
 **/

package org.jeecg.modules.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.ISysBaseAPI;
import org.jeecg.common.desensitization.util.SensitiveInfoUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.jeecg.common.util.oConvertUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
    @Resource
    private ISysUserService sysUserService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Set<String> queryUserRolesById(String userId) {
        return getUserRoleSetById(userId);
    }

    /**
     * 查询用户拥有的角色集合
     * @param userId
     * @return
     */
    @Override
    public Set<String> getUserRoleSetById(String userId) {
        // 查询用户拥有的角色集合
        List<String> roles = sysUserRoleMapper.getRoleCodeByUserId(userId);
        log.info("-------通过数据库读取用户拥有的角色Rules------useId： " + userId + ",Roles size: " + (roles == null ? 0 : roles.size()));
        return new HashSet<>(roles);
    }

    @Override
    public LoginUser getUserByName(String username) {
        if (oConvertUtils.isEmpty(username)) {
            return null;
        }
        LoginUser user = sysUserService.getEncodeUserInfo(username);

        //相同类中方法间调用时脱敏解密 Aop会失效，获取用户信息太重要，此处采用原生解密方法，不采用@SensitiveDecodeAble注解方式
        try {
            SensitiveInfoUtil.handlerObject(user, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * 查询用户拥有的权限集合
     * @param userId
     * @return
     */
    @Override
    public Set<String> getUserPermissionSet(String userId) {
        Set<String> permissionSet = new HashSet<>();
        List<SysPermission> permissionList =
    }
}
