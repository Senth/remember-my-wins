package com.spiddekauga.celebratorica.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.spiddekauga.android.DialogFragment;
import com.spiddekauga.android.validate.TextValidator;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.AppActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Base class for adding/editing a celebration item
 */
public abstract class ItemDialogFragment extends DialogFragment {
private static final String TAG = ItemDialogFragment.class.getSimpleName();
private static final SimpleDateFormat DATE_FORMAT = Item.getDateFormat();
private static final String TEXT_SAVE_KEY = "text";
private static final String DATE_SAVE_KEY = "date";
private static final String ORIGINAL_SAVE_SUFFIX = "_original";
private static final String CATEGORY_ID_SAKE_KEY = "category_id";
private EditText mTextEdit;
private EditText mDateEdit;
private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
	@Override
	public void onDateSet(DatePickerDialog view, int year, int month, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, dayOfMonth);
		DATE_FORMAT.setCalendar(calendar);
		String date = DATE_FORMAT.format(calendar.getTime());
		mDateEdit.setText(date);
	}
};
private String mTextOriginal = "";
private String mDateOriginal = "";
private long mCategoryId = -1;
private DatePickerDialog mDatePickerDialog;

protected EditText getTextField() {
	return mTextEdit;
}

/**
 * Set the category id for this dialog
 * @param categoryId the category id for this dialog
 */
void setCategoryId(long categoryId) {
	mCategoryId = categoryId;
}

@Nullable
@Override
public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "onCreateViewImpl()");
	
	View view = inflater.inflate(R.layout.fragment_item_dialog, container, false);
	
	
	// Restore saved values
	String textValue = "";
	String dateValue = "";
	
	if (savedInstanceState != null) {
		textValue = savedInstanceState.getString(TEXT_SAVE_KEY);
		mTextOriginal = savedInstanceState.getString(TEXT_SAVE_KEY + ORIGINAL_SAVE_SUFFIX);
		dateValue = savedInstanceState.getString(DATE_SAVE_KEY);
		mDateOriginal = savedInstanceState.getString(DATE_SAVE_KEY + ORIGINAL_SAVE_SUFFIX);
		mCategoryId = savedInstanceState.getLong(CATEGORY_ID_SAKE_KEY);
	}
	
	if (mCategoryId == -1) {
		throw new IllegalStateException("List id has not been set");
	}
	
	
	initToolbar(view);
	
	
	// Text validation
	mTextEdit = (EditText) view.findViewById(R.id.text_edit);
	mTextEdit.setText(textValue);
	mValidatorGroup.add(new TextValidator.Builder(mTextEdit)
			.setRequired()
			.setMaxLength(AppActivity.getActivity().getResources().getInteger(R.integer.item_text_length_max))
			.build()
	);
	
	// Date
	mDateEdit = (EditText) view.findViewById(R.id.date_edit);
	mDateEdit.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			pickDate();
		}
	});
	if (dateValue == null || dateValue.isEmpty()) {
		clearDate();
		mDateOriginal = mDateEdit.getText().toString();
	} else {
		mDateEdit.setText(dateValue);
	}
	
	return view;
}

/**
 * Pick the date
 */
private void pickDate() {
	if (mDatePickerDialog == null) {
		Calendar calendar = new GregorianCalendar();
		ParsePosition parsePosition = new ParsePosition(0);
		calendar.setTime(DATE_FORMAT.parse(mDateEdit.getText().toString(), parsePosition));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		mDatePickerDialog = DatePickerDialog.newInstance(mDateSetListener, year, month, day);
	}
	mDatePickerDialog.show(getFragmentManager(), DatePickerDialog.class.getSimpleName());
}

/**
 * Clears the date to the current date
 */
private void clearDate() {
	mDateEdit.setText(DATE_FORMAT.format(new Date()));
}

@Override
protected boolean isChanged() {
	return !mTextOriginal.equals(mTextEdit.getText().toString()) ||
			!mDateOriginal.equals(mDateEdit.getText().toString());
}

@Override
public void onSaveInstanceState(Bundle outState) {
	outState.putString(TEXT_SAVE_KEY, mTextEdit.getText().toString());
	outState.putString(TEXT_SAVE_KEY + ORIGINAL_SAVE_SUFFIX, mTextOriginal);
	outState.putString(DATE_SAVE_KEY, mDateEdit.getText().toString());
	outState.putString(DATE_SAVE_KEY + ORIGINAL_SAVE_SUFFIX, mDateOriginal);
	outState.putLong(CATEGORY_ID_SAKE_KEY, mCategoryId);
	
	super.onSaveInstanceState(outState);
}

/**
 * Set fields from a specified item item
 * @param item item item to copy values from
 */
protected void setFields(Item item) {
	if (item != null) {
		mTextOriginal = item.getText();
		mTextEdit.setText(item.getText());
		mDateOriginal = item.getDate();
		mDateEdit.setText(item.getDate());
		mCategoryId = item.getCategoryId();
	}
}

/**
 * Set a item item from the field values
 * @param item the item to set
 */
protected void setItemFromFields(Item item) {
	item.setText(mTextEdit.getText().toString());
	item.setDate(mDateEdit.getText().toString());
	item.setCategoryId(mCategoryId);
}
}
