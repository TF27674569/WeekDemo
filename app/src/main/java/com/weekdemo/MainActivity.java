package com.weekdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.week.Calendar;
import com.week.OnWeekChangeListener;
import com.week.WeekCalendar;
import com.week.WeekView;

import org.annotation.ViewById;
import org.api.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.weekView)
    public WeekView weekView;
    @ViewById(R.id.weekLayout)
    public WeekCalendar weekLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.bind(this);
        List<Calendar> calendars = new ArrayList<>();

        for (int i = 1; i < 8; i++) {

            Calendar calendar = new Calendar();
            calendar.setDay(i);
            if (i == 2) {
                calendar.setCurrentDay(true);
            }
            calendars.add(calendar);

        }
        weekView.setCalendars(calendars);

        weekLayout.setScheme(getScheme());

        weekLayout.scrollCurrentDay();
        weekLayout.addOnWeekChangeListener(new OnWeekChangeListener() {
            @Override
            public void onWeekChangeListener(Calendar calendar) {
                Toast.makeText(MainActivity.this, calendar.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Calendar> getScheme() {
        List<Calendar> calendars = new ArrayList<>();
        Calendar calendar = new Calendar();
        calendar.setYear(2018);
        calendar.setMonth(2);
        calendar.setDay(6);

        Calendar calendar1 = new Calendar();
        calendar1.setYear(2018);
        calendar1.setMonth(2);
        calendar1.setDay(3);

        calendars.add(calendar);
        calendars.add(calendar1);
        return calendars;
    }
}
