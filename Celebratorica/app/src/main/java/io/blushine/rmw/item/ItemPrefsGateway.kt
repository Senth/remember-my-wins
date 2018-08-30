package io.blushine.rmw.item

import android.content.Context
import io.blushine.rmw.util.AppActivity

/**
 * Gateway for item settings in the preference file
 */
internal object ItemPrefsGateway {
	private val ADDED_DEFAULT_CATEGORIES = "added_default_categories"
	private val PREFERENCE_NAME = "item_preferences"
	private val preferences = AppActivity.getActivity()
			.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

	/**
	 * @return true if we've already added the default categories
	 */
	fun hasAddedDefaultCategories(): Boolean {
		return preferences.getBoolean(ADDED_DEFAULT_CATEGORIES, false);
	}

	/**
	 * Set if we've added the default categories
	 * @param addedDefaultCategories true if we added, false otherwise
	 */
	fun setAddedDefaultCategories(addedDefaultCategories: Boolean) {
		val editor = preferences.edit()
		editor.putBoolean(ADDED_DEFAULT_CATEGORIES, addedDefaultCategories)
		editor.apply()
	}
}