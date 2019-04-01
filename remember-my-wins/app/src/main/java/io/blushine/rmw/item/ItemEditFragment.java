package io.blushine.rmw.item;

import android.os.Bundle;
import android.view.MenuItem;

import io.blushine.android.common.ObjectEvent;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * Edit an existing celebration
 */
public class ItemEditFragment extends ItemDialogFragment {
private static final String ITEM_ARG_KEY = "item";
private Item mItem;

@Override
public void onViewStateRestored(Bundle savedInstanceState) {
	super.onViewStateRestored(savedInstanceState);
	
	setBackMessage(R.string.discard_changes);
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
	
	EventBus.getInstance().post(new ItemEvent(ItemEvent.Actions.EDIT, mItem));
	
	// Go back to list
	dismiss();
}

/**
 * Remove the item
 */
private void removeItem() {
	EventBus.getInstance().post(new ItemEvent(ObjectEvent.Actions.REMOVE, mItem));
	
	// Go back to list
	dismiss();
}

@Override
protected void onDeclareArguments() {
	super.onDeclareArguments();
	declareArgument(ITEM_ARG_KEY, ArgumentRequired.REQUIRED);
	declareArgument(TEXT_SAVE_KEY, ArgumentRequired.REQUIRED);
	declareArgument(DATE_SAVE_KEY, ArgumentRequired.REQUIRED);
}

@Override
protected void onArgumentsSet() {
	super.onArgumentsSet();
	mItem = getArgument(ITEM_ARG_KEY);
}

/**
 * Set Arguments
 * @param category the category of the item we're editing
 * @param item the item we're editing
 */
public void setArguments(Category category, Item item) {
	setArgument(category);
	Bundle bundle = new Bundle();
	bundle.putParcelable(ITEM_ARG_KEY, item);
	bundle.putString(TEXT_SAVE_KEY, item.getText());
	bundle.putString(DATE_SAVE_KEY, item.getDateFromFormat());
	addArguments(bundle);
}
}
