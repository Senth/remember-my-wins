package io.blushine.celebratorica.item;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.blushine.android.common.ObjectEvent;
import io.blushine.utils.EventBus;

/**
 * Controller for getting celebration items and lists
 */
class ItemRepo {
private static final EventBus mEventBus = EventBus.getInstance();
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
 * Get the specified category
 * @param categoryId the category to get
 * @return category with the categoryId, null if not found
 */
Category getCategory(long categoryId) {
	return mSqliteGateway.getCategory(categoryId);
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
		addItems(event.getObjects());
		mEventBus.post(new ItemEvent(event.getObjects(), ObjectEvent.Actions.ADDED));
		break;
	case EDIT:
		editItems(event.getObjects());
		mEventBus.post(new ItemEvent(event.getObjects(), ObjectEvent.Actions.EDITED));
		break;
	case REMOVE:
		removeItems(event.getObjects());
		mEventBus.post(new ItemEvent(event.getObjects(), ObjectEvent.Actions.REMOVED));
		break;
	}
}

/**
 * Add new item. Will automatically set the item id if no item id has been specified
 * @param items the items to add
 */
private void addItems(List<Item> items) {
	for (Item item : items) {
		mSqliteGateway.addItem(item);
	}
}

/**
 * Update items
 * @param items the item to update
 */
private void editItems(List<Item> items) {
	for (Item item : items) {
		mSqliteGateway.updateItem(item);
	}
}

/**
 * Remove items.
 * @param items the items to remove
 */
private void removeItems(List<Item> items) {
	for (Item item : items) {
		mSqliteGateway.removeItem(item);
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onCategory(CategoryEvent event) {
	switch (event.getAction()) {
	case ADD:
		addCategories(event.getObjects());
		mEventBus.post(new CategoryEvent(event.getObjects(), ObjectEvent.Actions.ADDED));
		break;
	case EDIT:
		editCategories(event.getObjects());
		mEventBus.post(new CategoryEvent(event.getObjects(), ObjectEvent.Actions.EDITED));
		break;
	case REMOVE:
		removeCategories(event.getObjects());
		mEventBus.post(new CategoryEvent(event.getObjects(), ObjectEvent.Actions.REMOVED));
		break;
	}
}

/**
 * Add new categories. Will automatically set the category id
 * @param categories the categories to add
 */
private void addCategories(List<Category> categories) {
	for (Category category : categories) {
		mSqliteGateway.addCategory(category);
	}
}

/**
 * Update the specified categories
 * @param categories the categories to update
 */
private void editCategories(List<Category> categories) {
	for (Category category : categories) {
		mSqliteGateway.updateCategory(category);
	}
}

/**
 * Remove categories
 * @param categories the categories to remove.
 */

private void removeCategories(List<Category> categories) {
	for (Category category : categories) {
		mSqliteGateway.removeCategory(category);
	}
}
}
