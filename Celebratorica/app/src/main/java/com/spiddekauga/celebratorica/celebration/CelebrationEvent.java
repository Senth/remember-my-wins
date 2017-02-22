package com.spiddekauga.celebratorica.celebration;

/**
 * Celebration event
 */
class CelebrationEvent {
private final Celebration mCelebration;
private final Actions mAction;

CelebrationEvent(Celebration celebration, Actions action) {
	mCelebration = celebration;
	mAction = action;
}

public Celebration getCelebration() {
	return mCelebration;
}

public Actions getAction() {
	return mAction;
}

enum Actions {
	ADD,
	EDIT,
	REMOVE,
}
}
