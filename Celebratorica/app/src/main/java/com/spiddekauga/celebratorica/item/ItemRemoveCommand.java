package com.spiddekauga.celebratorica.item;

import com.spiddekauga.android.ui.SnackbarUndoCommand;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.utils.EventBus;

/**
 * Remove a celebration
 */
class ItemRemoveCommand extends SnackbarUndoCommand {
private final EventBus mEventBus = EventBus.getInstance();
private Item mItem;

ItemRemoveCommand(Item item) {
	mItem = item;
}

@Override
public boolean undo() {
	mEventBus.post(new ItemEvent(mItem, ItemEvent.Actions.ADD));
	showSnackbar(R.string.item_add_success);
	return true;
}

@Override
public boolean execute() {
	mEventBus.post(new ItemEvent(mItem, ItemEvent.Actions.REMOVE));
	showSnackbar(R.string.item_removed);
	return true;
}
}
