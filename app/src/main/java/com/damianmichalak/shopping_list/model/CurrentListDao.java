package com.damianmichalak.shopping_list.model;


import android.util.Log;

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
public class CurrentListDao {

    @Nonnull
    private final Observable<ShoppingList> currentListObservable;
    @Nonnull
    private final Observable<String> currentListKeyObservable;
    @Nonnull
    private final UserPreferences userPreferences;
    @Nonnull
    private final Database database;
    @Nonnull
    private final PublishSubject<Object> currentListRefreshSubject = PublishSubject.create();

    @Inject
    public CurrentListDao(@Nonnull final UserPreferences userPreferences,
                          @Nonnull final Database database,
                          @Nonnull final EventsWrapper wrapper) {
        this.userPreferences = userPreferences;
        this.database = database;

        currentListObservable = currentListRefreshSubject.startWith(((Object) null))
                .flatMap(o -> Observable.fromCallable(userPreferences::getCurrentList))
                .filter(uid -> uid != null)
                .flatMap(uid -> RxUtils.createObservableForReference(database.singleListReference(uid), wrapper, ShoppingList.class))
                .replay(1)
                .refCount();

        currentListKeyObservable = currentListRefreshSubject.startWith(((Object) null))
                .flatMap(o -> Observable.fromCallable(userPreferences::getCurrentList))
                .filter(uid -> uid != null);

    }

    @Nonnull
    public Observable<String> getCurrentListKeyObservable() {
        return currentListKeyObservable;
    }

    @Nonnull

    @Deprecated //not sure
    public Observable<ShoppingList> getCurrentListObservable() {
        return currentListObservable;
    }

    @Nonnull
    public Observer<String> saveCurrentListIdObserver() {
        return Observers.create(currentListID1 -> {
            Log.d("CHUJ", "click in dao");
            userPreferences.setCurrentList(currentListID1);
            currentListRefreshSubject.onNext(null);
        });
    }
}
