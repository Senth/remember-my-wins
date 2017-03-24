package com.spiddekauga.celebratorica.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.ui.SnackbarHelper;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.utils.EventBus;

/**
 * Edit an existing celebration
 */
public class ItemEditFragment extends ItemDialogFragment {
private static final String ITEM_ID_SAVE_KEY = "item_id";
private Item mItem;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = super.onCreateView(inflater, container, savedInstanceState);

	if (savedInstanceState != null) {
		long itemId = savedInstanceState.getLong(ITEM_ID_SAVE_KEY, -1);
		mItem = new Item();
		mItem.setItemId(itemId);
	} else {
		setFields(mItem);
	}

	setBackMessage(R.string.discard_changes);

	return view;
}


@Override
public void onSaveInstanceState(Bundle outState) {
	outState.putLong(ITEM_ID_SAVE_KEY, mItem.getItemId());

	super.onSaveInstanceState(outState);
}

@Override
protected int getTitle() {
	return R.string.edit;
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
		removeCelebration();
		return true;

	default:
		return false;
	}
}

/**
 * Save the edited celebration
 */
private void saveItem() {
	setItemFromFields(mItem);
	
	EventBus.getInstance().post(new ItemEvent(mItem, ItemEvent.Actions.EDIT));
	SnackbarHelper.showSnackbar(R.string.item_edit_success);

	// Go back to list
	getFragmentManager().popBackStackImmediate();
}

/**
 * Remove the celebration
 */
private void removeCelebration() {
	ItemRemoveCommand removeCommand = new ItemRemoveCommand(mItem);
	removeCommand.execute();

	// Go back to list
	getFragmentManager().popBackStackImmediate();
}

/**
 * Set the item to be edited. Should be set before calling {@link #show()}
 * @param item the item to edit
 */
void setEditCelebration(Item item) {
	mItem = item;
}
}
