package io.blushine.rmw.item

/**
 * Interface for all Item Gateways
 */
internal interface ItemGateway {
	/**
	 * Add a new category. Will automatically set the category id, the category id won't be set
	 * if it failed to add the category
	 * @param category the category to add
	 */
	fun addCategory(category: Category)

	/**
	 * Get all categories
	 * @return all categories sorted by order
	 */
	fun getCategories()

	/**
	 * Update several categories
	 * @param categories the categories to update
	 */
	fun updateCategories(categories: List<Category>)

	/**
	 * Remove a category
	 * @param category the category to remove
	 */
	fun removeCategory(category: Category)

	/**
	 * Add several items. Will automatically set the item id
	 * @param items the items to add
	 */
	fun addItems(items: List<Item>)

	/**
	 * Get all items in the specified category
	 * @param categoryId the category id to get the item from, set to null to get from all categories
	 * @return list of all items in the category. Items are ordered by date with newest first
	 */
	fun getItems(categoryId: String?)

	/**
	 * Update all the specified items
	 * @param items all items to update
	 */
	fun updateItems(items: List<Item>)

	/**
	 * Remove all items
	 * @param items the items to remove
	 */
	fun removeItems(items: List<Item>)

	/**
	 * Import categories and items into the database. Duplicates won't be added
	 * @param categories all categories to import. If a category with the same name already exists
	 * items will be added to that category instead of creating a new category with the same name
	 * @param items all the items to import
	 */
	fun importData(categories: List<Category>, items: List<Item>)

}