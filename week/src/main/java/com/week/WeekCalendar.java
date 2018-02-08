package com.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

/**
 * description：周控件 跟布局
 * <p/>
 * Created by TIAN FENG on 2018/2/7.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */

public class WeekCalendar extends LinearLayout {

    // 容器viewpager
    private ViewPager mWeekPager;
    // 数据改变监听
    private OnWeekChangeListener mWeekChangeListener;
    // 时间段内有多少周
    private int mWeekCount;

    // 日期所在的周对象List<Calendar> 某一周的日期
    private SparseArray<List<Calendar>> mItemCalendars;

    // 自定义属性解析后对象
    private int weekFocusRadius, weekSchemeRadius, weekTextSize, weekBackgroundColor, weekNomerTextColor, weekFocusTextColor, weekWeekendTextColor,weekSchemeRes;

    // 是否是调用函数的Scroll 防止调用函数 scrollToCalendar 回掉两次日期 一次setCurrentItem里面performClickItem
    private boolean mIsScrollToCalendar;

    public WeekCalendar(@NonNull Context context) {
        this(context, null);
    }

    public WeekCalendar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekCalendar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_week, this);
        initWeekCalendar(context, attrs);
        // 先默认处理一条初始数据
        init();

    }

    private void initWeekCalendar(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeekCalendar);
        // 聚焦显示背景半径
        weekFocusRadius = a.getDimensionPixelSize(R.styleable.WeekCalendar_weekFocusRadius, 50);
        // 事件半径
        weekSchemeRadius = a.getDimensionPixelSize(R.styleable.WeekCalendar_weekSchemeRadius, 20);
        // 文字大小
        weekTextSize = a.getDimensionPixelSize(R.styleable.WeekCalendar_weekTextSize, 60);
        // 聚焦背景颜色
        weekBackgroundColor = a.getColor(R.styleable.WeekCalendar_weekBackground, Color.parseColor("#4666ee"));
        // 默认文字颜色
        weekNomerTextColor = a.getColor(R.styleable.WeekCalendar_weekNomerTextColor, Color.parseColor("#757575"));
        // 聚焦文字颜色
        weekFocusTextColor = a.getColor(R.styleable.WeekCalendar_weekFocusTextColor, Color.WHITE);
        // 周末颜色
        weekWeekendTextColor = a.getColor(R.styleable.WeekCalendar_weekWeekendTextColor, Color.parseColor("#CCCCCC"));

        weekSchemeRes = a.getResourceId(R.styleable.WeekCalendar_weekSchemeRes,R.mipmap.warn);
        a.recycle();
    }

    // 初始化
    private void init() {
        mWeekPager = (ViewPager) findViewById(R.id.weekPager);
        mWeekPager.setAdapter(new WeekViewPagerAdapter());

        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // 默认数据
        setRange(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), 1, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH) + 2, 1);

        // 监听动作
        mWeekPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 模拟点击事件回掉
                if (mWeekChangeListener != null) {
                    WeekView weekView = (WeekView) mWeekPager.findViewWithTag(position);
                    if (weekView != null && !mIsScrollToCalendar) {
                        Calendar calendar = mItemCalendars.get(position).get(1);// 周一拿第二条数据
                        // 点击并回调
                        weekView.performClickItem(calendar);
                    }
                    // 重置是否是滑动操作
                    mIsScrollToCalendar = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /************************************************************************************************************************************************************/

    /**
     * 移动到当天
     */
    public void scrollCurrentDay() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        // 当前年
        final int year = calendar.get(java.util.Calendar.YEAR);
        // 当前月
        final int month = (calendar.get(java.util.Calendar.MONTH)) + 1;
        // 当前月的第几天：即当前日
        final int day_of_month = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        // 移动
        scrollToCalendar(year, month, day_of_month);

    }

    /**
     * 滚动到指定日期
     */
    public void scrollToCalendar(final int year, final int month, final int day) {
        // 必须等绘制完成之后开始回掉
        mWeekPager.post(new Runnable() {
            @Override
            public void run() {
                int position = Util.getCalendarToPosition(year, month, day, mItemCalendars);

                // 没有查询到表示超出时间范围
                if (position == -1) {
                    throw new IllegalArgumentException(year + month + day + "不在可选时间范围内");
                }
                // 表示不是华东操作
                mIsScrollToCalendar = true;
                // 移动到指定周
                mWeekPager.setCurrentItem(position, false);

                WeekView weekView = (WeekView) mWeekPager.findViewWithTag(position);
                if (weekView != null) {
                    // 获取calendar对象
                    Calendar calendar = new Calendar();
                    calendar.setYear(year);
                    calendar.setMonth(month);
                    calendar.setDay(day);

                    // 如果一样就不点击
                    if (!calendar.equals(mItemCalendars.get(position).get(1))) {
                        weekView.performClickItem(calendar);
                    }
                }
            }
        });
    }


    /**
     * 设置事件日期 改变那个日期的状态
     *
     * @param calendars
     */
    public void setScheme(List<Calendar> calendars) {
        if (mItemCalendars != null) {
            // 遍历总周数
            for (int i = 0; i < mItemCalendars.size(); i++) {
                // 遍历每周的日期
                for (Calendar calendar : calendars) {
                    // 判断是否存在这个一天
                    int position = mItemCalendars.get(i).indexOf(calendar);

                    // 存在这一天表示有事件
                    if (position != -1) {
                        mItemCalendars.get(i).get(position).setScheme(true);
                    }
                }
            }
        }

        // 唤醒适配器
        mWeekPager.getAdapter().notifyDataSetChanged();
    }

    /**
     * 设置日期范围
     */
    public void setRange(int minYear, int minYearMonth, int minDay, int maxYear, int maxYearMonth, int maxDay) {
        // 获取周数
        mWeekCount = Util.getWeekCountBetweenYearAndYear(minYear, minYearMonth, minDay, maxYear, maxYearMonth, maxDay);
        // 获取这个时间段周数所有的日期
        mItemCalendars = Util.getItemCalendarBetweenYearAndYear(minYear, minYearMonth, minDay, maxYear, maxYearMonth, maxDay);
        // 唤醒适配器改变数据
        mWeekPager.getAdapter().notifyDataSetChanged();

        // 移动到今天 默认需不需要滚动
//        scrollCurrentDay();
    }


    /**
     * 设置监听
     *
     * @param listener
     */
    public void addOnWeekChangeListener(OnWeekChangeListener listener) {
        mWeekChangeListener = listener;
    }

/********************************************************************************************************************************************************************************************************/
    /**
     * 周视图切换
     */
    private class WeekViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mWeekCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // 处理Item
            WeekView weekView = new WeekView(getContext());
            // 设置属性
            weekView.setAttr(weekFocusRadius, weekSchemeRadius, weekTextSize, weekBackgroundColor, weekNomerTextColor, weekFocusTextColor, weekWeekendTextColor,weekSchemeRes);
            // 传递这一周包含的日期 这里会调重绘函数
            weekView.setCalendars(mItemCalendars.get(position));
            // 设置监听
            weekView.setOnWeekChangeListener(mWeekChangeListener);
            // 设置tag
            weekView.setTag(position);
            // 添加
            container.addView(weekView);
            return weekView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
