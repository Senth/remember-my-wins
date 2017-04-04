package com.spiddekauga.celebratorica.item;

import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for getting celebration items and lists
 */
class ItemRepo {
private static ItemRepo mInstance = null;
private ItemSqliteGateway mSqliteGateway = new ItemSqliteGateway();

/**
 * Enforces singleton pattern
 */
private ItemRepo() {
	EventBus.getInstance().register(this);
}

/**
 * Get singleton instance
 * @return get instance
 */
static ItemRepo getInstance() {
	if (mInstance == null) {
		mInstance = new ItemRepo();
	}
	return mInstance;
}

/**
 * Get all items in a specified list
 * @param categoryId the category id to get the items from
 * @return list of all items in the specified category
 */
List<Item> getItems(long categoryId) {
	return mSqliteGateway.getItems(categoryId);
}

/**
 * Get all items from all categories, sorted by date
 * @return list of all items from all categories, sorted by date
 */
List<Item> getItems() {
	// TODO get items from all categories
	return new ArrayList<>();
}

/**
 * Get all item lists
 * @return list of all item lists
 */
List<Category> getCategories() {
	return mSqliteGateway.getCategories();
}

@SuppressWarnings("unused")
@Subscribe
public void onItem(ItemEvent event) {
	switch (event.getAction()) {
	case ADD:
		addItem(event.getItem());
		break;
	case EDIT:
		editItem(event.getItem());
		break;
	case REMOVE:
		removeItem(event.getItem());
		break;
	}
}

/**
 * Add a new item. Will automatically set the item id.
 * @param item the item to add
 */
private void addItem(Item item) {
	mSqliteGateway.addItem(item);
}

/**
 * Update a item item.
 * @param item the item to update
 */
private void editItem(Item item) {
	mSqliteGateway.updateItem(item);
}

/**
 * Remove a item item.
 * @param item the item item to remove
 */
private void removeItem(Item item) {
	mSqliteGateway.removeItem(item);
}

@SuppressWarnings("unused")
@Subscribe
public void onCategory(CategoryEvent event) {
	switch (event.getAction()) {
	case ADD:
		addCategory(event.getCategory());
		break;
	case EDIT:
		editCategory(event.getCategory());
		break;
	case REMOVE:
		removeCategory(event.getCategory());
		break;
	}
}

/**
 * Add a new category. Will automatically set the category id
 * @param category the category to add
 */
private void addCategory(Category category) {
	mSqliteGateway.addCategory(category);
}

/**
 * Update a category.
 * @param category the category to update
 */
private void editCategory(Category category) {
	mSqliteGateway.updateCategory(category);
}

/**
 * Remove category.
 * @param category the category to remove.
 */
private void removeCategory(Category category) {
	mSqliteGateway.removeCategory(category);
}
}
