package com.spiddekauga.celebratorica.item;

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
class ItemAdapter extends AdvancedAdapter<Item, ItemAdapter.ViewHolder> {

/**
 * Add items to the correct chronological position
 * @param newItems the items to add
 */
public void add(List<Item> newItems) {
	List<Item> existingItems = getItems();
	int notifyChangedTo = 0;
	
	for (Item item : newItems) {
		boolean added = false;
		for (int i = 0; i < existingItems.size(); i++) {
			Item listItem = existingItems.get(i);
			
			boolean isNewNewerThanCurrentItem = item.compareTo(listItem) > 0;
			if (isNewNewerThanCurrentItem) {
				add(i, item);
				added = true;
				
				if (notifyChangedTo < i) {
					notifyChangedTo = i;
				}
				break;
			}
		}
		
		// Add to the end of the list
		if (!added) {
			add(getItemCount(), item);
			notifyChangedTo = getItemCount();
		}
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
	final Item item = getItem(position);
	
	view.mText.setText(item.getText());
	view.mDate.setText(item.getDate());
	
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
