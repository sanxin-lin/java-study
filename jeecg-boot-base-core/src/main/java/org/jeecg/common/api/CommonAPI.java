package org.jeecg.common.api;

import org.jeecg.common.system.vo.*;

import java.util.List;
import java.util.Set;

public interface CommonAPI {

    /**
     * 查询用户角色信息
     * @param userId
     * @return
     */
    Set<String> queryUserRolesById(String userId);

    /**
     * 查询用户权限信息
     * @param userId
     * @return
     */
    Set<String> queryUserAuths(String userId);

    /**
     * 5根据用户账号查询用户信息
     * @param username
     * @return
     */
    public LoginUser getUserByName(String username);

    /**
     * 获取表数据字典
     * @param tableFilterSql
     * @param text
     * @param code
     * @return
     */
    List<DictModel> queryTableDictItemsByCode(String tableFilterSql, String text, String code);
}
