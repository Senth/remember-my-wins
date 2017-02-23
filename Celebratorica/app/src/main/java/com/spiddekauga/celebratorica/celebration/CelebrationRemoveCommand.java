package com.spiddekauga.celebratorica.celebration;

import com.spiddekauga.android.ui.SnackbarUndoCommand;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.utils.EventBus;

/**
 * Remove a celebration
 */
class CelebrationRemoveCommand extends SnackbarUndoCommand {
private final EventBus mEventBus = EventBus.getInstance();
private Celebration mCelebration;

CelebrationRemoveCommand(Celebration celebration) {
	mCelebration = celebration;
}

@Override
public boolean undo() {
	mEventBus.post(new CelebrationEvent(mCelebration, CelebrationEvent.Actions.ADD));
	showSnackbar(R.string.item_add_success);
	return true;
}

@Override
public boolean execute() {
	mEventBus.post(new CelebrationEvent(mCelebration, CelebrationEvent.Actions.REMOVE));
	showSnackbar(R.string.item_removed);
	return true;
}
}
