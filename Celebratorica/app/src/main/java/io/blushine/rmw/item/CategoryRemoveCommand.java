package io.blushine.rmw.item;

import com.squareup.otto.Subscribe;

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
private List<Item> mItems = null;
private boolean mExecuteOnGetItems = false;

CategoryRemoveCommand(Category category) {
	mCategory = category;
	mEventBus.register(this);
	ItemRepo.getInstance().getItems(mCategory.getId());
}

@Override
public boolean undo() {
	mEventBus.post(new CategoryEvent(ObjectEvent.Actions.ADD, mCategory));
	if (!mItems.isEmpty()) {
		mEventBus.post(new ItemEvent(ObjectEvent.Actions.ADD, mItems));
	}
	showSnackbar(R.string.category_restored);
	return true;
}

@Override
public synchronized boolean execute() {
	if (mItems != null) {
		remove();
	} else {
		mExecuteOnGetItems = true;
	}
	return true;
}

private void remove() {
	mEventBus.post(new CategoryEvent(ObjectEvent.Actions.REMOVE, mCategory));
}

@SuppressWarnings("unused")
@Subscribe
public synchronized void onItemEvent(ItemEvent event) {
	if (event.getCategoryId().equals(mCategory.getId())) {
		switch (event.getAction()) {
		case GET_RESPONSE:
			mItems = event.getObjects();
			mEventBus.unregister(this);
			
			if (mExecuteOnGetItems) {
				mExecuteOnGetItems = false;
				remove();
			}
			break;
		
		case GET_FAILED:
			mEventBus.unregister(this);
			mEventBus.post(new CategoryEvent(ObjectEvent.Actions.REMOVE_FAILED, mCategory));
			break;
		}
	}
}
}
