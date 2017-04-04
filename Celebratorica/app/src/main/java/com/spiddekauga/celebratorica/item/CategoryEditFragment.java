package com.spiddekauga.celebratorica.item;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.spiddekauga.android.ui.SnackbarHelper;
import com.spiddekauga.android.util.ObjectEvent;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.utils.EventBus;

/**
 * Edit a category
 */
public class CategoryEditFragment extends CategoryDialogFragment {
private static final String TAG = CategoryEditFragment.class.getSimpleName();
private Category mCategory = new Category();

/**
 * Set the arguments from the specific category
 * @param category set arguments from this category
 */
public void setArguments(Category category) {
	Bundle bundle = new Bundle(3);
	bundle.putLong(CATEGORY_ID_KEY, category.getCategoryId());
	bundle.putString(NAME_KEY, category.getName());
	bundle.putInt(ORDER_KEY, category.getOrder());
	
	setArguments(bundle);
}

@Override
protected void onDeclareArguments() {
	super.onDeclareArguments();
	declareArgument(CATEGORY_ID_KEY, ArgumentRequired.REQUIRED);
	declareArgument(NAME_KEY, ArgumentRequired.REQUIRED);
	declareArgument(ORDER_KEY, ArgumentRequired.REQUIRED);
}

@Override
public void onViewStateRestored(Bundle savedInstanceState) {
	super.onViewStateRestored(savedInstanceState);
	
	if (savedInstanceState != null) {
		mCategory.setCategoryId((Long) getArgument(CATEGORY_ID_KEY));
		mCategory.setName((String) getArgument(NAME_KEY));
		mCategory.setOrder((Integer) getArgument(ORDER_KEY));
	} else {
		Log.w(TAG, "onViewStateRestored() — No arguments set!");
	}
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
		saveCategory();
		return true;
	
	case R.id.action_erase:
		removeCategory();
		return true;
	
	default:
		return false;
	}
}

/**
 * Save the edited category
 */
private void saveCategory() {
	setCategoryFromFields(mCategory);
	
	EventBus.getInstance().post(new CategoryEvent(mCategory, ObjectEvent.Actions.EDIT));
	SnackbarHelper.showSnackbar(R.string.edit_success);
	
	// Go back to list
	getFragmentManager().popBackStackImmediate();
}

/**
 * Remove the category
 */
private void removeCategory() {
	// TODO
}
}
