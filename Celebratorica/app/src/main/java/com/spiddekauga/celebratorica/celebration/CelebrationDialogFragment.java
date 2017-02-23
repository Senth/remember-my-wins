package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.spiddekauga.android.AppFragment;
import com.spiddekauga.android.validate.TextValidator;
import com.spiddekauga.android.validate.ValidatorGroup;
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
public abstract class CelebrationDialogFragment extends AppFragment implements Toolbar.OnMenuItemClickListener {
private static final String TAG = CelebrationDialogFragment.class.getSimpleName();
private static final SimpleDateFormat DATE_FORMAT = Celebration.getDateFormat();
private static final String TEXT_SAVE_KEY = "text";
private static final String DATE_SAVE_KEY = "date";
private static final String ORIGINAL_SAVE_SUFFIX = "_original";
private final ValidatorGroup mValidatorGroup = new ValidatorGroup();
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
private DatePickerDialog mDatePickerDialog;

/**
 * Validate all text fields
 * @return true if all text fields are valid
 */
protected boolean validateTextFields() {
	return mValidatorGroup.validate();
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "onCreateView()");

	View view = inflater.inflate(R.layout.fragment_celebrate_dialog, container, false);


	// Restore saved values
	String textValue = "";
	String dateValue = "";

	if (savedInstanceState != null) {
		textValue = savedInstanceState.getString(TEXT_SAVE_KEY);
		mTextOriginal = savedInstanceState.getString(TEXT_SAVE_KEY + ORIGINAL_SAVE_SUFFIX);
		dateValue = savedInstanceState.getString(DATE_SAVE_KEY);
		mDateOriginal = savedInstanceState.getString(DATE_SAVE_KEY + ORIGINAL_SAVE_SUFFIX);
	}


	// Toolbar
	Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	toolbar.setTitle(getTitle());
	toolbar.inflateMenu(getMenu());
	toolbar.setNavigationOnClickListener(new BackOnClickListener());
	toolbar.setOnMenuItemClickListener(this);


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

@Override
public void onSaveInstanceState(Bundle outState) {
	outState.putString(TEXT_SAVE_KEY, mTextEdit.getText().toString());
	outState.putString(TEXT_SAVE_KEY + ORIGINAL_SAVE_SUFFIX, mTextOriginal);
	outState.putString(DATE_SAVE_KEY, mDateEdit.getText().toString());
	outState.putString(DATE_SAVE_KEY + ORIGINAL_SAVE_SUFFIX, mDateOriginal);

	super.onSaveInstanceState(outState);
}

/**
 * @return title of this dialog
 */
@StringRes
protected abstract int getTitle();

/**
 * @return menu to inflate the toolbar with
 */
@MenuRes
protected abstract int getMenu();

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
public void onResume() {
	super.onResume();
	mValidatorGroup.clearError();
}

/**
 * Set fields from a specified celebration item
 * @param celebration celebration item to copy values from
 */
protected void setFields(Celebration celebration) {
	if (celebration != null) {
		mTextEdit.setText(celebration.getText());
		mDateEdit.setText(celebration.getDate());
	}
}

/**
 * Set a celebration item from the field values
 * @param celebration the celebration to set
 */
protected void setCelebrationFromFields(Celebration celebration) {
	celebration.setText(mTextEdit.getText().toString());
	celebration.setDate(mDateEdit.getText().toString());
	// TODO set list id
	celebration.setListId(1);
}
}
