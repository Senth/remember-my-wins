package io.blushine.celebratorica.item;

import java.util.List;

import io.blushine.android.common.ObjectEvent;
import io.blushine.android.ui.SnackbarUndoCommand;
import io.blushine.celebratorica.R;
import io.blushine.utils.EventBus;

/**
 * Remove a category, with undo functionality
 */
class CategoryRemoveCommand extends SnackbarUndoCommand {
private static final EventBus mEventBus = EventBus.getInstance();
private final Category mCategory;
private final List<Item> mItems;

CategoryRemoveCommand(Category category) {
	mCategory = category;
	mItems = ItemRepo.getInstance().getItems(mCategory.getCategoryId());
}

@Override
public boolean undo() {
	mEventBus.post(new CategoryEvent(mCategory, ObjectEvent.Actions.ADD));
	if (!mItems.isEmpty()) {
		mEventBus.post(new ItemEvent(mItems, ObjectEvent.Actions.ADD));
	}
	showSnackbar(R.string.category_restored);
	return true;
}

@Override
public boolean execute() {
	mEventBus.post(new CategoryEvent(mCategory, ObjectEvent.Actions.REMOVE));
	if (!mItems.isEmpty()) {
		mEventBus.post(new ItemEvent(mItems, ObjectEvent.Actions.REMOVE));
	}
	showSnackbarWithUndo(R.string.category_removed);
	return false;
}
}
