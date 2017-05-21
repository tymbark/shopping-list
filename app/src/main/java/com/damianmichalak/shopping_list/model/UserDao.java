package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.References;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;
import com.damianmichalak.shopping_list.model.api_models.User;

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
    private final Observable<User> userObservable;
    @Nonnull
    private final Observable<String> uidObservable;
    @Nonnull
    private final PublishSubject<Object> uidRefreshSubject = PublishSubject.create();
    @Nonnull
    private final PublishSubject<Object> userRefreshSubject = PublishSubject.create();
    @Nonnull
    private final References References;
    @Nonnull
    private UserPreferences userPreferences;

    @Inject
    public UserDao(@Nonnull final References References,
                   @Nonnull final EventsWrapper eventsWrapper,
                   @Nonnull final UserPreferences userPreferences) {
        this.References = References;
        this.userPreferences = userPreferences;

        uidObservable = uidRefreshSubject.startWith((Object) null)
                .switchMap(o -> Observable.fromCallable(userPreferences::getUid))
                .replay(1)
                .refCount();

        userObservable = Observable.merge(userRefreshSubject, uidObservable.filter(o -> o != null))
                .switchMap(f -> RxUtils.createObservableForReference(References.userReference(), eventsWrapper, User.class))
                .replay(1)
                .refCount();

    }

    @Nonnull
    public Observable<String> getUidObservable() {
        return uidObservable;
    }

    @Nonnull
    public Observable<User> getUserObservable() {
        return userObservable;
    }

    @Nonnull
    public Observer<String> uidObserver() {
        return Observers.create(uid -> {
            userPreferences.setUid(uid);
            uidRefreshSubject.onNext(null);
            References.userCreatedReference().setValue(true);
        });
    }

    @Nonnull
    public Observer<String> usernameObserver() {
        return Observers.create(username -> {
            References.userNameReference().setValue(username);
            userRefreshSubject.onNext(null);
        });
    }

}
