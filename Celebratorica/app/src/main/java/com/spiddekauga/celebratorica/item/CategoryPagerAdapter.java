package com.spiddekauga.celebratorica.item;


import android.app.FragmentManager;
import android.support.annotation.NonNull;

import com.spiddekauga.android.ui.list.FragmentPagerAdapter;

import java.util.List;

/**
 * Adapter for switching between item lists
 */
class CategoryPagerAdapter extends FragmentPagerAdapter<CategoryPageFragment> {
private final ItemRepo mItemRepo = ItemRepo.getInstance();
private List<Category> mCachedCategories;

CategoryPagerAdapter(@NonNull FragmentManager fragmentManager) {
	super(fragmentManager);
	invalidateCache();
}

public void invalidateCache() {
	mCachedCategories = mItemRepo.getCategories();
}

@Override
public CategoryPageFragment instantiateItem(int position) {
	long categoryId = mCachedCategories.get(position).getCategoryId();
	CategoryPageFragment fragment = new CategoryPageFragment();
	fragment.setArguments(categoryId);
	return fragment;
}

@Override
public int getCount() {
	return mCachedCategories.size();
}
}
