package com.j.openproject.excel;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Joyuce
 * @Type ExcelUtil
 * @Desc excel工具类
 * @date 2020年03月12日
 * @Version V1.0
 */
@Slf4j
public class ExcelUtil {

    private ExcelUtil() {
    }

    /**
     * 同步获取数据 ，数据量大时 不建议使用
     *
     * @param file
     * @param clazz
     * @param sheetNo       分片编号 从 0开始
     * @param headRowNumber 从第几行开始 从1开始
     * @param <T>
     * @return
     */
    public static <T> List<T> getListByExcelSync(File file, Class clazz, Integer sheetNo, Integer headRowNumber) {
        List<T> list = EasyExcel.read(file).head(clazz).sheet(sheetNo).headRowNumber(headRowNumber).doReadSync();
        return list;
    }

    public static <T> List<T> getListByExcelSync(File file, Class clazz) {
        return getListByExcelSync(file, clazz, null, null);
    }

    public static void writeToFile(File file, Class clazz, String sheetName, List data) {
        EasyExcel.write(file, clazz).sheet(sheetName).doWrite(data);
    }

    public static void writeToResponse(
            HttpServletResponse response, String fileName, Class clazz, String sheetName, List data
    ) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), clazz).sheet(sheetName).doWrite(data);
    }

}
