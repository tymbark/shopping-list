package com.damianmichalak.shopping_list.model

import com.damianmichalak.shopping_list.helper.Database
import com.damianmichalak.shopping_list.helper.References
import com.damianmichalak.shopping_list.model.apiModels.Product
import com.google.firebase.database.DatabaseReference
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductsDao @Inject
constructor(references: References,
            private val database: Database<Product>,
            currentListDao: CurrentListDao,
            private val historyDao: HistoryDao) {
    private val referenceObservable: Observable<DatabaseReference>
    val productsObservable: Observable<List<Product>>

    init {

        referenceObservable = currentListDao.currentListKeyObservable
                .filter { uid -> uid != null }
                .map { references.productsReference(it) }
                .replay(1)
                .refCount()

        productsObservable = referenceObservable
                .switchMap { reference -> database.itemsAsMap(reference, Product::class.java) }
                .map { products -> products.map { it -> it.value }.toList() }
                .replay(1)
                .refCount()
    }

    fun addNewItemObservable(product: Product): Observable<Boolean> {
        return referenceObservable.switchMap { reference -> database.put(product, reference) }
    }

    fun removeItemByKeyObservable(product: Product): Observable<*> {
        return referenceObservable
                .flatMap { reference -> database.remove(product.id, reference) }
                .flatMap { ignore -> historyDao.addNewItemObservable(product) }
    }
}
