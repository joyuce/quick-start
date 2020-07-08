package com.j.openproject.injector;

import java.util.List;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;

/**
 * @author Joyuce
 * @Type IncSqlInjector
 * @Desc sql注入增强
 * @date 2020年02月19日
 * @Version V1.0
 */
public class IncSqlInjector extends DefaultSqlInjector  {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new SelectLimitOne());
        return methodList;
    }
}
