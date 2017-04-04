package com.spiddekauga.celebratorica.item;

/**
 * Category for specific lists
 */
class Category {
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
}
