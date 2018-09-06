package io.blushine.rmw.item;

import android.content.res.Resources;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.blushine.android.common.ObjectEvent;
import io.blushine.android.firebase.FirebaseAuth;
import io.blushine.rmw.R;
import io.blushine.rmw.settings.SettingsRepo;
import io.blushine.rmw.settings.StorageLocationSetEvent;
import io.blushine.rmw.util.AppActivity;
import io.blushine.rmw.util.Sqlite;
import io.blushine.utils.EventBus;

/**
 * Controller for getting celebration items and lists
 */
class ItemRepo {
private static final EventBus mEventBus = EventBus.getInstance();
private static ItemRepo mInstance = null;
private ItemGateway mCurrentGateway;

/**
 * Enforces singleton pattern
 */
private ItemRepo() {
	EventBus.getInstance().register(this);
	
	switch (SettingsRepo.INSTANCE.getStorageLocation()) {
	case CLOUD:
		mCurrentGateway = new ItemFirestoreGateway();
		break;
	case LOCAL:
		mCurrentGateway = new ItemSqliteGateway();
		break;
	case NOT_SET:
		// TODO remove, just for implementation and testing
		mCurrentGateway = new ItemFirestoreGateway();
		break;
	}
	
	// Add default categories if first time
	if (!ItemPrefsGateway.INSTANCE.hasAddedDefaultCategories()) {
		createDefaultCategories();
		ItemPrefsGateway.INSTANCE.setAddedDefaultCategories(true);
	}
}

private void createDefaultCategories() {
	Resources resources = AppActivity.getActivity().getResources();
	
	List<Category> defaultCategories = new ArrayList<>(2);
	Category category = new Category();
	category.setName(resources.getString(R.string.category_default_1_name));
	category.setOrder(1);
	defaultCategories.add(category);
	
	category = new Category();
	category.setName(resources.getString(R.string.category_default_2_name));
	category.setOrder(2);
	defaultCategories.add(category);
	
	addCategories(defaultCategories);
}

/**
 * Add new categories. Will automatically set the category id
 * @param categories the categories to add
 */
private void addCategories(List<Category> categories) {
	for (Category category : categories) {
		mCurrentGateway.addCategory(category);
	}
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

@SuppressWarnings("unused")
@Subscribe
public void onStorageLocationSetEvent(StorageLocationSetEvent event) {
	switch (event.getStorageLocation()) {
	
	case CLOUD:
		FirebaseAuth.INSTANCE.getCurrentUser();
		mCurrentGateway = new ItemFirestoreGateway();
		break;
	case LOCAL:
		if (!Sqlite.isInitialized()) {
			Sqlite.init();
		}
		mCurrentGateway = new ItemSqliteGateway();
		break;
	case NOT_SET:
		throw new IllegalStateException("Storage location should never be NOT SET after it being set");
	}
}

/**
 * Get all items from the specified categories, sorted by date. The result (all items) will be sent as a
 * {@link ItemEvent} with the action set as {@link io.blushine.android.common.ObjectEvent.Actions#GET_RESPONSE}.
 * @param categoryId the category to get all items from
 */
void getItems(String categoryId) {
	mCurrentGateway.getItems(categoryId);
}

@SuppressWarnings("unused")
@Subscribe
public void onItem(ItemEvent event) {
	switch (event.getAction()) {
	case ADD:
		mCurrentGateway.addItems(event.getObjects());
		mEventBus.post(new ItemEvent(ObjectEvent.Actions.ADDED, event.getObjects()));
		break;
	case EDIT:
		mCurrentGateway.updateItems(event.getObjects());
		mEventBus.post(new ItemEvent(ObjectEvent.Actions.EDITED, event.getObjects()));
		break;
	case REMOVE:
		mCurrentGateway.removeItems(event.getObjects());
		mEventBus.post(new ItemEvent(ObjectEvent.Actions.REMOVED, event.getObjects()));
		break;
	}
}

/**
 * Get all categories. The result (all categories) will be sent as a {@link CategoryEvent} with the
 * action set as {@link io.blushine.android.common.ObjectEvent.Actions#GET_RESPONSE}.
 */
void getCategories() {
	mCurrentGateway.getCategories();
}

/**
 * Get all items from all categories, sorted by date. The result (all items) will be sent as a
 * {@link ItemEvent} with the action set as {@link io.blushine.android.common.ObjectEvent.Actions#GET_RESPONSE}.
 */
void getItems() {
	mCurrentGateway.getItems(null);
}

@SuppressWarnings("unused")
@Subscribe
public void onCategory(CategoryEvent event) {
	switch (event.getAction()) {
	case ADD:
		addCategories(event.getObjects());
		break;
	case EDIT:
		mCurrentGateway.updateCategories(event.getObjects());
		break;
	case REMOVE:
		removeCategories(event.getObjects());
		break;
	}
}

/**
 * Remove categories
 * @param categories the categories to remove.
 */
private void removeCategories(List<Category> categories) {
	for (Category category : categories) {
		mCurrentGateway.removeCategory(category);
	}
}
}
