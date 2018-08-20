package io.blushine.rmw.item;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.blushine.android.ui.list.ClickListener;
import io.blushine.rmw.R;
import io.blushine.rmw.util.Sqlite;
import io.blushine.rmw.util.SqliteInitializedEvent;
import io.blushine.utils.EventBus;

/**
 * Page fragment for showing all the items in a list
 */
public class CategoryPageFragment extends io.blushine.android.Fragment implements ClickListener<Item> {
static final long DISPLAY_ALL_CATEGORIES = -100;
private static final String CATEGORY_ID_KEY = "category_id";
private static final EventBus mEventBus = EventBus.getInstance();
private static final String TAG = CategoryPageFragment.class.getSimpleName();
private static final ItemRepo mItemRepo = ItemRepo.getInstance();
private final List<Item> mAddToAdapter = new ArrayList<>();
private long mCategoryId;
private ItemAdapter mItemAdapter = null;
private RecyclerView mItemListView = null;
private FloatingActionButton mAddButton;

/**
 * Set the argument used for an instance
 * @param categoryId the category to display on this page. Set to {@link #DISPLAY_ALL_CATEGORIES} to
 * display all categories
 */
void setArguments(long categoryId) {
	setArguments(createArguments(categoryId));
}

/**
 * Create the argument used for an instance
 * @param categoryId the category to display on this page. Set to {@link #DISPLAY_ALL_CATEGORIES} to
 * display all categories
 */
static Bundle createArguments(long categoryId) {
	Bundle bundle = new Bundle(1);
	bundle.putLong(CATEGORY_ID_KEY, categoryId);
	return bundle;
}

@Override
protected void onDeclareArguments() {
	super.onDeclareArguments();
	declareArgument(CATEGORY_ID_KEY, ArgumentRequired.REQUIRED);
}

@Override
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreatedImpl(view, savedInstanceState);
	
	mItemAdapter = new ItemAdapter();
	mItemAdapter.addEditFunctionality(this);
	mItemListView = (RecyclerView) mView.findViewById(R.id.item_list);
	mItemListView.setHasFixedSize(true);
	RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
	mItemListView.setLayoutManager(layoutManager);
	mItemListView.setAdapter(mItemAdapter);
	
	mAddButton = (FloatingActionButton) mView.getRootView().findViewById(R.id.add_button);
}

@Override
protected void onArgumentsSet() {
	super.onArgumentsSet();
	mCategoryId = getArgument(CATEGORY_ID_KEY);
}

@Nullable
@Override
public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	return inflater.inflate(R.layout.fragment_item_page, container, false);
}

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mEventBus.register(this);
}

@Override
public void onResume() {
	Log.d(TAG, "onResume() — " + getCategory().getName());
	super.onResume();
	
	mItemListView.invalidate();
	
	// Add new items to the list after a short delay
	if (!mAddToAdapter.isEmpty()) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mItemAdapter.add(mAddToAdapter);
				mAddToAdapter.clear();
			}
		}, 75);
	} else {
		populateItems();
	}
}

private Category getCategory() {
	Category category = mItemRepo.getCategory((Long) getArgument(CATEGORY_ID_KEY));
	
	if (category == null) {
		category = new Category();
		category.setCategoryId(mCategoryId);
	}
	return category;
}

/**
 * Populate the list with items. Does nothing if the list is already populated
 */
private void populateItems() {
	if (Sqlite.isInitialized() && mAddButton != null && mItemAdapter.getItemCount() == 0) {
		List<Item> items;
		if (mCategoryId == DISPLAY_ALL_CATEGORIES) {
			items = mItemRepo.getItems();
		} else {
			items = mItemRepo.getItems(mCategoryId);
		}
		mItemAdapter.setItems(items);
		
		// https://developer.android.com/reference/android/support/v4/view/ViewPager.SimpleOnPageChangeListener.html
		// Check this when the fragment is activated
//		if (items.isEmpty()) {
//			Showcases.ADD_ITEM.show(mAddButton);
//		}
	}
}

@Override
public void onStop() {
	Log.d(TAG, "onStop() — " + getCategory().getName());
	super.onStop();
}

@Override
public void onDestroy() {
	Log.d(TAG, "onDestroy() — " + getCategory().getName());
	super.onDestroy();
	mEventBus.unregister(this);
}

@Override
public void onClick(Item item) {
	if (mCategoryId > 0) {
		ItemEditFragment celebrationEditFragment = new ItemEditFragment();
		celebrationEditFragment.setEditItem(item);
		celebrationEditFragment.setCategoryId(mCategoryId);
		celebrationEditFragment.show();
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onItem(ItemEvent event) {
	// Only handle events for our list
	if (event.getFirstObject().getCategoryId() == mCategoryId) {
		switch (event.getAction()) {
		case ADDED:
			if (mItemAdapter.getItemCount() == 0) {
				mItemAdapter.setItems(event.getObjects());
			} else {
				mItemAdapter.add(event.getObjects());
			}
			break;
		
		case EDITED:
			// Remove and add - Updates the location in the adapter if date was changed
			mItemAdapter.remove(event.getObjects());
			mItemAdapter.add(event.getObjects());
			break;
		
		case REMOVED:
			// Removed all
			if (mItemAdapter.getItemCount() == event.getObjects().size()) {
				mItemAdapter.clear();
			}
			// Removed one or more
			else {
				mItemAdapter.remove(event.getObjects());
			}
			break;
		}
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onSqliteInitialized(SqliteInitializedEvent event) {
	populateItems();
}
}
