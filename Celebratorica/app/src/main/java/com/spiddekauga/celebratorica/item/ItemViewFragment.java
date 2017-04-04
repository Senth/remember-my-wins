package com.spiddekauga.celebratorica.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.AppFragment;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.Sqlite;
import com.spiddekauga.celebratorica.util.SqliteInitializedEvent;
import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

/**
 * Main fragment for showing different lists (pages) with fragments
 */
public class ItemViewFragment extends AppFragment {
private static final EventBus mEventBus = EventBus.getInstance();
private static final String PAGE_POSITION_KEY = "page_position";
private ViewPager mViewPager;
private CategoryPagerAdapter mPageAdapter;
private TabLayout mTabLayout;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mEventBus.register(this);
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_item_view, container, false);
	
	Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	AppActivity.getActivity().setSupportActionBar(toolbar);
	
	FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.add_button);
	addButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// Current category id
			Category category = getSelectedCategory();
			
			if (category.getCategoryId() > 0) {
				ItemAddFragment itemAddFragment = new ItemAddFragment();
				itemAddFragment.setCategoryId(category.getCategoryId());
				itemAddFragment.show();
			}
		}
	});
	setHasOptionsMenu(true);
	
	ImageButton imageButton = (ImageButton) view.findViewById(R.id.add_category_button);
	imageButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new CategoryAddFragment().show();
		}
	});
	
	mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
	mTabLayout = (TabLayout) view.findViewById(R.id.view_pager_tabs);
	mTabLayout.setupWithViewPager(mViewPager);
	bindAdapter();
	
	return view;
}

private Category getSelectedCategory() {
	int currentItemIndex = mViewPager.getCurrentItem();
	return mPageAdapter.getCategory(currentItemIndex);
}

private void bindAdapter() {
	if (Sqlite.isInitialized() && mViewPager != null && mViewPager.getAdapter() == null) {
		if (mPageAdapter == null) {
			mPageAdapter = new CategoryPagerAdapter(getChildFragmentManager());
		}
		mViewPager.setAdapter(mPageAdapter);
		updateLongPressListeners();
	}
}

private void updateLongPressListeners() {
	LinearLayout slidingTabStrip = (LinearLayout) mTabLayout.getChildAt(0);
	for (int i = 0; i < slidingTabStrip.getChildCount(); i++) {
		final int position = i;
		View tabView = slidingTabStrip.getChildAt(position);
		tabView.setLongClickable(true);
		tabView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Category category = mPageAdapter.getCategory(position);
				
				if (category != null && category.getCategoryId() > 0) {
					CategoryEditFragment categoryEditFragment = new CategoryEditFragment();
					categoryEditFragment.setArguments(category);
					categoryEditFragment.show();
				}
				
				return true;
			}
		});
	}
}

@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.menu_default, menu);
}

@Override
public void onViewStateRestored(Bundle savedInstanceState) {
	super.onViewStateRestored(savedInstanceState);
	if (savedInstanceState != null && mViewPager != null) {
		mViewPager.setCurrentItem(savedInstanceState.getInt(PAGE_POSITION_KEY, 0));
	}
}

@Override
public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	outState.putInt(PAGE_POSITION_KEY, mViewPager.getCurrentItem());
}

@Override
public void onDestroy() {
	super.onDestroy();
	mEventBus.unregister(this);
}

@SuppressWarnings("unused")
@Subscribe
public void onCategory(CategoryEvent event) {
	if (mPageAdapter != null) {
		mPageAdapter.invalidateCache();
		mPageAdapter.notifyDataSetChanged();
		updateLongPressListeners();
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onSqliteInitialized(SqliteInitializedEvent event) {
	bindAdapter();
}
}
