/**
 * @author: myqxin
 * @Desc:
 * @create: 2024-10-04 09:47
 **/

package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.system.entity.SysTableWhiteList;
import org.jeecg.modules.system.mapper.SysTableWhiteListMapper;
import org.jeecg.modules.system.service.ISysTableWhiteListService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysTableWhiteListServiceImpl extends ServiceImpl<SysTableWhiteListMapper, SysTableWhiteList> implements ISysTableWhiteListService {

    @Override
    public Map<String, String> getAllConfigMap() {
        Map<String, String> map = new HashMap<>();
        List<SysTableWhiteList> allData = super.list();
        for (SysTableWhiteList item : allData) {
            // 只有启用的才放入map
            if (CommonConstant.STATUS_1.equals(item.getStatus())) {
                // 表名和字段名都转成小写，防止大小写不一致
                map.put(item.getTableName().toLowerCase(), item.getFieldName().toLowerCase());
            }
        }
        return map;
    }
}
