package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.AmenitiesAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.AmenitiesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Amenities;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.util.ArrayList;
import java.util.List;

public class AmenitiesActivity extends AppCompatActivity implements AmenitiesListFragmentInteractionListener {

    public static AmenitiesAdapter amenitiesAdapter;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private RecyclerView _recyclerView;
    private TextView _feedback;
    private List<Amenities> amenitiesList = new ArrayList<>();
    private List<AmenitiesBooking> amenitiesBooking = new ArrayList<>();
    private boolean isRWTAmenities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities);
        toolBar();
        initControls();
    }

    private void initControls() {
        initViews();
    }

    private void initViews() {
        _context = this;
        isRWTAmenities = sharedPreferences.getBoolean("isRWTAmenities", true);
        MobileDataProvider.isAmenitiesEnable = false;
        _feedback = (TextView) findViewById(R.id.feedback);
        _recyclerView = (RecyclerView) findViewById(R.id.list);
        _recyclerView.setHasFixedSize(true);
        _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
        amenitiesAdapter = new AmenitiesAdapter(AmenitiesActivity.this, AmenitiesActivity.this, isRWTAmenities);
        _recyclerView.setAdapter(amenitiesAdapter);


        _swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.Amenities.value());
                refresh();
            }
        });
        refresh();

    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void refresh() {
        if (_swipeRefreshLayout == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    amenitiesList = MobileDataProvider.getInstance().getAmenitiesList();
                    /*amenitiesBooking = MobileDataProvider.getInstance().getAmenitiesBookingList();*/
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }

            }

            @Override
            protected void onPostExecute(Boolean success) {
                amenitiesAdapter.refresh(amenitiesList);
                amenitiesAdapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
                if (!success) {
                    _feedback.setText(MobileDataProvider.amenitiesString);
                    Utilities.showHide(_context, _feedback, true);
                    Utilities.showHide(_context, _recyclerView, false);
                } else {
                    _feedback.setText("No amenities available.\nSwipe down to refresh.");
                    Utilities.showHide(AmenitiesActivity.this, _recyclerView, amenitiesAdapter.getItemCount() > 0);
                    Utilities.showHide(AmenitiesActivity.this, _feedback, amenitiesAdapter.getItemCount() <= 0);
                }
                // Toast.makeText(getActivity(), "Unable to refresh content. Please try again later.", Toast.LENGTH_LONG).show();

            }
        }.execute();
    }

    @Override
    public void onListFragmentInteraction(Amenities item) {
        Intent intent = new Intent(this, AmenitiesCalenderActivity.class);
        intent.putExtra(AmenitiesCalenderActivity.AMENITIES_JSON, new Gson().toJson(item));
        intent.putExtra(AmenitiesCalenderActivity.AMENITIES_JSONLIST, new Gson().toJson(amenitiesList));
        startActivity(intent);
    }
}