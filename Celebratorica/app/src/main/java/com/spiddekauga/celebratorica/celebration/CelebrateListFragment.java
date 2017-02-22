package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.AppFragment;
import com.spiddekauga.celebratorica.R;

/**
 * Show all the items in a list
 */
public class CelebrateListFragment extends AppFragment {
private Toolbar mToolbar;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_celebrate_list, container, false);

	mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
	AppActivity.getActivity().setSupportActionBar(mToolbar);

	FloatingActionButton addItemButton = (FloatingActionButton) view.findViewById(R.id.add_button);
	addItemButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new CelebrateAddFragment().show();
		}
	});

	// TODO only show if empty
//	FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.add_button);
//	Showcases.ADD_CELEBRATION.show(addButton);

	return view;
}
}
