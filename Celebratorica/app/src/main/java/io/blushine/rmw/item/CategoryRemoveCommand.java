package io.blushine.rmw.item;

import java.util.ArrayList;
import java.util.List;

import io.blushine.android.common.ObjectEvent;
import io.blushine.android.ui.SnackbarUndoCommand;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * Remove a category, with undo functionality
 */
class CategoryRemoveCommand extends SnackbarUndoCommand {
private static final EventBus mEventBus = EventBus.getInstance();
private final Category mCategory;
private final List<Item> mItems = new ArrayList<>();

CategoryRemoveCommand(Category category) {
	mCategory = category;
	// TODO remove category
	ItemRepo.getInstance().getItems(mCategory.getId());
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
	return true;
}
}
