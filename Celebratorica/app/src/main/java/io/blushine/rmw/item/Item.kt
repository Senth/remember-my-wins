package io.blushine.rmw.item

import io.blushine.android.common.DateFormats
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

/**
 * A celebration item
 */
internal class Item : Comparable<Item> {
	var userId = ""
	var id = ""
	var categoryId = ""
	var text = ""
	var date: Long = -1

	/**
	 * @return The date in human readable format
	 */
	fun getDateString(): String {
		return DATE_FORMAT.format(Date(date))
	}

	/**
	 * Set the date from a string
	 */
	fun setDate(dateString: String) {
		val parsePosition = ParsePosition(0)
		date = DATE_FORMAT.parse(dateString, parsePosition).time
	}

	override fun compareTo(other: Item): Int {
		return java.lang.Long.compare(date, other.date)
	}

	override fun hashCode(): Int {
		return Objects.hash(id)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (other == null || javaClass != other.javaClass) {
			return false
		}

		val that = other as Item?

		return id == that!!.id

	}

	companion object {
		val DATE_FORMAT: SimpleDateFormat
			get() = DateFormats.getMediumDateFormat()
	}
}
