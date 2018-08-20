package io.blushine.rmw.firebase;

import com.firebase.ui.auth.ErrorCodes;
import com.google.firebase.auth.FirebaseUser;

/**
 * Event for when the user tried to sign in through Firebase
 */
public class FirebaseSignInEvent {
private FirebaseUser mFirebaseUser = null;
@ErrorCodes.Code
private int mErrorCode = -1;
private Statuses mStatus;

/**
 * Create sign in event for when the user pressed back
 */
FirebaseSignInEvent() {
	mStatus = Statuses.CANCELED_BY_USER;
}

/**
 * Create a sign in event for when an error occurred
 * @param errorCode the error code of the sign in
 */
FirebaseSignInEvent(@ErrorCodes.Code int errorCode) {
	mStatus = Statuses.ERROR;
	mErrorCode = errorCode;
}

/**
 * Create a sign in event for when the user successfully signed in
 */
FirebaseSignInEvent(FirebaseUser firebaseUser) {
	mStatus = Statuses.SIGNED_IN;
	mFirebaseUser = firebaseUser;
}

/**
 * Return the logged in firebase user. Returns null if {@link #getStatus()} is anything else than
 * {@link Statuses#SIGNED_IN}
 */
public FirebaseUser getFirebaseUser() {
	return mFirebaseUser;
}

/**
 * Get the error code. This is only set correctly if the status is set to {@link Statuses#ERROR}
 * @return one of the error codes found in {@link ErrorCodes}
 */
@ErrorCodes.Code
public final int getErrorCode() {
	return mErrorCode;
}

/**
 * Get the status of the sign in
 * @return status of the sign in
 */
public Statuses getStatus() {
	return mStatus;
}

/**
 * The various responses from the statuses
 */
public enum Statuses {
	SIGNED_IN,
	CANCELED_BY_USER,
	ERROR
}
}
