package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysDepart;

import java.util.List;

public interface SysDepartMapper extends BaseMapper<SysDepart> {
    /**
     * 根据用户ID查询部门集合
     * @param userId 用户id
     * @return List<SysDepart>
     */
    public List<SysDepart> queryUserDeparts(@Param("userId") String userId);
}
