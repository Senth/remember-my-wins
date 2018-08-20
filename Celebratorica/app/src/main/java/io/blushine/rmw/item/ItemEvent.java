package io.blushine.rmw.item;

import java.util.List;

import io.blushine.android.common.ObjectEvent;

/**
 * Item event
 */
class ItemEvent extends ObjectEvent<Item> {

ItemEvent(Item item, Actions action) {
	super(item, action);
}

ItemEvent(List<Item> items, Actions action) {
	super(items, action);
}
}
