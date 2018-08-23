package io.blushine.rmw.item;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Adapter for switching between item lists
 */
class CategoryPagerAdapter extends FragmentPagerAdapter {
private final ItemRepo mItemRepo = ItemRepo.getInstance();
private List<Category> mCachedCategories;

CategoryPagerAdapter(@NonNull FragmentManager fragmentManager) {
	super(fragmentManager);
	invalidateCache();
}

private void invalidateCache() {
	mCachedCategories = mItemRepo.getCategories();
}

@Override
public Fragment getItem(int position) {
	long categoryId = mCachedCategories.get(position).getId();
	CategoryPageFragment fragment = new CategoryPageFragment();
	fragment.setArguments(categoryId);
	return fragment;
}

@Override
public void restoreState(Parcelable state, ClassLoader loader) {
	super.restoreState(state, loader);
}

@Override
public int getCount() {
	return mCachedCategories.size();
}

@Override
public void notifyDataSetChanged() {
	invalidateCache();
	super.notifyDataSetChanged();
}

@Override
public CharSequence getPageTitle(int position) {
	Category category = mCachedCategories.get(position);
	return category.getName();
}

Category getCategory(int position) {
	if (!mCachedCategories.isEmpty()) {
		return mCachedCategories.get(position);
	} else {
		return null;
	}
}
}
