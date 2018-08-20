package io.blushine.rmw.firebase;

import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseUiException;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;

import io.blushine.android.ActivityResultEvent;
import io.blushine.rmw.util.AppActivity;
import io.blushine.utils.EventBus;

import static android.app.Activity.RESULT_OK;

/**
 * Wrapper class for authenticating users in Firebase
 */
public class FirebaseAuth {
private static final int RC_SIGN_IN = 5627;
private static FirebaseAuth mInstance = null;
private com.google.firebase.auth.FirebaseAuth mAuth;
private AuthUI mAuthUI;

/**
 * Enforces singleton pattern
 */
private FirebaseAuth() {
	mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
	mAuthUI = AuthUI.getInstance();
	EventBus.getInstance().register(this);
}

/**
 * Get singleton instance
 * @return get instance
 */
public static FirebaseAuth getInstance() {
	if (mInstance == null) {
		mInstance = new FirebaseAuth();
	}
	return mInstance;
}

/**
 * Shows the sign in screen for the user.
 * A {@link FirebaseSignInEvent} is posted to the {@link EventBus} when the user has successfully
 * signed in, pressed back, or an error occurred.
 */
public void signIn() {
	FirebaseApp firebaseApp = null;
	
	// Authenticators
	List<AuthUI.IdpConfig> providers = Arrays.asList(
			new AuthUI.IdpConfig.GoogleBuilder().build(),
			new AuthUI.IdpConfig.EmailBuilder().build(),
			new AuthUI.IdpConfig.PhoneBuilder().build()
	);
	
	Intent signInIntent = mAuthUI.createSignInIntentBuilder()
			.setAvailableProviders(providers)
			.build();
	
	AppActivity.getActivity().startActivityForResult(signInIntent, RC_SIGN_IN);
}

/**
 * Sign out the user
 */
public void signOut() {

}

/**
 * Get the current user
 * @return current logged in user
 */
public FirebaseUser getCurrentUser() {
	return mAuth.getCurrentUser();
}

@SuppressWarnings("unused")
@Subscribe
public void onActivityResultEvent(ActivityResultEvent event) {
	if (event.getRequestCode() == RC_SIGN_IN) {
		IdpResponse response = IdpResponse.fromResultIntent(event.getData());
		
		// Successfully signed in
		if (event.getResultCode() == RESULT_OK) {
			EventBus.getInstance().post(new FirebaseSignInEvent(mAuth.getCurrentUser()));
		}
		// Pressed back
		else if (response == null) {
			EventBus.getInstance().post(new FirebaseSignInEvent());
		}
		// Error while signing in
		else {
			FirebaseUiException error = response.getError();
			if (error != null) {
				EventBus.getInstance().post(new FirebaseSignInEvent(error.getErrorCode()));
			}
		}
	}
}
}
