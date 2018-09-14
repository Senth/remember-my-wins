package io.blushine.rmw.item.firestore

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.Query
import io.blushine.rmw.item.GET_ALL_ITEMS
import io.blushine.rmw.item.Item

/**
 * Task for getting items
 * @param categoryId get all items from this category. To get from all categories set this to [GET_ALL_ITEMS]
 * @param orderByDate set to true to order by date in DESCENDING order
 */
internal class GetItemTask(val categoryId: String, val orderByDate: Boolean = false) : FirestoreCallableTask<List<Item>>() {
	override fun call(): List<Item> {
		val itemCollection = db().collection(ITEM_TABLE)

		var query = itemCollection.whereEqualTo(USER_ID_FIELD, getUserId())

		if (categoryId != GET_ALL_ITEMS) {
			query = query.whereEqualTo(CATEGORY_ID_FIELD, categoryId)
		}

		if (orderByDate) {
			query = query.orderBy(DATE_FIELD, Query.Direction.DESCENDING)
		}

		val task = query.get()
		val querySnapshot = Tasks.await(task)
		return querySnapshot.toList(Item::class.java)
	}
}