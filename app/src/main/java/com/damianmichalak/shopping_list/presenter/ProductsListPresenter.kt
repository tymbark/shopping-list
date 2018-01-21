package com.damianmichalak.shopping_list.presenter


import com.damianmichalak.shopping_list.helper.guava.Lists
import com.damianmichalak.shopping_list.helper.guava.Objects
import com.damianmichalak.shopping_list.model.CurrentListDao
import com.damianmichalak.shopping_list.model.ListsDao
import com.damianmichalak.shopping_list.model.ProductsDao
import com.damianmichalak.shopping_list.model.apiModels.Product
import com.damianmichalak.shopping_list.model.apiModels.ShoppingList
import com.jacekmarchwicki.universaladapter.BaseAdapterItem
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

import rx.Observable
import rx.Observer
import rx.Scheduler
import rx.functions.Func1
import rx.observers.Observers
import rx.subjects.PublishSubject
import rx.subscriptions.SerialSubscription
import rx.subscriptions.Subscriptions

class ProductsListPresenter @Inject
internal constructor(productsDao: ProductsDao,
                     currentListDao: CurrentListDao,
                     listsDao: ListsDao,
                     @Named("AddListClickObservable") addListClickObservable: Observable<Void>,
                     @Named("UI") uiScheduler: Scheduler) {

    val listNameObservable: Observable<String>
    val currentShoppingListItemsObservable: Observable<List<BaseAdapterItem>>
    val removeItemSubject: PublishSubject<Product> = PublishSubject.create<Product>()
    val newShoppingListObserver: PublishSubject<String> = PublishSubject.create<String>()
    val emptyListObservable: Observable<Boolean>
    val noListsObservable: Observable<Boolean>
    val showNewListDialogObservable: Observable<Any>
    val subscription = SerialSubscription()

    val floatingActionButtonObservable: Observable<Boolean>
        get() = noListsObservable.map { o -> !o }

    init {

        listNameObservable = currentListDao.currentListObservable
                .filter { list -> list != null }
                .map { it.name }

        showNewListDialogObservable = addListClickObservable.map { v -> null }

        currentShoppingListItemsObservable = productsDao.productsObservable
                .map{ it.map { ShoppingListItemWithKey(it) }}

        noListsObservable = listsDao.availableListsObservable.map { it.isEmpty() }

        emptyListObservable = Observable
                .combineLatest(currentShoppingListItemsObservable.map { it.isEmpty() }, noListsObservable
                ) { currentListEmpty, noLists -> currentListEmpty!! && !noLists }
                .share()

        subscription.set(Subscriptions.from(
                removeItemSubject
                        .throttleFirst(100, TimeUnit.MILLISECONDS, uiScheduler)
                        .distinctUntilChanged()
                        .flatMap { productsDao.removeItemByKeyObservable(it) }
                        .subscribe(),
                newShoppingListObserver
                        .flatMap { listsDao.addNewListObservable(it) }
                        .subscribe()
        ))
    }

    inner class ShoppingListItemWithKey(val product: Product) : BaseAdapterItem {

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o !is ShoppingListItemWithKey) return false
            val that = o as ShoppingListItemWithKey?
            return Objects.equal(product, that!!.product)
        }

        override fun hashCode(): Int {
            return Objects.hashCode(product)
        }

        override fun adapterId(): Long {
            return 0
        }

        override fun matches(item: BaseAdapterItem): Boolean {
            return item is ShoppingListItemWithKey && item.product.id == product.id
        }

        override fun same(item: BaseAdapterItem): Boolean {
            return equals(item)
        }

        fun removeItem(): Observer<Any> {
            return Observers.create { o -> removeItemSubject.onNext(product) }
        }
    }

}
