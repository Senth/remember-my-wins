package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
public class CelebrationListFragment extends AppFragment {
private Toolbar mToolbar;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_celebration_list, container, false);

	mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
	AppActivity.getActivity().setSupportActionBar(mToolbar);

	return view;
}
}
