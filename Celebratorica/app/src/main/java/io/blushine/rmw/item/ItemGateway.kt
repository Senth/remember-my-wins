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
	 * Get the specified category
	 * @param categoryId the category to get
	 * @return category with the id, null if not found
	 */
	fun getCategory(categoryId: String): Category?

	/**
	 * Get all categories
	 * @return all categories sorted by order
	 */
	fun getCategories(): List<Category>

	/**
	 * Update a category
	 * @param category the category to update
	 */
	fun updateCategory(category: Category)

	/**
	 * Remove a category
	 * @param category the category to remove
	 */
	fun removeCategory(category: Category)

	/**
	 * Add a new item. Will automatically set the item id, the item id won't be set if it failed
	 * to add the item
	 * @param item the item to add
	 */
	fun addItem(item: Item)

	/**
	 * Get all items in the specified category
	 * @param categoryId the category id to get the item from, set to null to get from all categories
	 * @return list of all items in the category. Items are ordered by date with newest first
	 */
	fun getItems(categoryId: String?): List<Item>

	/**
	 * Update an item
	 * @param item the item to update
	 */
	fun updateItem(item: Item)

	/**
	 * Remove an item
	 * @param item the item to remove
	 */
	fun removeItem(item: Item)

	/**
	 * Import categories and items into the database. Duplicates won't be added
	 * @param categories all categories to import. If a category with the same name already exists
	 * items will be added to that category instead of creating a new category with the same name
	 * @param items all the items to import
	 */
	fun importData(categories: List<Category>, items: List<Item>)

}