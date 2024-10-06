package org.jeecg.common.api;

import java.util.Set;

public interface ISysBaseAPI extends CommonAPI {
    /**
     * 获取用户的角色集合
     * @param useId
     * @return
     */
    Set<String> getUserRoleSetById(String useId);

    /**
     * 获取用户的权限集合
     * @param userId
     * @return
     */
    Set<String> getUserPermissionSet(String userId);

    /**
     * 根据字典表或者字典编码，校验是否在白名单中
     *
     * @param tableOrDictCode 表名或dictCode
     * @param fields          如果传的是dictCode，则该参数必须传null
     * @return
     */
    boolean dictTableWhiteListCheckByDict(String tableOrDictCode, String... fields);
}
