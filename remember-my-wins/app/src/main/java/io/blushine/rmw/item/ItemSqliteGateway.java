package io.blushine.rmw.item;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.blushine.android.common.ObjectEvent;
import io.blushine.android.sqlite.SqliteGateway;
import io.blushine.rmw.R;
import io.blushine.rmw.util.AppActivity;
import io.blushine.utils.EventBus;

/**
 * Gateway for getting categories and items
 */
class ItemSqliteGateway extends SqliteGateway implements ItemGateway {
private static final String TAG = ItemSqliteGateway.class.getSimpleName();
private final EventBus mEventBus = EventBus.getInstance();

private static String idToString(long id) {
	return Long.toString(id);
}

public void addCategory(@NotNull Category category) {
	Resources resources = AppActivity.getActivity().getResources();
	
	
	// Update categories after this order
	if (category.getOrder() > 0) {
		try {
			String sql = "UPDATE " + resources.getString(R.string.table_category) + " SET " +
					resources.getString(R.string.table_category_order) + "=" + resources.getString(R.string.table_category_order) + "+1 " +
					"WHERE " +
					resources.getString(R.string.table_category_order) + ">=" + category.getOrder();
			execSQL(sql);
		} catch (SQLException e) {
			Log.e(TAG, "removeCategory() — Invalid SQL syntax", e);
		}
	}
	
	
	ContentValues contentValues = new ContentValues();
	contentValues.put(resources.getString(R.string.table_category_order), category.getOrder());
	contentValues.put(resources.getString(R.string.table_category_name), category.getName());
	
	
	long id = insert(resources.getString(R.string.table_category), contentValues);
	category.setId(idToString(id));
	
	mEventBus.post(new CategoryEvent(ObjectEvent.Actions.ADDED, category));
}

public void getCategories() {
	new GetCategoryTask().execute(this);
}

@Override
public void updateCategories(@NotNull List<Category> categories) {
	beginTransaction();
	
	for (Category category : categories) {
		updateCategory(category);
	}
	
	setTransactionSuccessful();
	endTransaction();
	
	mEventBus.post(new CategoryEvent(ObjectEvent.Actions.EDITED, categories));
}

private void updateCategory(@NotNull Category category) {
	Resources resources = AppActivity.getActivity().getResources();
	
	ContentValues contentValues = new ContentValues(2);
	contentValues.put(resources.getString(R.string.table_category_name), category.getName());
	contentValues.put(resources.getString(R.string.table_category_order), category.getOrder());
	
	String table = resources.getString(R.string.table_category);
	String where = resources.getString(R.string.table_category_id) + "=" + category.getId();
	update(table, contentValues, where);
}

private static class GetCategoryTask extends AsyncTask<ItemSqliteGateway, Void, List<Category>> {
	@Override
	protected List<Category> doInBackground(ItemSqliteGateway... itemSqliteGateways) {
		ItemSqliteGateway itemSqliteGateway = itemSqliteGateways[0];
		Resources resources = AppActivity.getActivity().getResources();
		
		String sql = "SELECT " +
				resources.getString(R.string.table_category_id) + ", " +
				resources.getString(R.string.table_category_name) + ", " +
				resources.getString(R.string.table_category_order) +
				" FROM " + resources.getString(R.string.table_category) +
				" ORDER BY " + resources.getString(R.string.table_category_order) + " ASC";
		
		Cursor cursor = itemSqliteGateway.rawQuery(sql);
		List<Category> categories = new ArrayList<>(cursor.getCount());
		while (cursor.moveToNext()) {
			Category category = new Category();
			int i = 0;
			category.setId(idToString(cursor.getLong(i++)));
			category.setName(cursor.getString(i++));
			category.setOrder(cursor.getInt(i));
			
			categories.add(category);
		}
		itemSqliteGateway.close(cursor);
		
		return categories;
	}
	
	@Override
	protected void onPostExecute(List<Category> categories) {
		EventBus.getInstance().post(new CategoryEvent(ObjectEvent.Actions.GET_RESPONSE, categories));
	}
}

public void removeCategory(@NotNull Category category) {
	Resources resources = AppActivity.getActivity().getResources();
	
	beginTransaction();
	
	String table = resources.getString(R.string.table_category);
	String where = resources.getString(R.string.table_category_id) + "=" + category.getId();
	delete(table, where);
	
	// Update order of categories after this category
	try {
		String sql = "UPDATE " + table + " SET " +
				resources.getString(R.string.table_category_order) + "=" + resources.getString(R.string.table_category_order) + "-1 " +
				"WHERE " +
				resources.getString(R.string.table_category_order) + ">" + category.getOrder();
		execSQL(sql);
	} catch (SQLException e) {
		Log.e(TAG, "removeCategory() — Invalid SQL syntax", e);
	}
	
	// Remove all items in the category
	try {
		String sql = "DELETE FROM " + resources.getString(R.string.table_item) +
				"WHERE " +
				resources.getString(R.string.table_category_id) + "=" + category.getId();
		execSQL(sql);
	} catch (SQLException e) {
		Log.e(TAG, "removeCategory() — Invalid SQL syntax", e);
	}
	
	setTransactionSuccessful();
	endTransaction();
	
	mEventBus.post(new CategoryEvent(ObjectEvent.Actions.REMOVED, category));
}

@Override
public void addItems(@NotNull List<Item> items) {
	beginTransaction();
	
	for (Item item : items) {
		addItem(item);
	}
	
	setTransactionSuccessful();
	endTransaction();
	
	mEventBus.post(new ItemEvent(ObjectEvent.Actions.ADDED, items));
}

public void getItems(String categoryId) {
	Resources resources = AppActivity.getActivity().getResources();
	
	String sql = "SELECT " +
			resources.getString(R.string.table_item_id) + ", " +
			resources.getString(R.string.table_category_id) + ", " +
			resources.getString(R.string.table_item_text) + ", " +
			resources.getString(R.string.table_item_date) +
			" FROM " + resources.getString(R.string.table_item);
	
	if (categoryId != null) {
		sql += " WHERE " + resources.getString(R.string.table_category_id) + "=" + categoryId;
	}
	sql += " ORDER BY " +
			resources.getString(R.string.table_item_date) + " DESC, " +
			resources.getString(R.string.table_item_id) + " DESC";
	
	
	Cursor cursor = rawQuery(sql);
	List<Item> items = new ArrayList<>(cursor.getCount());
	while (cursor.moveToNext()) {
		Item item = new Item();
		int i = 0;
		item.setId(idToString(cursor.getLong(i++)));
		item.setCategoryId(idToString(cursor.getLong(i++)));
		item.setText(cursor.getString(i++));
		item.setDate(cursor.getLong(i));
		
		items.add(item);
	}
	close(cursor);
	
	EventBus.getInstance().post(new ItemEvent(ObjectEvent.Actions.GET_RESPONSE, items));
}

@Override
public void updateItems(@NotNull List<Item> items) {
	beginTransaction();
	
	for (Item item : items) {
		updateItem(item);
	}
	
	setTransactionSuccessful();
	endTransaction();
	
	mEventBus.post(new ItemEvent(ObjectEvent.Actions.EDITED, items));
}

private void updateItem(@NotNull Item item) {
	Resources resources = AppActivity.getActivity().getResources();
	
	ContentValues contentValues = new ContentValues(2);
	contentValues.put(resources.getString(R.string.table_item_text), item.getText());
	contentValues.put(resources.getString(R.string.table_item_date), item.getDate());
	
	String table = resources.getString(R.string.table_item);
	String where = resources.getString(R.string.table_item_id) + "=" + item.getId();
	update(table, contentValues, where);
}

public void importData(@NotNull List<Category> categories, @NotNull List<Item> items) {
	Resources resources = AppActivity.getActivity().getResources();
	HashMap<String, String> idMap = new HashMap<>(categories.size());
	
	// Check database for existing categories with matching names
	for (Category category : categories) {
		String sql = "SELECT " +
				resources.getString(R.string.table_category_id) +
				" FROM " + resources.getString(R.string.table_category) +
				" WHERE " + resources.getString(R.string.table_category_name) + "=" + category.getName() +
				" COLLATE NOCASE";
		
		Cursor cursor = rawQuery(sql);
		
		// Found category with same name -> Use its id
		if (cursor.moveToNext()) {
			String existingId = idToString(cursor.getLong(0));
			idMap.put(category.getId(), existingId);
		}
		// Didn't find category with same name -> Insert category
		else {
			String oldId = category.getId();
			addCategory(category);
			idMap.put(oldId, category.getId());
		}
		
		close(cursor);
	}
	
	// Add all items
	beginTransaction();
	boolean success = true;
	for (Item item : items) {
		String newCategoryId = idMap.get(item.getCategoryId());
		item.setCategoryId(newCategoryId);
		addItem(item);
		
		// Failed to add item
		if (item.getId().isEmpty()) {
			success = false;
			break;
		}
	}
	if (success) {
		setTransactionSuccessful();
	}
	endTransaction();
}


@Override
public void removeItems(@NotNull List<Item> items) {
	beginTransaction();
	
	for (Item item : items) {
		removeItem(item);
	}
	
	setTransactionSuccessful();
	endTransaction();
	
	mEventBus.post(new ItemEvent(ObjectEvent.Actions.REMOVED, items));
}

private void removeItem(@NotNull Item item) {
	Resources resources = AppActivity.getActivity().getResources();
	
	String table = resources.getString(R.string.table_item);
	String where = resources.getString(R.string.table_item_id) + "=" + item.getId();
	delete(table, where);
}

/**
 * Add a new item. Will automatically set the item id.
 * @param item the item to add
 */
private void addItem(@NotNull Item item) {
	Resources resources = AppActivity.getActivity().getResources();
	
	ContentValues contentValues = new ContentValues();
	contentValues.put(resources.getString(R.string.table_category_id), item.getCategoryId());
	contentValues.put(resources.getString(R.string.table_item_text), item.getText());
	contentValues.put(resources.getString(R.string.table_item_date), item.getDate());
	
	long id = insert(resources.getString(R.string.table_item), contentValues);
	item.setId(idToString(id));
}


}
