package io.blushine.rmw.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import io.blushine.android.common.DocumentChangeChecker;
import io.blushine.rmw.R;
import io.blushine.rmw.firebase.FirebaseAuth;
import io.blushine.rmw.item.CategoryOrderFragment;
import io.blushine.rmw.settings.SettingsActivity;

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
	// TODO only init Firebase or Sqlite not both (depending on what backend the user has selected)
	Sqlite.init();
	FirebaseAuth.getInstance();
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
	
	case R.id.action_settings:
		AppActivity.switchTo(SettingsActivity.class);
		return true;
	
	case R.id.action_edit_categories:
		CategoryOrderFragment categoryOrderFragment = new CategoryOrderFragment();
		categoryOrderFragment.show();
		return true;
	
	// TODO temporary sign in and out testing
	case R.id.action_sign_in:
		FirebaseAuth.getInstance().signIn();
		return true;
	
	case R.id.action_sign_out:
		FirebaseAuth.getInstance().signOut();
		return true;
	
	default:
		return false;
	}
}
}
