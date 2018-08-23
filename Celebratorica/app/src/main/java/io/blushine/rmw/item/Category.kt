package io.blushine.rmw.item

import java.util.*

/**
 * Category for specific lists
 */
internal class Category : Comparable<Category> {
	var id = ""
	var name = ""
	var order = -1

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
}
