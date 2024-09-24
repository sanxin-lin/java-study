/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-24 22:18
 **/

package org.jeecg.modules.base;

import org.jeecg.common.api.dto.LogDTO;
import org.jeecg.common.system.vo.LoginUser;

public interface BaseCommonService {
    /**
     * 保存日志
     * @param logDTO
     */
    void addLog(LogDTO logDTO);

    /**
     * 保存日志
     * @param logContent
     * @param logType
     * @param operateType
     * @param user
     */
    void addLog(String logContent, Integer logType, Integer operateType, LoginUser user);

    /**
     * 保存日志
     * @param logContent
     * @param logType
     * @param operateType
     */
    void addLog(String logContent, Integer logType, Integer operateType);
}
