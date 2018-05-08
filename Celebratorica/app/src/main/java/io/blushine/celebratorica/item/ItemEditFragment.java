package io.blushine.celebratorica.item;

import android.os.Bundle;
import android.view.MenuItem;

import io.blushine.android.ui.SnackbarHelper;
import io.blushine.celebratorica.R;
import io.blushine.utils.EventBus;

/**
 * Edit an existing celebration
 */
public class ItemEditFragment extends ItemDialogFragment {
private static final String ITEM_ID_SAVE_KEY = "item_id";
private Item mItem;

@Override
public void onViewStateRestored(Bundle savedInstanceState) {
	super.onViewStateRestored(savedInstanceState);
	
	if (savedInstanceState != null) {
		long itemId = savedInstanceState.getLong(ITEM_ID_SAVE_KEY, -1);
		mItem = new Item();
		mItem.setItemId(itemId);
	} else {
		setFields(mItem);
	}
	
	setBackMessage(R.string.discard_changes);
}

@Override
public void onSaveInstanceState(Bundle outState) {
	outState.putLong(ITEM_ID_SAVE_KEY, mItem.getItemId());
	
	super.onSaveInstanceState(outState);
}

@Override
protected String getTitle() {
	String title = getString(R.string.item_edit_header);
	return String.format(title, getCategory().getName());
}

@Override
protected int getMenu() {
	return R.menu.menu_save_remove;
}

@Override
public boolean onMenuItemClick(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.action_save:
		saveItem();
		return true;
	
	case R.id.action_erase:
		removeItem();
		return true;
	
	default:
		return false;
	}
}

/**
 * Save the edited item
 */
private void saveItem() {
	setItemFromFields(mItem);
	
	EventBus.getInstance().post(new ItemEvent(mItem, ItemEvent.Actions.EDIT));
	SnackbarHelper.showSnackbar(R.string.item_edit_success);
	
	// Go back to list
	dismiss();
}

/**
 * Remove the item
 */
private void removeItem() {
	ItemRemoveCommand removeCommand = new ItemRemoveCommand(mItem);
	removeCommand.execute();
	
	// Go back to list
	dismiss();
}

/**
 * Set the item to be edited. Should be set before calling {@link #show()}
 * @param item the item to edit
 */
void setEditItem(Item item) {
	mItem = item;
}
}
