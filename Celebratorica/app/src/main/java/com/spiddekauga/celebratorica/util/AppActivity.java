package com.spiddekauga.celebratorica.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.spiddekauga.android.feedback.FeedbackFragment;
import com.spiddekauga.android.util.DocumentChangeChecker;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.item.CategoryOrderFragment;
import com.spiddekauga.celebratorica.settings.SettingsActivity;

/**
 * Base activity for all activities in this app
 */
public abstract class AppActivity extends com.spiddekauga.android.AppActivity {
private static boolean mFirstTime = true;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	if (mFirstTime) {
		mFirstTime = false;
		onFirstTime();
	}
}

private void onFirstTime() {
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
	
	case R.id.action_edit_categories:
		CategoryOrderFragment categoryOrderFragment = new CategoryOrderFragment();
		categoryOrderFragment.show();
		return true;
	
	default:
		return false;
	}
}
}
