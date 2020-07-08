package com.j.openproject.injector;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;

/**
 * @author Joyuce
 * @Type InBaseMapper
 * @Desc 增强BaseMapper
 * @date 2020年02月19日
 * @Version V1.0
 */
public interface InBaseMapper<T> extends BaseMapper<T> {

    /**
     * 根据 entity 条件，查询一条记录，多条记录不报错
     *
     * @param queryWrapper 实体对象封装操作类（可以为 null）
     */
    T selectLimitOne(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);
}
