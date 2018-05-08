package io.blushine.celebratorica.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import io.blushine.android.ui.SnackbarHelper;
import io.blushine.android.util.ObjectEvent;
import io.blushine.celebratorica.R;
import io.blushine.utils.EventBus;

/**
 * Adds a new category
 */
public class CategoryAddFragment extends CategoryDialogFragment {
@Override
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreatedImpl(view, savedInstanceState);
	
	focusNameTextField();
	setBackMessage(R.string.category_add_discard);
}

@Override
protected String getTitle() {
	return getString(R.string.category_add_header);
}

@Override
protected int getMenu() {
	return R.menu.menu_save;
}

@Override
public boolean onMenuItemClick(MenuItem item) {
	if (validateTextFields()) {
		addItem();
	}
	return true;
}

private void addItem() {
	Category category = new Category();
	setCategoryFromFields(category);
	
	CategoryEvent categoryEvent = new CategoryEvent(category, ObjectEvent.Actions.ADD);
	EventBus.getInstance().post(categoryEvent);
	SnackbarHelper.showSnackbar(R.string.category_add_success);
	
	dismiss();
}
}
