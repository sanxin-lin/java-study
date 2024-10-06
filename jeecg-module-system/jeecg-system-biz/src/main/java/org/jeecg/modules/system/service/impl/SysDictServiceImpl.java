/**
 * @author: myqxin
 * @Desc:
 * @create: 2024-09-25 21:57
 **/

package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.ISysBaseAPI;
import org.jeecg.common.config.TenantContext;
import org.jeecg.common.constant.DataBaseConstant;
import org.jeecg.common.system.util.ResourceUtil;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.DictModelMany;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.system.entity.SysDict;
import org.jeecg.modules.system.mapper.SysDictMapper;
import org.jeecg.modules.system.security.DictQueryBlackListHandler;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.util.oConvertUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    @Resource
    private SysDictMapper sysDictMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private DictQueryBlackListHandler dictQueryBlackListHandler;

    @Override
    public Map<String, List<DictModel>> queryAllDictItems() {
        Map<String, List<DictModel>> sysAllDictItems = new HashMap(5);
        List<Integer> tenantIds = null;

        //是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】
        if (MybatisPlusSaasConfig.OPEN_SYSTEM_TENANT_CONTROL) {
            tenantIds = new ArrayList<>();
            tenantIds.add(0);
            if (TenantContext.getTenant() != null) {
                tenantIds.add(oConvertUtils.getInt(TenantContext.getTenant()));
            }
        }

        List<DictModelMany> sysDictItemList = sysDictMapper.queryAllDictItems(tenantIds);
        // 使用groupingBy根据dictCode分组
        sysAllDictItems = sysDictItemList.stream()
                .collect(Collectors.groupingBy(DictModelMany::getDictCode,
                        Collectors.mapping(d -> new DictModel(d.getValue(), d.getText(), d.getColor()), Collectors.toList())));

        Map<String, List<DictModel>> enumRes = ResourceUtil.getEnumDictData();
        sysAllDictItems.putAll(enumRes);
        return sysAllDictItems;
    }

    /**
     * 通过查询指定table的 text code 获取字典
     * dictTableCache采用redis缓存有效期10分钟
     * @param tableFilterSql
     * @param text
     * @param code
     * @return
     */
    @Override
    @Deprecated
    public List<DictModel> queryTableDictItemsByCode(String tableFilterSql, String text, String code) {
        log.debug("无缓存dictTableList的时候调用这里！");
        String str = tableFilterSql+","+text+","+code;
        // 【QQYUN-6533】表字典白名单check
        sysBaseAPI.dictTableWhiteListCheckByDict(tableFilterSql, text, code);
        // 1.表字典黑名单check
        if(!dictQueryBlackListHandler.isPass(str)){
            log.error(dictQueryBlackListHandler.getError());
            return null;
        }

        // 2.分割SQL获取表名和条件
        String table = null;
        String filterSql = null;
        if(tableFilterSql.toLowerCase().indexOf(DataBaseConstant.SQL_WHERE)>0){
            String[] arr = tableFilterSql.split(" (?i)where ");
            table = arr[0];
            filterSql = oConvertUtils.getString(arr[1], null);
        }else{
            table = tableFilterSql;
        }

        // 3.SQL注入check
        SqlInjectionUtil.filterContentMulti(table, text, code);
        SqlInjectionUtil.specialFilterContentForDictSql(filterSql);

        // 4.针对采用 ${}写法的表名和字段进行转义和check
        table = SqlInjectionUtil.getSqlInjectTableName(table);
        text = SqlInjectionUtil.getSqlInjectField(text);
        code = SqlInjectionUtil.getSqlInjectField(code);

        //return sysDictMapper.queryTableDictItemsByCode(tableFilterSql,text,code);
        return sysDictMapper.queryTableDictWithFilter(table,text,code,filterSql);
    }
}
