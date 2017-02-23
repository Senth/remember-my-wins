package com.spiddekauga.celebratorica.celebration;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spiddekauga.android.ui.list.AdvancedAdapter;
import com.spiddekauga.celebratorica.R;

import java.util.List;

/**
 * Adapter for viewing celebration items
 */
class CelebrationAdapter extends AdvancedAdapter<Celebration, CelebrationAdapter.ViewHolder> {

void addCelebration(Celebration celebration) {
	List<Celebration> celebrations = getItems();
	boolean added = false;

	int notifyChangedTo = celebrations.size();
	for (int i = 0; i < celebrations.size(); i++) {
		Celebration listCelebration = celebrations.get(i);

		boolean isNewNewer = celebration.compareTo(listCelebration) > 0;
		if (isNewNewer) {
			addItem(i, celebration);
			added = true;
			notifyChangedTo = i;
			break;
		}
	}

	// Add to the end of the list
	if (!added) {
		addItem(celebration);
	}

	// Update count for all later items
	if (notifyChangedTo > 0) {
		notifyItemRangeChanged(0, notifyChangedTo);
	}
}

@Override
public void remove(int itemIndex) {
	super.remove(itemIndex);

	// Update count for all later items (i.e. lower index)
	if (itemIndex > 0) {
		notifyItemRangeChanged(0, itemIndex);
	}
}

@Override
protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
	View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_celebration, parent, false);
	return new ViewHolder(itemView);
}

@Override
protected void onBindView(ViewHolder view, int position) {
	final Celebration celebration = getItem(position);

	view.mText.setText(celebration.getText());
	view.mDate.setText(celebration.getDate());

	int count = getItemCount() - position;
	view.mCount.setText(String.valueOf(count));
}

class ViewHolder extends RecyclerView.ViewHolder {
	final TextView mText;
	final TextView mCount;
	final TextView mDate;

	ViewHolder(View itemView) {
		super(itemView);
		mText = (TextView) itemView.findViewById(R.id.celebration_text);
		mCount = (TextView) itemView.findViewById(R.id.celebration_count);
		mDate = (TextView) itemView.findViewById(R.id.celebration_date);
	}
}
}
