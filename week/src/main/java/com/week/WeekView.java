/*
 * Copyright (C) 2016 huanghaibin_dev <huanghaibin_dev@163.com>
 * WebSite https://github.com/MiracleTimes-Dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.week;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;


/**
 * description：周控件绘制 可以继承textView在onDraw中super之前处理画布背景（这样不用画文字）
 * <p/>
 * Created by TIAN FENG on 2018/2/7.
 * QQ：27674569
 * Email: 27674569@qq.com
 * Version：1.0
 */
public class WeekView extends View implements View.OnClickListener {
    private static final String TAG = "WeekView";

    // 文字的画笔
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    // 点中后背景的画笔
    private Paint mFocusBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // 条目的宽度
    private int mItemWidth;
    // 文字基线
    private int mTextBaseLine;

    // 此周每一天的数据
    private List<Calendar> mDatas;
    // 数据改变监听
    private OnWeekChangeListener mWeekChangeListener;

    // 周末文本的颜色
    private int mWeekendColor = Color.parseColor("#CCCCCC");
    // 默认颜色
    private int mNomerColor = Color.parseColor("#757575");
    // 选中文本的颜色
    private int mFocusColor = Color.WHITE;
    // 背景颜色
    private int mBackgroundColor = Color.parseColor("#4666ee");

    private int mWeekSchemeRes;

    private int mBackRadius = 50;
    private int mSchemeRadius = 20;
    private int mRadiusPadding = 4;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 设置点击事件为了防止在onTouch事件中吃掉父布局viewpager的事件
        setOnClickListener(this);
        init(context);
    }

    private void init(Context context) {
        mTextPaint.setColor(mNomerColor);
        mTextPaint.setDither(true);
    }

    // 设置颜色相关属性
    void setAttr(int weekFocusRadius,
                 int weekSchemeRadius,
                 int weekTextSize,
                 int weekBackgroundColor,
                 int weekNomerTextColor,
                 int weekFocusTextColor,
                 int weekWeekendTextColor,
                 int weekSchemeRes) {
        mNomerColor = weekNomerTextColor;
        mFocusBackgroundPaint.setColor(mBackgroundColor = weekBackgroundColor);
        mTextPaint.setTextSize(weekTextSize);
        mBackRadius = weekFocusRadius;
        mSchemeRadius = weekSchemeRadius;
        mFocusColor = weekFocusTextColor;
        mWeekendColor = weekWeekendTextColor;
        mWeekSchemeRes = weekSchemeRes;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 算宽度
        mItemWidth = MeasureSpec.getSize(widthMeasureSpec) / 7;

        // 算基线
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        mTextBaseLine = height / 2 + ((fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
    }


    private float mX, mY;
    private boolean isClick;
    private int mClickItem = 1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() > 1)
            return false;
        // 判断时滑动还是点击
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mDY;
                if (isClick) {
                    mX = event.getY() - mX;
                    isClick = Math.abs(mX) <= 50;
                }
                break;
            case MotionEvent.ACTION_UP:
                mX = event.getX();
                mY = event.getY();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置日期数
     *
     * @param calendars
     */
    public void setCalendars(List<Calendar> calendars) {
        this.mDatas = calendars;
        if (mDatas == null || mDatas.size() != 7) {
            throw new IllegalArgumentException("日历控件单周必须为7天");
        }
        invalidate();
    }


    // 设置监听回掉
    public void setOnWeekChangeListener(OnWeekChangeListener weekChangeListener) {
        mWeekChangeListener = weekChangeListener;
    }

    /**
     * 绘制日历文本
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDatas == null || mDatas.isEmpty()) return;
        if (mDatas.size() != 7) {
            throw new IllegalArgumentException("日历控件单周必须为7天");
        }

        for (int i = 0; i < 7; i++) {
            Calendar calendar = mDatas.get(i);

            // 是否是周末
            if (calendar.isWeekend()) {
                // 是否是今天
                if (calendar.isCurrentDay()) {
                    // 是否是点击
                    if (mClickItem == i) {
                        // 当天需要画背景 改变画笔颜色
                        drawBackgroud(canvas, mItemWidth * i, mItemWidth * (i + 1), mItemWidth * i + mItemWidth / 2, getHeight() / 2);
                        mTextPaint.setColor(mFocusColor);
                    } else {
                        //  重置画笔颜色
                        mTextPaint.setColor(mBackgroundColor);
                    }
                } else {
                    // 是否是点击
                    if (mClickItem == i) {
                        // 当天需要画背景 改变画笔颜色
                        drawBackgroud(canvas, mItemWidth * i, mItemWidth * (i + 1), mItemWidth * i + mItemWidth / 2, getHeight() / 2);
                        mTextPaint.setColor(mFocusColor);
                    } else {
                        //  重置画笔颜色
                        mTextPaint.setColor(mWeekendColor);
                    }
                }
            } else {
                // 是否是今天
                if (calendar.isCurrentDay()) {
                    // 是否是点击
                    if (mClickItem == i) {
                        // 当天需要画背景 改变画笔颜色
                        drawBackgroud(canvas, mItemWidth * i, mItemWidth * (i + 1), mItemWidth * i + mItemWidth / 2, getHeight() / 2);
                        mTextPaint.setColor(mFocusColor);
                    } else {
                        //  重置画笔颜色
                        mTextPaint.setColor(mBackgroundColor);
                    }
                } else {
                    // 是否是点击
                    if (mClickItem == i) {
                        // 当天需要画背景 改变画笔颜色
                        drawBackgroud(canvas, mItemWidth * i, mItemWidth * (i + 1), mItemWidth * i + mItemWidth / 2, getHeight() / 2);
                        mTextPaint.setColor(mFocusColor);
                    } else {
                        //  重置画笔颜色
                        mTextPaint.setColor(mNomerColor);
                    }
                }
            }

            // 画文字
            drawText(canvas, calendar.getDay() + "", mItemWidth * i);


            // 是否存在事件
            if (calendar.isScheme() && mClickItem != i) {
                // 画事件
                drawScheme(canvas, mItemWidth * i, mItemWidth * (i + 1));
            }
        }
    }

    /**
     * 画背景
     *
     * @param canvas 画布
     * @param startX 起始x
     * @param endX   结束x
     * @param cx     item圆心 x
     * @param cy     item圆心y
     */
    private void drawBackgroud(Canvas canvas, int startX, int endX, int cx, int cy) {
        canvas.drawCircle(cx, cy, mBackRadius, mFocusBackgroundPaint);
    }


    /**
     * 画文字
     *
     * @param canvas 画布
     * @param day    当天
     * @param startX 起始X
     */
    private void drawText(Canvas canvas, String day, int startX) {
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        int textStartX = startX + mItemWidth / 2;
        canvas.drawText(day, textStartX, mTextBaseLine, mTextPaint);
    }


    /**
     * 画事件
     *
     * @param canvas
     * @param startX
     * @param endX
     */
    private void drawScheme(Canvas canvas, int startX, int endX) {
        int cx = startX + mItemWidth / 2;
        int cy = getHeight() / 2;
        int x = (int) (cx + (Math.sqrt(2) / 2 * (mBackRadius + mSchemeRadius + mRadiusPadding)));
        int y = (int) (cy - (Math.sqrt(2) / 2 * (mBackRadius + mSchemeRadius + mRadiusPadding)));
        canvas.drawBitmap(getBitmap(), x - mSchemeRadius, y - mSchemeRadius, null);

    }

    private Bitmap getBitmap() {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), mWeekSchemeRes);// 这个图可以用属性传入
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 设置想要的大小
        int newWidth = mSchemeRadius * 2;
        int newHeight = mSchemeRadius * 2;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    @Override
    public void onClick(View v) {
        // 点击
        if (isClick) {
            // 处理角标
            Calendar calendar = getIndex();
            if (calendar != null) {
                // 回掉
                if (mWeekChangeListener != null) {
                    mWeekChangeListener.onWeekChangeListener(mDatas.get(mClickItem));
                }
                // 更新
                invalidate();
            }
        }
    }

    private Calendar getIndex() {

        // 表示不是点击事件处理的更新 滚动或者强制调取函数
        if (mX == 0) {
            if (mClickItem >= 0 && mClickItem < mDatas.size())
                return mDatas.get(mClickItem);
            return null;
        }

        // 点击后处理更新 位置选择
        int indexX = (int) mX / mItemWidth;
        if (indexX >= 7) {
            indexX = 6;
        }
        mClickItem = indexX;// 选择项
        if (mClickItem >= 0 && mClickItem < mDatas.size())
            return mDatas.get(mClickItem);
        return null;
    }

    // 点击日期所在item
    public void performClickItem(Calendar calendar) {
        // 获取当前日期的年月日 只需要判断年月日
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).equals(calendar)) {
                mClickItem = i;
                break;
            }
        }
        // 回掉
        if (mWeekChangeListener != null) {
            mWeekChangeListener.onWeekChangeListener(mDatas.get(mClickItem));
        }
        // 更新
        invalidate();
    }
}
