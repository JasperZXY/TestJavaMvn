package com.zxy.demo.excel.demo1;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel相关操作，只能处理第一行为标题头，第二行开始为数据行的excel文件
 */
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * 读取数据
     * @param <T>
     */
    public interface Reader<T> {
        /**
         * 头部的读取不会调用该方法
         * @param row 从0开始，但头部的row为0，所以这里一般从1开始
         * @param data 数据
         * @return
         */
        void readLine(int row, T data);

        /**
         * 读取数据行失败回调
         * @param row
         * @param message
         */
        void readError(int row, String message);

        /**
         * 读取文件失败之类的回调
         * @param message
         */
        void readError(String message);

    }

    /**
     * 读取文件数据
     * @param beanClass
     * @param is IO流，注意这个工具包不会对这个IO流进行关闭
     * @param stopOnError 是否当读取失败就停止
     * @param reader 监听器
     * @param <T>
     */
    public static <T> void read(Class<T> beanClass, InputStream is, boolean stopOnError, Reader reader) {
        if (beanClass == null) {
            reader.readError("Class为空");
            return;
        }
        if (is == null) {
            reader.readError("输入流为空");
            return;
        }

        Map<Integer, ExcelFieldBean> fieldBeanMap;
        Map<Integer, Field> fieldMap;
        List<ExcelFieldBean> list = null;
        try {
            list = getColomn(beanClass);
        } catch (Exception e) {
            logger.error("getColomn error.", e);
            reader.readError("字段解析失败");
            return;
        }
        Collections.sort(list, new Comparator<ExcelFieldBean>() {
            @Override
            public int compare(ExcelFieldBean o1, ExcelFieldBean o2) {
                return o1.getColumn() - o2.getColumn();
            }
        });

        fieldBeanMap = new HashMap<>();
        fieldMap = new HashMap<>();
        for (ExcelFieldBean bean : list) {
            fieldBeanMap.put(bean.getColumn(), bean);
            Field field = null;
            try {
                field = beanClass.getDeclaredField(bean.getField());
            } catch (NoSuchFieldException e) {
                logger.error("read getDeclaredField error.", e);
                reader.readError("字段解析出错");
                return;
            }
            field.setAccessible(true);
            fieldMap.put(bean.getColumn(), field);
        }

        HSSFSheet sheet;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            sheet = workbook.getSheetAt(0);

            // 获取总行数
            int rowNum = sheet.getPhysicalNumberOfRows();
            // 获取总列数
            int columnNum = sheet.getRow(0).getPhysicalNumberOfCells();

            if (rowNum <= 1) {
                reader.readError("文件的数据行数小于等于1");
                return;
            }
        } catch (Exception e) {
            logger.error("read error.", e);
            reader.readError("文件读取失败");
            return;
        }

        boolean isStop = false;
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            if (isStop) {
                break;
            }
            HSSFRow hssfRow = sheet.getRow(rowNum);
            T item;

            try {
                item = beanClass.newInstance();
                for (int j = 0; j < hssfRow.getLastCellNum(); j++) {
                    HSSFCell cell = hssfRow.getCell(j);

                    final ExcelFieldBean excelFieldBean = fieldBeanMap.get(j);
                    final Field field = fieldMap.get(j);
                    if (excelFieldBean == null || field == null) {
                        continue;
                    }

                    if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                            field.set(item, cell.getDateCellValue());
                        } else {
                            if (field.getType() == Date.class) {
                                field.set(item, cell.getDateCellValue());
                            } else if (field.getType() == int.class || field.getType() == Integer.class) {
                                field.set(item, (int) cell.getNumericCellValue());
                            } else if (field.getType() == float.class || field.getType() == Float.class) {
                                field.set(item, (float) cell.getNumericCellValue());
                            } else if (field.getType() == double.class || field.getType() == Double.class) {
                                field.set(item, cell.getNumericCellValue());
                            } else {
                                logger.error("read row:{} column:{} type:{}", rowNum, j, field.getType());
                                reader.readError(rowNum, "无法识别的字段类型，第" + (j+1) + "列");
                                if (stopOnError) {
                                    isStop = true;
                                }
                                break;
                            }
                        }
                    } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        if (field.getType() == Date.class) {
                            field.set(item, excelFieldBean.getSimpleDateFormat().parse(cell.getStringCellValue()));
                        } else if (field.getType() == int.class || field.getType() == Integer.class) {
                            field.set(item, Integer.parseInt(cell.getStringCellValue()));
                        } else if (field.getType() == float.class || field.getType() == Float.class) {
                            field.set(item, Float.parseFloat(cell.getStringCellValue()));
                        } else if (field.getType() == double.class || field.getType() == Double.class) {
                            field.set(item, Double.parseDouble(cell.getStringCellValue()));
                        } else {
                            field.set(item, cell.getStringCellValue());
                        }
                    } else {
                        logger.error("read row:{} column:{} type:{}", rowNum, j, cell.getCellType());
                        reader.readError(rowNum, "无法识别的单元格类型，第" + (j+1) + "列");
                        if (stopOnError) {
                            isStop = true;
                        }
                        break;
                    }
                }
                reader.readLine(rowNum, item);
            } catch (Exception e) {
                logger.error("read row:{} error.", rowNum, e);
                reader.readError(rowNum, "文件解析出错");
                if (stopOnError) {
                    isStop = true;
                }
            }

        }
    }

    /**
     * 读取文件数据
     * @param beanClass
     * @param filePath 文件路径
     * @param stopOnError 是否当读取失败就停止
     * @param reader 监听器
     * @param <T>
     */
    public static <T> void read(Class<T> beanClass, String filePath, boolean stopOnError, Reader reader) {
        if (StringUtils.isBlank(filePath)) {
            reader.readError("文件路径为空");
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            read(beanClass, fis, stopOnError, reader);
        } catch (FileNotFoundException e) {
            reader.readError("文件读取失败");
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * 读取数据，若遇到错误，则返回的list为empty
     * @param filePath
     * @return
     */
    public static <T> List<T> read(Class<T> beanClass, String filePath) {
        final List<T> list = new ArrayList<>();
        read(beanClass, filePath, true, new Reader<T>() {
            @Override
            public void readLine(int row, T data) {
                list.add(data);
            }

            @Override
            public void readError(int row, String message) {
                list.clear();
            }

            @Override
            public void readError(String message) {
                list.clear();
            }
        });
        return list;
    }

    /**
     * 导出数据到文件
     * @param beanClass
     * @param filePath 
     * @param list
     * @param <T>
     * @throws Exception
     */
    public static <T> void export(Class<T> beanClass, String filePath, List<T> list) throws Exception {
        Map<Integer, ExcelFieldBean> fieldBeanMap;
        Map<Integer, Field> fieldMap;
        List<ExcelFieldBean> excelFieldBeanList = getColomn(beanClass);
        Collections.sort(excelFieldBeanList, new Comparator<ExcelFieldBean>() {
            @Override
            public int compare(ExcelFieldBean o1, ExcelFieldBean o2) {
                return o1.getColumn() - o2.getColumn();
            }
        });

        fieldBeanMap = new HashMap<>();
        fieldMap = new HashMap<>();
        for (ExcelFieldBean bean : excelFieldBeanList) {
            fieldBeanMap.put(bean.getColumn(), bean);
            Field field = null;
            try {
                field = beanClass.getDeclaredField(bean.getField());
            } catch (NoSuchFieldException e) {
                throw e;
            }
            field.setAccessible(true);
            fieldMap.put(bean.getColumn(), field);
        }

        FileOutputStream fos = null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();

            // 头部
            HSSFRow rowTitle = sheet.createRow(0);
            for (ExcelFieldBean excelFieldBean : fieldBeanMap.values()) {
                HSSFCell cell = rowTitle.createCell(excelFieldBean.getColumn());
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(excelFieldBean.getTitle());
            }

            for (int i=0; i<list.size(); i++) {
                HSSFRow rowData = sheet.createRow(i + 1);
                T item = list.get(i);
                for (Map.Entry<Integer, Field> entry : fieldMap.entrySet()) {
                    Field field = entry.getValue();
                    HSSFCell cell = rowData.createCell(entry.getKey());

                    if (field.getType() == Date.class) {
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);  // 这里不能为CELL_TYPE_NUMERIC，不然写到文件会是6位的一个数字
                        ExcelFieldBean excelFieldBean = fieldBeanMap.get(entry.getKey());
                        if (excelFieldBean != null && excelFieldBean.getSimpleDateFormat() != null) {
                            cell.setCellValue(excelFieldBean.getSimpleDateFormat().format((Date) field.get(item)));
                        }
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue((int) field.get(item));
                    } else if (field.getType() == float.class || field.getType() == Float.class) {
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue((float) field.get(item));
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                        cell.setCellValue((double) field.get(item));
                    } else {
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(field.get(item).toString());
                    }
                }
            }

            fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private static List<ExcelFieldBean> getColomn(Class<?> beanClass) throws Exception {
        List<ExcelFieldBean> columns = new ArrayList<>();
        Field[] fields = beanClass.getDeclaredFields();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                ExcelFieldBean excelFieldBean = new ExcelFieldBean();
                excelFieldBean.setField(field.getName());
                excelFieldBean.setFieldType(field.getType());

                if (field.isAnnotationPresent(ExcelField.class)) {
                    Annotation annotation = field.getAnnotation(ExcelField.class);

                    Method methodTitle = ExcelField.class.getMethod("title");
                    excelFieldBean.setTitle((String) methodTitle.invoke(annotation));

                    Method methodOrder = ExcelField.class.getMethod("column");
                    excelFieldBean.setColumn((Integer) methodOrder.invoke(annotation));

                    Method methodDateFormat = ExcelField.class.getMethod("dateFormat");
                    excelFieldBean.setDateFormat((String) methodDateFormat.invoke(annotation));
                    if (excelFieldBean.getDateFormat() != null && !"".equals(excelFieldBean.getDateFormat().trim())) {
                        excelFieldBean.setSimpleDateFormat(new SimpleDateFormat(excelFieldBean.getDateFormat()));
                    }

                    columns.add(excelFieldBean);
                }

            }
        }
        return columns;
    }
}
