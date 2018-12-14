package io.blushine.rmw.item;

import android.os.Bundle;

import io.blushine.rmw.util.AppActivity;

public class ItemActivity extends AppActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	if (savedInstanceState == null) {
		new TabViewFragment().show();
	}
}
}
