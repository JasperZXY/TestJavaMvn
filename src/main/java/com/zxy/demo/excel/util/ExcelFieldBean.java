package com.zxy.demo.excel.util;

import java.text.SimpleDateFormat;

public class ExcelFieldBean {
    private String field;
    private Class<?> fieldType;
    private String title;
    private int column;
    private String dateFormat = "yyyy/MM/dd";
    private SimpleDateFormat simpleDateFormat;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    @Override
    public String toString() {
        return "ExcelFieldBean{" +
                "field='" + field + '\'' +
                ", fieldType=" + fieldType +
                ", title='" + title + '\'' +
                ", column=" + column +
                ", dateFormat='" + dateFormat + '\'' +
                ", simpleDateFormat=" + simpleDateFormat +
                '}';
    }
}
