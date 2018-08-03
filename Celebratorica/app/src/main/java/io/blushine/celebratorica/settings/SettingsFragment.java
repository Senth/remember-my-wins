package io.blushine.celebratorica.settings;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import de.mrapp.android.preference.SwitchPreference;
import io.blushine.android.AppPreferenceFragment;
import io.blushine.android.legal.SettingsLegalFragment;
import io.blushine.android.preference.TimePreference;
import io.blushine.celebratorica.R;
import io.blushine.celebratorica.util.AppActivity;

/**
 * All the settings for Celebratorica App
 */
public class SettingsFragment extends AppPreferenceFragment {
@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.settings);
	
	Resources resources = getResources();
	
	// Notification reminder
	final TimePreference timePreference = (TimePreference) findPreference(resources.getString(R.string.setting_reminder_time_1_key));
	timePreference.setOnPreferenceChangeListener((preference, newValue) -> {
		// TODO update notification time
		return true;
	});
	
	SwitchPreference notificationEnabledPreference = (SwitchPreference) findPreference(resources.getString(R.string.setting_reminder_key));
	notificationEnabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
		if (newValue instanceof Boolean) {
			boolean enabled = (boolean) newValue;
			
			// TODO enable/disable notification
		}
		return true;
	});
	
	
	// Legal
	Preference legalPreference = findPreference(resources.getString(R.string.legal_key));
	legalPreference.setOnPreferenceClickListener(preference -> {
		SettingsLegalFragment fragment = new SettingsLegalFragment();
		fragment.show();
		return true;
	});
}

@Override
public void onResume() {
	super.onResume();
	ActionBar actionBar = AppActivity.getActivity().getSupportActionBar();
	if (actionBar != null) {
		actionBar.setTitle(R.string.settings);
	}
}
}
