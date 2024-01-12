package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.enum_utils.NotificationStatusEnum;
import net.invictusmanagement.invictuslifestyle.interfaces.IRefreshableFragment;

public class DigitalKeysFragment extends Fragment implements IRefreshableFragment {

    public ViewPager viewpager;
    public int _currentTabPosition = 0;
    public static Boolean isActive = true;
    public static TabLayout _tabLayout;
    private TextView _tvAll, _tvActive;
    private ImageView _switch;

    public DigitalKeysFragment() {
    }

    @SuppressWarnings("unused")
    public static DigitalKeysFragment newInstance() {
        return new DigitalKeysFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_digitalkeys_list, container, false);
        if (view instanceof ConstraintLayout) {

            _tabLayout = view.findViewById(R.id.tabs);
            _tabLayout.addTab(_tabLayout.newTab().setText("SEND"));
            _tabLayout.addTab(_tabLayout.newTab().setText("RECEIVE"));
            _tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            _tvAll = view.findViewById(R.id.tvAll);
            _tvActive = view.findViewById(R.id.tvActive);

            _tvActive.setTypeface(Typeface.DEFAULT_BOLD);
            _tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));


            _switch = view.findViewById(R.id.filterSwitch);
            _switch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchVisibility(!isActive);
                    if (_currentTabPosition == 0) {
                        new AllDigitalKeysFragment().refreshAdapter();
                    } else {
                        new InviteKeysFragment().refreshAdapter();
                    }
                }
            });

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
                        new AllDigitalKeysFragment().refreshAdapter();
                    } else {
                        HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.DigitalKey.value());
                        new InviteKeysFragment().refreshAdapter();
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        new AllDigitalKeysFragment().refreshAdapter();
                    } else {
                        HomeFragment.newInstance().updateNotificationStatus(NotificationStatusEnum.DigitalKey.value());
                        new InviteKeysFragment().refreshAdapter();
                    }
                }
            });
        }
        return view;
    }

    private void switchVisibility(Boolean isUnActive) {
        if (isUnActive) {
            _switch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_deactive_switch));
            _tvActive.setTypeface(Typeface.DEFAULT_BOLD);
            _tvAll.setTypeface(Typeface.DEFAULT);

            _tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSwitchTextNotSelected));

            isActive = true;
        } else {
            _switch.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_active_switch));
            _tvAll.setTypeface(Typeface.DEFAULT_BOLD);
            _tvActive.setTypeface(Typeface.DEFAULT);

            _tvAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
            _tvActive.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSwitchTextNotSelected));

            isActive = false;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void refresh() {
        if (viewpager.getCurrentItem() == 0) {
            Fragment fragment = AllDigitalKeysFragment.newInstance();
            if (fragment != null) {
                ((AllDigitalKeysFragment) fragment).refresh();
            }
        } else {
            Fragment fragment = InviteKeysFragment.newInstance();
            if (fragment != null) {
                ((InviteKeysFragment) fragment).refresh();
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
                    return new AllDigitalKeysFragment();
                case 1:
                    return new InviteKeysFragment();
            }
            return null;
        }
    }
}
