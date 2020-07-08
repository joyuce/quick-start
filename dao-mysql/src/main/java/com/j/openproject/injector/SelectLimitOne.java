package com.j.openproject.injector;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

/**
 * @author Joyuce
 * @Type SelectLimitOne
 * @Desc 只查一条记录，如果有多条只返回一条
 * @date 2020年02月19日
 * @Version V1.0
 */
public class SelectLimitOne extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String sql =
                "<script>\nSELECT " + this.sqlSelectColumns(tableInfo, true) + " FROM " + tableInfo.getTableName() + " "
                        + this.sqlWhereEntityWrapper(true, tableInfo) + " " + sqlComment() + " limit 1\n</script>";
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForTable(mapperClass, "selectLimitOne", sqlSource, tableInfo);
    }
}
