package com.spiddekauga.celebratorica.item;


import android.app.FragmentManager;
import android.os.Parcelable;
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

void invalidateCache() {
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

@Override
public CharSequence getPageTitle(int position) {
	Category category = mCachedCategories.get(position);
	return category.getName();
}

Category getCategory(int position) {
	return mCachedCategories.get(position);
}

@Override
public void restoreState(Parcelable state, ClassLoader loader) {
	super.restoreState(state, loader);
}
}
