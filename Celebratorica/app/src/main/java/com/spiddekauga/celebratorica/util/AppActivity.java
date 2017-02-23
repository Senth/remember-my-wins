package com.spiddekauga.celebratorica.util;

import android.view.MenuItem;

import com.spiddekauga.android.feedback.FeedbackFragment;
import com.spiddekauga.celebratorica.R;

/**
 * Base activity for all activities in this app
 */
public abstract class AppActivity extends com.spiddekauga.android.AppActivity {
@Override
protected void onFirstTime() {
	super.onFirstTime();

	Sqlite.init();
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.action_feedback:
		FeedbackFragment feedbackFragment = new FeedbackFragment();
		feedbackFragment.show();
		return true;

	default:
		return false;
	}
}
}
