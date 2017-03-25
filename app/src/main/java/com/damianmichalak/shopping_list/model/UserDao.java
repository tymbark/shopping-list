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
    private final Observable<String> userNameObservable;
    @Nonnull
    private final Observable<String> uidObservable;
    @Nonnull
    private final PublishSubject<Object> uidRefreshSubject = PublishSubject.create();
    private UserPreferences userPreferences;

    @Inject
    public UserDao(@Nonnull final Database database,
                   @Nonnull final EventsWrapper eventsWrapper,
                   @Nonnull final UserPreferences userPreferences) {
        this.userPreferences = userPreferences;

        uidObservable = uidRefreshSubject.startWith((Object) null)
                .switchMap(o -> Observable.fromCallable(userPreferences::getUid))
                .filter(o -> o != null);

        userNameObservable = uidObservable.filter(o -> o != null)
                .switchMap(f -> RxUtils.createObservableForReference(database.userReference(), eventsWrapper, User.class)
                        .map(User::getName));

    }

    @Nonnull
    public Observable<String> getUidObservable() {
        return uidObservable;
    }

    @Nonnull
    public Observable<String> getUserNameObservable() {
        return userNameObservable;
    }

    @Nonnull
    public Observer<String> uidObserver() {
        return Observers.create(uid -> {
            userPreferences.saveUid(uid);
            uidRefreshSubject.onNext(null);
        });
    }
}
