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
    private final Observable<Map<String, String>> availableListsObservable;
    @Nonnull
    private final Database database;
    @Nonnull
    private final EventsWrapper singleListEW;

    @Inject
    public ListsDao(@Nonnull final Database database,
                    @Nonnull final EventsWrapper singleListEW,
                    @Nonnull final EventsWrapper availableListsEW,
                    @Nonnull final UserDao userDao) {
        this.database = database;
        this.singleListEW = singleListEW;

        availableListsObservable = userDao.getUidObservable()
                .filter(uid -> uid != null)
                .switchMap(o -> RxUtils.createObservableMapForReference(
                        database.userListsReference(), availableListsEW, String.class))
                .replay(1)
                .refCount();

    }

    @Nonnull
    public Observable<Object> addNewListObservable(final String itemName) {
        return Observable.fromCallable(() -> {
            final DatabaseReference newObject = database.userListsReference().push();
            newObject.setValue(itemName);
            database.singleListNameReference(newObject.getKey()).setValue(itemName);
            return null;
        });
    }

    @Nonnull
    public Observable<Object> addNewAvailableListObservable(final String existingListKey, final String existingListName) {
        return Observable.fromCallable(() -> database.userListsReference().child(existingListKey).setValue(existingListName));
    }

    @Nonnull
    public Observable<Object> removeListObservable(final String key) {
        return Observable.fromCallable(() -> {
            database.userListsReference().child(key).removeValue();
            database.allListsReference().child(key).removeValue();
            return null;
        });
    }

    @Nonnull
    public Observable<Map<String, String>> getAvailableListsObservable() {
        return availableListsObservable;
    }

    @Nonnull
    public Observable<ShoppingList> getObservableForSingleList(String key) {
        return RxUtils.createObservableForReference(database.singleListReference(key), new EventsWrapper(), ShoppingList.class);
    }
}
