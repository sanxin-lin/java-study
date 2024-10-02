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
}
