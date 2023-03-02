package com.profit.commons.utils;

import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @Author:liulongling
 * @Date:2022/3/18 15:49
 */
public class DateUtils {
    public static final String DATE_PATTERM = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date string2Date(String date, String patterm) {
        try {
            return new SimpleDateFormat(patterm).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean compTime(Date nowDate, Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int total = hour * 3600 + minute * 60 + second;

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        int hour1 = calendar1.get(Calendar.HOUR);
        int minute1 = calendar1.get(Calendar.MINUTE);
        int second1 = calendar1.get(Calendar.SECOND);
        int total1 = hour1 * 3600 + minute1 * 60 + second1;

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        int hour2 = calendar2.get(Calendar.HOUR);
        int minute2 = calendar2.get(Calendar.MINUTE);
        int second2 = calendar2.get(Calendar.SECOND);
        int total2 = hour2 * 3600 + minute2 * 60 + second2;
        return total >= total1 && total <= total2;
    }


    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意三个参数的时间格式要一致
     *
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return 在时间段内返回true，不在返回false
     */
    public static boolean compareToData(String nowTime, String startTime, String endTime) throws ParseException {
        if (nowTime.equals(startTime)
                || nowTime.equals(endTime)) {
            return true;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = sdf.parse(nowTime);
        Date date2 = sdf.parse(startTime);
        Date date3 = sdf.parse(endTime);
        return isEffectiveDate(date1, date2, date3);
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意三个参数的时间格式要一致
     *
     * @param nowTime
     * @param startTime
     * @param endTime
     * @return 在时间段内返回true，不在返回false
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        return date.after(begin) && date.before(end);
    }

    public static String getDateString(Date date, String patterm) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(patterm);
        return dateFormat.format(date);
    }

    public static String getDateString(Date date) {
        return getDateString(date, DATE_PATTERM);
    }

    public static String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN);
        return dateFormat.format(date);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }


    public static Date getBeginTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate localDate = yearMonth.atDay(1);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.of("Asia/Shanghai"));

        return Date.from(zonedDateTime.toInstant());
    }

    public static Date getEndTime(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();
        LocalDateTime localDateTime = endOfMonth.atTime(23, 59, 59, 999);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * 获取当天指定时间
     *
     * @param hour   几点
     * @param minute 分钟
     * @param second 秒
     * @return
     */
    public static Date getTime(Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /**
     * 获取当天指定时间
     *
     * @param date 日期
     * @return
     */
    public static boolean isToday(Date date) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH) + 1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH) + 1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if (year1 == year2 && month1 == month2 && day1 == day2) {
            return true;
        }
        return false;
    }

    /**
     * 获取当天指定时间
     *
     * @param hour   几点
     * @param minute 分钟
     * @param second 秒
     * @return
     */
    public static String getTodayTime(int hour, int minute, int second) {
        return getTimeString(getTime(new Date(), hour, minute, second));
    }

    /**
     * 最近几天开始时间
     *
     * @return
     */
    public static Date getNeverDayStartTime(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -day);
        return DateUtil.beginOfDay(calendar.getTime());
    }

    /**
     * 最近几天结束时间
     *
     * @return
     */
    public static Date getNeverDayEndTime(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getNeverDayStartTime(day));
        calendar.add(Calendar.DATE, day - 1);
        return DateUtil.endOfDay(calendar.getTime());
    }


    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static Date getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(new java.util.Date());
        // Monday
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static Date getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(new java.util.Date());
        // Sunday
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 6);
        return cal.getTime();
    }

    /**
     * 获取本月的第一天
     *
     * @return String
     **/
    public static Date getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取本月的最后一天
     *
     * @return String
     **/
    public static Date getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }
}
