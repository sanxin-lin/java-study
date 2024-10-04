/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-10-04 09:18
 **/

package org.jeecg.config.firewall.SqlInjection.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.util.sqlparse.JSqlParserUtils;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.config.firewall.SqlInjection.IDictTableWhiteListHandler;
import org.jeecg.modules.system.service.ISysTableWhiteListService;
import org.springframework.stereotype.Service;
import org.jeecg.common.util.oConvertUtils;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.*;

/**
 * 通用情况的白名单处理，若有无法处理的情况，可以单独写实现类
 */
@Slf4j
@Service
public class DictTableWhiteListHandlerImpl implements IDictTableWhiteListHandler {


    /**
     * key-表名
     * value-字段名，多个逗号隔开
     * 两种配置方式-- 全部配置成小写
     * whiteTablesRuleMap.put("sys_user", "*")  sys_user所有的字段都支持查询
     * whiteTablesRuleMap.put("sys_user", "username,password")  sys_user中的username和password支持查询
     */
    private static final Map<String, String> whiteTablesRuleMap = new HashMap<>();
    /**
     * LowCode 是否为 dev 模式
     */
    private static Boolean LOW_CODE_IS_DEV = null;/**
     * key-表名
     * value-字段名，多个逗号隔开
     * 两种配置方式-- 全部配置成小写
     * whiteTablesRuleMap.put("sys_user", "*")  sys_user所有的字段都支持查询
     * whiteTablesRuleMap.put("sys_user", "username,password")  sys_user中的username和password支持查询
     */
    private static final Map<String, String> whiteTablesRuleMap = new HashMap<>();
    /**
     * LowCode 是否为 dev 模式
     */
    private static Boolean LOW_CODE_IS_DEV = null;

    @Resource
    private ISysTableWhiteListService sysTableWhiteListService;
    @Resource
    private JeecgBaseConfig jeecgBaseConfig;

    /**
     * 初始化 whiteTablesRuleMap 方法
     */
    private void init() {
        // 如果当前为dev模式，则每次都查询数据库，防止缓存
        if (this.isDev()) {
            DictTableWhiteListHandlerImpl.whiteTablesRuleMap.clear();
        }
        // 如果map为空，则从数据库中查询
        if (DictTableWhiteListHandlerImpl.whiteTablesRuleMap.isEmpty()) {
            Map<String, String> ruleMap = sysTableWhiteListService.getAllConfigMap();
            log.info("表字典白名单初始化完成：{}", ruleMap);
            DictTableWhiteListHandlerImpl.whiteTablesRuleMap.putAll(ruleMap);
        }
    }

    /**
     * 判断当前 LowCode 是否为 dev 模式
     */
    private boolean isDev() {
        if (DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV == null) {
            if (this.jeecgBaseConfig.getFirewall() != null) {
                String lowCodeMode = this.jeecgBaseConfig.getFirewall().getLowCodeMode();
                DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV = LowCodeModeInterceptor.LOW_CODE_MODE_DEV.equals(lowCodeMode);
            } else {
                // 如果没有 firewall 配置，则默认为 false
                DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV = false;
            }
        }
        return DictTableWhiteListHandlerImpl.LOW_CODE_IS_DEV;
    }

    @Override
    public boolean isPassByDict(String dictCodeString) {
        if (oConvertUtils.isEmpty(dictCodeString)) {
            return true;
        }
        try {
            // 针对转义字符进行解码
            dictCodeString = URLDecoder.decode(dictCodeString, "UTF-8");
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        dictCodeString = dictCodeString.trim();
        String[] arr = dictCodeString.split(SymbolConstant.COMMA);
        // 获取表名
        String tableName = getTableName(arr[0]);
        // 获取查询字段
        arr = Arrays.copyOfRange(arr, 1, arr.length);
        // distinct的作用是去重，相当于 Set<String>
        String[] fields = Arrays.stream(arr).map(String::trim).distinct().toArray(String[]::new);
        // 校验表名和字段是否允许查询
        return this.isPassByDict(tableName, fields);
    }

    @Override
    public boolean isPassByDict(String tableName, String... fields) {
        if (oConvertUtils.isEmpty(tableName)) {
            return true;
        }
        if (fields == null || fields.length == 0) {
            fields = new String[]{"*"};
        }
        String sql = "select " + String.join(",", fields) + " from " + tableName;
        log.info("字典拼接的查询SQL：{}", sql);
        try {
            // 进行SQL解析
            JSqlParserUtils.parseSelectSqlInfo(sql);
        } catch (Exception e) {
            // 如果SQL解析失败，则通过字段名和表名进行校验
            return checkWhiteList(tableName, new HashSet<>(Arrays.asList(fields)));
        }
        // 通过SQL解析进行校验，可防止SQL注入
        return this.isPassBySql(sql);
    }

    /**
     * 校验表名和字段是否在白名单内
     *
     * @param tableName
     * @param queryFields
     * @return
     */



    /**
     * 取where前面的为：table name
     *
     * @param str
     */
    private String getTableName(String str) {
        String[] arr = str.split("\\s+(?i)where\\s+");
        String tableName = arr[0].trim();
        //【20230814】解决使用参数tableName=sys_user t&复测，漏洞仍然存在
        if (tableName.contains(".")) {
            tableName = tableName.substring(tableName.indexOf(".") + 1, tableName.length()).trim();
        }
        if (tableName.contains(" ")) {
            tableName = tableName.substring(0, tableName.indexOf(" ")).trim();
        }

        //【issues/4393】 sys_user , (sys_user), sys_user%20, %60sys_user%60
        String reg = "\\s+|\\(|\\)|`";
        return tableName.replaceAll(reg, "");
    }

    /**
     * 校验表名和字段是否在白名单内
     *
     * @param tableName
     * @param queryFields
     * @return
     */
    public boolean checkWhiteList(String tableName, Set<String> queryFields) {}
}
