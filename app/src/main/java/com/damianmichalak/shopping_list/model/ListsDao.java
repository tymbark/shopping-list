package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.Map;
import java.util.concurrent.Callable;

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
//        todo use Database.path() to get children, then make a transaction
//        todo adding names to new lists should be done in transaction
        return Observable.fromCallable(() -> database.userListsReference().push().setValue(itemName));
    }

//    public Observable<Object> removeListByKeyObservable(final String key) {
//        return Observable.fromCallable(() -> database.DEPRECATEDproductsReference().child(key).removeValue());
//    }

    @Nonnull
    public Observable<Map<String, String>> getCurrentListObservable() {
        return currentListObservable;
    }
}
