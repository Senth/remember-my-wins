package com.spiddekauga.celebratorica.celebration;

import android.support.annotation.NonNull;

import com.spiddekauga.android.util.DateFormats;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A celebration item
 */
class Celebration implements Comparable<Celebration> {
private final SimpleDateFormat DATE_FORMAT = getDateFormat();
private long mItemId = -1;
private long mListId = -1;
private String mText = "";
private String mDate = "";
private long mDateTime = -1;

/**
 * @return date format used for displaying celebrations
 */
static SimpleDateFormat getDateFormat() {
	return DateFormats.getMediumDateFormat();
}

long getItemId() {
	return mItemId;
}

void setItemId(long itemId) {
	mItemId = itemId;
}

long getListId() {
	return mListId;
}

void setListId(long listId) {
	mListId = listId;
}

String getText() {
	return mText;
}

void setText(String text) {
	mText = text;
}

String getDate() {
	return mDate;
}

void setDate(long dateTime) {
	mDateTime = dateTime;
	mDate = DATE_FORMAT.format(new Date(dateTime));
}

void setDate(String date) {
	mDate = date;
	ParsePosition parsePosition = new ParsePosition(0);
	mDateTime = DATE_FORMAT.parse(mDate, parsePosition).getTime();
}

long getDateTime() {
	return mDateTime;
}

@Override
public int compareTo(@NonNull Celebration other) {
	// This is before
	if (mDateTime < other.mDateTime) {
		return -1;
	}
	// Other is before
	else if (mDateTime > other.mDateTime) {
		return 1;
	}
	// Else date is same, sort by id
	else if (mItemId < other.mItemId) {
		return -1;
	} else {
		return 1;
	}
}

@Override
public int hashCode() {
	int result = (int) (mItemId ^ (mItemId >>> 32));
	result = 31 * result + (int) (mListId ^ (mListId >>> 32));
	return result;
}

@Override
public boolean equals(Object o) {
	if (this == o) {
		return true;
	}
	if (o == null || getClass() != o.getClass()) {
		return false;
	}

	Celebration that = (Celebration) o;

	return mItemId == that.mItemId && mListId == that.mListId;

}
}
