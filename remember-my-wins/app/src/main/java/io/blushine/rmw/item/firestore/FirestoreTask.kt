package io.blushine.rmw.item.firestore

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.blushine.android.firebase.FirebaseAuth
import io.blushine.android.task.ThreadPools
import io.blushine.rmw.R
import io.blushine.rmw.util.AppActivity
import java.util.concurrent.Callable

internal val CATEGORY_TABLE = AppActivity.getActivity()!!.resources!!.getString(R.string.table_category);
internal val ITEM_TABLE = AppActivity.getActivity()!!.resources!!.getString(R.string.table_item)
internal const val USER_ID_FIELD = "userId"
internal const val CATEGORY_ID_FIELD = "categoryId"
internal const val ORDER_FIELD = "order"
internal const val DATE_FIELD = "date"

private val TAG = FirestoreTask::class.simpleName ?: "INVALID"

/**
 * Base class for Firestore tasks
 */
internal abstract class FirestoreTask() {
	companion object {
		internal fun db(): FirebaseFirestore = FirebaseFirestore.getInstance()

		internal fun getUserId(): String {
			val user = FirebaseAuth.getCurrentUser()
			return if (user != null) {
				user.uid
			} else {
				Log.w(TAG, "getUserId() — No user logged in")
				""
			}
		}

		/**
		 * Get the specified category
		 * @param id category id, if not specified creates a new Category document
		 */
		internal fun getCategory(id: String? = null): DocumentReference {
			val collectionReference = db().collection(CATEGORY_TABLE)

			return if (id != null) {
				collectionReference.document(id)
			} else {
				collectionReference.document()
			}
		}

		/**
		 * Get the specified item
		 * @param id item id, if not specified creates a new Item document
		 */
		internal fun getItem(id: String? = null): DocumentReference {
			val collectionReference = db().collection(ITEM_TABLE)

			return if (id != null) {
				collectionReference.document(id)
			} else {
				collectionReference.document()
			}
		}
	}
}

/**
 * Callable Firestore Task
 */
internal abstract class FirestoreCallableTask<TResult>() : FirestoreTask(), Callable<TResult> {
	/**
	 * Run the task in another thread. Will reuse tasks
	 */
	fun run(): Task<TResult> {
		return Tasks.call(ThreadPools.CACHED_THREAD_POOL, this)
	}
}

internal fun <TResult> QuerySnapshot.toList(objectClass: Class<TResult>): List<TResult> {
	val items = ArrayList<TResult>()
	for (categoryDocument in documents) {
		val item = categoryDocument.toObject(objectClass)
		if (item != null) {
			items.add(item)
		}
	}
	return items
}