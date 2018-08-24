package io.blushine.rmw.item;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;

import de.mrapp.android.dialog.MaterialDialog;
import io.blushine.android.common.ObjectEvent;
import io.blushine.android.ui.SnackbarHelper;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * Edit a category
 */
public class CategoryEditFragment extends CategoryDialogFragment {
private Category mCategory = new Category();

/**
 * Set the arguments from the specific category
 * @param category set arguments from this category
 */
public void setArguments(Category category) {
	Bundle bundle = new Bundle(3);
	bundle.putString(CATEGORY_ID_KEY, category.getId());
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
	
	mCategory.setId(getArgument(CATEGORY_ID_KEY));
	mCategory.setName(getArgument(NAME_KEY));
	mCategory.setOrder(getArgument(ORDER_KEY));
}

@Override
protected String getTitle() {
	return getString(R.string.edit);
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
	dismiss();
}

/**
 * Remove the category
 */
private void removeCategory() {
	MaterialDialog dialog = new MaterialDialog.Builder(getContext())
			.setTitle(R.string.category_remove_dialog_title)
			.setMessage(R.string.category_remove_dialog_message)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CategoryRemoveCommand removeCommand = new CategoryRemoveCommand(mCategory);
					removeCommand.execute();
					
					dismiss();
				}
			})
			.show();
}
}
