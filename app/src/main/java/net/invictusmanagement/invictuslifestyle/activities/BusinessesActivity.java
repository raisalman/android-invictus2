package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.adapters.BusinessesAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.interfaces.BusinessesListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Business;
import net.invictusmanagement.invictuslifestyle.models.BusinessType;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class BusinessesActivity extends BaseActivity implements BusinessesListFragmentInteractionListener {

    private BusinessType _businessType;
    private RecyclerView _recyclerView;
    private BusinessesAdapter _adapter;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private TextView _feedback;
    private ConstraintLayout _clSwitch;
    private Boolean isDataAvailable = false;
    private Boolean isAnytime = false;
    private ImageView _switch;
    private TextView _tvAll, _tvFavourites, _tvNoFavourite;
    private List<Business> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businesses);

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

        if (getIntent().getExtras() != null) {
            _businessType = new Gson().fromJson(getIntent().getStringExtra(Utilities.EXTRA_BUSINESS_TYPE_JSON), new TypeToken<BusinessType>() {
            }.getType());

            if (!TextUtils.isEmpty(_businessType.getName()))
                setTitle(_businessType.getName());
        }

        _clSwitch = findViewById(R.id.clSwitch);
        _tvAll = findViewById(R.id.tvAll);
        _tvFavourites = findViewById(R.id.tvFavourites);
        _tvNoFavourite = findViewById(R.id.tvNoFavourite);
        _switch = findViewById(R.id.favSwitch);

        _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
        _tvAll.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        _feedback = findViewById(R.id.feedback);
        _recyclerView = findViewById(R.id.list);
        _recyclerView.setHasFixedSize(true);
        _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        _adapter = new BusinessesAdapter(this);
        _recyclerView.setAdapter(_adapter);

        _swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        setOnClickListeners();
        refresh();
    }

    private void switchVisibility(Boolean isUnFavourite) {
        if (isUnFavourite) {
            _switch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_active_switch));
            _tvFavourites.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTypeface(Typeface.DEFAULT);

            _tvFavourites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            _tvAll.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSwitchTextNotSelected));

            isAnytime = true;
        } else {
            _switch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_deactive_switch));
            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvFavourites.setTypeface(Typeface.DEFAULT);

            _tvAll.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            _tvFavourites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSwitchTextNotSelected));

            isAnytime = false;
        }

    }

    private void setOnClickListeners() {
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        _switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchVisibility(!isAnytime);
                if (isDataAvailable) {
                    _adapter.refresh(list, isAnytime);
                    _adapter.notifyDataSetChanged();
                    if (_adapter.getItemCount() > 0) {
                        _tvNoFavourite.setVisibility(View.GONE);
                    } else {
                        _tvNoFavourite.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }

    public void onListFragmentInteraction(final Business item) {
        Intent intent = new Intent(this, PromotionsActivity.class);
        intent.putExtra(PromotionsActivity.EXTRA_BUSINESS_TYPE_JSON, new Gson().toJson(_businessType));
        intent.putExtra(PromotionsActivity.EXTRA_BUSINESS_JSON, new Gson().toJson(item));
        startActivity(intent);
    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        _swipeRefreshLayout.setRefreshing(true);
        WebService.getInstance().getBusiness(String.valueOf(_businessType.getId()),
                new RestCallBack<List<Business>>() {
                    @Override
                    public void onResponse(List<Business> response) {
                        _swipeRefreshLayout.setRefreshing(false);
                        if (response != null) {
                            list = response;
                            _adapter.refresh(list, isAnytime);
                            _adapter.notifyDataSetChanged();
                            _swipeRefreshLayout.setRefreshing(false);
                            setVisibility();
                        } else {
                            Toast.makeText(BusinessesActivity.this,
                                    getString(R.string.error_load_business), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        _swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(BusinessesActivity.this,
                                getString(R.string.error_load_business), Toast.LENGTH_LONG).show();
                    }
                });


//        new AsyncTask<Void, Void, Boolean>() {
//
//            @Override
//            protected void onPreExecute() {
//                _swipeRefreshLayout.setRefreshing(true);
//            }
//
//            @Override
//            protected Boolean doInBackground(Void... args) {
//                try {
//                    list = MobileDataProvider.getInstance().getBusinesses(_businessType);
//                    return true;
//                } catch (Exception ex) {
//                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
//                    return false;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Boolean success) {
//                _adapter.refresh(list, isAnytime);
//                _adapter.notifyDataSetChanged();
//                _swipeRefreshLayout.setRefreshing(false);
//                setVisibility();
//                if (!success)
//
//            }
//        }.execute();
    }

    private void setVisibility() {
        Utilities.showHide(BusinessesActivity.this, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(BusinessesActivity.this, _recyclerView, _adapter.totalItemCount() > 0);
        Utilities.showHide(BusinessesActivity.this, _clSwitch, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                _tvNoFavourite.setVisibility(View.GONE);
            } else {
                _tvNoFavourite.setVisibility(View.VISIBLE);
            }
        }
    }
}
