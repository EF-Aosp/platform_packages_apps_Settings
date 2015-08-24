package com.android.settings.simpleaosp;

import android.os.Bundle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import com.android.settings.R;
import android.provider.Settings;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    // Statusbar general category
    private static String STATUS_BAR_GENERAL_CATEGORY = "status_bar_general_category";
    private static final String KEY_STATUS_BAR_CLOCK = "clock_style_pref";

    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
     private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final int STATUS_BAR_BATTERY_STYLE_HIDDEN = 4;
    private static final int STATUS_BAR_BATTERY_STYLE_TEXT = 6;

    private PreferenceScreen mClockStyle;
    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.status_bar_settings);
	PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();

	mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

 	int batteryStyle = Settings.System.getInt(resolver, Settings.System.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

	int batteryShowPercent = Settings.System.getInt(resolver, 		Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(String.valueOf(batteryShowPercent));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);
	enableStatusBarBatteryDependents(batteryStyle);

	mClockStyle = (PreferenceScreen) prefSet.findPreference(KEY_STATUS_BAR_CLOCK);
        updateClockStyleDescription();
       
	mQuickPulldown = (ListPreference) findPreference(PRE_QUICK_PULLDOWN);
        if (!Utils.isPhone(getActivity())) {
            prefSet.removePreference(mQuickPulldown);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateClockStyleDescription();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver cr = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(
		cr, Settings.System.STATUS_BAR_BATTERY_STYLE, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);
	    enableStatusBarBatteryDependents(batteryStyle);
            return true;
        } else if (preference == mStatusBarBatteryShowPercent) {
            int batteryShowPercent = Integer.valueOf((String) newValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    cr, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryShowPercent);
            mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void updateClockStyleDescription() {
        if (mClockStyle == null) {
            return;
        }
        if (Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_CLOCK, 1) == 1) {
            mClockStyle.setSummary(getString(R.string.enabled_string));
        } else {
            mClockStyle.setSummary(getString(R.string.disabled_string));
         }
    }

	private void enableStatusBarBatteryDependents(int batteryIconStyle) {
        if (batteryIconStyle == STATUS_BAR_BATTERY_STYLE_HIDDEN || 
		batteryIconStyle == STATUS_BAR_BATTERY_STYLE_TEXT) {
            mStatusBarBatteryShowPercent.setEnabled(false);
        } else {
            mStatusBarBatteryShowPercent.setEnabled(true);
        }
    }
}
