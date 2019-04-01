package io.blushine.rmw.item;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.blushine.android.ui.list.AdvancedAdapter;
import io.blushine.rmw.R;

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
			Category currentCategory = existingCategories.get(i);
			
			boolean isNewBeforeCurrentCategory = category.compareTo(currentCategory) < 1;
			if (isNewBeforeCurrentCategory) {
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
	
	updateOrder();
}

@Override
public void remove(int itemIndex) {
	super.remove(itemIndex);
	updateOrder();
}

@Override
public void move(int fromPosition, int toPosition) {
	Category categoryToMove = getItem(fromPosition);
	
	// Update category order
	int adjustOrderDiff = toPosition - fromPosition;
	int newOrder = categoryToMove.getOrder() + adjustOrderDiff;
	categoryToMove.setOrder(newOrder);
	
	
	// Increase or decrease the category order?
	int increment;
	int beginIndex;
	int endIndex;
	if (fromPosition < toPosition) {
		increment = -1;
		beginIndex = fromPosition + 1;
		endIndex = toPosition;
	} else {
		increment = 1;
		beginIndex = toPosition;
		endIndex = fromPosition - 1;
	}
	
	// Adjust order for the rest of the categories
	for (int i = beginIndex; i <= endIndex; ++i) {
		Category category = getItem(i);
		newOrder = category.getOrder() + increment;
		category.setOrder(newOrder);
	}
	
	super.move(fromPosition, toPosition);
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

private void updateOrder() {
	for (int i = 0; i < getItemCount(); ++i) {
		Category category = getItem(i);
		category.setOrder(i + 1);
	}
}

static class ViewHolder extends RecyclerView.ViewHolder {
	final TextView mName;
	
	ViewHolder(View itemView) {
		super(itemView);
		mName = (TextView) itemView.findViewById(R.id.category_name);
	}
}
}
