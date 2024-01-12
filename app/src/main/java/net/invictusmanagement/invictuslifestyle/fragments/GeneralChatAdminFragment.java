package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.PendingChatRequestListActivity;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;

public class GeneralChatAdminFragment extends Fragment {

    public ViewPager viewpager;
    public int _currentTabPosition = 0;
    private Button btnPendingChatRequest;
    public static TabLayout _tabLayout;
    public static Context _context;
    public static boolean active = false;
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);

    public GeneralChatAdminFragment() {
        // Required empty public constructor
    }


    @SuppressWarnings("unused")
    public static GeneralChatAdminFragment newInstance() {

        return new GeneralChatAdminFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general_chat_admin, container, false);
        if (view instanceof ConstraintLayout) {

            btnPendingChatRequest = view.findViewById(R.id.btnPendingRequest);
            _tabLayout = view.findViewById(R.id.tabs);
            _tabLayout.addTab(_tabLayout.newTab().setText("RECENT"));
            _tabLayout.addTab(_tabLayout.newTab().setText("USERS"));
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
                    if (_currentTabPosition == 0) {
                        new RecentChatFragment().refreshAdapter();
                    } else {
                        new UsersChatFragment().refreshAdapter();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (_currentTabPosition == 0) {
                        new RecentChatFragment().refreshAdapter();
                    } else {
                        new UsersChatFragment().refreshAdapter();
                    }
                }
            });
        }

        btnPendingChatRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, PendingChatRequestListActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
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
                    return new RecentChatFragment();
                case 1:
                    return new UsersChatFragment();
            }
            return null;
        }
    }
}