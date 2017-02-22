package com.spiddekauga.celebratorica.celebration;

import com.spiddekauga.android.util.DateFormats;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A celebration item
 */
class Celebration {
private final SimpleDateFormat DATE_FORMAT = getDateFormat();
private long mItemId = -1;
private long mListId = -1;
private String mText = "";
private String mDate = "";

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
	mDate = DATE_FORMAT.format(new Date(dateTime));
}

void setDate(String date) {
	mDate = date;
}

long getDateTime() {
	ParsePosition parsePosition = new ParsePosition(0);
	return DATE_FORMAT.parse(mDate, parsePosition).getTime();
}
}
