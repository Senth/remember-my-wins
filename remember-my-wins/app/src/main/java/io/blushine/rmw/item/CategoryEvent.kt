package io.blushine.rmw.item

import io.blushine.android.common.ObjectEvent

/**
 * Category event
 * @param movedFrom set if this is a move event
 * @param movedTo set if this is a move event
 */
internal class CategoryEvent @JvmOverloads constructor(action: Actions, items: List<Category> = listOf(), val movedFrom: Int = -1, val movedTo: Int = -1) : ObjectEvent<Category>(action, items) {
	constructor(action: ObjectEvent.Actions, category: Category) : this(action, listOf(category))

	fun isMoveEvent(): Boolean {
		return movedFrom != -1 && movedTo != -1
	}
}
