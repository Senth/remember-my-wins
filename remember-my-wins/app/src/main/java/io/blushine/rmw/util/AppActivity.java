package io.blushine.rmw.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.squareup.otto.ThreadEnforcer;

import de.mrapp.android.dialog.MaterialDialog;
import io.blushine.android.common.DocumentChangeChecker;
import io.blushine.android.firebase.FirebaseAuth;
import io.blushine.rmw.R;
import io.blushine.rmw.item.CategoryOrderFragment;
import io.blushine.rmw.settings.SettingsActivity;
import io.blushine.rmw.settings.SettingsRepo;
import io.blushine.utils.EventBus;

/**
 * Base activity for all activities in this app
 */
public abstract class AppActivity extends io.blushine.android.AppActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
}

protected void onFirstTime() {
	EventBus.setThreadEnforcer(ThreadEnforcer.ANY);
	
	// TODO only init Firebase or Sqlite not both (depending on what backend the user has selected)
	switch (SettingsRepo.INSTANCE.getStorageLocation()) {
	case CLOUD:
		FirebaseAuth.INSTANCE.getCurrentUser();
		checkDocumentUpdates();
		break;
	case LOCAL:
		Sqlite.init();
		checkDocumentUpdates();
		break;
	case NOT_SET:
		// TODO Initialize onboarding?
		break;
	}
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
		FirebaseAuth.INSTANCE.signIn();
		return true;
	
	case R.id.action_sign_out:
		FirebaseAuth.INSTANCE.signOut();
		return true;
	
	default:
		return false;
	}
}
}
