/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-29 12:19
 **/

package org.jeecg.modules.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.ISysBaseAPI;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.desensitization.util.SensitiveInfoUtil;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.config.firewall.SqlInjection.IDictTableWhiteListHandler;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.mapper.SysPermissionMapper;
import org.jeecg.modules.system.mapper.SysUserRoleMapper;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.jeecg.common.util.oConvertUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private SysPermissionMapper sysPermissionMapper;
    @Resource
    private ISysDictService sysDictService;
    @Resource
    private IDictTableWhiteListHandler dictTableWhiteListHandler;

    @Override
    public Set<String> queryUserRolesById(String userId) {
        return getUserRoleSetById(userId);
    }

    /**
     * 查询用户拥有的权限集合 common api 里面的接口实现
     * @param userId
     * @return
     */
    @Override
    public Set<String> queryUserAuths(String userId) {
        return getUserPermissionSet(userId);
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
    public List<DictModel> queryTableDictItemsByCode(String tableFilterSql, String text, String code) {
        if (tableFilterSql.indexOf(SymbolConstant.SYS_VAR_PREFIX) >= 0) {
            tableFilterSql = QueryGenerator.getSqlRuleValue(tableFilterSql);
        }
        return sysDictService.queryTableDictItemsByCode(tableFilterSql, text, code);
    }

    @Override
    public boolean dictTableWhiteListCheckByDict(String tableOrDictCode, String... fields) {
        if (fields == null || fields.length == 0) {
            return dictTableWhiteListHandler.isPassByDict(tableOrDictCode);
        } else {
            return dictTableWhiteListHandler.isPassByDict(tableOrDictCode, fields);
        }
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
        List<SysPermission> permissionList = sysPermissionMapper.queryByUserId(userId);
        //================= begin 开启租户的时候 如果没有test角色，默认加入test角色================
        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            if (permissionList == null) {
                permissionList = new ArrayList<>();
            }
            List<SysPermission> testRoleList = sysPermissionMapper.queryPermissionByTestRoleId();
            permissionList.addAll(testRoleList);
        }
        //================= end 开启租户的时候 如果没有test角色，默认加入test角色================
        for (SysPermission sysPermission : permissionList) {
            if (oConvertUtils.isNotEmpty(sysPermission.getPerms())) {
                permissionSet.add(sysPermission.getPerms());
            }
        }
        log.info("-------通过数据库读取用户拥有的权限Perms------userId： "+ userId+",Perms size: "+ (permissionSet==null?0:permissionSet.size()) );
        return permissionSet;
    }
}
