package io.blushine.rmw.item;

import java.util.List;

import io.blushine.android.common.ObjectEvent;

/**
 * Category event
 */
class CategoryEvent extends ObjectEvent<Category> {

CategoryEvent(Category category, Actions action) {
	super(category, action);
}

CategoryEvent(List<Category> categories, Actions action) {
	super(categories, action);
}
}
