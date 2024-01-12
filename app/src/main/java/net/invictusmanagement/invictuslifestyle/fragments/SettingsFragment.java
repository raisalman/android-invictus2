package net.invictusmanagement.invictuslifestyle.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.widget.Toast;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.models.User;
import net.invictusmanagement.invictuslifestyle.models.UserUpdate;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.RestCallBack;
import net.invictusmanagement.invictuslifestyle.webservice.WSException;
import net.invictusmanagement.invictuslifestyle.webservice.WebService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import okhttp3.ResponseBody;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreference _doNotDisturbSwitch, _hapticSwitch, notificationSwitch;
    private User _user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);
        _doNotDisturbSwitch = (SwitchPreference) findPreference("dnd_switch");
        _hapticSwitch = (SwitchPreference) findPreference("haptic_switch");
        notificationSwitch = (SwitchPreference) findPreference("notification_switch");

        WebService.getInstance().getUserData(new RestCallBack<User>() {
            @Override
            public void onResponse(User user) {
                if (user != null) {
                    _user = user;
                    _doNotDisturbSwitch.setChecked(user.isDoNotDisturb());
                    _doNotDisturbSwitch.setEnabled(true);

                    _hapticSwitch.setChecked(user.isHapticOn());

                    findPreference("displayName").setSummary(user.getDisplayName());
                    findPreference("email").setSummary(user.getEmail());
                    if (user.getPhoneNumber() != null) {
                        findPreference("phone").setSummary(Utilities.formatPhone(user.getPhoneNumber()));
                    }
                    findPreference("locationName").setSummary(user.getLocationName());
                    DateFormat formatter = SimpleDateFormat.getDateInstance(DateFormat.FULL);
                    findPreference("leaseRenewalDateUtc").setSummary(formatter.format(user.getLeaseRenewalDateUtc()));
                } else {
                    Toast.makeText(TabbedActivity.tabbedActivity, "Unable to refresh current user. Please try again later.", Toast.LENGTH_LONG).show();
                }
                SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
                prefs.registerOnSharedPreferenceChangeListener(SettingsFragment.this);
                onSharedPreferenceChanged(prefs, "chat_ringtone");
            }

            @Override
            public void onFailure(WSException wse) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
    }

    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {

        if (key.equals("chat_ringtone")) {
            Preference pref = findPreference(key);
            String ringtoneName = sharedPreferences.getString(key, "");
            if (TextUtils.isEmpty(ringtoneName)) {
                pref.setSummary(R.string.pref_ringtone_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(pref.getContext(), Uri.parse(ringtoneName));
                pref.setSummary(ringtone == null ? null : ringtone.getTitle(pref.getContext()));
            }
        } else if (key.equals("dnd_switch")) {

            UserUpdate model = new UserUpdate();
            model.isDoNotDisturb = _doNotDisturbSwitch.isChecked();
            model.isHapticOn = _hapticSwitch.isChecked();
            WebService.getInstance().updateUserSettings(model, new RestCallBack<ResponseBody>() {
                @Override
                public void onResponse(ResponseBody response) {
                    if (response == null) {
                        Toast.makeText(getActivity(),
                                "Unable to save profile. Please try again later.",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(WSException wse) {
                    Toast.makeText(getActivity(),
                            "Unable to save profile. Please try again later.",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else if (key.equals("haptic_switch")) {
            UserUpdate model = new UserUpdate();
            model.isDoNotDisturb = _doNotDisturbSwitch.isChecked();
            model.isHapticOn = _hapticSwitch.isChecked();
            WebService.getInstance().updateUserSettings(model, new RestCallBack<ResponseBody>() {
                @Override
                public void onResponse(ResponseBody response) {
                    if (response == null) {
                        Toast.makeText(getActivity(),
                                "Unable to save profile. Please try again later.",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(WSException wse) {
                    Toast.makeText(getActivity(),
                            "Unable to save profile. Please try again later.",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else if (key.equals("notification_switch")) {
            UserUpdate model = new UserUpdate();
            model.isPushSilent = notificationSwitch.isChecked();
            WebService.getInstance().updatePushSilentValue(model, new RestCallBack<ResponseBody>() {
                @Override
                public void onResponse(ResponseBody response) {
                    if (response == null) {
                        Toast.makeText(getActivity(),
                                "Unable to save profile. Please try again later.",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(WSException wse) {
                    Toast.makeText(getActivity(),
                            "Unable to save profile. Please try again later.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
