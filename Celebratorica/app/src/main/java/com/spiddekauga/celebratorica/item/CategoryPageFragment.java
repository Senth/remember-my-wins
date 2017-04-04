package com.spiddekauga.celebratorica.item;

import android.app.Fragment;
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

import com.spiddekauga.android.AppFragmentHelper;
import com.spiddekauga.android.ui.list.ClickListener;
import com.spiddekauga.android.ui.list.RemoveListener;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.Sqlite;
import com.spiddekauga.celebratorica.util.SqliteInitializedEvent;
import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Page fragment for showing all the items in a list
 */
public class CategoryPageFragment extends Fragment implements RemoveListener<Item>, ClickListener<Item> {
static final long DISPLAY_ALL_CATEGORIES = -100;
private static final String CATEGORY_ID_KEY = "category_id";
private static final EventBus mEventBus = EventBus.getInstance();
private static final String TAG = CategoryPageFragment.class.getSimpleName();
private final ItemRepo mItemRepo = ItemRepo.getInstance();
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
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mEventBus.register(this);
	
	mItemAdapter = new ItemAdapter();
	mItemAdapter.addEditFunctionality(this);
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	readArguments();
	
	return inflater.inflate(R.layout.fragment_item_page, container, false);
}

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	
	mItemListView = (RecyclerView) view.findViewById(R.id.list_item);
	mItemListView.setHasFixedSize(true);
	RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
	mItemListView.setLayoutManager(layoutManager);
	mItemListView.setAdapter(mItemAdapter);
	
	mAddButton = (FloatingActionButton) view.getRootView().findViewById(R.id.add_button);
}

@Override
public void onResume() {
	Log.d(TAG, "onResume() — Id: " + mCategoryId);
	super.onResume();
	mItemListView.invalidate();
	
	// Add new items to the list after a short delay
	if (!mAddToAdapter.isEmpty()) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				for (final Item item : mAddToAdapter) {
					mItemAdapter.add(item);
				}
				mAddToAdapter.clear();
			}
		}, 75);
	} else {
		populateItems();
	}
}

@Override
public void onStop() {
	Log.d(TAG, "onStop() — Id: " + mCategoryId);
	super.onStop();
}

@Override
public void onDestroy() {
	Log.d(TAG, "onDestroy() — Id: " + mCategoryId);
	super.onDestroy();
	mEventBus.unregister(this);
}

/**
 * Populate the list with celebrations. Does nothing if the list is already populated
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
//			Showcases.ADD_CELEBRATION.show(mAddButton);
//		}
	}
}

private void readArguments() {
	Bundle arguments = getArguments();
	mCategoryId = arguments.getLong(CATEGORY_ID_KEY, -1);
}

/**
 * @return get the category id for this fragment
 */
long getCategoryId() {
	return mCategoryId;
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

@Override
public void onRemoved(Item item) {
	ItemRemoveCommand removeCommand = new ItemRemoveCommand(item);
	removeCommand.execute();
}

@SuppressWarnings("unused")
@Subscribe
public void onItem(ItemEvent event) {
	// Only handle events for our list
	if (event.getItem().getCategoryId() == mCategoryId) {
		switch (event.getAction()) {
		case ADD:
			// Add later when this fragment becomes active
			if (AppFragmentHelper.getFragment() != this) {
				mAddToAdapter.add(event.getItem());
			}
			// Add directly
			else {
				mItemAdapter.add(event.getItem());
			}
			break;
		
		case EDIT:
			// Remove and add - Updates the location in the adapter if date was changed
			mItemAdapter.remove(event.getItem());
			mItemAdapter.add(event.getItem());
			break;
		
		case REMOVE:
			mItemAdapter.remove(event.getItem());
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
