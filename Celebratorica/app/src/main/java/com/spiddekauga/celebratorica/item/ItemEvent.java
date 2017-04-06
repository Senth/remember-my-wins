package com.spiddekauga.celebratorica.item;

import com.spiddekauga.android.util.ObjectEvent;

import java.util.List;

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
