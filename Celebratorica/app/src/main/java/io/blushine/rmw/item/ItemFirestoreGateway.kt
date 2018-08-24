package io.blushine.rmw.item

import android.content.res.Resources
import com.google.firebase.firestore.FirebaseFirestore
import io.blushine.rmw.R
import io.blushine.rmw.util.AppActivity

/**
 * Firestore gateway for [Item] and [Category]
 */
internal object ItemFirestoreGateway : ItemGateway {
	val resources: Resources = AppActivity.getActivity()!!.resources

	override fun addCategory(category: Category) {
		val db = FirebaseFirestore.getInstance()
		db.collection(resources.getString(R.string.table_category))
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getCategory(categoryId: String): Category? {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getCategories(): List<Category> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

	override fun getItems(categoryId: String?): List<Item> {
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