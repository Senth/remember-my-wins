package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;

import com.spiddekauga.android.AppActivity;

public class CelebrationListActivity extends AppActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	if (savedInstanceState == null) {
		new CelebrationListFragment().show();
	}
}
}
