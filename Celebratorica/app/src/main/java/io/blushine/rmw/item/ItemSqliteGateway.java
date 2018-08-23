package io.blushine.rmw.item;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.blushine.android.sqlite.SqliteGateway;
import io.blushine.rmw.R;
import io.blushine.rmw.util.AppActivity;

/**
 * Gateway for getting celebration and list items
 */
class ItemSqliteGateway extends SqliteGateway implements ItemGateway {
private static final String TAG = ItemSqliteGateway.class.getSimpleName();

private String idToString(long id) {
	return Long.toString(id);
}

public Category getCategory(@NotNull String categoryId) {
	Resources resources = AppActivity.getActivity().getResources();
	
	String sql = "SELECT " +
			resources.getString(R.string.table_list_name) + ", " +
			resources.getString(R.string.table_list_order) +
			" FROM " + resources.getString(R.string.table_list) +
			" WHERE " + resources.getString(R.string.table_list_id) + "=" + categoryId;
	
	Cursor cursor = rawQuery(sql);
	Category category = null;
	if (cursor.moveToNext()) {
		int i = 0;
		category = new Category();
		category.setId(categoryId);
		category.setName(cursor.getString(i++));
		category.setOrder(cursor.getInt(i));
	}
	close(cursor);
	
	return category;
}

@NotNull
public List<Category> getCategories() {
	Resources resources = AppActivity.getActivity().getResources();
	
	String sql = "SELECT " +
			resources.getString(R.string.table_list_id) + ", " +
			resources.getString(R.string.table_list_name) + ", " +
			resources.getString(R.string.table_list_order) +
			" FROM " + resources.getString(R.string.table_list) +
			" ORDER BY " + resources.getString(R.string.table_list_order) + " ASC";
	
	Cursor cursor = rawQuery(sql);
	List<Category> categories = new ArrayList<>(cursor.getCount());
	while (cursor.moveToNext()) {
		Category category = new Category();
		int i = 0;
		category.setId(idToString(cursor.getLong(i++)));
		category.setName(cursor.getString(i++));
		category.setOrder(cursor.getInt(i));
		
		categories.add(category);
	}
	close(cursor);
	
	return categories;
}

@NotNull
public List<Item> getItems(String categoryId) {
	Resources resources = AppActivity.getActivity().getResources();
	
	String sql = "SELECT " +
			resources.getString(R.string.table_item_id) + ", " +
			resources.getString(R.string.table_list_id) + ", " +
			resources.getString(R.string.table_item_text) + ", " +
			resources.getString(R.string.table_item_date) +
			" FROM " + resources.getString(R.string.table_item);
	
	if (categoryId != null) {
		sql += " WHERE " + resources.getString(R.string.table_list_id) + "=" + categoryId;
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
	
	return items;
}

public void updateCategory(@NotNull Category category) {
	Resources resources = AppActivity.getActivity().getResources();
	
	ContentValues contentValues = new ContentValues(2);
	contentValues.put(resources.getString(R.string.table_list_name), category.getName());
	contentValues.put(resources.getString(R.string.table_list_order), category.getOrder());
	
	String table = resources.getString(R.string.table_list);
	String where = resources.getString(R.string.table_list_id) + "=" + category.getId();
	update(table, contentValues, where);
}

public void removeCategory(@NotNull Category category) {
	Resources resources = AppActivity.getActivity().getResources();
	
	String table = resources.getString(R.string.table_list);
	String where = resources.getString(R.string.table_list_id) + "=" + category.getId();
	delete(table, where);
	
	// Update order of categories after this category
	try {
		String sql = "UPDATE " + table + " SET " +
				resources.getString(R.string.table_list_order) + "=" + resources.getString(R.string.table_list_order) + "-1 " +
				"WHERE " +
				resources.getString(R.string.table_list_order) + ">" + category.getOrder();
		execSQL(sql);
	} catch (SQLException e) {
		Log.e(TAG, "removeCategory() — Invalid SQL syntax", e);
	}
	
}

public void updateItem(@NotNull Item item) {
	Resources resources = AppActivity.getActivity().getResources();
	
	ContentValues contentValues = new ContentValues(2);
	contentValues.put(resources.getString(R.string.table_item_text), item.getText());
	contentValues.put(resources.getString(R.string.table_item_date), item.getDate());
	
	String table = resources.getString(R.string.table_item);
	String where = resources.getString(R.string.table_item_id) + "=" + item.getId();
	update(table, contentValues, where);
}

public void removeItem(@NotNull Item item) {
	Resources resources = AppActivity.getActivity().getResources();
	
	String table = resources.getString(R.string.table_item);
	String where = resources.getString(R.string.table_item_id) + "=" + item.getId();
	delete(table, where);
}

public void importData(@NotNull List<Category> categories, @NotNull List<Item> items) {
	Resources resources = AppActivity.getActivity().getResources();
	HashMap<String, String> idMap = new HashMap<>(categories.size());
	
	// Check database for existing categories with matching names
	for (Category category : categories) {
		String sql = "SELECT " +
				resources.getString(R.string.table_list_id) +
				" FROM " + resources.getString(R.string.table_list) +
				" WHERE " + resources.getString(R.string.table_list_name) + "=" + category.getName() +
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

public void addCategory(@NotNull Category category) {
	Resources resources = AppActivity.getActivity().getResources();
	
	
	// Update categories after this order
	if (category.getOrder() > 0) {
		try {
			String sql = "UPDATE " + resources.getString(R.string.table_list) + " SET " +
					resources.getString(R.string.table_list_order) + "=" + resources.getString(R.string.table_list_order) + "+1 " +
					"WHERE " +
					resources.getString(R.string.table_list_order) + ">=" + category.getOrder();
			execSQL(sql);
		} catch (SQLException e) {
			Log.e(TAG, "removeCategory() — Invalid SQL syntax", e);
		}
	}
	// Get new order
	else {
		category.setOrder(getCategoryCount() + 1);
	}
	
	
	ContentValues contentValues = new ContentValues();
	contentValues.put(resources.getString(R.string.table_list_order), category.getOrder());
	contentValues.put(resources.getString(R.string.table_list_name), category.getName());
	
	
	long id = insert(resources.getString(R.string.table_list), contentValues);
	category.setId(idToString(id));
}

/**
 * Add a new item. Will automatically set the item id.
 * @param item the item to add
 */
public void addItem(@NotNull Item item) {
	Resources resources = AppActivity.getActivity().getResources();
	
	ContentValues contentValues = new ContentValues();
	contentValues.put(resources.getString(R.string.table_list_id), item.getCategoryId());
	contentValues.put(resources.getString(R.string.table_item_text), item.getText());
	contentValues.put(resources.getString(R.string.table_item_date), item.getDate());
	
	long id = insert(resources.getString(R.string.table_item), contentValues);
	item.setId(idToString(id));
}

/**
 * @return number of categories
 */
private int getCategoryCount() {
	Resources resources = AppActivity.getActivity().getResources();
	
	String sql = "SELECT " +
			resources.getString(R.string.table_list_order) +
			" FROM " + resources.getString(R.string.table_list) +
			" ORDER BY " + resources.getString(R.string.table_list_order) + " DESC" +
			" LIMIT 1";
	
	Cursor cursor = rawQuery(sql);
	int count = 0;
	if (cursor.moveToNext()) {
		count = cursor.getInt(0);
	}
	close(cursor);
	
	return count;
}


}