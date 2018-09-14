package io.blushine.rmw.item.firestore

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Transaction
import io.blushine.rmw.item.Category
import io.blushine.rmw.item.Item

/**
 * Task for getting categories
 * @param greaterThanOrEqualToOrder only get categories that are greater than or equal to this order
 * @param orderByOrder set to true to sort by this order in ASC
 */
internal class GetCategoriesTask(
		private val greaterThanOrEqualToOrder: Int? = null,
		private val orderByOrder: Boolean = false
) : FirestoreCallableTask<List<Category>>() {
	override fun call(): List<Category> {
		val categoryCollection = db().collection(CATEGORY_TABLE)

		var query = categoryCollection.whereEqualTo(USER_ID_FIELD, getUserId())

		if (greaterThanOrEqualToOrder != null) {
			query = query.whereGreaterThanOrEqualTo(ORDER_FIELD, greaterThanOrEqualToOrder)
		}
		if (orderByOrder) {
			query = query.orderBy(ORDER_FIELD)
		}

		val task = query.get()
		val querySnapshot = Tasks.await(task)
		return querySnapshot.toList(Category::class.java)
	}
}

/**
 * Task for adding a new category. This will also update all categories after this one.
 * Call [GetCategoriesTask] before calling this one
 * @param category the category to add
 */
internal class AddCategoryTask(val category: Category) : FirestoreTask(), Continuation<List<Category>, Unit> {
	override fun then(getCategoriesTask: Task<List<Category>>) {
		val task = db().runTransaction { transaction ->
			// Add new category
			val newDoc = getCategory()
			category.userId = getUserId()
			category.id = newDoc.id

			transaction.set(newDoc, category)

			// Update all categories after this one
			UpdateCategoriesTask.transactionImpl(transaction, getCategoriesTask.result)
		}

		Tasks.await(task)
	}
}

internal class UpdateCategoriesTask(val categories: List<Category>) : FirestoreCallableTask<Unit>() {
	override fun call() {
		val task = db().runTransaction { transaction ->
			transactionImpl(transaction, categories)
		}

		Tasks.await(task)
	}

	companion object {
		fun transactionImpl(transaction: Transaction, categories: List<Category>) {
			val userId = getUserId()

			categories.forEach { category ->
				val doc = getCategory(category.id)
				category.userId = userId
				transaction.set(doc, category)
			}
		}
	}
}

internal class RemoveCategoryTask(val category: Category) : FirestoreTask(), Continuation<Pair<List<Item>, List<Category>>, Unit> {
	override fun then(combineTask: Task<Pair<List<Item>, List<Category>>>) {
		val itemsToRemove = combineTask.result.first
		val categories = combineTask.result.second

		db().runTransaction { transaction ->
			// Delete category
			transaction.delete(getCategory(category.id))

			// Delete items
			itemsToRemove.forEach { item ->
				val doc = getItem(item.id)
				transaction.delete(doc)
			}

			// Decrease category order for categories after this
			categories.forEach { category ->
				// Skip if it's this category
				if (category != this.category) {
					category.order -= 1
					val doc = getCategory(category.id)
					transaction.set(doc, category)
				}
			}
		}
	}
}
