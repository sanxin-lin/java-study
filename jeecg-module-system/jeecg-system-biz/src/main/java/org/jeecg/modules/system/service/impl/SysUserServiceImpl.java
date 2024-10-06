/**
 * @author: myqxin
 * @Desc:
 * @create: 2024-09-24 22:32
 **/

package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.desensitization.annotation.SensitiveEncode;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.service.ISysUserService;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.jeecg.common.util.oConvertUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Resource
    private BaseCommonService baseCommonService;
    @Resource
    private SysUserTenantMapper sysUserTenantMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserPositionMapper sysUserPositionMapper;
    @Resource
    private SysDepartMapper sysDepartMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;

    /**
     * 校验用户是否有效
     * @param sysUser
     * @return
     */
    @Override
    public Result<?> checkUserIsEffective(SysUser sysUser) {
        Result<?> result = new Result<Object>();
        //情况1：根据用户信息查询，该用户不存在
        if (sysUser == null) {
            result.error500("该用户不存在，请注册");
            baseCommonService.addLog("用户登录失败，用户不存在！", CommonConstant.LOG_TYPE_1, null);
            return result;
        }
        //情况2：根据用户信息查询，该用户已注销
        if (CommonConstant.DEL_FLAG_1.equals(sysUser.getDelFlag())) {
            baseCommonService.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已注销！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已注销");
            return result;
        }
        //情况3：根据用户信息查询，该用户已冻结
        if (CommonConstant.USER_FREEZE.equals(sysUser.getStatus())) {
            baseCommonService.addLog("用户登录失败，用户名:" + sysUser.getUsername() + "已冻结！", CommonConstant.LOG_TYPE_1, null);
            result.error500("该用户已冻结");
            return result;
        }
        return result;
    }

    @Override
    @CacheEvict(value = {CacheConstant.SYS_USERS_CACHE}, key="#username")
    public void updateUserDepart(String username, String orgCode, Integer loginTenantId) {
        baseMapper.updateUserDepart(username, orgCode, loginTenantId);
    }

    /**
     * 设置登录租户
     * @param sysUser
     * @return
     */
    @Override
    public <T> T  setLoginTenant(SysUser sysUser, String username, JSONObject obj){
        List<SysTenant> tenantList = sysUserTenantMapper.getTenantNoCancel(sysUser.getId());
        obj.put("tenantList", tenantList);

        //登录会话租户ID，有效性重置
        if (tenantList != null && tenantList.size() > 0) {
            if (tenantList.size() == 1) {
                sysUser.setLoginTenantId(tenantList.get(0).getId());
            } else {
                List<SysTenant> listAfterFilter = tenantList.stream().filter(s -> s.getId().equals(sysUser.getLoginTenantId())).collect(Collectors.toList());
                //如果上次登录租户ID，在用户拥有的租户集合里面没有了，则随机取用户拥有的第一个租户ID
                sysUser.setLoginTenantId(listAfterFilter.get(0).getId());
            }
        } else {
            //无租户的时候，设置为 0
            sysUser.setLoginTenantId(0);
        }

        //设置用户登录缓存租户
        updateUserDepart(username, null, sysUser.getLoginTenantId());
        log.info(" 登录接口用户的租户ID = {}", sysUser.getLoginTenantId());
        if (sysUser.getLoginTenantId() != null) {
            //登录的时候需要手工设置下会话中的租户ID,不然登录接口无法通过租户隔离查询到数据
            TenantContext.setTenant(sysUser.getLoginTenantId() + "");
        }
        return null;
    }

    @Override
    @Cacheable(cacheNames = CacheConstant.SYS_USERS_CACHE, key="#username")
    @SensitiveEncode
    public LoginUser getEncodeUserInfo(String username) {
        if (oConvertUtils.isEmpty(username)) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        SysUser sysUser = sysUserMapper.getUserByName(username);
        if (sysUser == null) {
            return null;
        }
        //查询用户的租户ids
        setUserTenantIds(sysUser);
        //设置职位id
        userPositionId(sysUser);
        BeanUtils.copyProperties(sysUser, loginUser);
        // 查询当前登录用户的部门id
        loginUser.setOrgId(getDepartIdByOrgCode(sysUser.getOrgCode()));
        // 查询当前登录用户的角色code（多个逗号分割）
        loginUser.setRoleCode(getJoinRoleCodeByUserId(sysUser.getId()));
        return loginUser;
    }

    /**
     * 设置用户职位id(已逗号拼接起来)
     * @param sysUser
     */
    private void userPositionId(SysUser sysUser) {
        if (sysUser != null) {
            List<String> positionList = sysUserPositionMapper.getPositionIdByUserId(sysUser.getId());
            sysUser.setPost(CommonUtils.getSplitText(positionList, SymbolConstant.COMMA));
        }
    }

    /**
     * 获取租户id
     * @param sysUser
     */
    private void setUserTenantIds(SysUser sysUser) {
        if (ObjectUtils.isNotEmpty((sysUser))) {
            List<Integer> list = sysUserTenantMapper.getTenantIdsNoStatus(sysUser.getId());
            if (list != null && list.size() > 0) {
                sysUser.setRelTenantIds((StringUtils.join(list.toArray(), ",")));
            } else {
                sysUser.setRelTenantIds("");
            }
        }
    }

    /**
     * 查询用户当前登录部门的id
     *
     * @param orgCode
     */
    private @Nullable String getDepartIdByOrgCode (String orgCode) {
        if (oConvertUtils.isEmpty(orgCode)) {
            return null;
        }
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepart::getOrgCode, orgCode);
        queryWrapper.select(SysDepart::getId);
        SysDepart depart = sysDepartMapper.selectOne(queryWrapper);
        if (depart == null || oConvertUtils.isEmpty(depart.getId())) {
            return null;
        }
        return depart.getId();
    }

    /**
     * 查询用户的角色code（多个逗号分割）
     *
     * @param userId
     */
    private @Nullable String getJoinRoleCodeByUserId(String userId) {
        if (oConvertUtils.isEmpty(userId)) {
            return null;
        }
        // 判断是否开启saas模式，根据租户id过滤
        Integer tenantId = null;
        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            // 开启了但是没有租户ID，默认-1，使其查询不到任何数据
            tenantId = oConvertUtils.getInt(TenantContext.getTenant(), -1);
        }
        List<SysRole> roleList = sysRoleMapper.getRoleCodeListByUserId(userId, tenantId);
        if (CollectionUtils.isEmpty(roleList)) {
            return null;
        }
        return roleList.stream().map(SysRole::getRoleCode).collect(Collectors.joining(SymbolConstant.COMMA));
    }
}
