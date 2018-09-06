package io.blushine.rmw.item;

import java.util.List;

import io.blushine.android.common.ObjectEvent;

/**
 * Item event
 */
class ItemEvent extends ObjectEvent<Item> {

ItemEvent(Actions action) {
	super(action);
}

ItemEvent(Actions action, Item item) {
	super(action, item);
}

ItemEvent(Actions action, List<Item> items) {
	super(action, items);
}
}
