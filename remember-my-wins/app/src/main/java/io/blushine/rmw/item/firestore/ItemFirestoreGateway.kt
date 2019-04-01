package io.blushine.rmw.item.firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.blushine.android.common.ObjectEvent
import io.blushine.android.firebase.largeBatch
import io.blushine.android.task.CombineTask2
import io.blushine.rmw.item.*
import io.blushine.utils.EventBus


private val TAG = ItemFirestoreGateway::class.simpleName ?: "INVALID"

/**
 * Firestore gateway for [Item] and [Category]
 */
internal class ItemFirestoreGateway : ItemGateway {
	private val eventBus = EventBus.getInstance()

	private fun db(): FirebaseFirestore {
		return FirestoreTask.db()
	}

	private fun getUserId(): String {
		return FirestoreTask.getUserId()
	}

	/**
	 * Get the specified category
	 * @param id category id, if not specified creates a new Category document
	 */
	private fun getCategory(id: String? = null): DocumentReference {
		return FirestoreTask.getCategory(id)
	}

	/**
	 * Get the specified item
	 * @param id item id, if not specified creates a new Item document
	 */
	private fun getItem(id: String? = null): DocumentReference {
		return FirestoreTask.getItem(id)
	}

	override fun getCategories() {
		GetCategoriesTask(orderByOrder = true)
				.run()
				.addOnSuccessListener { categories ->
					eventBus.post(CategoryEvent(ObjectEvent.Actions.GET_RESPONSE, categories))
				}
				.addOnFailureListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.GET_FAILED))
				}
	}

	override fun addCategory(category: Category) {
		GetCategoriesTask()
				.run()
				.continueWith(AddCategoryTask(category))
				.addOnSuccessListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.ADDED, category))
				}
				.addOnFailureListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.ADD_FAILED, category))
				}
	}

	override fun updateCategories(categories: List<Category>) {
		UpdateCategoriesTask(categories)
				.run()
				.addOnSuccessListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.EDITED, categories))
				}
				.addOnFailureListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.EDIT_FAILED))
				}
	}

	override fun removeCategory(category: Category) {
		CombineTask2(GetItemTask(category.id), GetCategoriesTask(category.order))
				.run()
				.continueWith(RemoveCategoryTask(category))
				.addOnSuccessListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.REMOVED, category))
				}
				.addOnFailureListener {
					eventBus.post(CategoryEvent(ObjectEvent.Actions.REMOVE_FAILED, category))
				}
	}

	override fun addItems(items: List<Item>) {
		val largeBatch = db().largeBatch()
		val userId = getUserId()
		for (item in items) {
			item.userId = userId
			val doc = getItem()
			item.id = doc.id
			largeBatch.set(doc, item)
		}

		largeBatch.commit()
				.addOnSuccessListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.ADDED, items))
				}
				.addOnFailureListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.ADD_FAILED, items))
				}
	}

	override fun updateItems(items: List<Item>) {
		val largeBatch = db().largeBatch()

		for (item in items) {
			val doc = getItem(item.id)
			largeBatch.set(doc, item)
		}

		largeBatch.commit()
				.addOnSuccessListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.EDITED, items))
				}
				.addOnFailureListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.EDIT_FAILED, items))
				}
	}

	override fun removeItems(items: List<Item>) {
		val largeBatch = db().largeBatch()

		for (item in items) {
			val doc = getItem(item.id)
			largeBatch.delete(doc)
		}

		largeBatch.commit()
				.addOnSuccessListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.REMOVED, items))
				}
				.addOnFailureListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.REMOVE_FAILED, items))
				}
	}

	override fun getItems(categoryId: String) {
		GetItemTask(categoryId, orderByDate = true)
				.run()
				.addOnSuccessListener { items ->
					eventBus.post(ItemEvent(ObjectEvent.Actions.GET_RESPONSE, items, categoryId))
				}
				.addOnFailureListener {
					eventBus.post(ItemEvent(ObjectEvent.Actions.GET_FAILED, categoryId = categoryId))
				}
	}

	override fun importData(categories: List<Category>, items: List<Item>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}