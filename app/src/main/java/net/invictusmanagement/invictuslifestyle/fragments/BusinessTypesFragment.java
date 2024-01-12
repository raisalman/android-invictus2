package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

public class BusinessTypesFragment extends Fragment implements IRefreshableFragment {

    public ViewPager viewpager;
    public int _currentTabPosition = 0;
    public static Double totalLoyaltyPoint;
    public static TextView _tvLoyaltyPoints;

    public BusinessTypesFragment() {
    }

    @SuppressWarnings("unused")
    public static BusinessTypesFragment newInstance() {
        return new BusinessTypesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_business_types_list, container, false);
        if (view instanceof ConstraintLayout) {

            _tvLoyaltyPoints = view.findViewById(R.id.tvLoyaltyPoints);
            TabLayout _tabLayout = view.findViewById(R.id.tabs);
            _tabLayout.addTab(_tabLayout.newTab().setText("All"));
            _tabLayout.addTab(_tabLayout.newTab().setText("Favorite"));
            _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


            viewpager = view.findViewById(R.id.container);
            TabsAdapter couponsPagerAdapter = new TabsAdapter(getChildFragmentManager(), _tabLayout.getTabCount());

            viewpager.setAdapter(couponsPagerAdapter);
            viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(_tabLayout));
            /*_swipeRefreshLayout = (SwipeRefreshLayout) view;*/
            /*_swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(_context, R.color.colorPrimary));*/
            _tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    _currentTabPosition = tab.getPosition();
                    viewpager.setCurrentItem(_currentTabPosition);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            TextView _feedback = view.findViewById(R.id.feedback);
            getLoyaltyPoint();
        }
        return view;
    }

    public void refresh() {
        /*getLoyaltyPointList();*/
        getLoyaltyPoint();
        if (viewpager.getCurrentItem() == 0) {
            Fragment fragment = AllCouponsFragment.newInstance();
            if (fragment != null) {
                ((AllCouponsFragment) fragment).refresh();
            }
        } else {
            Fragment fragment = BusinessCouponsFragment.newInstance();
            if (fragment != null) {
                ((BusinessCouponsFragment) fragment).refresh();
            }
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void getLoyaltyPoint() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    totalLoyaltyPoint = MobileDataProvider.getInstance().getLoyaltyPoint();
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean success) {

                if (totalLoyaltyPoint != null) {
                    if (totalLoyaltyPoint <= 0) {
                        _tvLoyaltyPoints.setText("-");
                    } else {
                        _tvLoyaltyPoints.setText(String.valueOf(totalLoyaltyPoint));
                    }

                }


                if (success != null) {
                    if (!success)
                        Toast.makeText(getActivity(), "Unable to refresh loyalty point. Please try again later.", Toast.LENGTH_LONG).show();
                }

            }
        }.execute();
    }

    public static class TabsAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public TabsAdapter(FragmentManager fm, int NoofTabs) {
            super(fm);
            this.mNumOfTabs = NoofTabs;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new AllCouponsFragment();
                case 1:
                    return new BusinessCouponsFragment();
            }
            return null;
        }
    }

}


