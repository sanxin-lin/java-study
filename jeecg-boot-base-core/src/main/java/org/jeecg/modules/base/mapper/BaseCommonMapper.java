/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-22 12:42
 **/

package org.jeecg.modules.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.dto.LogDTO;

/**
 * @Description: BaseCommonMapper
 * @author: Sunshine_Lin
 */
public interface BaseCommonMapper {

    /**
     * 保存日志
     * @param dto
     */
    @InterceptorIgnore(illegalSql = "true", tenantLine = "true")
    void saveLog(@Param("dto")LogDTO dto);

}
