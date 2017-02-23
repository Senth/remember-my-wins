package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;

import com.spiddekauga.celebratorica.util.AppActivity;

public class CelebrationActivity extends AppActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	if (savedInstanceState == null) {
		new CelebrationListFragment().show();
	}
}
}
