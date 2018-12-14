package io.blushine.rmw.item

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Category for specific lists
 */
internal class Category() : Comparable<Category>, Parcelable {
	var userId = ""
	var id = ""
	var name = ""
	var order = -1

	constructor(parcel: Parcel) : this() {
		userId = parcel.readString()
		id = parcel.readString()
		name = parcel.readString()
		order = parcel.readInt()
	}

	override fun compareTo(other: Category): Int {
		return Integer.compare(order, other.order)
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

		val category = other as Category?

		return id == category!!.id
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(userId)
		parcel.writeString(id)
		parcel.writeString(name)
		parcel.writeInt(order)
	}

	override fun describeContents(): Int {
		return 0
	}

	fun set(category: Category) {
		userId = category.userId
		id = category.id
		name = category.name
		order = category.order
	}

	companion object CREATOR : Parcelable.Creator<Category> {
		override fun createFromParcel(parcel: Parcel): Category {
			return Category(parcel)
		}

		override fun newArray(size: Int): Array<Category?> {
			return arrayOfNulls(size)
		}
	}
}
