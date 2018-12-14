package io.blushine.rmw.settings

import android.support.v7.preference.PreferenceManager
import io.blushine.android.common.Time
import io.blushine.rmw.R
import io.blushine.rmw.util.AppActivity

/**
 * Gateway for settings in the preference file
 */
internal object SettingsPrefsGateway {
	private val REMINDER_TIME_1_KEY = AppActivity.getActivity()
			.resources.getString(R.string.setting_reminder_time_1_key)
	private val STORE_LOCATION_KEY = AppActivity.getActivity()!!.resources!!.getString(R.string.setting_store_location_key)
	private val preferences = PreferenceManager.getDefaultSharedPreferences(AppActivity.getActivity())

	/**
	 * @return reminder (notification) time
	 */
	fun getReminderTime(): Time {
		val seconds = preferences.getInt(REMINDER_TIME_1_KEY, 0)
		return Time(seconds)
	}

	/**
	 * @return current storage location
	 */
	fun getStorageLocation(): StorageLocations {
		val key = preferences.getString(STORE_LOCATION_KEY, "")
		return StorageLocations.toEnum(key)
	}

	/**
	 * Set the storage location
	 * @param storageLocation the location to store the items
	 */
	fun setStorageLocation(storageLocation: StorageLocations) {
		val editor = preferences.edit()
		editor.putString(STORE_LOCATION_KEY, storageLocation.key)
		editor.apply()
	}
}
