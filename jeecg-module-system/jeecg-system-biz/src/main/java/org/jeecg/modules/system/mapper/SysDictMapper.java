package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.DictModelMany;
import org.jeecg.modules.system.entity.SysDict;

import java.util.List;

public interface SysDictMapper extends BaseMapper<SysDict> {
    /**
     * 查询系统所有字典项
     * @return
     */
    public List<DictModelMany> queryAllDictItems(List<Integer> tenantIdList);

    /**
     * 查询 字典表数据 支持查询条件 查询所有
     * @param table
     * @param text
     * @param code
     * @param filterSql
     * @return
     */
    @Deprecated
    List<DictModel> queryTableDictWithFilter(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("filterSql") String filterSql);
}
