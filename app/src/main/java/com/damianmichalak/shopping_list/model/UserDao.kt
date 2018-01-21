package com.damianmichalak.shopping_list.model

import com.damianmichalak.shopping_list.helper.EventsWrapper
import com.damianmichalak.shopping_list.helper.References
import com.damianmichalak.shopping_list.helper.RxUtils
import com.damianmichalak.shopping_list.model.apiModels.User
import rx.Observable
import rx.Observer
import rx.observers.Observers
import rx.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDao @Inject
constructor(private val references: References,
            eventsWrapper: EventsWrapper,
            private val userPreferences: UserPreferences) {

    val userObservable: Observable<User>
    /**
     * This can emit null if not logged in yet
     */
    val uidObservable: Observable<String?>
    private val uidRefreshSubject = PublishSubject.create<Any>()
    private val userRefreshSubject = PublishSubject.create<Any>()

    init {

        uidObservable = uidRefreshSubject.startWith(null as Any?)
                .switchMap { o -> Observable.fromCallable { userPreferences.uid } }
                .replay(1)
                .refCount()

        userObservable = Observable.merge(userRefreshSubject, uidObservable.filter { o -> o != null })
                .switchMap { f -> RxUtils.createObservableForReference(references.userReference(), eventsWrapper, User::class.java) }
                .replay(1)
                .refCount()

    }

    fun uidObserver(): Observer<String> {
        return Observers.create { uid ->
            userPreferences.uid = uid
            uidRefreshSubject.onNext(null)
            references.userCreatedReference().setValue(true)
        }
    }

    fun usernameObserver(): Observer<String> {
        return Observers.create { username ->
            references.userNameReference().setValue(username)
            userRefreshSubject.onNext(null)
        }
    }

}
