package io.blushine.rmw.item;

import android.content.res.Resources;

import com.squareup.otto.Subscribe;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.blushine.android.firebase.FirebaseAuth;
import io.blushine.android.ui.SnackbarHelper;
import io.blushine.rmw.R;
import io.blushine.rmw.item.firestore.ItemFirestoreGateway;
import io.blushine.rmw.settings.SettingsRepo;
import io.blushine.rmw.settings.StorageLocationSetEvent;
import io.blushine.rmw.settings.StorageLocations;
import io.blushine.rmw.util.AppActivity;
import io.blushine.rmw.util.Sqlite;
import io.blushine.utils.EventBus;

import static io.blushine.rmw.item.ItemEventKt.GET_ALL_ITEMS;

/**
 * Controller for getting celebration items and lists
 */
class ItemRepo {
private static ItemRepo mInstance = null;
private ItemGateway mCurrentGateway;
private List<Item> mUndoItems = null;

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
		// mCurrentGateway = new ItemEmptyGateway();
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
public void onStorageLocation(StorageLocations storageLocation) {
	switch (storageLocation) {
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
	
	EventBus.getInstance().post(new StorageLocationSetEvent(storageLocation));
}

/**
 * Get all items from the specified categories, sorted by date. The result (all items) will be sent as a
 * {@link ItemEvent} with the action set as {@link io.blushine.android.common.ObjectEvent.Actions#GET_RESPONSE}.
 * @param categoryId the category to get all items from
 */
void getItems(@NotNull String categoryId) {
	mCurrentGateway.getItems(categoryId);
}

@SuppressWarnings("unused")
@Subscribe
public void onItem(ItemEvent event) {
	switch (event.getAction()) {
	case ADD:
		mCurrentGateway.addItems(event.getObjects());
		break;
	case EDIT:
		mCurrentGateway.updateItems(event.getObjects());
		break;
	case REMOVE:
		mCurrentGateway.removeItems(event.getObjects());
		break;
	
	case ADDED:
		// Added items
		if (mUndoItems == null) {
			SnackbarHelper.showSnackbar(R.string.item_add_success);
		}
		// Restored removed items
		else {
			SnackbarHelper.showSnackbar(R.string.item_restored);
			mUndoItems = null;
		}
		break;
	
	case EDITED:
		if (!SnackbarHelper.isShownOrQueued()) {
			SnackbarHelper.showSnackbar(R.string.item_edit_success);
		}
		break;
	
	case REMOVED:
		SnackbarHelper.showSnackbarUndo(R.string.item_remove_success, v -> {
			mUndoItems = event.getObjects();
			mCurrentGateway.addItems(event.getObjects());
		});
		break;
	
	case ADD_FAILED:
		// Adding items
		if (mUndoItems == null) {
			SnackbarHelper.showSnackbar(R.string.item_add_failed);
		}
		// Undoing items
		else {
			SnackbarHelper.showSnackbar(R.string.item_restore_failed);
			mUndoItems = null;
		}
		break;
	
	case EDIT_FAILED:
		SnackbarHelper.showSnackbar(R.string.item_edit_failed);
		break;
	
	case REMOVE_FAILED:
		SnackbarHelper.showSnackbar(R.string.item_remove_failed);
		break;
	
	case GET_FAILED:
		SnackbarHelper.showSnackbar(R.string.item_get_failed);
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
	mCurrentGateway.getItems(GET_ALL_ITEMS);
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
	
	case ADDED:
		if (!SnackbarHelper.isShownOrQueued()) {
			SnackbarHelper.showSnackbar(R.string.category_add_success);
		}
		break;
	
	case EDITED:
		if (!SnackbarHelper.isShownOrQueued()) {
			SnackbarHelper.showSnackbar(R.string.category_edit_success);
		}
		break;
	
	case REMOVED:
		SnackbarHelper.showSnackbar(R.string.category_remove_success);
		break;
	
	case ADD_FAILED:
		SnackbarHelper.showSnackbar(R.string.category_add_failed);
		break;
	
	case EDIT_FAILED:
		SnackbarHelper.showSnackbar(R.string.category_edit_failed);
		break;
	
	case REMOVE_FAILED:
		SnackbarHelper.showSnackbar(R.string.category_remove_failed);
		break;
	
	case GET_FAILED:
		SnackbarHelper.showSnackbar(R.string.category_get_failed);
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
