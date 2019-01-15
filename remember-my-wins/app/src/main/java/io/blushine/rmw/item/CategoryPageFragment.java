package io.blushine.rmw.item;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.List;

import io.blushine.android.ui.list.ClickListener;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * Page fragment for showing all the items in a category
 */
public class CategoryPageFragment extends io.blushine.android.Fragment implements ClickListener<Item> {
private static final String CATEGORY_ARG = "category";
private static final EventBus mEventBus = EventBus.getInstance();
private static final String TAG = CategoryPageFragment.class.getSimpleName();
private static final ItemRepo mItemRepo = ItemRepo.getInstance();
private Category mCategory = new Category();
private ItemAdapter mItemAdapter = null;
private RecyclerView mItemListView = null;
private FloatingActionButton mAddButton;

/**
 * Set the argument used for an instance
 * @param category the category to display on this page. Set to null to display all categories
 */
void setArguments(Category category) {
	addArguments(createArguments(category));
}

/**
 * Create the argument used for an instance
 * @param category the category to display on this page. Set to null to
 * display all categories
 */
static Bundle createArguments(Category category) {
	Bundle bundle = new Bundle(1);
	bundle.putParcelable(CATEGORY_ARG, category);
	return bundle;
}

@Override
protected void onDeclareArguments() {
	super.onDeclareArguments();
	declareArgument(CATEGORY_ARG, ArgumentRequired.REQUIRED);
}

@Override
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreatedImpl(view, savedInstanceState);
	
	mItemAdapter = new ItemAdapter();
	mItemAdapter.addEditFunctionality(this);
	mItemListView = mView.findViewById(R.id.item_list);
	mItemListView.setHasFixedSize(true);
	RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
	mItemListView.setLayoutManager(layoutManager);
	mItemListView.setAdapter(mItemAdapter);
	mAddButton = mView.getRootView().findViewById(R.id.add_button);
	populateItems();
}

@Override
protected void onArgumentsSet() {
	super.onArgumentsSet();
	mCategory = getArgument(CATEGORY_ARG);
}

@Nullable
@Override
public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	return inflater.inflate(R.layout.fragment_item_page, container, false);
}

/**
 * Populate the list with items. Does nothing if the list is already populated
 */
private void populateItems() {
	if (mItemAdapter.getItemCount() == 0) {
		List<Item> items;
		if (mCategory == null) {
			mItemRepo.getItems();
		} else {
			mItemRepo.getItems(mCategory.getId());
		}
	}
}

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mEventBus.register(this);
}

@Override
public void onResume() {
	Log.d(TAG, "onResume() — " + getCategoryName());
	super.onResume();
	
	mItemListView.invalidate();
}

@Override
public void onStop() {
	Log.d(TAG, "onStop() — " + getCategoryName());
	super.onStop();
}

@Override
public void onDestroy() {
	Log.d(TAG, "onDestroy() — " + getCategoryName());
	super.onDestroy();
	mEventBus.unregister(this);
}

private String getCategoryName() {
	return mCategory != null ? mCategory.getName() : "Display All";
}

@Override
public void onClick(Item item) {
	if (mCategory != null) {
		ItemEditFragment itemEditFragment = new ItemEditFragment();
		itemEditFragment.setArguments(mCategory, item);
		itemEditFragment.show();
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onItem(ItemEvent event) {
	// Only handle events for our category
	if (mCategory != null && event.hasObjects() && event.getFirstObject().getCategoryId().equals(mCategory.getId())) {
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
		
		case GET_RESPONSE:
			mItemAdapter.setItems(event.getObjects());
			break;
		}
	}
}
}
