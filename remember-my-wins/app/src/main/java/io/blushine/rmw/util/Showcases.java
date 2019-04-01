package io.blushine.rmw.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.view.View;

import com.squareup.otto.Subscribe;

import io.blushine.android.AppActivity;
import io.blushine.android.FragmentEvent;
import io.blushine.android.common.ColorHelper;
import io.blushine.android.ui.showcase.MaterialShowcase;
import io.blushine.android.ui.showcase.MaterialShowcaseView;
import io.blushine.rmw.R;
import io.blushine.utils.EventBus;

/**
 * All different showcases
 */
public enum Showcases {
	ADD_ITEM(null) {
		@Override
		MaterialShowcase create(View target) {
			if (target == null) {
				throw new IllegalArgumentException("target must be non-null");
			}
			return new MaterialShowcaseView.Builder(AppActivity.getActivity())
					.setTitleText(R.string.showcase_add_item_title)
					.setContentText(R.string.showcase_add_item_message)
					.setTarget(target)
					.setDelay(DELAY_DEFAULT)
					.build();
		}
	},
	ADD_ANOTHER_CATEGORY(R.string.showcase_add_another_category_id, 5) {
		@Override
		MaterialShowcase create(View target) {
			if (target == null) {
				throw new IllegalArgumentException("target must be non-null");
			}
			Resources resources = AppActivity.getActivity().getResources();
			return new MaterialShowcaseView.Builder(AppActivity.getActivity())
					.setTitleText(R.string.showcase_add_another_category_title)
					.setContentText(R.string.showcase_add_another_category_message)
					.setTarget(target)
					.setDelay(DELAY_DEFAULT)
					.setSingleUse(mShowcaseId)
					.setBackgroundColor(ColorHelper.getColor(resources, R.color.material_showcase_background_inverted, null))
					.build();
		}
	},
	ADD_FIRST_CATEGORY(null) {
		@Override
		MaterialShowcase create(View target) {
			if (target == null) {
				throw new IllegalArgumentException("target must be non-null");
			}
			Resources resources = AppActivity.getActivity().getResources();
			return new MaterialShowcaseView.Builder(AppActivity.getActivity())
					.setTitleText(R.string.showcase_add_first_category_title)
					.setContentText(R.string.showcase_add_first_category_message)
					.setTarget(target)
					.setDelay(DELAY_DEFAULT)
					.setBackgroundColor(ColorHelper.getColor(resources, R.color.material_showcase_background_inverted, null))
					.build();
		}
	};
private static final String PREFERENCE_NAME = "showcase_prefs";
private static final String PREF_SESSION_KEY = "session";
private static final int MAX_SHOWCASES_PER_SESSION = 1;
private static final int DELAY_DEFAULT = 8000;
private static final int SESSION_COUNT;
private static int mShowcasesShownThisSession = 0;
private static MaterialShowcase mActiveShowcase = null;

static {
	SharedPreferences preferences = AppActivity.getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	int session = preferences.getInt(PREF_SESSION_KEY, 1);
	preferences.edit().putInt(PREF_SESSION_KEY, session + 1).apply();
	SESSION_COUNT = session;
	new FragmentListener();
}

String mShowcaseId = null;
int mShowOnSession;

/**
 * @param showcaseId set this to make it single use, null will make it not single use
 */
Showcases(@Nullable @StringRes Integer showcaseId) {
	this(showcaseId, 0);
}

/**
 * @param showcaseId set this to make it single use, null will make it not single use
 * @param showOnSession when to earliest show this showcase, default is 0
 */
Showcases(@Nullable @StringRes Integer showcaseId, int showOnSession) {
	mShowOnSession = showOnSession;
	if (showcaseId != null) {
		mShowcaseId = AppActivity.getActivity().getString(showcaseId);
	}
}

/**
 * Reset all showcases (so they can be shown again)
 */
public static void resetAll() {
	MaterialShowcaseView.resetAll(AppActivity.getActivity());
}

/**
 * Show this showcase. Will not show this showcase if it's a single use and it has already been
 * shown or another single use showcase has been shown this session.
 * @param target if the showcase should have a target, specify one, else null.
 */
public void show(View target) {
	if (mShowOnSession <= SESSION_COUNT) {
		if (isSingleUse()) {
			if (!hasFired()) {
				if (mShowcasesShownThisSession < MAX_SHOWCASES_PER_SESSION) {
					show(create(target));
					mShowcasesShownThisSession++;
				}
			}
		} else {
			show(create(target));
		}
	}
}

private boolean isSingleUse() {
	return mShowcaseId != null;
}

private boolean hasFired() {
	return isSingleUse() && MaterialShowcaseView.hasFired(AppActivity.getActivity(), mShowcaseId);
}

/**
 * Actually show the showcase and also apply the current toolbar color if applicable
 * @param showcase the created showcase to show
 */
private static void show(MaterialShowcase showcase) {
	// TODO temporary disabled all showcases
//	mActiveShowcase = showcase;
//	showcase.show();
}

/**
 * Create the showcase
 * @param target if the showcase should have a target, specify one, else null
 * @return created showcase
 */
abstract MaterialShowcase create(View target);

/**
 * Set this showcase as shown if it is a single use showcase
 */
public void setAsShown() {
	if (isSingleUse()) {
		MaterialShowcaseView.setFired(AppActivity.getActivity(), mShowcaseId);
	}
}

private static class FragmentListener {
	FragmentListener() {
		EventBus.getInstance().register(this);
	}
	
	@SuppressWarnings("unused")
	@Subscribe
	public void onFragment(FragmentEvent event) {
		switch (event.getEventType()) {
		case RESUME:
			// TODO abort showcase
			break;
		}
	}
}
}
