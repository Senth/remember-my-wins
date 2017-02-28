package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.AppFragmentHelper;
import com.spiddekauga.android.ui.SnackbarHelper;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.utils.EventBus;

/**
 * Adds a new celebration item to a specific list
 */
public class CelebrationAddFragment extends CelebrationDialogFragment {
@Override
public boolean onMenuItemClick(MenuItem item) {
	if (validateTextFields()) {
		addCelebration();
	}
	return true;
}

private void addCelebration() {
	Celebration celebration = new Celebration();
	setCelebrationFromFields(celebration);

	CelebrationEvent celebrationEvent = new CelebrationEvent(celebration, CelebrationEvent.Actions.ADD);
	EventBus.getInstance().post(celebrationEvent);
	SnackbarHelper.showSnackbar(R.string.item_add_success);

	dismiss();
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = super.onCreateView(inflater, container, savedInstanceState);

	AppFragmentHelper.focusEditText(getTextField());


	setBackMessage(R.string.item_add_discard);

	return view;
}

@Override
protected int getTitle() {
	return R.string.item_add_header;
}

@Override
protected int getMenu() {
	return R.menu.menu_save;
}
}
