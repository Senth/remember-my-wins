package io.blushine.rmw.item;

import java.util.List;

import io.blushine.android.common.ObjectEvent;

/**
 * Category event
 */
class CategoryEvent extends ObjectEvent<Category> {

CategoryEvent(Actions action) {
	super(action);
}

CategoryEvent(Actions action, Category category) {
	super(action, category);
}

CategoryEvent(Actions action, List<Category> categories) {
	super(action, categories);
}
}
