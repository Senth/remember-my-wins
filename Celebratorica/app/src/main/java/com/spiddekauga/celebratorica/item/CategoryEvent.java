package com.spiddekauga.celebratorica.item;

import com.spiddekauga.android.util.ObjectEvent;

import java.util.List;

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
