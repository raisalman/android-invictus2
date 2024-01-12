package net.invictusmanagement.invictuslifestyle.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.customviews.CustomViewPager;
import net.invictusmanagement.invictuslifestyle.fragments.GuestKeyFragment;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.fragments.QuickKeyFragment;

public class NewDigitalKeyTabActivity extends BaseActivity {

    public CustomViewPager viewpager;
    public int _currentTabPosition = 0;
    public static TabLayout _tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_key_tab);
        toolBar();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        _tabLayout = findViewById(R.id.tabs);
        _tabLayout.addTab(_tabLayout.newTab().setText("Guest Key"));
        _tabLayout.addTab(_tabLayout.newTab().setText("Quick Key"));
        _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        viewpager = findViewById(R.id.container);
        TabsAdapter couponsPagerAdapter = new TabsAdapter(getSupportFragmentManager(),
                _tabLayout.getTabCount());

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

        if (HomeFragment.isQuickKey) {
            HomeFragment.isQuickKey = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewpager.setCurrentItem(1);
                }
            }, 500);

        }
    }

    private void toolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create New Digital Key");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
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
                    return new GuestKeyFragment();
                case 1:
                    return new QuickKeyFragment();
            }
            return null;
        }
    }
}
