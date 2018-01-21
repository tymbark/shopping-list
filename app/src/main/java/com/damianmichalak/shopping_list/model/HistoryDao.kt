package com.damianmichalak.shopping_list.model

import com.damianmichalak.shopping_list.helper.Database
import com.damianmichalak.shopping_list.helper.References
import com.damianmichalak.shopping_list.model.apiModels.Product
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Singleton

import rx.Observable

@Singleton
class HistoryDao @Inject
constructor(references: References,
            private val database: Database<Product>,
            currentListDao: CurrentListDao) {
    private val referenceObservable: Observable<DatabaseReference>
    val productsObservable: Observable<Map<String, Product>>

    init {

        referenceObservable = currentListDao.currentListKeyObservable
                .filter { uid -> uid != null }
                .map { references.historyReference(it) }
                .replay(1)
                .refCount()

        productsObservable = referenceObservable.switchMap { reference -> database.itemsAsMap(reference, Product::class.java, "datePurchased") }
                .replay(1)
                .refCount()
    }

    fun addNewItemObservable(product: Product): Observable<Boolean> {
        return referenceObservable.switchMap { reference -> database.put(product.purchased(), reference) }
    }

    fun removeItemByKeyObservable(key: String): Observable<Boolean> {
        return referenceObservable.switchMap { reference -> database.remove(key, reference) }
    }
}
