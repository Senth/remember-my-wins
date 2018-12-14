package io.blushine.rmw.item;

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

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import io.blushine.android.AppFragment;
import io.blushine.android.common.ObjectEvent;
import io.blushine.android.ui.list.ClickListener;
import io.blushine.android.ui.list.MoveListener;
import io.blushine.rmw.R;
import io.blushine.rmw.util.AppActivity;
import io.blushine.utils.EventBus;

/**
 * Fragment for reordering categories
 */
public class CategoryOrderFragment extends AppFragment implements ClickListener<Category>, MoveListener<Category> {
private static final EventBus mEventBus = EventBus.getInstance();
private static final ItemRepo mItemRepo = ItemRepo.getInstance();
private final List<Category> mAddToAdapter = new ArrayList<>();
private CategoryOrderAdapter mCategoryAdapter;
private RecyclerView mCategoryRecyclerView;
private FloatingActionButton mAddButton;

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
		new Handler().postDelayed(() -> {
			mCategoryAdapter.add(mAddToAdapter);
			mAddToAdapter.clear();
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
	
	Toolbar toolbar = view.findViewById(R.id.toolbar);
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
	
	mAddButton = mView.findViewById(R.id.add_button);
	mAddButton.setVisibility(View.GONE);
	mAddButton.setOnClickListener(v -> {
		CategoryAddFragment categoryAddFragment = new CategoryAddFragment();
		categoryAddFragment.setArguments(mCategoryAdapter.getItemCount());
		categoryAddFragment.show();
	});
	
	bindAdapter();
}

private void bindAdapter() {
	if (mCategoryRecyclerView != null && mCategoryRecyclerView.getAdapter() == null) {
		if (mCategoryAdapter == null) {
			mCategoryAdapter = new CategoryOrderAdapter();
			mCategoryAdapter.addEditFunctionality(this);
			mCategoryAdapter.addDragDropMoveFunctionality(this, R.id.reorder_button);
		}
		mCategoryRecyclerView.setAdapter(mCategoryAdapter);
		mItemRepo.getCategories();
	}
}

@Override
public void onClick(Category item) {
	CategoryEditFragment categoryEditFragment = new CategoryEditFragment();
	categoryEditFragment.setArguments(item);
	categoryEditFragment.show();
}

@Override
public void onMoved(Category item, int fromPosition, int toPosition) {
	List<Category> categoriesToUpdate = new ArrayList<>(Math.abs(toPosition - fromPosition + 1));
	
	// Increase or decrease the category order?
	int beginIndex;
	int endIndex;
	if (fromPosition < toPosition) {
		beginIndex = fromPosition;
		endIndex = toPosition;
	} else {
		beginIndex = toPosition;
		endIndex = fromPosition;
	}
	
	// Adjust order for the rest of the categories
	for (int i = beginIndex; i <= endIndex; ++i) {
		Category category = mCategoryAdapter.getItem(i);
		categoriesToUpdate.add(category);
	}
	
	mEventBus.post(new CategoryEvent(ObjectEvent.Actions.EDIT, categoriesToUpdate, fromPosition, toPosition));
}

@SuppressWarnings("unused")
@Subscribe
public void onCategory(CategoryEvent event) {
	switch (event.getAction()) {
	case ADD:
		mCategoryAdapter.add(event.getObjects());
		break;
	
	case ADD_FAILED:
		mCategoryAdapter.remove(event.getObjects());
		break;
	
	case EDIT:
		if (!event.isMoveEvent() && event.hasObjects()) {
			mCategoryAdapter.notifyItemChanged(event.getFirstObject());
		}
		break;
	
	case EDIT_FAILED:
		if (event.isMoveEvent()) {
			mCategoryAdapter.move(event.getMovedTo(), event.getMovedFrom());
		} else if (event.hasObjects()) {
			mCategoryAdapter.notifyItemChanged(event.getFirstObject());
		}
		break;
	
	case REMOVE:
		mCategoryAdapter.remove(event.getObjects());
		break;
	
	case REMOVE_FAILED:
		mCategoryAdapter.add(event.getObjects());
		break;
	
	case GET_RESPONSE:
		mCategoryAdapter.setItems(event.getObjects());
		mAddButton.setVisibility(View.VISIBLE);
		break;
	}
}
}
