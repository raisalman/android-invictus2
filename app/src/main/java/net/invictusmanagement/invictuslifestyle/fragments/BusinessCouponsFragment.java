package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.FavouriteRedeemActivity;
import net.invictusmanagement.invictuslifestyle.activities.SingleVideoImageActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.adapters.PromotionsFragmentAdapter;
import net.invictusmanagement.invictuslifestyle.customviews.SimpleDividerItemDecoration;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.PromotionsFavListFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.MaintenanceRequesFiles;
import net.invictusmanagement.invictuslifestyle.models.PromotionFav;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

public class BusinessCouponsFragment extends Fragment implements PromotionsFavListFragmentInteractionListener, IRefreshableFragment {

    public static RecyclerView _recyclerView;
    public static PromotionsFragmentAdapter _adapter;
    public static TextView _tvNoFavourite;
    public static SwipeRefreshLayout _swipeRefreshLayout;
    public static Context _context;


    public BusinessCouponsFragment() {
    }

    @SuppressWarnings("unused")
    public static BusinessCouponsFragment newInstance() {
        return new BusinessCouponsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_coupons, viewGroup, false);

        _tvNoFavourite = view.findViewById(R.id.tvNoFavourite);

        _recyclerView = view.findViewById(R.id.couponsList);
        _recyclerView.setHasFixedSize(true);

        _recyclerView.addItemDecoration(new SimpleDividerItemDecoration(_context));
        _adapter = new PromotionsFragmentAdapter(_context, this, this);
        _recyclerView.setAdapter(_adapter);

        _swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        _swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BusinessTypesFragment.newInstance().getLoyaltyPoint();
                refresh();
            }
        });
        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
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
                    _adapter.refresh(MobileDataProvider.getInstance().getFavCoupons(), true);
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
                    Toast.makeText(getActivity(), "Unable to refresh coupons. Please try again later.", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }

    private void setVisibility() {
        Utilities.showHide(_context, _tvNoFavourite, _adapter.totalItemCount() <= 0);
        Utilities.showHide(_context, _recyclerView, _adapter.totalItemCount() > 0);
        if (_adapter.totalItemCount() > 0) {
            if (_adapter.getItemCount() > 0) {
                _tvNoFavourite.setVisibility(View.GONE);
            } else {
                _tvNoFavourite.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onListFragmentInteraction(PromotionFav item, boolean isForThumbnail) {
        if (isForThumbnail) {
            MaintenanceRequesFiles maintenanceRequesFiles = new MaintenanceRequesFiles();
            if (item.promotionAdvertises.get(0).isImage()) {
                maintenanceRequesFiles.isImage = true;
                maintenanceRequesFiles.maintenanceRequestImageSrc = item.promotionAdvertises
                        .get(0).getAdvertiseFileSrc() + ".jpg";
                Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
                i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
                startActivity(i);
            } else {
                maintenanceRequesFiles.isImage = false;
                maintenanceRequesFiles.maintenanceRequestImageSrc = item.promotionAdvertises
                        .get(0).getAdvertiseFileSrc() + ".mp4";
                Intent i = new Intent(TabbedActivity.tabbedActivity, SingleVideoImageActivity.class);
                i.putExtra("files", new Gson().toJson(maintenanceRequesFiles));
                startActivity(i);
            }
        } else {
            Intent intent = new Intent(getContext(), FavouriteRedeemActivity.class);
            intent.putExtra(FavouriteRedeemActivity.EXTRA_PROMOTION_ITEM, new Gson().toJson(item));
            startActivity(intent);
        }
    }

}