package io.blushine.rmw.settings;

import android.app.FragmentManager;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import io.blushine.android.AppFragmentHelper;
import io.blushine.android.R;
import io.blushine.rmw.util.AppActivity;

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
		SettingsFragment fragment = new SettingsFragment();
		fragment.show();
	}
}
}
