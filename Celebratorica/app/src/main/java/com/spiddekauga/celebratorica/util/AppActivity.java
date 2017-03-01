package com.spiddekauga.celebratorica.util;

import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import com.spiddekauga.android.feedback.FeedbackFragment;
import com.spiddekauga.android.util.DocumentChangeChecker;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.settings.SettingsActivity;

/**
 * Base activity for all activities in this app
 */
public abstract class AppActivity extends com.spiddekauga.android.AppActivity {
@Override
protected void onFirstTime() {
	super.onFirstTime();

	Sqlite.init();
	checkDocumentUpdates();
}

private void checkDocumentUpdates() {
	DocumentChangeChecker documentChangeChecker = DocumentChangeChecker.getInstance();

	// Privacy Policy
	documentChangeChecker.checkDocument(R.raw.privacy_policy, R.string.legal_privacy_policy_title, R.string.legal_privacy_policy_changed);
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.action_open_article:
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppActivity.getActivity().getResources().getString(R.string.article_url)));
		startActivity(browserIntent);
		return true;

	case R.id.action_feedback:
		FeedbackFragment feedbackFragment = new FeedbackFragment();
		feedbackFragment.show();
		return true;

	case R.id.action_legal:
		AppActivity.switchTo(SettingsActivity.class);
		return true;

	default:
		return false;
	}
}
}
