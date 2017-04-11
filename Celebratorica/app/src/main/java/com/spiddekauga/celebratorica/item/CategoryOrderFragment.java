package com.spiddekauga.celebratorica.item;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.AppFragment;
import com.spiddekauga.android.AppFragmentHelper;
import com.spiddekauga.android.ui.list.ClickListener;
import com.spiddekauga.android.ui.list.MoveListener;
import com.spiddekauga.android.util.ObjectEvent;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.AppActivity;
import com.spiddekauga.celebratorica.util.Sqlite;
import com.spiddekauga.celebratorica.util.SqliteInitializedEvent;
import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for reordering categories
 */
public class CategoryOrderFragment extends AppFragment implements ClickListener<Category>, MoveListener<Category> {
private static final EventBus mEventBus = EventBus.getInstance();
private static final ItemRepo mItemRepo = ItemRepo.getInstance();
private final List<Category> mAddToAdapter = new ArrayList<>();
private CategoryOrderAdapter mCategoryAdapter;
private RecyclerView mCategoryRecyclerView;

@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mEventBus.register(this);
}

@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.menu_default, menu);
}

@Override
public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
	return inflater.inflate(R.layout.fragment_category_reorder, container, false);
}

@Override
public void onResume() {
	super.onResume();
	
	mCategoryRecyclerView.invalidate();
	
	// Add new items to the list after a short delay
	if (!mAddToAdapter.isEmpty()) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mCategoryAdapter.add(mAddToAdapter);
				mAddToAdapter.clear();
			}
		}, 75);
	}
}

@Override
public void onDestroy() {
	super.onDestroy();
	mEventBus.unregister(this);
}

@Override
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreatedImpl(view, savedInstanceState);
	
	Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	AppActivity.getActivity().setSupportActionBar(toolbar);
	toolbar.setTitle(R.string.category_edit_header);
	toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
	toolbar.setNavigationOnClickListener(new BackOnClickListener());
	setHasOptionsMenu(true
	
	);
	
	mCategoryRecyclerView = (RecyclerView) mView.findViewById(R.id.category_list);
	mCategoryRecyclerView.setHasFixedSize(true);
	RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
	mCategoryRecyclerView.setLayoutManager(layoutManager);
	
	FloatingActionButton addButton = (FloatingActionButton) mView.findViewById(R.id.add_button);
	addButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new CategoryAddFragment().show();
		}
	});
	
	bindAdapter();
}

private void bindAdapter() {
	if (Sqlite.isInitialized() && mCategoryRecyclerView != null && mCategoryRecyclerView.getAdapter() == null) {
		if (mCategoryAdapter == null) {
			mCategoryAdapter = new CategoryOrderAdapter();
			mCategoryAdapter.addEditFunctionality(this);
			mCategoryAdapter.addDragDropMoveFunctionality(this, R.id.reorder_button);
		}
		mCategoryRecyclerView.setAdapter(mCategoryAdapter);
		populateItems();
	}
}

private void populateItems() {
	if (Sqlite.isInitialized() && mCategoryAdapter.getItemCount() == 0) {
		mCategoryAdapter.setItems(mItemRepo.getCategories());
	}
}

@Override
public void onClick(Category item) {
	CategoryEditFragment categoryEditFragment = new CategoryEditFragment();
	categoryEditFragment.setArguments(item);
	categoryEditFragment.show();
}

@Override
public void onMove(Category item, int fromPosition, int toPosition) {
	List<Category> categoriesToUpdate = new ArrayList<>(Math.abs(toPosition - fromPosition + 1));
	
	// Adjust order for the category
	int adjustOrderDiff = toPosition - fromPosition;
	int newOrder = item.getOrder() + adjustOrderDiff;
	item.setOrder(newOrder);
	categoriesToUpdate.add(item);
	
	
	// Increase or decrease the category order?
	int increment;
	int beginIndex;
	int endIndex;
	if (fromPosition < toPosition) {
		increment = -1;
		beginIndex = fromPosition;
		endIndex = toPosition;
	} else {
		increment = 1;
		beginIndex = toPosition + 1;
		endIndex = fromPosition + 1;
	}
	
	// Adjust order for the rest of the categories
	for (int i = beginIndex; i < endIndex; ++i) {
		Category category = mCategoryAdapter.getItem(i);
		newOrder = category.getOrder() + increment;
		category.setOrder(newOrder);
		categoriesToUpdate.add(category);
	}
	
	mEventBus.post(new CategoryEvent(categoriesToUpdate, ObjectEvent.Actions.EDIT));
}

@SuppressWarnings("unused")
@Subscribe
public void onCategory(CategoryEvent event) {
	switch (event.getAction()) {
	case ADDED:
		// Add when fragment becomes active
		if (AppFragmentHelper.getFragment() != this) {
			mAddToAdapter.addAll(event.getObjects());
		}
		// Add directly
		else {
			mCategoryAdapter.add(event.getObjects());
		}
		break;
	
	case EDITED:
		mCategoryAdapter.notifyItemsChanged(event.getObjects());
		break;
	
	case REMOVED:
		mCategoryAdapter.remove(event.getObjects());
		break;
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onSqliteInitialized(SqliteInitializedEvent event) {
	bindAdapter();
}
}
