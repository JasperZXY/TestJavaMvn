package com.jasper.mvntest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    
    private final static SimpleDateFormat[] SIMPLE_DATE_FORMATS = {
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
        new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss"),
        new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒"),
        new SimpleDateFormat("yyyy年MM月dd日 E HH:mm:ss"),
        new SimpleDateFormat("yyyy年MM月dd日 E HH时mm分ss秒"),
        new SimpleDateFormat("yyyyMMddHHmmss"),
        new SimpleDateFormat("yyyyMMdd"),
        new SimpleDateFormat("yyw"),
        new SimpleDateFormat("yyMM")
    };
    
    /**
     * 返回系统当前的完整日期时间 <br>
     * 格式 1：yyyy-MM-dd HH:mm:ss <br>
     * 格式 2：yyyy/MM/dd HH:mm:ss <br>
     * 格式 3：yyyy年MM月dd日 HH:mm:ss <br>
     * 格式 4：yyyy年MM月dd日 HH时mm分ss秒 <br>
     * 格式 5：2008年05月02日 星期五 13:12:44 <br>
     * 格式 6：2008年05月02日 星期五 13时12分44秒 <br>
     * 格式 7：yyyyMMddHHmmss <br>
     * 格式 8：yyyyMMdd <br>
     * 格式 9：yyw(year + week in year) <br>
     * 格式 10：yyMM <br>
     * 默认：格式1<br>
     * @param 参数(formatType) :格式代码号
     * @return 字符串
     */
    public static String get(int formatType, Date date) {
        if (formatType < 0 || formatType > SIMPLE_DATE_FORMATS.length) {
            formatType = 1;
        }
        formatType --;
        SimpleDateFormat sdf = SIMPLE_DATE_FORMATS[formatType];
        sdf.setLenient(false);
        return sdf.format(date);
    }
    
    /**
     * 返回系统当前的完整日期时间 <br>
     * 格式 1：yyyy-MM-dd HH:mm:ss <br>
     * 格式 2：yyyy/MM/dd HH:mm:ss <br>
     * 格式 3：yyyy年MM月dd日 HH:mm:ss <br>
     * 格式 4：yyyy年MM月dd日 HH时mm分ss秒 <br>
     * 格式 5：2008年05月02日 星期五 13:12:44 <br>
     * 格式 6：2008年05月02日 星期五 13时12分44秒 <br>
     * 格式 7：yyyyMMddHHmmss <br>
     * 格式 8：yyyyMMdd <br>
     * 格式 9：yyw(year + week in year) <br>
     * 格式 10：yyMM <br>
     * 默认：格式1<br>
     * @param
     * @return 
     * @throws ParseException 
     */
    public static Date get(int formatType, String date) throws ParseException {
        if (formatType < 0 || formatType > SIMPLE_DATE_FORMATS.length) {
            formatType = 1;
        }
        formatType --;
        SimpleDateFormat sdf = SIMPLE_DATE_FORMATS[formatType];
        sdf.setLenient(false);
        return sdf.parse(date);
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameMonth(cal1, cal2);
    }

    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
                .get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }
    
    /**
     * 本月最后一天最后一秒的时间戳
     * @return
     */
    public static long getTheLastDayOfMonth() {
        Calendar cld = Calendar.getInstance();
        cld.setTime(new Date());
        cld.set(Calendar.DAY_OF_MONTH, 1);
        cld.set(Calendar.HOUR_OF_DAY, 0);
        cld.set(Calendar.MINUTE, 0);
        cld.set(Calendar.SECOND, 0);
        cld.set(Calendar.MILLISECOND, 0);
        
        cld.set(Calendar.MONTH, cld.get(Calendar.MONTH) + 1);
        return cld.getTimeInMillis() - 1;
    }
    
}
