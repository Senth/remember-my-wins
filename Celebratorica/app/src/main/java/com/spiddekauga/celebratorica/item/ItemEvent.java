package com.spiddekauga.celebratorica.item;

import com.spiddekauga.android.util.ObjectEvent;

/**
 * Item event
 */
class ItemEvent extends ObjectEvent {
private final Item mItem;

ItemEvent(Item item, ObjectEvent.Actions action) {
	super(action);
	mItem = item;
}

public Item getItem() {
	return mItem;
}
}
