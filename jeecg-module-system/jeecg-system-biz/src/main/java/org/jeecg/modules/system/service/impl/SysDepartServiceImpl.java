/**
 * @author: Sunshine_Lin
 * @Desc:
 * @create: 2024-09-26 21:35
 **/

package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.mapper.SysDepartMapper;
import org.jeecg.modules.system.service.ISysDepartService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 部门表 服务实现类
 * <p>
 *
 * @Author Sunshine_Lin
 */
@Service
public class SysDepartServiceImpl extends ServiceImpl<SysDepartMapper, SysDepart> implements ISysDepartService {
    @Override
    public List<SysDepart> queryUserDeparts(String userId) {
        return baseMapper.queryUserDeparts(userId);
    }
}
