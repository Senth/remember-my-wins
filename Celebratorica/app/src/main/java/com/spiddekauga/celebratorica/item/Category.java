package com.spiddekauga.celebratorica.item;

import android.support.annotation.NonNull;

/**
 * Category for specific lists
 */
class Category implements Comparable<Category> {
private long mCategoryId = -1;
private String mName = "";
private int mOrder = -1;

int getOrder() {
	return mOrder;
}

void setOrder(int order) {
	mOrder = order;
}

long getCategoryId() {
	return mCategoryId;
}

void setCategoryId(long categoryId) {
	mCategoryId = categoryId;
}

String getName() {
	return mName;
}

void setName(String name) {
	mName = name;
}

@Override
public int compareTo(@NonNull Category other) {
	// This is before
	if (mOrder < other.mOrder) {
		return -1;
	}
	// Other is before
	if (mOrder > other.mOrder) {
		return 1;
	}
	// Else same order
	else {
		return 0;
	}
}

@Override
public int hashCode() {
	return (int) (mCategoryId ^ (mCategoryId >>> 32));
}

@Override
public boolean equals(Object o) {
	if (this == o) {
		return true;
	}
	if (o == null || getClass() != o.getClass()) {
		return false;
	}
	
	Category category = (Category) o;
	
	return mCategoryId == category.mCategoryId;
	
}
}
