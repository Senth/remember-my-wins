package io.blushine.rmw.item

import io.blushine.android.common.ObjectEvent

const val GET_ALL_ITEMS = "get_all_items"

/**
 * Item event
 */
internal class ItemEvent @JvmOverloads constructor(action: Actions, items: List<Item> = listOf(), val categoryId: String = "") : ObjectEvent<Item>(action, items) {
	constructor(action: ObjectEvent.Actions, item: Item) : this(action, listOf(item))
}
