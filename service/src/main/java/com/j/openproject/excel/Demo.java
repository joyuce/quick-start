package com.j.openproject.excel;

import java.io.File;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author Joyuce
 * @Type Demo
 * @Desc
 * @date 2020年03月12日
 * @Version V1.0
 */
@Data
public class Demo {

    @ExcelProperty(value = "名字")
    private String name;

    @ExcelProperty(value = "数字")
    private Integer num;

    /**
     * 忽略这个字段
     */
    @ExcelIgnore
    private String ignore;

    public static void main(String[] args) {
        File file = new File("C:\\Users\\EDZ\\Desktop\\1.xlsx");
        List<Demo> list = ExcelUtil.getListByExcelSync(file, Demo.class, 0, 6);
        System.out.println(list);

        File out = new File("C:\\Users\\EDZ\\Desktop\\2.xlsx");
        EasyExcel.write(out, Demo.class).sheet().doWrite(list);

    }

}
