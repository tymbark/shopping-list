package com.damianmichalak.shopping_list.model


import com.damianmichalak.shopping_list.helper.EventsWrapper
import com.damianmichalak.shopping_list.helper.References
import com.damianmichalak.shopping_list.helper.RxUtils
import com.damianmichalak.shopping_list.model.apiModels.ShoppingList
import rx.Observable
import rx.Observer
import rx.observers.Observers
import rx.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentListDao @Inject constructor(private val userPreferences: UserPreferences,
                                         references: References,
                                         wrapper: EventsWrapper) {

    val currentListObservable: Observable<ShoppingList>
    val currentListKeyObservable: Observable<String>
    private val currentListRefreshSubject = PublishSubject.create<Any>()

    init {

        currentListObservable = currentListRefreshSubject.startWith(null as Any?)
                .flatMap { o -> Observable.fromCallable { userPreferences.currentList } }
                .filter { uid -> uid != null }
                .flatMap { uid -> RxUtils.createObservableForReference(references.singleListReference(uid), wrapper, ShoppingList::class.java) }
                .replay(1)
                .refCount()

        currentListKeyObservable = currentListRefreshSubject.startWith(null as Any?)
                .flatMap { o -> Observable.fromCallable { userPreferences.currentList } }
                .filter { uid -> uid != null }
                .map { it }

    }

    fun saveCurrentListIdObserver(): Observer<String> {
        return Observers.create { currentListID1 ->
            userPreferences.currentList = currentListID1
            currentListRefreshSubject.onNext(null)
        }
    }
}
