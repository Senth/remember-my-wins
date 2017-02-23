package com.spiddekauga.celebratorica.celebration;

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
public class CelebrationEditFragment extends CelebrationDialogFragment {
private static final String ITEM_ID_SAVE_KEY = "item_id";
private static final String LIST_ID_SAVE_KEY = "list_id";
private Celebration mCelebration;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = super.onCreateView(inflater, container, savedInstanceState);

	if (savedInstanceState != null) {
		long listId = savedInstanceState.getLong(LIST_ID_SAVE_KEY, -1);
		long itemId = savedInstanceState.getLong(ITEM_ID_SAVE_KEY, -1);
		mCelebration = new Celebration();
		mCelebration.setListId(listId);
		mCelebration.setItemId(itemId);
	} else {
		setFields(mCelebration);
	}

	setBackMessage(R.string.discard_changes);

	return view;
}


@Override
public void onSaveInstanceState(Bundle outState) {
	outState.putLong(LIST_ID_SAVE_KEY, mCelebration.getListId());
	outState.putLong(ITEM_ID_SAVE_KEY, mCelebration.getItemId());

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
		saveCelebration();
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
private void saveCelebration() {
	setCelebrationFromFields(mCelebration);

	EventBus.getInstance().post(new CelebrationEvent(mCelebration, CelebrationEvent.Actions.EDIT));
	SnackbarHelper.showSnackbar(R.string.item_edit_success);

	// Go back to list
	getFragmentManager().popBackStackImmediate();
}

/**
 * Remove the celebration
 */
private void removeCelebration() {
	CelebrationRemoveCommand removeCommand = new CelebrationRemoveCommand(mCelebration);
	removeCommand.execute();

	// Go back to list
	getFragmentManager().popBackStackImmediate();
}

/**
 * Set the celebration to be edited. Should be set before calling {@link #show()}
 * @param celebration the celebration to edit
 */
void setEditCelebration(Celebration celebration) {
	mCelebration = celebration;
}
}
