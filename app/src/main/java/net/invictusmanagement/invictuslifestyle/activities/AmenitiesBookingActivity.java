package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.AmenitiesBookingAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.AmenitiesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Amenities;
import net.invictusmanagement.invictuslifestyle.models.AmenitiesBooking;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

public class AmenitiesBookingActivity extends AppCompatActivity implements AmenitiesListFragmentInteractionListener {

    public static AmenitiesBookingAdapter amenitiesAdapter;
    SharedPreferences sharedPreferences;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private Context _context;
    private RecyclerView _recyclerView;
    private TextView _feedback;
    private List<AmenitiesBooking> amenitiesBooking = new ArrayList<>();
    private boolean isRWTAmenities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_booking);
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(AmenitiesBookingActivity.this);
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
        _feedback = findViewById(R.id.feedback);
        _recyclerView = findViewById(R.id.list);
        _recyclerView.setHasFixedSize(true);
        _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
        amenitiesAdapter = new AmenitiesBookingAdapter(AmenitiesBookingActivity.this,
                AmenitiesBookingActivity.this, isRWTAmenities);
        _recyclerView.setAdapter(amenitiesAdapter);


        _swipeRefreshLayout = findViewById(R.id.swipeRefresh);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_calendar:
                Intent intent = new Intent(this, AmenitiesBookingCalenderActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
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

        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getAmenitiesBookingList(new RestCallBack<List<AmenitiesBooking>>() {
            @Override
            public void onResponse(List<AmenitiesBooking> response) {
                _swipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    amenitiesBooking = response;
                    amenitiesAdapter.refresh(amenitiesBooking);
                    amenitiesAdapter.notifyDataSetChanged();

                    Utilities.showHide(_context, _feedback, false);
                    Utilities.showHide(_context, _recyclerView, true);

                } else {
                    _feedback.setText("No amenities available.\nSwipe down to refresh.");
                    Utilities.showHide(_context, _feedback, true);
                    Utilities.showHide(_context, _recyclerView, false);
                }
            }

            @Override
            public void onFailure(WSException wse) {
                _swipeRefreshLayout.setRefreshing(false);
                _feedback.setText("No amenities available.\nSwipe down to refresh.");
                Utilities.showHide(_context, _feedback, true);
                Utilities.showHide(_context, _recyclerView, false);
            }
        });
    }

    public void callAPIApproveRejectReq(AmenitiesBooking item) {
        WebService.getInstance().updateAmenitiesStatus(item.id, !item.isApproved,
                new RestCallBack<ResponseBody>() {
                    @Override
                    public void onResponse(ResponseBody response) {
                        Toast.makeText(AmenitiesBookingActivity.this,
                                "Amenities Status Updated Successfully",
                                Toast.LENGTH_SHORT).show();
                        refresh();
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        Toast.makeText(AmenitiesBookingActivity.this,
                                "Failed to update Amenities Status",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onListFragmentInteraction(Amenities item) {
        Intent intent = new Intent(this, AmenitiesCalenderActivity.class);
        intent.putExtra(AmenitiesCalenderActivity.AMENITIES_JSON, new Gson().toJson(item));
//        intent.putExtra(AmenitiesCalenderActivity.AMENITIES_JSONLIST, new Gson().toJson(amenitiesList));
        startActivity(intent);
    }
}