package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.fragments.AccessPointMainFragment;
import net.invictusmanagement.invictuslifestyle.fragments.AccessPointsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.BillBoardFragment;
import net.invictusmanagement.invictuslifestyle.fragments.BrivoDevicesFragment;
import net.invictusmanagement.invictuslifestyle.fragments.BusinessTypesFragment;
import net.invictusmanagement.invictuslifestyle.fragments.CommunityNotificationFragment;
import net.invictusmanagement.invictuslifestyle.fragments.DigitalKeysFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GeneralChatAdminFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GeneralChatFragment;
import net.invictusmanagement.invictuslifestyle.fragments.GuestDigitalKeysFragment;
import net.invictusmanagement.invictuslifestyle.fragments.HealthFragment;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;
import net.invictusmanagement.invictuslifestyle.fragments.NotificationsFragment;
import net.invictusmanagement.invictuslifestyle.fragments.RentalToolFragment;
import net.invictusmanagement.invictuslifestyle.fragments.ServiceKeysFragment;
import net.invictusmanagement.invictuslifestyle.fragments.VoiceMailFragment;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;

public class SectionViewPagerAdapter extends FragmentStateAdapter {

    public int FRAGMENT_POSITION_HOME;
    public int FRAGMENT_POSITION_ACCESS_POINTS;
    public int FRAGMENT_POSITION_BRIVO_DEVICES;
    public int FRAGMENT_POSITION_PROMOTIONS;
    public int FRAGMENT_POSITION_DIGITAL_KEYS;
    public int FRAGMENT_POSITION_SERVICE_KEYS;
    public int FRAGMENT_POSITION_NOTIFICATIONS;
    public int FRAGMENT_POSITION_RENTAL_TOOL;
    public int FRAGMENT_POSITION_HEALTH;
    public int FRAGMENT_POSITION_VOICE_MAIL;
    public int FRAGMENT_POSITION_BILLBOARD;
    public int FRAGMENT_POSITION_CHAT;
    public int FRAGMENT_POSITION_COMMUNITY_NOTIFICATIONS;

    private final String[] guestTabs = {
            Utilities.FRAGMENT_HOME,
            Utilities.FRAGMENT_ACCESS_POINTS,
//            Utilities.FRAGMENT_BRIVO_DEVICES,
            Utilities.FRAGMENT_PROMOTIONS,
            Utilities.FRAGMENT_DIGITAL_KEYS,
            Utilities.FRAGMENT_NOTIFICATIONS,
            Utilities.FRAGMENT_HEALTH
    };

    public String[] getGuestTabs() {
        return guestTabs;
    }

    private final String[] vendorTabs = {
            Utilities.FRAGMENT_HOME,
            Utilities.FRAGMENT_ACCESS_POINTS,
//            Utilities.FRAGMENT_BRIVO_DEVICES,
            Utilities.FRAGMENT_PROMOTIONS,
            Utilities.FRAGMENT_DIGITAL_KEYS,
            Utilities.FRAGMENT_NOTIFICATIONS,
            Utilities.FRAGMENT_RENTAL_TOOL,
            Utilities.FRAGMENT_HEALTH,
            Utilities.FRAGMENT_CHAT
    };

    public String[] getVendorTabs() {
        return vendorTabs;
    }

    private final ArrayList<String> residentTabsName = new ArrayList<>();

    private final String[] residentTabs = {
            Utilities.FRAGMENT_HOME,
            Utilities.FRAGMENT_ACCESS_POINTS,
//            Utilities.FRAGMENT_BRIVO_DEVICES,
            Utilities.FRAGMENT_PROMOTIONS,
            Utilities.FRAGMENT_DIGITAL_KEYS,
            Utilities.FRAGMENT_NOTIFICATIONS,
            Utilities.FRAGMENT_RENTAL_TOOL,
            Utilities.FRAGMENT_HEALTH,
            Utilities.FRAGMENT_VOICE_MAIL
    };

    public ArrayList<String> getResidentTabsName() {
        return residentTabsName;
    }

    private final String[] adminTabs = {
            Utilities.FRAGMENT_HOME,
            Utilities.FRAGMENT_ACCESS_POINTS,
//            Utilities.FRAGMENT_BRIVO_DEVICES,
            Utilities.FRAGMENT_SERVICE_KEYS,
            Utilities.FRAGMENT_PROMOTIONS,
            Utilities.FRAGMENT_DIGITAL_KEYS,
            Utilities.FRAGMENT_NOTIFICATIONS,
            Utilities.FRAGMENT_RENTAL_TOOL,
            Utilities.FRAGMENT_HEALTH,
            Utilities.FRAGMENT_VOICE_MAIL
    };

    private final ArrayList<String> adminTabsName = new ArrayList<>();

    public ArrayList<String> getAdminTabsName() {
        return adminTabsName;
    }

    private final String[] facilityTabs = {
            Utilities.FRAGMENT_HOME,
            Utilities.FRAGMENT_ACCESS_POINTS,
//            Utilities.FRAGMENT_BRIVO_DEVICES,
            Utilities.FRAGMENT_SERVICE_KEYS,
            Utilities.FRAGMENT_PROMOTIONS,
            Utilities.FRAGMENT_NOTIFICATIONS,
            Utilities.FRAGMENT_RENTAL_TOOL,
            Utilities.FRAGMENT_COMMUNITY_NOTIFICATIONS,
            Utilities.FRAGMENT_CHAT
    };

    private final ArrayList<String> facilityTabsName = new ArrayList<>();

    public ArrayList<String> getFacilityTabsName() {
        return facilityTabsName;
    }

    private final SharedPreferences sharedPreferences;
    private final String role;
    private final Context context;

    public SectionViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        context = fragmentActivity;
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(fragmentActivity);
        role = sharedPreferences.getString("userRole", "");

        //add all confirm data to array list
        residentTabsName.addAll(Arrays.asList(residentTabs));
        adminTabsName.addAll(Arrays.asList(adminTabs));
        facilityTabsName.addAll(Arrays.asList(facilityTabs));

        if (role.equals(context.getString(R.string.role_vendor))) {
            FRAGMENT_POSITION_HOME = 0;
            FRAGMENT_POSITION_ACCESS_POINTS = 1;
//            FRAGMENT_POSITION_BRIVO_DEVICES = 2;
//            FRAGMENT_POSITION_PROMOTIONS = 3;
//            FRAGMENT_POSITION_DIGITAL_KEYS = 4;
//            FRAGMENT_POSITION_NOTIFICATIONS = 5;
//            FRAGMENT_POSITION_RENTAL_TOOL = 6;
//            FRAGMENT_POSITION_HEALTH = 7;
//            FRAGMENT_POSITION_CHAT = 8;

            FRAGMENT_POSITION_PROMOTIONS = 2;
            FRAGMENT_POSITION_DIGITAL_KEYS = 3;
            FRAGMENT_POSITION_NOTIFICATIONS = 4;
            FRAGMENT_POSITION_RENTAL_TOOL = 5;
            FRAGMENT_POSITION_HEALTH = 6;
            FRAGMENT_POSITION_CHAT = 7;
        } else if (role.equals(context.getString(R.string.role_facility))) {
            FRAGMENT_POSITION_HOME = 0;
            FRAGMENT_POSITION_ACCESS_POINTS = 1;
//            FRAGMENT_POSITION_BRIVO_DEVICES = 2;
//            FRAGMENT_POSITION_SERVICE_KEYS = 3;
//            FRAGMENT_POSITION_PROMOTIONS = 4;
//            FRAGMENT_POSITION_NOTIFICATIONS = 5;
//            FRAGMENT_POSITION_RENTAL_TOOL = 6;
//            FRAGMENT_POSITION_COMMUNITY_NOTIFICATIONS = 7;
//            FRAGMENT_POSITION_CHAT = 8;

            FRAGMENT_POSITION_SERVICE_KEYS = 2;
            FRAGMENT_POSITION_PROMOTIONS = 3;
            FRAGMENT_POSITION_NOTIFICATIONS = 4;
            FRAGMENT_POSITION_RENTAL_TOOL = 5;
            FRAGMENT_POSITION_COMMUNITY_NOTIFICATIONS = 6;
            FRAGMENT_POSITION_CHAT = 7;
        } else if (role.equals(context.getString(R.string.role_resident))) {
            FRAGMENT_POSITION_HOME = 0;
            FRAGMENT_POSITION_ACCESS_POINTS = 1;
//            FRAGMENT_POSITION_BRIVO_DEVICES = 2;
//            FRAGMENT_POSITION_PROMOTIONS = 3;
//            FRAGMENT_POSITION_DIGITAL_KEYS = 4;
//            FRAGMENT_POSITION_NOTIFICATIONS = 5;
//            FRAGMENT_POSITION_RENTAL_TOOL = 6;
//            FRAGMENT_POSITION_HEALTH = 7;
//            FRAGMENT_POSITION_VOICE_MAIL = 8;

            FRAGMENT_POSITION_PROMOTIONS = 2;
            FRAGMENT_POSITION_DIGITAL_KEYS = 3;
            FRAGMENT_POSITION_NOTIFICATIONS = 4;
            FRAGMENT_POSITION_RENTAL_TOOL = 5;
            FRAGMENT_POSITION_HEALTH = 6;
            FRAGMENT_POSITION_VOICE_MAIL = 7;

            int position = 7; //last position
            if (sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                FRAGMENT_POSITION_BILLBOARD = position + 1;
                position++;
                getResidentTabsName().add(Utilities.FRAGMENT_BILLBOARD);
            }
            if (sharedPreferences.getBoolean("allowGeneralChat", true)) {
                FRAGMENT_POSITION_CHAT = position + 1;
                getResidentTabsName().add(Utilities.FRAGMENT_CHAT);
            }
        } else if (role.equals(context.getString(R.string.role_property_manager)) ||
                role.equals(context.getString(R.string.role_leasing_officer))) {
            FRAGMENT_POSITION_HOME = 0;
            FRAGMENT_POSITION_ACCESS_POINTS = 1;
//            FRAGMENT_POSITION_BRIVO_DEVICES = 2;
//            FRAGMENT_POSITION_SERVICE_KEYS = 3;
//            FRAGMENT_POSITION_PROMOTIONS = 4;
//            FRAGMENT_POSITION_DIGITAL_KEYS = 5;
//            FRAGMENT_POSITION_NOTIFICATIONS = 6;
//            FRAGMENT_POSITION_RENTAL_TOOL = 7;
//            FRAGMENT_POSITION_HEALTH = 8;
//            FRAGMENT_POSITION_VOICE_MAIL = 9;

            FRAGMENT_POSITION_SERVICE_KEYS = 2;
            FRAGMENT_POSITION_PROMOTIONS = 3;
            FRAGMENT_POSITION_DIGITAL_KEYS = 4;
            FRAGMENT_POSITION_NOTIFICATIONS = 5;
            FRAGMENT_POSITION_RENTAL_TOOL = 6;
            FRAGMENT_POSITION_HEALTH = 7;
            FRAGMENT_POSITION_VOICE_MAIL = 8;

            int position = 8; //last position
            if (sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                FRAGMENT_POSITION_BILLBOARD = position + 1;
                position++;
                getAdminTabsName().add(Utilities.FRAGMENT_BILLBOARD);
            }
            if (sharedPreferences.getBoolean("allowGeneralChat", true)) {
                FRAGMENT_POSITION_CHAT = position + 1;
                getAdminTabsName().add(Utilities.FRAGMENT_CHAT);
            }
        } else {
            FRAGMENT_POSITION_HOME = 0;
            FRAGMENT_POSITION_ACCESS_POINTS = 1;
//            FRAGMENT_POSITION_BRIVO_DEVICES = 2;
//            FRAGMENT_POSITION_PROMOTIONS = 3;
//            FRAGMENT_POSITION_DIGITAL_KEYS = 4;
//            FRAGMENT_POSITION_NOTIFICATIONS = 5;
//            FRAGMENT_POSITION_HEALTH = 6;

            FRAGMENT_POSITION_PROMOTIONS = 2;
            FRAGMENT_POSITION_DIGITAL_KEYS = 3;
            FRAGMENT_POSITION_NOTIFICATIONS = 4;
            FRAGMENT_POSITION_HEALTH = 5;
        }

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //common fragment at first position
        if (position == FRAGMENT_POSITION_HOME) {
            return HomeFragment.newInstance();
        }

        //Role and Bit wise fragments
        if (role.equalsIgnoreCase(context.getString(R.string.role_vendor))) {
            if (position == FRAGMENT_POSITION_ACCESS_POINTS) {
                return AccessPointsFragment.newInstance();
            } /*else if (position == FRAGMENT_POSITION_BRIVO_DEVICES)
                return BrivoDevicesFragment.newInstance();*/
            else if (position == FRAGMENT_POSITION_PROMOTIONS)
                return BusinessTypesFragment.newInstance();
            else if (position == FRAGMENT_POSITION_DIGITAL_KEYS)
                return GuestDigitalKeysFragment.newInstance();
            else if (position == FRAGMENT_POSITION_NOTIFICATIONS)
                return NotificationsFragment.newInstance();
            else if (position == FRAGMENT_POSITION_RENTAL_TOOL)
                return RentalToolFragment.newInstance();
            else if (position == FRAGMENT_POSITION_HEALTH)
                return HealthFragment.newInstance();
            else if (position == FRAGMENT_POSITION_CHAT)
                return GeneralChatFragment.newInstance();
        } else if (role.equalsIgnoreCase(context.getString(R.string.role_facility))) {

            if (position == FRAGMENT_POSITION_ACCESS_POINTS) {
                return AccessPointsFragment.newInstance();
            } /*else if (position == FRAGMENT_POSITION_BRIVO_DEVICES) {
                return BrivoDevicesFragment.newInstance();
            }*/ else if (position == FRAGMENT_POSITION_SERVICE_KEYS) {
                return ServiceKeysFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_PROMOTIONS) {
                return BusinessTypesFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_NOTIFICATIONS) {
                return NotificationsFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_RENTAL_TOOL) {
                return RentalToolFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_COMMUNITY_NOTIFICATIONS) {
                return CommunityNotificationFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_CHAT) {
                return GeneralChatAdminFragment.newInstance();
            }
        } else if (role.equalsIgnoreCase(context.getString(R.string.role_leasing_officer)) ||
                role.equalsIgnoreCase(context.getString(R.string.role_property_manager))) {

            if (position == FRAGMENT_POSITION_ACCESS_POINTS) {
                return AccessPointMainFragment.newInstance();
            } /*else if (position == FRAGMENT_POSITION_BRIVO_DEVICES) {
                return BrivoDevicesFragment.newInstance();
            }*/ else if (position == FRAGMENT_POSITION_SERVICE_KEYS) {
                return ServiceKeysFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_PROMOTIONS) {
                return BusinessTypesFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_DIGITAL_KEYS) {
                return DigitalKeysFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_NOTIFICATIONS) {
                return NotificationsFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_RENTAL_TOOL) {
                return RentalToolFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_HEALTH) {
                return HealthFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_VOICE_MAIL) {
                return VoiceMailFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_BILLBOARD
                    && sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                return BillBoardFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_CHAT
                    && sharedPreferences.getBoolean("allowGeneralChat", true)) {
                return GeneralChatAdminFragment.newInstance();
            }
        } else if (role.equalsIgnoreCase(context.getString(R.string.role_resident))) {

            if (position == FRAGMENT_POSITION_ACCESS_POINTS) {
                return AccessPointMainFragment.newInstance();
            } /*else if (position == FRAGMENT_POSITION_BRIVO_DEVICES) {
                return BrivoDevicesFragment.newInstance();
            } */else if (position == FRAGMENT_POSITION_PROMOTIONS) {
                return BusinessTypesFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_DIGITAL_KEYS) {
                return DigitalKeysFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_NOTIFICATIONS) {
                return NotificationsFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_RENTAL_TOOL) {
                return RentalToolFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_HEALTH) {
                return HealthFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_VOICE_MAIL) {
                return VoiceMailFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_BILLBOARD
                    && sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                return BillBoardFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_CHAT
                    && sharedPreferences.getBoolean("allowGeneralChat", true)) {
                return GeneralChatFragment.newInstance();
            }
        } else {
            if (position == FRAGMENT_POSITION_ACCESS_POINTS) {
                return AccessPointsFragment.newInstance();
            } /*else if (position == FRAGMENT_POSITION_BRIVO_DEVICES) {
                return BrivoDevicesFragment.newInstance();
            } */else if (position == FRAGMENT_POSITION_PROMOTIONS) {
                return BusinessTypesFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_DIGITAL_KEYS) {
                return GuestDigitalKeysFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_NOTIFICATIONS) {
                return NotificationsFragment.newInstance();
            } else if (position == FRAGMENT_POSITION_HEALTH) {
                return HealthFragment.newInstance();
            }
        }
        Log.e("SET POSITION", position + " >> " + role);
        return HomeFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        if (role.equalsIgnoreCase(context.getString(R.string.role_resident))) {
            int count = residentTabs.length;
            if (sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                count++;
            }
            if (sharedPreferences.getBoolean("allowGeneralChat", true)) {
                count++;
            }
            return count;
        } else if (role.equalsIgnoreCase(context.getString(R.string.role_leasing_officer)) ||
                role.equalsIgnoreCase(context.getString(R.string.role_property_manager))) {
            int count = adminTabs.length;
            if (sharedPreferences.getBoolean("allowBulletinBoard", true)) {
                count++;
            }
            if (sharedPreferences.getBoolean("allowGeneralChat", true)) {
                count++;
            }
            return count;
        } else if (role.equalsIgnoreCase(context.getString(R.string.role_vendor))) {
            return vendorTabs.length;
        } else if (role.equalsIgnoreCase(context.getString(R.string.role_facility))) {
            return getFacilityTabsName().size();
        }
        return guestTabs.length;
    }

    public void setOnSelectView(TabLayout tabLayout, int position) {
        int whiteColor = ContextCompat.getColor(context, android.R.color.white);
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            View selected = tab.getCustomView();
            if (selected != null) {
                TextView iv_text = selected.findViewById(R.id.tabTitle);
                iv_text.setTextColor(whiteColor);
            }
        }
    }

    public void setUnSelectView(TabLayout tabLayout, int position) {
        int textNotSelectedColor = ContextCompat.getColor(context,
                R.color.colorTabTextNotSelectedCustom);
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            View selected = tab.getCustomView();
            if (selected != null) {
                TextView iv_text = selected.findViewById(R.id.tabTitle);
                iv_text.setTextColor(textNotSelectedColor);
            }
        }
    }
}
