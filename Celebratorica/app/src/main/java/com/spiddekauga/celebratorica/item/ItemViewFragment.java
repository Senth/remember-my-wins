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
private TabLayout mTabLayout;
private CategoryPagerAdapter mPageAdapter;

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
			int currentItemIndex = mViewPager.getCurrentItem();
			CategoryPageFragment currentFragment = mPageAdapter.getItem(currentItemIndex);
			long categoryId = currentFragment.getCategoryId();
			
			if (categoryId > 0) {
				ItemAddFragment itemAddFragment = new ItemAddFragment();
				itemAddFragment.setCategoryId(categoryId);
				itemAddFragment.show();
			}
		}
	});
	setHasOptionsMenu(true);
	
	mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
	mTabLayout = (TabLayout) view.findViewById(R.id.view_pager_tabs);
	mTabLayout.setupWithViewPager(mViewPager);
	bindAdapter();
	
	return view;
}

private void bindAdapter() {
	if (Sqlite.isInitialized() && mViewPager.getAdapter() == null) {
		if (mPageAdapter == null) {
			mPageAdapter = new CategoryPagerAdapter(getChildFragmentManager());
		}
		mViewPager.setAdapter(mPageAdapter);
	}
}

@Override
public void onViewStateRestored(Bundle savedInstanceState) {
	super.onViewStateRestored(savedInstanceState);
	// TODO restore current tab position
}

@Override
public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	// TODO save current tab position
}

@Override
public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	inflater.inflate(R.menu.menu_default, menu);
}

@Override
public void onDestroy() {
	super.onDestroy();
	mEventBus.unregister(this);
}

@SuppressWarnings("unused")
@Subscribe
public void onSqliteInitialized(SqliteInitializedEvent event) {
	bindAdapter();
}
}
