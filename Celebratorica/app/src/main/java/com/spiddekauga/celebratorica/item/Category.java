package com.spiddekauga.celebratorica.item;

/**
 * Category for specific lists
 */
public class Category {
private long mCategoryId = -1;
private String mName = "";
private int mOrder = -1;

public int getOrder() {
	return mOrder;
}

public void setOrder(int order) {
	mOrder = order;
}

public long getCategoryId() {
	return mCategoryId;
}

public void setCategoryId(long categoryId) {
	mCategoryId = categoryId;
}

public String getName() {
	return mName;
}

public void setName(String name) {
	mName = name;
}
}
