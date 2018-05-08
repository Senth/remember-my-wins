package io.blushine.celebratorica.settings;

import io.blushine.android.util.Time;

/**
 * Repository for settings
 */
class SettingsRepo {
private static final String TAG = SettingsRepo.class.getSimpleName();
private static SettingsRepo mInstance = null;
private SettingsPrefsGateway mPrefsGateway = new SettingsPrefsGateway();

/**
 * Enforces singleton pattern
 */
private SettingsRepo() {
}

/**
 * Get singleton instance
 * @return get instance
 */
public static SettingsRepo getInstance() {
	if (mInstance == null) {
		mInstance = new SettingsRepo();
	}
	return mInstance;
}

/**
 * Get the reminder (notification) time
 * @return reminder (notification) time
 */
Time getReminderTime() {
	return mPrefsGateway.getReminderTime();
}
}
