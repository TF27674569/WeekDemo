package com.week;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * description： 日期计算相关
 * <p/>
 * Created by TIAN FENG on 2018/2/7.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */

public class Util {

    /**
     * 获取两天之间有多少周
     *
     * @param minYear
     * @param minYearMonth
     * @param minDay
     * @param maxYear
     * @param maxYearMonth
     * @param maxDay
     * @return
     */
    static int getWeekCountBetweenYearAndYear(int minYear, int minYearMonth, int minDay, int maxYear, int maxYearMonth, int maxDay) {

        // 计算最小天数 往前的第一个周日是几号
        java.util.Calendar minDate = java.util.Calendar.getInstance();
        minDate.set(minYear, minYearMonth - 1, minDay);
        int minWeek = minDate.get(java.util.Calendar.DAY_OF_WEEK);// 1-7
        if (minWeek != 0) {
            minDate.add(java.util.Calendar.DAY_OF_MONTH, 1 - minWeek);
        }

        // 计算最大天数 往后的第一个周六
        java.util.Calendar maxDate = java.util.Calendar.getInstance();
        maxDate.set(maxYear, maxYearMonth - 1, maxDay);
        int maxWeek = maxDate.get(java.util.Calendar.DAY_OF_WEEK);// 1 - 7
        if (maxWeek != 7) {
            maxDate.add(java.util.Calendar.DAY_OF_MONTH, 7 - maxWeek);

        }
        // 计算两天之间有几周
        Long weeks = (maxDate.getTime().getTime() - minDate.getTime().getTime()) / (7 * 24 * 60 * 60 * 1000);

        System.out.println(weeks + "");

        return (int) (weeks + 1);
    }

    /**
     * 计算 某个时间段内 周的条目 item
     *
     * @param minYear
     * @param minYearMonth
     * @param minDay
     * @param maxYear
     * @param maxYearMonth
     * @param maxDay
     * @return
     */
    public static SparseArray<List<Calendar>> getItemCalendarBetweenYearAndYear(int minYear, int minYearMonth, int minDay, int maxYear, int maxYearMonth, int maxDay) {
        SparseArray<List<Calendar>> sparseCalendars = new SparseArray<>();

        // 先计算有多少周
        int weekCount = getWeekCountBetweenYearAndYear(minYear, minYearMonth, minDay, maxYear, maxYearMonth, maxDay);


        // 周数
        for (int i = 0; i < weekCount; i++) {
            List<Calendar> calendars = new ArrayList<>();
            for (int y = 0; y < 7; y++) {

                // 获取往前数的第一个周日
                java.util.Calendar minDate = getCalendar(minYear, minYearMonth, minDay);
                // 获取下几天
                java.util.Calendar nextDay = getAfterDay(minDate, i * 7 + y);
                // 当前年
                int year = nextDay.get(java.util.Calendar.YEAR);
                // 当前月
                int month = (nextDay.get(java.util.Calendar.MONTH)) + 1;
                // 当前月的第几天：即当前日
                int day_of_month = nextDay.get(java.util.Calendar.DAY_OF_MONTH);

                Calendar calendar = new Calendar();
                calendar.setYear(year);
                calendar.setMonth(month);
                calendar.setDay(day_of_month);
                // 是否是周末
                calendar.setWeekend(nextDay.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY || nextDay.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY);
                // 是否是今天
                calendar.setCurrentDay(nextDay.getTime().getTime() == java.util.Calendar.getInstance().getTime().getTime());

                calendars.add(calendar);
            }
            sparseCalendars.put(i, calendars);
        }

        return sparseCalendars;
    }

    /**
     * 往前数第一个周日
     *
     * @param minYear
     * @param minYearMonth
     * @param minDay
     * @return
     */
    private static java.util.Calendar getCalendar(int minYear, int minYearMonth, int minDay) {
        // 计算最小天数 往前的第一个周日是几号
        java.util.Calendar minDate = java.util.Calendar.getInstance();
        minDate.set(minYear, minYearMonth - 1, minDay);
        int minWeek = minDate.get(java.util.Calendar.DAY_OF_WEEK);// 1-7
        if (minWeek != 0) {
            minDate.add(java.util.Calendar.DAY_OF_MONTH, 1 - minWeek);
        }
        return minDate;
    }

    /**
     * 获取当前时间的后几天天时间
     *
     * @param cl
     * @return
     */
    private static java.util.Calendar getAfterDay(java.util.Calendar cl, int dy) {
        //使用set方法直接设置时间值
        int day = cl.get(java.util.Calendar.DATE);
        cl.set(java.util.Calendar.DATE, day + dy);
        return cl;
    }

    /**
     * 获取这一天在哪一周
     *
     * @param year
     * @param month
     * @param day
     * @param calendars
     * @return
     */
    static int getCalendarToPosition(int year, int month, int day, SparseArray<List<Calendar>> calendars) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);

        for (int i = 0; i < calendars.size(); i++) {
            if (calendars.get(i).contains(calendar)) {
                return i;
            }
        }
        return -1;
    }
}
