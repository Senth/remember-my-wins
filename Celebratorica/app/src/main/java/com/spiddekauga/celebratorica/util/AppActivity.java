package com.spiddekauga.celebratorica.util;

/**
 * Base activity for all activities in this app
 */
public abstract class AppActivity extends com.spiddekauga.android.AppActivity {
@Override
protected void onFirstTime() {
	super.onFirstTime();

	Sqlite.init();
}
}
