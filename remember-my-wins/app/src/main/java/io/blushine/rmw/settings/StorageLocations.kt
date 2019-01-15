package io.blushine.rmw.settings

import io.blushine.rmw.R
import io.blushine.rmw.util.AppActivity

/**
 * The various storage locations
 */
enum class StorageLocations(val key: String) {
	CLOUD(AppActivity.getActivity()!!.resources!!.getString(R.string.setting_store_location_cloud_key)),
	LOCAL(AppActivity.getActivity()!!.resources!!.getString(R.string.setting_store_location_local_key)),
	NOT_SET(AppActivity.getActivity()!!.resources!!.getString(R.string.setting_store_location_not_set_key)),
	;

	/**
	 * @return the enum matching to the specified key. If not found [NOT_SET] will be returned.
	 */
	companion object {
		fun toEnum(key: String?): StorageLocations {
			if (key == null) {
				return NOT_SET
			}

			for (enum in StorageLocations.values()) {
				if (enum.key == key) {
					return enum
				}
			}
			return NOT_SET
		}
	}
}