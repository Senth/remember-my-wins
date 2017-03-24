package com.spiddekauga.celebratorica.item;

import android.os.Bundle;

import com.spiddekauga.celebratorica.util.AppActivity;

public class ItemActivity extends AppActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	if (savedInstanceState == null) {
		new ItemViewFragment().show();
	}
}
}
