package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.common.system.vo.DictModelMany;
import org.jeecg.modules.system.entity.SysDict;

import java.util.List;

public interface SysDictMapper extends BaseMapper<SysDict> {
    /**
     * 查询系统所有字典项
     * @return
     */
    public List<DictModelMany> queryAllDictItems(List<Integer> tenantIdList);
}
