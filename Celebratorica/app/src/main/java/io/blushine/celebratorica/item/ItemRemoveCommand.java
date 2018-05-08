package io.blushine.celebratorica.item;

import io.blushine.android.ui.SnackbarUndoCommand;
import io.blushine.celebratorica.R;
import io.blushine.utils.EventBus;

/**
 * Remove an item, with undo functionality
 */
class ItemRemoveCommand extends SnackbarUndoCommand {
private static final EventBus mEventBus = EventBus.getInstance();
private final Item mItem;

ItemRemoveCommand(Item item) {
	mItem = item;
}

@Override
public boolean undo() {
	mEventBus.post(new ItemEvent(mItem, ItemEvent.Actions.ADD));
	showSnackbar(R.string.item_restored);
	return true;
}

@Override
public boolean execute() {
	mEventBus.post(new ItemEvent(mItem, ItemEvent.Actions.REMOVE));
	showSnackbarWithUndo(R.string.item_removed);
	return true;
}
}
