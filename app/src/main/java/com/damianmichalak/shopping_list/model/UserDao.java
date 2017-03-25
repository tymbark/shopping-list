package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Observer;
import rx.observers.Observers;
import rx.subjects.PublishSubject;

@Singleton
public class UserDao {

    @Nonnull
    private final Observable<User> userNameObservable;
    @Nonnull
    private final Observable<String> uidObservable;
    @Nonnull
    private final PublishSubject<Object> uidRefreshSubject = PublishSubject.create();
    @Nonnull
    private final Database database;
    private UserPreferences userPreferences;

    @Inject
    public UserDao(@Nonnull final Database database,
                   @Nonnull final EventsWrapper eventsWrapper,
                   @Nonnull final UserPreferences userPreferences) {
        this.database = database;
        this.userPreferences = userPreferences;

        uidObservable = uidRefreshSubject.startWith((Object) null)
                .switchMap(o -> Observable.fromCallable(userPreferences::getUid))
                .replay(1)
                .refCount();

        userNameObservable = uidObservable.filter(o -> o != null)
                .switchMap(f -> RxUtils.createObservableForReference(database.userReference(), eventsWrapper, User.class));

    }

    @Nonnull
    public Observable<String> getUidObservable() {
        return uidObservable;
    }

    @Nonnull
    public Observable<User> getUserObservable() {
        return userNameObservable;
    }

    @Nonnull
    public Observer<String> uidObserver() {
        return Observers.create(uid -> {
            userPreferences.setUid(uid);
            uidRefreshSubject.onNext(null);
        });
    }
}
