package com.spiddekauga.celebratorica.item;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.spiddekauga.android.AppFragmentHelper;
import com.spiddekauga.android.DialogFragment;
import com.spiddekauga.android.validate.TextValidator;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.AppActivity;

/**
 * Base class for adding/editing categories
 */
public abstract class CategoryDialogFragment extends DialogFragment {
protected static final String NAME_KEY = "name";
protected static final String CATEGORY_ID_KEY = "category_id";
protected static final String ORDER_KEY = "order";
private EditText mNameEdit;

@Nullable
@Override
public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_category_dialog, container, false);
	
	initToolbar(view);
	
	// Name validation
	mNameEdit = (EditText) view.findViewById(R.id.name_edit);
	addSaveView(mNameEdit, NAME_KEY);
	mValidatorGroup.add(new TextValidator.Builder(mNameEdit)
			.setRequired()
			.setMaxLength(AppActivity.getActivity().getResources().getInteger(R.integer.category_name_length_max))
			.build()
	);
	
	return view;
}

/**
 * Focus the name text field
 */
protected void focusNameTextField() {
	AppFragmentHelper.focusEditText(mNameEdit);
}

/**
 * Set category from field
 * @param category the category to set
 */
protected void setCategoryFromFields(Category category) {
	category.setName(mNameEdit.getText().toString());
}
	
}
