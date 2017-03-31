package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ListsDao {

    @Nonnull
    private final Observable<Map<String, String>> currentListObservable;
    @Nonnull
    private final Database database;
    @Nonnull
    private final UserPreferences userPreferences;

    @Inject
    public ListsDao(@Nonnull final Database database,
                    @Nonnull final EventsWrapper listEventsWrapper,
                    @Nonnull final UserPreferences userPreferences,
                    @Nonnull final UserDao userDao) {
        this.database = database;
        this.userPreferences = userPreferences;

        currentListObservable = userDao.getUidObservable()
                .filter(uid -> uid != null)
                .switchMap(o -> RxUtils.createObservableMapForReference(
                        database.userListsReference(), listEventsWrapper, String.class))
                .replay(1)
                .refCount();

    }

    public Observable<Object> addNewListObservable(final String itemName) {
        return Observable.fromCallable(() -> {
            final DatabaseReference newObject = database.userListsReference().push();
            newObject.setValue(itemName);
            database.singleListNameReference(newObject.getKey()).setValue(itemName);
            return null;
        });
    }

    @Nonnull
    public Observable<Map<String, String>> getCurrentListObservable() {
        return currentListObservable;
    }
}
