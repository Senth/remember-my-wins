package io.blushine.rmw.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.blushine.android.DialogFragment;
import io.blushine.android.common.DateFormats;
import io.blushine.android.validate.TextValidator;
import io.blushine.rmw.R;
import io.blushine.rmw.util.AppActivity;

/**
 * Base class for adding/editing a celebration item
 */
public abstract class ItemDialogFragment extends DialogFragment {
protected static final String TEXT_SAVE_KEY = "text";
protected static final String DATE_SAVE_KEY = "date";
private static final String TAG = ItemDialogFragment.class.getSimpleName();
private static final SimpleDateFormat DATE_FORMAT = DateFormats.getMediumDateFormat();
private static final String CATEGORY_ARG_KEY = "category";
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
private Category mCategory;
private DatePickerDialog mDatePickerDialog;

protected EditText getTextField() {
	return mTextEdit;
}

/**
 * The category of the item we're adding or changing
 * @param category the category of the item we're adding or changing
 */
void setArgument(Category category) {
	Bundle bundle = new Bundle();
	bundle.putParcelable(CATEGORY_ARG_KEY, category);
	addArguments(bundle);
}

@Override
protected void onDeclareArguments() {
	super.onDeclareArguments();
	declareArgument(CATEGORY_ARG_KEY, ArgumentRequired.REQUIRED);
}

@Override
protected void onArgumentsSet() {
	super.onArgumentsSet();
	mCategory = getArgument(CATEGORY_ARG_KEY);
}

@Nullable
@Override
public View onCreateViewImpl(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	Log.d(TAG, "onCreateViewImpl()");
	
	View view = inflater.inflate(R.layout.fragment_item_dialog, container, false);
	
	initToolbar(view);
	
	// Text validation
	mTextEdit = view.findViewById(R.id.text_edit);
	addSaveView(mTextEdit, TEXT_SAVE_KEY);
	mValidatorGroup.add(new TextValidator.Builder(mTextEdit)
			.setRequired()
			.setMaxLength(AppActivity.getActivity().getResources().getInteger(R.integer.item_text_length_max))
			.build()
	);
	
	// Date
	mDateEdit = view.findViewById(R.id.date_edit);
	addSaveView(mDateEdit, DATE_SAVE_KEY);
	mDateEdit.setOnClickListener(v -> pickDate());
	if (mDateEdit.getText().toString().isEmpty()) {
		clearDate();
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
		mDatePickerDialog.vibrate(false);
	}
	if (getFragmentManager() != null) {
		mDatePickerDialog.show(getFragmentManager(), DatePickerDialog.class.getSimpleName());
	}
}

/**
 * Clears the date to the current date
 */
private void clearDate() {
	mDateEdit.setText(DATE_FORMAT.format(new Date()));
}

protected Category getCategory() {
	return mCategory;
}

/**
 * Set a item item from the field values
 * @param item the item to set
 */
protected void setItemFromFields(Item item) {
	item.setText(mTextEdit.getText().toString());
	item.setDateFromFormat(mDateEdit.getText().toString());
	item.setCategoryId(mCategory.getId());
}
}
