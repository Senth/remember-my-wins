package io.blushine.rmw.settings

import io.blushine.android.common.Time

/**
 * Repository for settings
 */
object SettingsRepo {
	/**
	 * Get the reminder (notification) time
	 * @return reminder (notification) time
	 */
	fun getReminderTime(): Time {
		return SettingsPrefsGateway.getReminderTime()
	}

	/**
	 * @return current storage location
	 */
	fun getStorageLocation(): StorageLocations {
		return SettingsPrefsGateway.getStorageLocation()
	}

	/**
	 * Set the storage location
	 * @param storageLocation the location to store the items
	 */
	internal fun setStorageLocation(storageLocation: StorageLocations) {
		SettingsPrefsGateway.setStorageLocation(storageLocation)
	}
}
