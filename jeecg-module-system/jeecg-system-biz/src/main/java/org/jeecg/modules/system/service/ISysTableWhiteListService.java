package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysTableWhiteList;

import java.util.Map;

public interface ISysTableWhiteListService extends IService<SysTableWhiteList> {

    /**
     * 以map的方式获取所有数据
     * key=tableName，value=fieldName
     *
     * @return
     */
    Map<String, String> getAllConfigMap();
}
