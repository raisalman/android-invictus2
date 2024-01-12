package net.invictusmanagement.invictuslifestyle.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import net.invictusmanagement.invictuslifestyle.adapters.PromotionsAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.interfaces.PromotionsListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.Business;
import net.invictusmanagement.invictuslifestyle.models.BusinessType;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.models.Promotion;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.util.List;

public class PromotionsActivity extends BaseActivity implements PromotionsListFragmentInteractionListener {

    public static final String EXTRA_BUSINESS_TYPE_JSON = "net.invictusmanagement.invictusmobile.business.type";
    public static final String EXTRA_BUSINESS_JSON = "net.invictusmanagement.invictusmobile.business";

    private Business _business;
    private BusinessType _businessType;
    private RecyclerView _recyclerView;
    private PromotionsAdapter _adapter;
    private ImageView _switch;
    private Boolean isFavourite = false;
    private SwipeRefreshLayout _swipeRefreshLayout;
    private TextView _feedback, _tvAll, _tvFavourites, _tvNoFavourite;
    private ConstraintLayout _clSwitch;
    private Boolean isDataAvailable = false;
    private List<Promotion> promotionList;
    private List<Business> businessList;
    private String businessName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotions);

        setToolbar();
        getBundleData();

        iniViews();
        initAdapter();
        setOnClickListeners();
        getBusinessList();
    }

    private void getBundleData() {
        if (getIntent().getExtras() != null) {
            _businessType = new Gson().fromJson(getIntent().getStringExtra(EXTRA_BUSINESS_TYPE_JSON), new TypeToken<BusinessType>() {
            }.getType());
            _business = new Gson().fromJson(getIntent().getStringExtra(EXTRA_BUSINESS_JSON), new TypeToken<Business>() {
            }.getType());
            businessName = _business.getName();

            if (!TextUtils.isEmpty(_business.getName()))
                setTitle(_business.getName() + " Coupons");
        }
    }

    private void setOnClickListeners() {
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBusinessList();
            }
        });

        _switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchVisibility(!isFavourite);
                if (isDataAvailable) {
                    _adapter.refresh(promotionList, _business, isFavourite);
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

    private void setToolbar() {
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

    private void initAdapter() {
        _recyclerView.setHasFixedSize(true);
        _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        _adapter = new PromotionsAdapter(this, this, this);
        _recyclerView.setAdapter(_adapter);
    }

    private void iniViews() {
        _clSwitch = findViewById(R.id.clSwitch);
        _feedback = findViewById(R.id.feedback);
        _tvAll = findViewById(R.id.tvAll);
        _tvFavourites = findViewById(R.id.tvFavourites);
        _tvNoFavourite = findViewById(R.id.tvNoFavourite);
        _recyclerView = findViewById(R.id.list);
        _switch = findViewById(R.id.favSwitch);

        _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
        _tvAll.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        _swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    private void switchVisibility(Boolean isUnFavourite) {
        if (isUnFavourite) {
            _switch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_active_switch));
            _tvFavourites.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTypeface(Typeface.DEFAULT);

            _tvFavourites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            _tvAll.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSwitchTextNotSelected));

            isFavourite = true;
        } else {
            _switch.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_deactive_switch));
            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvFavourites.setTypeface(Typeface.DEFAULT);

            _tvAll.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            _tvFavourites.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSwitchTextNotSelected));

            isFavourite = false;
        }

    }

    @Override
    public void onListFragmentInteraction(Promotion item, boolean isForThumbnail) {
        if (isForThumbnail) {
            MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
            if (item.getPromotionAdvertises().get(0).isImage()) {
                maintenanceRequesFiles.isImage = true;
                maintenanceRequesFiles.maintenanceRequestImageSrc =
                        _business.getAdvertiseUrl() + item.getPromotionAdvertises()
                                .get(0).getAdvertiseFileSrc() + ".jpg";
                Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
                i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
                startActivity(i);
            } else {
                maintenanceRequesFiles.isImage = false;
                maintenanceRequesFiles.maintenanceRequestImageSrc = _business.getAdvertiseUrl()
                        + item.getPromotionAdvertises().get(0).getAdvertiseFileSrc() + ".mp4";
                Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
                i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
                startActivity(i);
            }
        } else {
            Intent intent = new Intent(this, RedeemActivity.class);
            intent.putExtra(RedeemActivity.EXTRA_BUSINESS_TYPE_JSON, new Gson().toJson(_businessType));
            intent.putExtra(RedeemActivity.EXTRA_BUSINESS_JSON, new Gson().toJson(_business));
            intent.putExtra(RedeemActivity.EXTRA_PROMOTION_ITEM, new Gson().toJson(item));
            startActivity(intent);
        }

    }

    public void refresh() {
        if (_swipeRefreshLayout == null || _adapter == null)
            return;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                _swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    /*promotionList = _business.promotions;*/
                    _adapter.refresh(promotionList, _business, isFavourite);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                _adapter.notifyDataSetChanged();
                _swipeRefreshLayout.setRefreshing(false);
                setVisibility();
                if (!success)
                    Toast.makeText(PromotionsActivity.this, "Unable to refresh coupons. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void setVisibility() {
        Utilities.showHide(PromotionsActivity.this, _feedback, _adapter.totalItemCount() <= 0);
        Utilities.showHide(PromotionsActivity.this, _recyclerView, _adapter.totalItemCount() > 0);
        Utilities.showHide(PromotionsActivity.this, _clSwitch, _adapter.totalItemCount() > 0);
        isDataAvailable = _adapter.totalItemCount() > 0;
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                _tvNoFavourite.setVisibility(View.GONE);
            } else {
                _tvNoFavourite.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getBusinessList() {
        _swipeRefreshLayout.setRefreshing(true);

        WebService.getInstance().getBusiness(String.valueOf(_businessType.getId()),
                new RestCallBack<List<Business>>() {
                    @Override
                    public void onResponse(List<Business> response) {
                        _swipeRefreshLayout.setRefreshing(false);
                        if (response != null) {
                            businessList = response;
                            for (int i = 0; i < businessList.size(); i++) {
                                if (businessName.equals(businessList.get(i).getName())) {
                                    _business = businessList.get(i);
                                    promotionList = businessList.get(i).getPromotions();
                                    break;
                                }
                            }
                            refresh();
                        }
                    }

                    @Override
                    public void onFailure(WSException wse) {
                        _swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }
}
