package com.spiddekauga.celebratorica.settings;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spiddekauga.android.AppFragmentHelper;
import com.spiddekauga.android.R;
import com.spiddekauga.android.legal.SettingsLegalFragment;
import com.spiddekauga.celebratorica.util.AppActivity;

/**
 * Celebratorica settings class
 */
public class SettingsActivity extends AppActivity {
@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_settings);
	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
	if (toolbar != null) {
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AppFragmentHelper fragmentHelper = AppFragmentHelper.getHelper();
				if (fragmentHelper != null) {
					fragmentHelper.back();
				}
			}
		});
	}
	FragmentManager fragmentManager = getFragmentManager();

	// Only create if we don't have a fragment
	if (fragmentManager.getBackStackEntryCount() == 0) {
		SettingsLegalFragment fragment = new SettingsLegalFragment();
		fragment.show();
	}
}
}
