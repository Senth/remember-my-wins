package io.blushine.rmw.util;

import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import io.blushine.android.AppActivity;
import io.blushine.android.sqlite.SqliteGateway;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

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
	Log.d(TAG, "onCreate()");
	
	Resources resources = AppActivity.getActivity().getResources();
	createCategoryTable(resources, db);
	createItemTable(resources, db);
}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	Log.d(TAG, "onUpgrade() — " + oldVersion + " -> " + newVersion);
	
	Resources resources = AppActivity.getActivity().getResources();
	
	// 1 -> 3 - Changed name of list to category
	if (oldVersion < 3) {
		upgrade1To3(resources, db);
	}
}

private void upgrade1To3(Resources resources, SQLiteDatabase db) {
	db.beginTransaction();
	// List -> Category
	createCategoryTable(resources, db);
	String sql = "INSERT INTO category " +
			"(category_id, category_order, category_name) SELECT " +
			"list_id, list_order, list_name FROM list";
	db.execSQL(sql);
	db.execSQL("DROP TABLE list");
	
	// Item (list_id -> item_id)
	sql = "ALTER TABLE item RENAME TO item_old";
	db.execSQL(sql);
	createItemTable(resources, db);
	sql = "INSERT INTO item " +
			"(item_id, category_id, item_text, item_date) SELECT " +
			"item_id, list_id, item_text, item_date FROM item_old";
	db.execSQL(sql);
	db.execSQL("DROP TABLE item_old");
	db.setTransactionSuccessful();
	db.endTransaction();
}

private void createCategoryTable(Resources resources, SQLiteDatabase db) {
	Log.d(TAG, "createCategoryTable()");
	
	String sql = "CREATE TABLE IF NOT EXISTS " + resources.getString(R.string.table_category) + " (" +
			resources.getString(R.string.table_category_id) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			resources.getString(R.string.table_category_order) + " INTEGER, " +
			resources.getString(R.string.table_category_name) + " TEXT)";
	
	db.execSQL(sql);
}

private void createItemTable(Resources resources, SQLiteDatabase db) {
	Log.d(TAG, "createItemTable()");
	
	String sql = "CREATE TABLE IF NOT EXISTS " + resources.getString(R.string.table_item) + " (" +
			resources.getString(R.string.table_item_id) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			resources.getString(R.string.table_category_id) + " INTEGER, " +
			resources.getString(R.string.table_item_text) + " TEXT, " +
			resources.getString(R.string.table_item_date) + " INTEGER, " +
			"FOREIGN KEY(" + resources.getString(R.string.table_category_id) + ") REFERENCES " + resources.getString(R.string.table_category) + "(" + resources.getString(R.string.table_category_id) + "))";
	db.execSQL(sql);
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
