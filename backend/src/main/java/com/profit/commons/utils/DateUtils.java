package com.profit.commons.utils;

import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 时间工具类
 *
 * @Author:liulongling
 * @Date:2022/3/18 15:49
 */
public class DateUtils {
    public static final String DATE_PATTERM = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyyMMdd";
    private static final String PATTERN_MONTH_DATE = "yyyyMM";
    private static final String PATTERN_TIME = "HH:mm:ss";
    private static final String PATTERN_DATE_TIME = "yyyyMMdd HH:mm:ss";
    private static final String PATTERN_DATE_TIME_DURATION = "%时间1% 至 %时间2%";
    private static final String PATTERN_DURATION = "%02d天%02d时%02d分%02d秒";
    private static final String PATTERN_DATE_ES_INDEX_SUFFIX = "yyyy.MM.dd";
    public static final int ONE_DAY_SEC = 24 * 60 * 60;//一天(秒)
    public static final int ONE_HOUR_TO_SEL = 60 * 60;//一小时秒数
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(DateUtils.PATTERN_DATE).withLocale(Locale.getDefault());
    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(DateUtils.PATTERN_TIME).withLocale(Locale.getDefault());
    public static final DateTimeFormatter FORMATTER_DATE_TIME = DateTimeFormatter.ofPattern(DateUtils.PATTERN_DATE_TIME).withLocale(Locale.getDefault());
    private static final DateTimeFormatter FORMATTER_MONTH_DATE = DateTimeFormatter.ofPattern(DateUtils.PATTERN_MONTH_DATE).withLocale(Locale.SIMPLIFIED_CHINESE);
    private static final DateTimeFormatter FORMATTER_DATE_ES_INDEX_SUFFIX = DateTimeFormatter.ofPattern(DateUtils.PATTERN_DATE_ES_INDEX_SUFFIX).withLocale(Locale.getDefault());

    /**
     * 查询2个时间差 单位天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long compareDateInterval(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate11 = LocalDate.of(localDate1.getYear(), localDate1.getMonthValue(), localDate1.getDayOfMonth()); // 第一个日期

        LocalDate localDate2 = date2.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate22 = LocalDate.of(localDate2.getYear(), localDate2.getMonthValue(), localDate2.getDayOfMonth()); // 第二个日期
        return ChronoUnit.DAYS.between(localDate11, localDate22);
    }

    public static Date string2Date(String date, String patterm) {
        try {
            return new SimpleDateFormat(patterm).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime parse(int unixTimeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimeStamp), ZoneId.systemDefault());
    }

    public static LocalDateTime parse(String dateTime) {
        return LocalDateTime.from(DateUtils.FORMATTER_DATE_TIME.parse(dateTime));
    }

    public static LocalDate parseDate(String dateTime) {
        return LocalDate.from(DateUtils.FORMATTER_DATE.parse(dateTime));
    }

    public static LocalDate parseTime(String dateTime) {
        return LocalDate.from(DateUtils.FORMATTER_TIME.parse(dateTime));
    }

    public static int parseUnixTime(String string) {
        return DateUtils.parseUnixTime(DateUtils.parse(string));
    }

    public static int parseUnixTime(LocalDateTime localDateTime) {
        return Math.toIntExact(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond());
    }

    public static int parseUnixTimeFromDate(String string) {
        return DateUtils.parseUnixTimeFromDate(DateUtils.parseDate(string));
    }

    public static int parseUnixTimeFromDate(LocalDate localDateTime) {
        return Math.toIntExact(localDateTime.atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
    }


    public static List<String> getDateList(LocalDateTime startTime, LocalDateTime endTime) {
        return DateUtils.getDateList(DateUtils.parseUnixTime(startTime), DateUtils.parseUnixTime(endTime));
    }

    public static List<String> getDateList(String startDate, String endDate) {
        return DateUtils.getDateList(DateUtils.parseUnixTimeFromDate(startDate), DateUtils.parseUnixTimeFromDate(endDate));
    }

    public static String format(LocalDateTime dateTime) {
        return DateUtils.FORMATTER_DATE_TIME.format(dateTime);
    }

    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return formatter.format(dateTime);
    }

    public static String format(LocalDateTime dateTime, String format) {
        return DateTimeFormatter.ofPattern(format).format(dateTime);
    }


    public static String formatTime(LocalDateTime dateTime) {
        return DateUtils.FORMATTER_TIME.format(dateTime);
    }

    public static String formatDate(int dateTime) {
        return DateUtils.formatDate(DateUtils.parse(dateTime));
    }

    public static String formatDate(LocalDateTime dateTime) {
        return DateUtils.FORMATTER_DATE.format(dateTime);
    }

    public static List<String> getDateList(int startTime, int endTime) {
        if (startTime == endTime) {
            return new ArrayList<String>() {{
                this.add(DateUtils.formatDate(startTime));
            }};
        }
        Set<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        int time = startTime;
        while (time < endTime) {
            String date = DateUtils.formatDate(time);
            if (!set.contains(date)) {
                set.add(DateUtils.formatDate(time));
                list.add(date);
            }
            time += ONE_DAY_SEC;
        }
        String date = DateUtils.formatDate(time);
        if (!set.contains(date)) {
            set.add(DateUtils.formatDate(time));
            list.add(date);
        }
        return list;
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
    public static Date getTodayDateTime(int hour, int minute, int second) {
        return string2Date(getTimeString(getTime(new Date(), hour, minute, second)), TIME_PATTERN);
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
     * 获取当前月第一天
     *
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
        String firstDayDate = sdf.format(calendar.getTime());
        return firstDayDate;

    }

    /**
     * 获取当前月最后一天
     *
     * @param month
     * @return
     */
    public static String getLastDayOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        // 设置月份
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = 0;
        //2月的平年瑞年天数
        if (month == 2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        } else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
        String lastDayDate = sdf.format(calendar.getTime());
        return lastDayDate;

    }


    /**
     * 获取本月的第一天
     *
     * @return String
     **/
    public static String getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return getDateString(cal.getTime());
    }

    /**
     * 获取本月的最后一天
     *
     * @return String
     **/
    public static String getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getDateString(cal.getTime());
    }

    /**
     * 获取上个月月开始时间和结束时间
     *
     * @return
     */
    public static Map<String, String> getLastMonthTime() throws Exception {
        Long startTime = getLastMonthStartTime();
        Long endTime = getLastMonthEndTime();
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startTimeStr = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()));
        String endTimeStr = ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), ZoneId.systemDefault()));
        Map map = new HashMap();
        map.put("startDate", startTimeStr);
        map.put("endDate", endTimeStr);
        return map;
    }

    public static Long getLastMonthStartTime() throws Exception {
        Long currentTime = System.currentTimeMillis();

        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static Long getLastMonthEndTime() {
        Long currentTime = System.currentTimeMillis();

        String timeZone = "GMT+8:00";
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.setTimeInMillis(currentTime);
        calendar.add(Calendar.YEAR, 0);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));// 获取当前月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }
}
