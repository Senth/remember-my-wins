package com.spiddekauga.celebratorica.settings;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.spiddekauga.android.AppPreferenceFragment;
import com.spiddekauga.android.legal.SettingsLegalFragment;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.AppActivity;

/**
 * All the settings for Celebratorica App
 */
public class SettingsFragment extends AppPreferenceFragment {
@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.settings);
	
	Resources resources = getResources();
	
	// Notification Settings
	
	// Legal
	Preference legalPreference = findPreference(resources.getString(R.string.legal_key));
	legalPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
		@Override
		public boolean onPreferenceClick(Preference preference) {
			SettingsLegalFragment fragment = new SettingsLegalFragment();
			fragment.show();
			return true;
		}
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
