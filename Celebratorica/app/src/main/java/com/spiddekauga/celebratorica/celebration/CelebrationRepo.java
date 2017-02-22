package com.spiddekauga.celebratorica.celebration;

import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Controller for getting celebration items and lists
 */
class CelebrationRepo {
private static CelebrationRepo mInstance = null;
private CelebrationSqliteGateway mSqliteGateway = new CelebrationSqliteGateway();

/**
 * Enforces singleton pattern
 */
private CelebrationRepo() {
	EventBus.getInstance().register(this);
}

/**
 * Get singleton instance
 * @return get instance
 */
public static CelebrationRepo getInstance() {
	if (mInstance == null) {
		mInstance = new CelebrationRepo();
	}
	return mInstance;
}

/**
 * Get all celebrations in a specified list
 * @param listId the list id to get the celebration from
 * @return list of all celebrations in the specified list
 */
List<Celebration> getCelebrations(long listId) {
	return mSqliteGateway.getCelebrations(listId);
}

@SuppressWarnings("unused")
@Subscribe
public void onCelebration(CelebrationEvent event) {
	switch (event.getAction()) {
	case ADD:
		addCelebration(event.getCelebration());
		break;
	case EDIT:
		editCelebration(event.getCelebration());
		break;
	case REMOVE:
		removeCelebration(event.getCelebration());
		break;
	}
}

/**
 * Add a new celebration. Will automatically set the item id.
 * @param celebration the celebration to add
 */
private void addCelebration(Celebration celebration) {
	mSqliteGateway.addCelebration(celebration);
}

/**
 * Update a celebration item.
 * @param celebration the celebration to update
 */
private void editCelebration(Celebration celebration) {
	mSqliteGateway.updateCelebration(celebration);
}

/**
 * Remove a celebration item.
 * @param celebration the celebration item to remove
 */
private void removeCelebration(Celebration celebration) {
	mSqliteGateway.removeCelebration(celebration);
}
}
