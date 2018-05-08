package io.blushine.celebratorica.item;

import android.os.Bundle;

import io.blushine.celebratorica.util.AppActivity;

public class ItemActivity extends AppActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	if (savedInstanceState == null) {
		new ItemViewFragment().show();
	}
}
}
