package io.blushine.celebratorica.item;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.blushine.android.ui.list.AdvancedAdapter;
import io.blushine.celebratorica.R;

/**
 * Adapter for viewing and reordering categories
 */
class CategoryOrderAdapter extends AdvancedAdapter<Category, CategoryOrderAdapter.ViewHolder> {
@Override
public void add(List<Category> newCategories) {
	List<Category> existingCategories = getItems();
	
	for (Category category : newCategories) {
		boolean added = false;
		
		// TODO could probably add item directly into the specific place
		for (int i = 0; i < existingCategories.size(); ++i) {
			Category currentListCategory = existingCategories.get(i);
			
			boolean isNewBeforeListCategory = category.compareTo(currentListCategory) < 1;
			if (isNewBeforeListCategory) {
				add(i, category);
				added = true;
				break;
			}
		}
		
		// Add to the end of the list
		if (!added) {
			add(getItemCount(), category);
		}
	}
}

@Override
protected ViewHolder onCreateView(ViewGroup parent, int viewType) {
	View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category_reorder, parent, false);
	return new ViewHolder(itemView);
}

@Override
protected void onBindView(ViewHolder view, int position) {
	final Category category = getItem(position);
	view.mName.setText(category.getName());
}

static class ViewHolder extends RecyclerView.ViewHolder {
	final TextView mName;
	
	ViewHolder(View itemView) {
		super(itemView);
		mName = (TextView) itemView.findViewById(R.id.category_name);
	}
}
}
