package io.blushine.rmw.item

import io.blushine.android.common.ObjectEvent

/**
 * Category event
 */
internal class CategoryEvent @JvmOverloads constructor(action: Actions, items: List<Category> = listOf()) : ObjectEvent<Category>(action, items) {
	constructor(action: ObjectEvent.Actions, category: Category) : this(action, listOf(category))
}
