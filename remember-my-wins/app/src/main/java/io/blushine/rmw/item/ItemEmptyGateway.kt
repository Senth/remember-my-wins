package io.blushine.rmw.item

/**
 * Empty (invalid) gateway for items
 */
internal class ItemEmptyGateway : ItemGateway {
	override fun addCategory(category: Category) {

	}

	override fun getCategories() {

	}

	override fun updateCategories(categories: List<Category>) {

	}

	override fun removeCategory(category: Category) {

	}

	override fun addItems(items: List<Item>) {

	}

	override fun getItems(categoryId: String) {

	}

	override fun updateItems(items: List<Item>) {

	}

	override fun removeItems(items: List<Item>) {

	}

	override fun importData(categories: List<Category>, items: List<Item>) {

	}

}