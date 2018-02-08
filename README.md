# 周日历控件
## 效果
![](https://github.com/TF27674569/WeekDemo/blob/master/app/gif/demo.gif)

### 介绍
1.设置时间段自动补满不够一周的天数
```java
  weekCalendar.setRange(2018,2,6,2018,2,25);
```
这里会自动补齐到 2018-2-4 到 2018-3-3 （4周）</br>
超过边界不可滑动</br></br>

2.移动到某一天
```java
   //移动到当天 
   weekCalendar.scrollCurrentDay();
   // 移动到指定日期 注意，如果超过给定范围会异常
   weekCalendar.scrollToCalendar(year, month, day_of_month);
```
</br></br>
3.设置某一天的事件
```java
 // 传入需要添加事件的日期
 weekCalendar.setScheme(List<Calendar>);
```
</br></br>
3.监听日期变化
```java
  weekCalendar.addOnWeekChangeListener(new OnWeekChangeListener() {
            @Override
            public void onWeekChangeListener(Calendar calendar) {
                // 回调日期对象
                Toast.makeText(MainActivity.this, calendar.toString(), Toast.LENGTH_SHORT).show();
            }
        });
```
