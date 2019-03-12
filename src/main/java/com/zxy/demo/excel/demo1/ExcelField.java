package com.zxy.demo.excel.demo1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField {
    /**
     * 在excel中标题头，只用于导出
     * @return
     */
    String title();

    /**
     * 第N列，从0开始
     * @return
     */
    int column();

    /**
     * 如果为日期的，则用这个字段进行日期格式化
     * @return
     */
    String dateFormat() default "yyyy/MM/dd";

}
