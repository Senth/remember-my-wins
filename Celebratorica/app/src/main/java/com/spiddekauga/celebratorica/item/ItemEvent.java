package com.spiddekauga.celebratorica.item;

/**
 * Item event
 */
class ItemEvent {
private final Item mItem;
private final Actions mAction;

ItemEvent(Item item, Actions action) {
	mItem = item;
	mAction = action;
}

public Item getItem() {
	return mItem;
}

public Actions getAction() {
	return mAction;
}

enum Actions {
	ADD,
	EDIT,
	REMOVE,
}
}
