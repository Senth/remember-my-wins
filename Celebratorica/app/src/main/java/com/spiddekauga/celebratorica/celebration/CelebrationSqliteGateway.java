package com.spiddekauga.celebratorica.celebration;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;

import com.spiddekauga.android.sqlite.SqliteGateway;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.AppActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway for getting celebration and list items
 */
class CelebrationSqliteGateway extends SqliteGateway {
/**
 * Get all celebrations in a specified list
 * @param listId the list id to get the celebration from
 * @return list of all celebrations in the specified list
 */
List<Celebration> getCelebrations(long listId) {
	Resources resources = AppActivity.getActivity().getResources();

	String sql = "SELECT " +
			resources.getString(R.string.table_item_id) + ", " +
			resources.getString(R.string.table_item_text) + ", " +
			resources.getString(R.string.table_item_date) +
			" FROM " + resources.getString(R.string.table_item) +
			" WHERE " + resources.getString(R.string.table_list_id) + "=" + listId;


	List<Celebration> celebrations = new ArrayList<>();
	Cursor cursor = rawQuery(sql);
	while (cursor.moveToNext()) {
		Celebration celebration = new Celebration();
		celebration.setListId(listId);
		int i = 0;
		celebration.setItemId(cursor.getLong(i++));
		celebration.setText(cursor.getString(i++));
		celebration.setDate(cursor.getLong(i++));

		celebrations.add(celebration);
	}

	return celebrations;
}

/**
 * Add a new celebration. Will automatically set the item id.
 * @param celebration the celebration to add
 */
void addCelebration(Celebration celebration) {
	Resources resources = AppActivity.getActivity().getResources();

	ContentValues contentValues = new ContentValues();
	contentValues.put(resources.getString(R.string.table_list_id), celebration.getListId());
	contentValues.put(resources.getString(R.string.table_item_text), celebration.getText());
	contentValues.put(resources.getString(R.string.table_item_date), celebration.getDateTime());

	long id = insert(resources.getString(R.string.table_item), contentValues);
	celebration.setItemId(id);
}

/**
 * Update a celebration item.
 * @param celebration the celebration to update
 */
void updateCelebration(Celebration celebration) {
	Resources resources = AppActivity.getActivity().getResources();

	ContentValues contentValues = new ContentValues();
	contentValues.put(resources.getString(R.string.table_item_text), celebration.getText());
	contentValues.put(resources.getString(R.string.table_item_date), celebration.getDateTime());

	String table = resources.getString(R.string.table_item);
	String where = resources.getString(R.string.table_item_id) + "=" + celebration.getItemId();
	update(table, contentValues, where);
}

/**
 * Remove a celebration item.
 * @param celebration the celebration item to remove
 */
void removeCelebration(Celebration celebration) {
	Resources resources = AppActivity.getActivity().getResources();

	String table = resources.getString(R.string.table_item);
	String where = resources.getString(R.string.table_item_id) + "=" + celebration.getItemId();
	delete(table, where);
}
}
