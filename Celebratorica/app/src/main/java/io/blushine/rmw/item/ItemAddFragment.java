package io.blushine.rmw.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import io.blushine.android.AppFragmentHelper;
import io.blushine.android.ui.SnackbarHelper;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * Adds a new item to a category
 */
public class ItemAddFragment extends ItemDialogFragment {
@Override
public boolean onMenuItemClick(MenuItem item) {
	if (validateTextFields()) {
		addItem();
	}
	return true;
}

private void addItem() {
	Item item = new Item();
	setItemFromFields(item);
	
	ItemEvent itemEvent = new ItemEvent(ItemEvent.Actions.ADD, item);
	EventBus.getInstance().post(itemEvent);
	SnackbarHelper.showSnackbar(R.string.item_add_success);
	
	dismiss();
}

@Override
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreatedImpl(view, savedInstanceState);
	
	AppFragmentHelper.focusEditText(getTextField());
	setBackMessage(R.string.item_add_discard);
}

@Override
protected String getTitle() {
	String title = getString(R.string.item_add_header);
	return String.format(title, getCategory().getName());
}

@Override
protected int getMenu() {
	return R.menu.menu_save;
}
}
