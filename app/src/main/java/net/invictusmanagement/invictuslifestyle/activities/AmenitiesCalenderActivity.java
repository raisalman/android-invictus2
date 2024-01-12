package net.invictusmanagement.invictuslifestyle.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customCalendarView.WeekDayView;
import net.invictusmanagement.invictuslifestyle.customCalendarView.WeekHeaderView;
import net.invictusmanagement.invictuslifestyle.customCalendarView.WeekViewEvent;
import net.invictusmanagement.invictuslifestyle.customviews.ProgressDialog;
import net.invictusmanagement.invictuslifestyle.interfaces.DateTimeInterpreter;
import net.invictusmanagement.invictuslifestyle.models.Amenities;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;
import net.invictusmanagement.invictuslifestyle.models.BookAmenity;
import net.invictusmanagement.invictuslifestyle.models.CheckAvailBookAmenity;
import net.invictusmanagement.invictuslifestyle.models.CheckAvailBookAmenityResponse;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AmenitiesCalenderActivity extends AppCompatActivity implements WeekDayView.MonthChangeListener,
        WeekDayView.EventClickListener, WeekDayView.EventLongPressListener, WeekDayView.EmptyViewClickListener, WeekDayView.EmptyViewLongPressListener, WeekDayView.ScrollListener {

    public static final String AMENITIES_JSON = "net.invictusmanagement.invictusmobile.amenities";
    public static final String AMENITIES_JSONLIST = "net.invictusmanagement.invictusmobile.amenities.list";
    List<WeekViewEvent> mNewEvent = new ArrayList<>();
    ArrayAdapter<String> amenitiesAdapter;
    ArrayList<String> amenitiesDisplayNameList = new ArrayList<>();
    private Amenities amenities;
    private Amenities originalAmenities;
    private int amenityId;
    private WeekDayView mWeekView;
    private WeekHeaderView mWeekHeaderView;
    private TextView tvMaxHours;
    private List<AmenitiesBooking> amenitiesBooking = new ArrayList<>();
    //    private FloatingActionButton fab;
    private RelativeLayout rlCreate;
    private EditText from_date, from_time, to_time, edDescription, edMaxPerson;
    private Calendar _toDateTime, _fromDateTime, _fromDateOnly, headerSelectedDate,
            datePicker, setTimeFrom, setTimeTo;
    private List<Amenities> amenitiesList = new ArrayList<>();
    private String maxHour, maxMin, colorHexCode;
    private Boolean isSameDay = true;
    private Boolean isBookingAvailable;
    private Boolean isAmenityActive;
    private CheckAvailBookAmenityResponse checkAvailBookAmenityResponse;
    private int maxBookingCount;
    private int availabilityBookingCount;
    private int advanceBookingDays;

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_calender);

        initViews();
        assignViews();
        toolBar();
        getBundleData();
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Utilities.hideKeyboard(AmenitiesCalenderActivity.this);
            }
        });
    }

    private void assignViews() {
//        fab = findViewById(R.id.fab);

        rlCreate = findViewById(R.id.rlCreate);
        rlCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.addHaptic(v);
                openDialogToBooking();
            }
        });

        mWeekView = findViewById(R.id.weekdayview);
        mWeekHeaderView = findViewById(R.id.weekheaderview);
        /*mTv_date =(TextView)findViewById(R.id.tv_date);*/
        //init WeekView
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setScrollListener(this);
        datePicker = Calendar.getInstance();
        headerSelectedDate = Calendar.getInstance();
        mWeekHeaderView.setDateSelectedChangeListener(new WeekHeaderView.DateSelectedChangeListener() {
            @Override
            public void onDateSelectedChange(Calendar oldSelectedDay, Calendar newSelectedDay) {
                mWeekView.goToDate(newSelectedDay);
                if (System.currentTimeMillis() <= newSelectedDay.getTimeInMillis()) {
                    datePicker = newSelectedDay;
                    headerSelectedDate = newSelectedDay;
                    isSameDay = datePicker.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
                            datePicker.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
                }
            }
        });
        mWeekHeaderView.setScrollListener(new WeekHeaderView.ScrollListener() {
            @Override
            public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {
                mWeekView.goToDate(mWeekHeaderView.getSelectedDay());
                if (System.currentTimeMillis() <= mWeekHeaderView.getSelectedDay().getTimeInMillis()) {
                    datePicker = mWeekHeaderView.getSelectedDay();
                    headerSelectedDate = mWeekHeaderView.getSelectedDay();
                    isSameDay = datePicker.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
                            datePicker.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
                }
            }
        });
        setupDateTimeInterpreter(false);

    }

    @SuppressLint("SetTextI18n")
    private void openDialogToBooking() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AmenitiesCalenderActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_amenities_book, null);
        builder.setView(view);

        final AlertDialog show = builder.setCancelable(true).show();


        setTimeFrom = Calendar.getInstance();
        setTimeTo = getCalendarModDate(Calendar.getInstance());

        _fromDateTime = getCalendarModDate(Calendar.getInstance());
        _toDateTime = getCalendarModDate(getCalendarModDate(Calendar.getInstance()));
        _toDateTime.add(Calendar.DATE, 1);

        _fromDateOnly = getCalendarModDate(getCalendarModDate(Calendar.getInstance()));

        tvMaxHours = view.findViewById(R.id.tvMaxHours);
        from_date = view.findViewById(R.id.edDate);
        from_time = view.findViewById(R.id.edFromTime);
        to_time = view.findViewById(R.id.edToTime);

        from_date.setInputType(InputType.TYPE_NULL);
        /*from_date.setText(dateFormat(datePicker.getTime()));*/
        from_time.setInputType(InputType.TYPE_NULL);
        from_time.setText(timeFormat(_fromDateTime.getTime()));
        to_time.setInputType(InputType.TYPE_NULL);
        to_time.setText(timeFormat(_toDateTime.getTime()));

        ((Spinner) view.findViewById(R.id.spnAmenitiesType)).setAdapter(amenitiesAdapter);
        for (int i = 0; i < amenitiesDisplayNameList.size(); i++) {
            if (amenitiesDisplayNameList.get(i).equals(amenities.displayName + " (" + amenities.amenitiesType.name + ")")) {
                ((Spinner) view.findViewById(R.id.spnAmenitiesType)).setSelection(i);
                break;
            }
        }
        tvMaxHours.setText("You can book maximum " + maxHour + "." + maxMin + " hours for this amenity");

        ((Spinner) view.findViewById(R.id.spnAmenitiesType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String selectedText = ((Spinner) view.findViewById(R.id.spnAmenitiesType)).getSelectedItem().toString();
                for (int i = 0; i < amenitiesList.size(); i++) {
                    if (selectedText.equals(amenitiesList.get(i).displayName + " (" + amenitiesList.get(i).amenitiesType.name + ")")) {
                        amenities = amenitiesList.get(i);
                        getMaxHourMin();
                        advanceBookingDays = Integer.parseInt(amenities.advanceBookingDays);

                        long differenceDates = daysBetweenDays();
                        if (differenceDates == 0) {
                            isSameDay = true;
                            datePicker = Calendar.getInstance();
                        } else {
                            if (advanceBookingDays != -1) {
                                if (advanceBookingDays > differenceDates) {
                                    isSameDay = false;
                                    datePicker = headerSelectedDate;
                                } else {
                                    isSameDay = true;
                                    datePicker = Calendar.getInstance();
                                }
                            } else {
                                isSameDay = false;
                                datePicker = headerSelectedDate;
                            }

                        }

                        from_date.setText(dateFormat(datePicker.getTime()));

                        _fromDateTime = getCalendarModDate(Calendar.getInstance());
                        _toDateTime = getCalendarModDate(getCalendarModDate(Calendar.getInstance()));
                        _toDateTime.add(Calendar.DATE, 1);

                        from_time.setText(timeFormat(_fromDateTime.getTime()));
                        to_time.setText(timeFormat(_toDateTime.getTime()));

                        tvMaxHours.setText("You can book maximum " + maxHour + "." + maxMin + " hours for this amenity");

                        if (edMaxPerson.getText().toString().length() > 0) {
                            checkAvailability(amenities);
                        }
                        break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        edDescription = view.findViewById(R.id.edDescription);
        edMaxPerson = view.findViewById(R.id.edMaxPerson);

        edMaxPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().startsWith("0")) {
                    edMaxPerson.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (edMaxPerson.getText().toString().length() > 0) {
                    checkAvailability(amenities);
                }

            }
        });


        edDescription.setText("");
        edMaxPerson.setText("");

        from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                Date date = null;
                try {
                    date = sdf.parse(from_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                setTimeFrom = cal;
                openTimePicker(true);
            }
        });
        to_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                Date date = null;
                try {
                    date = sdf.parse(from_time.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                _fromDateTime = cal;
                openTimePicker(false);
            }
        });
        view.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //submit form
                Utilities.addHaptic(v);
                checkValidation(show);
            }
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss dialog
                show.dismiss();
            }
        });


    }

    private long daysBetweenDays() {
        String CurrentDate = dateFormat(Calendar.getInstance().getTime());
        String FinalDate = dateFormat(headerSelectedDate.getTime());
        Date date1 = new Date();
        Date date2 = new Date();
        SimpleDateFormat dates = new SimpleDateFormat("dd/mm/yy");
        try {
            date1 = dates.parse(CurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            date2 = dates.parse(FinalDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = Math.abs(date1.getTime() - date2.getTime());
        return difference / (24 * 60 * 60 * 1000);
    }

    @NotNull
    private Calendar getCalendarModDate(Calendar calendar) {
        int unboundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unboundedMinutes % 15;
        calendar.add(Calendar.MINUTE, (15 - mod));
        return calendar;
    }

    private void initViews() {
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            amenities = new Gson().fromJson(getIntent().getStringExtra(AMENITIES_JSON), new TypeToken<Amenities>() {
            }.getType());
            amenitiesList = new Gson().fromJson(getIntent().getStringExtra(AMENITIES_JSONLIST), new TypeToken<List<Amenities>>() {
            }.getType());

            originalAmenities = amenities;
            amenityId = Integer.parseInt(String.valueOf(amenities.id));
            colorHexCode = amenities.colorCode;
            advanceBookingDays = Integer.parseInt(amenities.advanceBookingDays);
            if (amenities.isActive) {
                rlCreate.setVisibility(View.VISIBLE);
            }
            getMaxHourMin();
            getAmenitiesBookingList(amenities, false);
        }
    }

    private void getMaxHourMin() {
        if ((amenities != null ? amenities.maxBookingHours : "") != null) {
            if (amenities != null && amenities.maxBookingHours.length() > 0) {
                if (amenities.maxBookingHours.contains(".")) {
                    maxHour = amenities.maxBookingHours.split("\\.")[0];
                    maxMin = amenities.maxBookingHours.split("\\.")[1];
                }
            }

        }
    }

    private void getAmenitiesList() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*ProgressDialog.showProgress(AmenitiesCalenderActivity.this);*/
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    amenitiesList = MobileDataProvider.getInstance().getAmenitiesList();
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                ProgressDialog.dismissProgress();
                amenitiesDisplayNameList = new ArrayList<>();
                for (int i = 0; i < amenitiesList.size(); i++) {
                    if (amenitiesList.get(i).isActive) {
                        amenitiesDisplayNameList.add(amenitiesList.get(i).displayName + " (" + amenitiesList.get(i).amenitiesType.name + ")");
                    }
                }
                amenitiesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, amenitiesDisplayNameList);
                // Toast.makeText(getActivity(), "Unable to refresh content. Please try again later.", Toast.LENGTH_LONG).show();

            }
        }.execute();
    }

    private void getAminitesListFromBundle() {
        amenitiesDisplayNameList = new ArrayList<>();
        for (int i = 0; i < amenitiesList.size(); i++) {
            if (amenitiesList.get(i).isActive) {
                amenitiesDisplayNameList.add(amenitiesList.get(i).displayName + " (" + amenitiesList.get(i).amenitiesType.name + ")");
            }
        }
        amenitiesAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, amenitiesDisplayNameList);
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

    private Date convertToDateTime(String input) {
        //key.toUtc = convertToDateTime(_toDateEditText.getText() + " " + _toTimeEditText.getText());
        Date result = null;
        try {
            result = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).parse(input);
        } catch (ParseException ex) {
            Log.e(Utilities.TAG, Log.getStackTraceString(ex));
        }
        return result;
    }

    private void checkValidation(AlertDialog show) {
        boolean cancel = false;
        View focusView = null;

        edDescription.setError(null);
        edMaxPerson.setError(null);


        if (TextUtils.isEmpty(edDescription.getText().toString())) {
            edDescription.setError(getString(R.string.error_field_required));
            focusView = edDescription;
            cancel = true;
        } else if (TextUtils.isEmpty(edMaxPerson.getText().toString())) {
            edMaxPerson.setError(getString(R.string.error_field_required));
            focusView = edMaxPerson;
            cancel = true;
        } else if (edMaxPerson.getText().toString().equals("0")) {
            edMaxPerson.setError("This field can not be zero");
            focusView = edMaxPerson;
            cancel = true;
        } else if (!isBookingAvailable) {
            cancel = true;
            Toast.makeText(getApplicationContext(), "No booking available. for this time", Toast.LENGTH_LONG).show();
        } else if (Integer.parseInt(edMaxPerson.getText().toString()) > availabilityBookingCount) {
            edMaxPerson.setError("Max Person count " + availabilityBookingCount);
            focusView = edMaxPerson;
            cancel = true;
        }

        if (!cancel) {

            String date = SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(datePicker.getTime());
            String fromTime = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(timeFormat(from_time.getText().toString()));
            String toTime = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(timeFormat(to_time.getText().toString()));

            BookAmenity bookAmenity = new BookAmenity();
            bookAmenity.amenitiesId = Integer.parseInt(String.valueOf(amenities.id));
            bookAmenity.description = edDescription.getText().toString().trim();
            bookAmenity.bookingPersonCount = Integer.parseInt(edMaxPerson.getText().toString());
            bookAmenity.bookFrom = convertToDateTime(date + " " + fromTime);
            bookAmenity.bookTo = convertToDateTime(date + " " + toTime);

            bookAmenity(bookAmenity, show);


        } else {
            if (focusView != null) {
                focusView.requestFocus();
            }
        }
    }

    private void getAmenitiesBookingList(Amenities amenities, Boolean isAfterAdd) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                if (!isAfterAdd)
                    ProgressDialog.showProgress(AmenitiesCalenderActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().getAmenitiesBookingList(amenities.id, AmenitiesCalenderActivity.this);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @SuppressLint("NewApi")
            @Override
            protected void onPostExecute(Boolean success) {
                /*ProgressDialog.dismissProgress();*/
                if (success) {
                    if (isAfterAdd) {
                        ProgressDialog.dismissProgress();
                    } else {
                        //getAmenitiesList();
                        getAminitesListFromBundle();
                        ProgressDialog.dismissProgress();
                    }
                    mWeekView.notifyDatasetChanged();

                } else {
                    ProgressDialog.dismissProgress();
                }
                // Toast.makeText(getActivity(), "Unable to refresh content. Please try again later.", Toast.LENGTH_LONG).show();

            }
        }.execute();
    }

    private void checkAvailability(Amenities amenities) {

        String date = SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(datePicker.getTime());
        String fromTime = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(timeFormat(from_time.getText().toString()));
        String toTime = SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(timeFormat(to_time.getText().toString()));

        CheckAvailBookAmenity checkAvailBookAmenity = new CheckAvailBookAmenity();
        checkAvailBookAmenity.amenitiesId = Integer.parseInt(String.valueOf(amenities.id));
        checkAvailBookAmenity.bookFrom = convertToDateTime(date + " " + fromTime);
        checkAvailBookAmenity.bookTo = convertToDateTime(date + " " + toTime);
        checkAvailBookAmenity.description = edDescription.getText().toString().length() > 0 ?
                edDescription.getText().toString() : "";

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                /*ProgressDialog.showProgress(AmenitiesCalenderActivity.this);*/
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().checkAvailiblity(checkAvailBookAmenity, AmenitiesCalenderActivity.this);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                Log.d("CheckAvailability-- ", success.toString());
                if (success) {
                    maxBookingCount = checkAvailBookAmenityResponse.bookingCapacity;
                    availabilityBookingCount = checkAvailBookAmenityResponse.bookingCapacity - checkAvailBookAmenityResponse.totalBookingCount;
                    if (isBookingAvailable) {
                        if (edMaxPerson.getText().toString().length() > 0) {
                            if (Integer.parseInt(edMaxPerson.getText().toString()) > (checkAvailBookAmenityResponse.bookingCapacity - checkAvailBookAmenityResponse.totalBookingCount)) {
                                View view = edMaxPerson;
                                view.requestFocus();
                                edMaxPerson.setError("Max Person count " + availabilityBookingCount);
                            }
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No booking available. for this time", Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), "No booking available. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
    }

    private void bookAmenity(BookAmenity bookAmenity, AlertDialog show) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                ProgressDialog.showProgress(AmenitiesCalenderActivity.this);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().bookAmenity(bookAmenity, AmenitiesCalenderActivity.this);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    show.dismiss();
                    //getAmenitiesBookingList(amenities, true);
                    getAmenitiesBookingList(originalAmenities, true);
                } else {
                    ProgressDialog.dismissProgress();
                    Toast.makeText(getApplicationContext(), "Unable to book amenity. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
    }

    private void openTimePicker(boolean isForFrom) {

        if (isForFrom) {
            Utilities.showTimePickerTo(isSameDay, getSupportFragmentManager(), AmenitiesCalenderActivity.this, getCalendarModDate(Calendar.getInstance()), setTimeFrom, new Utilities.onDateTimePickerChangedListener() {
                @Override
                public void dateTimeChanged(Calendar date) {
                    setTimeFrom = date;

                    from_time.setText(timeFormat(date.getTime()));

                    _toDateTime = date;
                    _toDateTime.add(Calendar.DATE, 1);
                    Calendar toDate = getCalendarModDate(_toDateTime);
                    if (toDate.get(Calendar.HOUR_OF_DAY) == 0) {
                        toDate.set(0, 0, 0, 23, 59);
                    }
                    to_time.setText(timeFormat(toDate.getTime()));
                    if (edMaxPerson.getText().toString().length() > 0) {
                        checkAvailability(amenities);
                    }
                    _fromDateOnly = date;
                    setTimeTo = toDate;
                }
            });
        } else {
            Utilities.showTimePickerFromWithMaxHour(getSupportFragmentManager(), _fromDateOnly, _fromDateTime, setTimeTo, maxHour, maxMin, new Utilities.onDateTimePickerChangedListener() {
                @Override
                public void dateTimeChanged(Calendar date) {
                    setTimeTo = date;
                    _toDateTime = date;
                    to_time.setText(timeFormat(date.getTime()));

                    if (edMaxPerson.getText().toString().length() > 0) {
                        checkAvailability(amenities);
                    }
                }
            });
        }

    }

    private void openDatePicker() {

        Utilities.showDatePickerWithMinMaxDate(advanceBookingDays, AmenitiesCalenderActivity.this, getSupportFragmentManager(), datePicker, new Utilities.onDateTimePickerChangedListener() {
            @Override
            public void dateTimeChanged(Calendar date) {
                datePicker = date;
                isSameDay = date.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
                        date.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
                from_date.setText(dateFormat(date.getTime()));
                if (edMaxPerson.getText().toString().length() > 0) {
                    checkAvailability(amenities);
                }

            }
        });
    }

    private String dateFormat(Date calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);*/
        return sdf.format(calendar.getTime());
    }

    private String timeFormat(Date calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return sdf.format(calendar.getTime());
    }

    private Date timeFormat(String dtStart) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        try {
            return format.parse(dtStart);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public void getAmenitiesBookingListResponse(String successModel) {
        amenitiesBooking = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create().fromJson(successModel, new TypeToken<List<AmenitiesBooking>>() {
        }.getType());

    }

    public void responseBookAmenity(String string, boolean isSuccess) {
        ProgressDialog.dismissProgress();
        runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(AmenitiesCalenderActivity.this)
                        .setCancelable(false)
                        .setMessage(string)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                // dismiss dialog
                            }
                        }).create().show();
            }
        });

    }

    public void checkAvail(String string) {
        checkAvailBookAmenityResponse = new Gson().fromJson(string, new TypeToken<CheckAvailBookAmenityResponse>() {
        }.getType());

        isBookingAvailable = checkAvailBookAmenityResponse.isBookingAvailable;
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        List<AmenitiesBooking> amenitiesBookings = amenitiesBooking;

        for (int i = 0; i < amenitiesBookings.size(); i++) {
            Date startDate = amenitiesBookings.get(i).bookFrom;
            Date endDate = amenitiesBookings.get(i).bookTo;
            if (newMonth == startDate.getMonth()) {
                makeNewEvent(amenitiesBookings.get(i).id, amenitiesBookings.get(i).description, toCalendar(startDate), toCalendar(endDate), events);
            }

        }


        events.addAll(mNewEvent);
        return events;
    }

    private void makeNewEvent(long id, String title, Calendar starCalender, Calendar endCalender, List<WeekViewEvent> events) {
        WeekViewEvent event = new WeekViewEvent(id, title, starCalender, endCalender);
        event.setColor(Color.parseColor(colorHexCode));
        events.add(event);

    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        /*Toast.makeText(AmenitiesCalenderActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        /*Toast.makeText(AmenitiesCalenderActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        /*Toast.makeText(AmenitiesCalenderActivity.this, "Empty View clicked " + time.get(Calendar.YEAR) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        /*Toast.makeText(AmenitiesCalenderActivity.this, "Empty View long  clicked " + time.get(Calendar.YEAR) + "/" + time.get(Calendar.MONTH) + "/" + time.get(Calendar.DAY_OF_MONTH), Toast.LENGTH_LONG).show();*/

    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {

    }

    @Override
    public void onSelectedDaeChange(Calendar selectedDate) {
        mWeekHeaderView.setSelectedDay(selectedDate);
        if (System.currentTimeMillis() <= selectedDate.getTimeInMillis()) {
            datePicker = selectedDate;
            headerSelectedDate = selectedDate;
            isSameDay = datePicker.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
                    datePicker.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR);
        }
    }

    private class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                return formatter.parse(date);
            } catch (ParseException ex) {
                Log.e(Utilities.TAG, "JsonDeserializer<Date>() failed", ex);
                return null;
            }
        }
    }
}