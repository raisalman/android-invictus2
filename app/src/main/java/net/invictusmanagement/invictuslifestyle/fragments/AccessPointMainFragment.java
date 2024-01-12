package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.CustomViewPager;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;

public class AccessPointMainFragment extends Fragment implements IRefreshableFragment {

    public CustomViewPager viewpager;
    public int _currentTabPosition = 0;
    public static TabLayout _tabLayout;

    public AccessPointMainFragment() {
    }

    @SuppressWarnings("unused")
    public static AccessPointMainFragment newInstance() {
        return new AccessPointMainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_access_point_main, container, false);
        if (view instanceof ConstraintLayout) {

            _tabLayout = view.findViewById(R.id.tabs);
            _tabLayout.addTab(_tabLayout.newTab().setText("MY"));
            _tabLayout.addTab(_tabLayout.newTab().setText("GUEST"));
            _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


            viewpager = view.findViewById(R.id.container);
            TabsAdapter couponsPagerAdapter = new TabsAdapter(getChildFragmentManager(), _tabLayout.getTabCount());

            viewpager.setAdapter(couponsPagerAdapter);
            viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(_tabLayout));

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
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void refresh() {
        /*getLoyaltyPointList();*/
        if (viewpager.getCurrentItem() == 0) {
            Fragment fragment = AccessPointsFragment.newInstance();
            if (fragment != null) {
                ((AccessPointsFragment) fragment).refresh();
            }
        } else {
            Fragment fragment = GuestAccessPointsFragment.newInstance();
            if (fragment != null) {
                ((GuestAccessPointsFragment) fragment).refresh();
            }
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();

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
                    return new AccessPointsFragment();
                case 1:
                    return new GuestAccessPointsFragment();
            }
            return null;
        }
    }
}
