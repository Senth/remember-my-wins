package io.blushine.rmw.item

import android.content.res.Resources
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import io.blushine.android.common.ObjectEvent
import io.blushine.android.firebase.FirebaseAuth
import io.blushine.rmw.R
import io.blushine.rmw.util.AppActivity
import io.blushine.utils.EventBus


/**
 * Firestore gateway for [Item] and [Category]
 */
internal class ItemFirestoreGateway : ItemGateway {
	private val TAG = ItemFirestoreGateway::class.simpleName
	private val resources: Resources = AppActivity.getActivity()!!.resources
	private val eventBus = EventBus.getInstance();

	private fun db(): FirebaseFirestore {
		return FirebaseFirestore.getInstance();
	}

	override fun addCategory(category: Category) {
		category.userId = getUserId();
		val newDoc = db().collection(resources.getString(R.string.table_category)).document()
		category.id = newDoc.id
		newDoc.set(category)
				.addOnSuccessListener {
					Log.d(TAG, "addCategory() — ${category.id}")
					eventBus.post(CategoryEvent(category, ObjectEvent.Actions.ADDED))
				}
				.addOnFailureListener {
					Log.w(TAG, "addCategory() — Error adding document ${category.id}", it)
				}
	}

	private fun getUserId(): String {
		val user = FirebaseAuth.getCurrentUser()
		if (user != null) {
			return user.uid
		} else {
			Log.w(TAG, "getUserId() — No user logged in")
			return ""
		}
	}

	override fun getCategories() {
		val categoryCollection = db().collection(resources.getString(R.string.table_category))

		categoryCollection
				.whereEqualTo("userId", getUserId())
				.orderBy("order")
				.get()
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						val categories = ArrayList<Category>()
						Log.d(TAG, "getCategories() — Found categories")
						for (categoryDocument in task.result.documents) {
							val category = categoryDocument.toObject(Category::class.java)
							if (category != null) {
								categories.add(category)
							}
						}
						EventBus.getInstance()
								.post(CategoryEvent(categories, ObjectEvent.Actions.GET_RESPONSE))
					} else {
						Log.w(TAG, "getCategories() — Failed to get categories")
					}
				}
	}

	override fun updateCategory(category: Category) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun removeCategory(category: Category) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun addItem(item: Item) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getItems(categoryId: String?) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun updateItem(item: Item) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun removeItem(item: Item) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun importData(categories: List<Category>, items: List<Item>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}