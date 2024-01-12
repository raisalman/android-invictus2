package net.invictusmanagement.invictuslifestyle.activities;

import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customCalendarView.WeekDayView;
import net.invictusmanagement.invictuslifestyle.customCalendarView.WeekHeaderView;
import net.invictusmanagement.invictuslifestyle.customCalendarView.WeekViewEvent;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.DateTimeInterpreter;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AmenitiesBookingCalenderActivity extends AppCompatActivity implements WeekDayView.MonthChangeListener,
        WeekDayView.EventClickListener, WeekDayView.EventLongPressListener, WeekDayView.EmptyViewClickListener, WeekDayView.EmptyViewLongPressListener, WeekDayView.ScrollListener {

    List<WeekViewEvent> mNewEvent = new ArrayList<>();
    private WeekDayView mWeekView;
    private WeekHeaderView mWeekHeaderView;
    private List<AmenitiesBooking> amenitiesBooking = new ArrayList<>();
    private Calendar datePicker;

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_calender_bookings);

        initViews();
        assignViews();
        toolBar();
    }

    private void initViews() {

    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(AmenitiesBookingCalenderActivity.this);
            }
        });
    }

    private void assignViews() {
        mWeekView = findViewById(R.id.weekdayview);
        mWeekHeaderView = findViewById(R.id.weekheaderview);
        /*mTv_date =(TextView)findViewById(R.id.tv_date);*/
        //init WeekView
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setScrollListener(this);
        datePicker = Calendar.getInstance();
        mWeekHeaderView.setDateSelectedChangeListener(new WeekHeaderView.DateSelectedChangeListener() {
            @Override
            public void onDateSelectedChange(Calendar oldSelectedDay, Calendar newSelectedDay) {
                mWeekView.goToDate(newSelectedDay);
                if (System.currentTimeMillis() <= newSelectedDay.getTimeInMillis()) {
                    datePicker = newSelectedDay;
                }
            }
        });

        mWeekHeaderView.setScrollListener(new WeekHeaderView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                mWeekView.goToDate(mWeekHeaderView.getSelectedDay());
                if (System.currentTimeMillis() <= mWeekHeaderView.getSelectedDay().getTimeInMillis()) {
                    datePicker = mWeekHeaderView.getSelectedDay();
                }
            }
        });

        setupDateTimeInterpreter(false);
        ProgressDialog.showProgress(this);
        WebService.getInstance().getAmenitiesBookingList(new RestCallBack<List<AmenitiesBooking>>() {
            @Override
            public void onResponse(List<AmenitiesBooking> response) {
                ProgressDialog.dismissProgress();
                if (response != null) {
                    amenitiesBooking = response;
                    mWeekView.notifyDatasetChanged();
                }
            }

            @Override
            public void onFailure(WSException wse) {
                ProgressDialog.dismissProgress();
                Toast.makeText(AmenitiesBookingCalenderActivity.this,
                        "Unable to refresh Booking list, Please try again after some time.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        final String[] weekLabels = {"S", "M", "T", "W", "T", "F", "S"};

        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat("d", Locale.getDefault());
                return format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return String.format("%02d:00", hour);

            }

            @Override
            public String interpretWeek(int date) {
                if (date > 7 || date < 1) {
                    return null;
                }
                return weekLabels[date - 1];
            }
        });
    }

    private void makeNewEvent(long id, String title, String colorHexCode,
                              Calendar starCalender, Calendar endCalender,
                              List<WeekViewEvent> events) {
        WeekViewEvent event = new WeekViewEvent(id, title, starCalender, endCalender);
        event.setColor(Color.parseColor(colorHexCode));
        events.add(event);
    }


    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<>();
        List<AmenitiesBooking> amenitiesBookings = amenitiesBooking;

        for (int i = 0; i < amenitiesBookings.size(); i++) {
            Date startDate = amenitiesBookings.get(i).bookFrom;
            Date endDate = amenitiesBookings.get(i).bookTo;
            if (newMonth == startDate.getMonth()) {
                makeNewEvent(amenitiesBookings.get(i).id,
                        amenitiesBookings.get(i).description,
                        amenitiesBookings.get(i).amenities.colorCode,
                        toCalendar(startDate), toCalendar(endDate), events);
            }
        }
        events.addAll(mNewEvent);
        return events;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

    @Override
    public void onEmptyViewClicked(Calendar time) {

    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {

    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {

    }

    @Override
    public void onSelectedDaeChange(Calendar selectedDate) {
        mWeekHeaderView.setSelectedDay(selectedDate);
        if (System.currentTimeMillis() <= selectedDate.getTimeInMillis()) {
            datePicker = selectedDate;
        }
    }
}