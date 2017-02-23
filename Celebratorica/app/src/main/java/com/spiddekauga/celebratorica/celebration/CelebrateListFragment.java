package com.spiddekauga.celebratorica.celebration;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.AppFragment;
import com.spiddekauga.android.AppFragmentHelper;
import com.spiddekauga.android.ui.list.ClickListener;
import com.spiddekauga.android.ui.list.RemoveListener;
import com.spiddekauga.celebratorica.R;
import com.spiddekauga.celebratorica.util.Showcases;
import com.spiddekauga.celebratorica.util.Sqlite;
import com.spiddekauga.celebratorica.util.SqliteInitializedEvent;
import com.spiddekauga.utils.EventBus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Show all the items in a list
 */
public class CelebrateListFragment extends AppFragment implements RemoveListener<Celebration>, ClickListener<Celebration> {
private static final EventBus mEventBus = EventBus.getInstance();
private final CelebrationRepo mCelebrationRepo = CelebrationRepo.getInstance();
private final List<Celebration> mAddToAdapter = new ArrayList<>();
private Toolbar mToolbar;
private CelebrationAdapter mCelebrationAdapter = null;
private RecyclerView mCelebrationListView = null;
private FloatingActionButton mAddButton = null;

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mEventBus.register(this);

	mCelebrationAdapter = new CelebrationAdapter();
	mCelebrationAdapter.addSwipeRemoveFunctionality(this, false);
	mCelebrationAdapter.addEditFunctionality(this);

	if (Sqlite.isInitialized()) {
		populateCelebrations();
	}
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_celebrate_list, container, false);

	// Celebration list view
	mCelebrationListView = (RecyclerView) view.findViewById(R.id.celebration_list_view);
	mCelebrationListView.setHasFixedSize(true);
	RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
	mCelebrationListView.setLayoutManager(layoutManager);
	mCelebrationListView.setAdapter(mCelebrationAdapter);

	mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
	AppActivity.getActivity().setSupportActionBar(mToolbar);

	mAddButton = (FloatingActionButton) view.findViewById(R.id.add_button);
	mAddButton.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			new CelebrateAddFragment().show();
		}
	});

	return view;
}

@Override
public void onDestroy() {
	super.onDestroy();
	mEventBus.unregister(this);
}

private void populateCelebrations() {
	if (mCelebrationAdapter.getItemCount() == 0) {
		List<Celebration> celebrations = mCelebrationRepo.getCelebrations();
		mCelebrationAdapter.setItems(celebrations);

		if (celebrations.isEmpty()) {
			Showcases.ADD_CELEBRATION.show(mAddButton);
		}
	}
}

@Override
public void onResume() {
	super.onResume();
	mCelebrationListView.invalidate();

	new Handler().postDelayed(new Runnable() {
		@Override
		public void run() {
			for (final Celebration celebration : mAddToAdapter) {
				mCelebrationAdapter.addCelebration(celebration);
			}
			mAddToAdapter.clear();
		}
	}, 50);
}

@Override
public void onClick(Celebration item) {
	// TODO open edit fragment
}

@Override
public void onRemoved(Celebration item) {
	CelebrationRemoveCommand removeCommand = new CelebrationRemoveCommand(item);
	removeCommand.execute();
}

@SuppressWarnings("unused")
@Subscribe
public void onCelebration(CelebrationEvent event) {
	switch (event.getAction()) {
	case ADD:
		// Add later when this fragment becomes active
		if (AppFragmentHelper.getFragment() != this) {
			mAddToAdapter.add(event.getCelebration());
		}
		// Add directly
		else {
			mCelebrationAdapter.addCelebration(event.getCelebration());
		}
		break;

	case EDIT:
		mCelebrationAdapter.notifyItemChanged(event.getCelebration());
		break;

	case REMOVE:
		mCelebrationAdapter.remove(event.getCelebration());
		break;
	}
}

@SuppressWarnings("unused")
@Subscribe
public void onSqliteInitialized(SqliteInitializedEvent event) {
	populateCelebrations();
}
}
