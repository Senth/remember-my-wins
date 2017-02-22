package com.spiddekauga.celebratorica.util;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.sqlite.SqliteGateway;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.utils.EventBus;

/**
 * Contains the SQLite DB and handles upgrades
 */
public class Sqlite extends SQLiteOpenHelper {
private static final String DB_NAME = AppActivity.getActivity().getResources().getString(R.string.sqlite_db_name);
private static final int DB_VERSION = AppActivity.getActivity().getResources().getInteger(R.integer.sqlite_db_version);
private static final String TAG = Sqlite.class.getSimpleName();
private static InitTask mInitTask = null;

private Sqlite() {
	super(AppActivity.getActivity(), DB_NAME, null, DB_VERSION);
}

/**
 * Initialize the SQL DB in a background thread
 */
static void init() {
	Log.d(TAG, "init()");
	if (!isInitialized() && mInitTask == null) {
		Log.d(TAG, "init() - Not initialized, initializing");
		mInitTask = new InitTask();
		mInitTask.execute();
	} else {
		Log.d(TAG, "init() - Already initialized");
	}
}

/**
 * @return true if Sqlite has been initialized
 */
public static boolean isInitialized() {
	return SqliteGateway.isInitialized();
}

@Override
public void onCreate(SQLiteDatabase db) {
	Resources resources = AppActivity.getActivity().getResources();
	createListTable(resources, db);
	createItemTable(resources, db);

	// Create default items
	createDefaultLists(resources, db);
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

}

private void createListTable(Resources resources, SQLiteDatabase db) {
	String sql = "CREATE TABLE IF NOT EXISTS " + resources.getString(R.string.table_list) + " (" +
			resources.getString(R.string.table_list_id) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			resources.getString(R.string.table_list_order) + " INTEGER, " +
			resources.getString(R.string.table_list_name) + " TEXT)";

	db.execSQL(sql);
}

private void createItemTable(Resources resources, SQLiteDatabase db) {
	String sql = "CREATE TABLE IF NOT EXISTS " + resources.getString(R.string.table_item) + " (" +
			resources.getString(R.string.table_item_id) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			resources.getString(R.string.table_list_id) + " INTEGER, " +
			resources.getString(R.string.table_item_text) + " TEXT, " +
			resources.getString(R.string.table_item_date) + " INTEGER, " +
			"FOREIGN KEY(" + resources.getString(R.string.table_list_id) + ") REFERENCES " + resources.getString(R.string.table_list) + "(" + resources.getString(R.string.table_list_id) + "))";
	db.execSQL(sql);
}

private void createDefaultLists(Resources resources, SQLiteDatabase db) {
	String table = resources.getString(R.string.table_list);
	ContentValues values = new ContentValues();
	values.put(resources.getString(R.string.table_list_id), 1);
	values.put(resources.getString(R.string.table_list_order), 1);
	values.put(resources.getString(R.string.table_list_name), resources.getString(R.string.celebration_header));
	db.insert(table, null, values);
}

private static class InitTask extends AsyncTask<Void, Void, Sqlite> {
	@Override
	protected Sqlite doInBackground(Void... params) {
		return new Sqlite();
	}

	@Override
	protected void onPostExecute(Sqlite sqlite) {
		if (sqlite != null) {
			Log.d(TAG, "onPostExecute() - Setting sqlite");
			SqliteGateway.setSqlite(sqlite);
			EventBus.getInstance().post(new SqliteInitializedEvent());
		} else {
			Log.e(TAG, "Failed to initialize SQLite");
		}
		mInitTask = null;
	}
}
}
