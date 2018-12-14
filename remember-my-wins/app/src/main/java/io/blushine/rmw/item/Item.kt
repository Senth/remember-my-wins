package io.blushine.rmw.item

import android.os.Parcel
import android.os.Parcelable
import io.blushine.android.common.DateFormats
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

@JvmField
val DATE_FORMAT: SimpleDateFormat = DateFormats.getMediumDateFormat()

/**
 * A celebration item
 */
internal class Item() : Comparable<Item>, Parcelable {
	var userId = ""
	var id = ""
	var categoryId = ""
	var text = ""
	var date: Long = -1

	constructor(parcel: Parcel) : this() {
		userId = parcel.readString()
		id = parcel.readString()
		categoryId = parcel.readString()
		text = parcel.readString()
		date = parcel.readLong()
	}

	/**
	 * @return The date in human readable format
	 */
	fun getDateFromFormat(): String {
		return DATE_FORMAT.format(Date(date))
	}

	/**
	 * Set the date from a string
	 */
	fun setDateFromFormat(dateString: String) {
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

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(userId)
		parcel.writeString(id)
		parcel.writeString(categoryId)
		parcel.writeString(text)
		parcel.writeLong(date)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Item> {
		override fun createFromParcel(parcel: Parcel): Item {
			return Item(parcel)
		}

		override fun newArray(size: Int): Array<Item?> {
			return arrayOfNulls(size)
		}
	}
}
