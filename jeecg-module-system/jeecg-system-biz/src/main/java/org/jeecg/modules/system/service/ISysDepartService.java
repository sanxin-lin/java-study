/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-24 19:40
 **/

package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysDepart;

import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author: Sunshine_Lin
 */

public interface ISysDepartService extends IService<SysDepart> {
    /**
     * 查询SysDepart集合
     * @param userId
     * @return
     */
    public List<SysDepart> queryUserDeparts(String userId);
}
