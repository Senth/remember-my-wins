package io.blushine.rmw.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import io.blushine.android.common.ObjectEvent;
import io.blushine.android.ui.SnackbarHelper;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * Adds a new category
 */
public class CategoryAddFragment extends CategoryDialogFragment {
private static final String CATEGORY_COUNT_ARG = "category_count";
private int mNextOrder = 1;

@Override
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreatedImpl(view, savedInstanceState);
	
	focusNameTextField();
	setBackMessage(R.string.category_add_discard);
}

public void setArguments(int categoryCount) {
	Bundle bundle = new Bundle(1);
	bundle.putInt(CATEGORY_COUNT_ARG, categoryCount);
	setArguments(bundle);
}

@Override
protected void onDeclareArguments() {
	super.onDeclareArguments();
	declareArgument(CATEGORY_COUNT_ARG, ArgumentRequired.REQUIRED);
}

@Override
protected void onArgumentsSet() {
	super.onArgumentsSet();
	
	mNextOrder = ((int) getArgument(CATEGORY_COUNT_ARG)) + 1;
	
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
		saveCategory();
	}
	return true;
}

protected void saveCategory() {
	Category category = new Category();
	setCategoryFromFields(category);
	category.setOrder(mNextOrder);
	
	CategoryEvent categoryEvent = new CategoryEvent(ObjectEvent.Actions.ADD, category);
	EventBus.getInstance().post(categoryEvent);
	SnackbarHelper.showSnackbar(R.string.category_add_success);
	
	dismiss();
}
}
