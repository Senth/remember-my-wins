package com.spiddekauga.celebratorica.item;

import com.spiddekauga.android.util.ObjectEvent;

/**
 * Category event
 */
class CategoryEvent extends ObjectEvent {
private final Category mCategory;

CategoryEvent(Category category, Actions action) {
	super(action);
	mCategory = category;
}

public Category getCategory() {
	return mCategory;
}
}
