package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysUserPosition;

import java.util.List;

/**
 * @Description: 用户职位关系表
 * @Author: Sunshine_Lin
 * @Version: V1.0
 */
public interface SysUserPositionMapper extends BaseMapper<SysUserPosition> {
    /**
     * 根据用户id查询职位id
     * @param userId
     * @return
     */
    List<String> getPositionIdByUserId(@Param("userId") String userId);
}
