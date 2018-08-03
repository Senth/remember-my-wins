package io.blushine.celebratorica.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import io.blushine.android.common.DocumentChangeChecker;
import io.blushine.celebratorica.R;
import io.blushine.celebratorica.item.CategoryOrderFragment;
import io.blushine.celebratorica.settings.SettingsActivity;

/**
 * Base activity for all activities in this app
 */
public abstract class AppActivity extends io.blushine.android.AppActivity {
private static boolean mFirstTime = true;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	if (mFirstTime) {
		mFirstTime = false;
		onFirstTime();
	}
}

protected void onFirstTime() {
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

//	case R.id.action_feedback:
//		FeedbackFragment feedbackFragment = new FeedbackFragment();
//		feedbackFragment.show();
//		return true;
	
	case R.id.action_settings:
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
