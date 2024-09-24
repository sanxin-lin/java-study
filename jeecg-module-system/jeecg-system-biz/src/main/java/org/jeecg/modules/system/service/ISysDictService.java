/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-24 19:51
 **/

package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.system.entity.SysDict;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @Author Sunshine_Lin
 */
public interface ISysDictService extends IService<SysDict> {
    /**
     * 登录加载系统字典
     * @return
     */
    public Map<String, List<DictModel>> queryAllDictItems();
}
