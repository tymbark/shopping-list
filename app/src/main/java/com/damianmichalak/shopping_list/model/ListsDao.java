package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.References;
import com.damianmichalak.shopping_list.model.apiModels.ShoppingList;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;

@Singleton
public class ListsDao {

    @Nonnull
    private final Observable<Map<String, String>> availableListsObservable;
    @Nonnull
    private final References references;
    @Nonnull
    private final Database<ShoppingList> shoppingListDB;

    @Inject
    public ListsDao(@Nonnull final References references,
                    @Nonnull final Database<ShoppingList> shoppingListDB,
                    @Nonnull final Database<String> listsIdsDB,
                    @Nonnull final CurrentListDao currentListDao,
                    @Nonnull final UserDao userDao) {
        this.references = references;
        this.shoppingListDB = shoppingListDB;

        availableListsObservable = userDao.getUidObservable()
                .filter(uid -> uid != null)
                .switchMap(o -> listsIdsDB.itemsAsMap(references.userListsReference(), String.class))
                .doOnNext(setAsCurrentIfThereIsOnlyOneItem(currentListDao))
                .replay(1)
                .refCount();

    }

    @Nonnull
    private Action1<Map<String, String>> setAsCurrentIfThereIsOnlyOneItem(@Nonnull CurrentListDao currentListDao) {
        return stringStringMap -> {
            if (stringStringMap.size() == 1) {
                final String key = stringStringMap.keySet().iterator().next();
                currentListDao.saveCurrentListIdObserver().onNext(key);
            }
        };
    }

    @Nonnull
    public Observable<Object> addNewListObservable(final String itemName) {
        return Observable.fromCallable(() -> {
            final DatabaseReference newObject = references.userListsReference().push();
            newObject.setValue(itemName);
            references.singleListNameReference(newObject.getKey()).setValue(itemName);
            return null;
        });
    }

    @Nonnull
    public Observable<Object> addNewAvailableListObservable(final String existingListKey, final String existingListName) {
        return Observable.fromCallable(() -> references.userListsReference().child(existingListKey).setValue(existingListName));
    }

    @Nonnull
    public Observable<Object> removeListObservable(final String key) {
        return Observable.fromCallable(() -> {
            references.userListsReference().child(key).removeValue();
            references.allListsReference().child(key).removeValue();
            return null;
        });
    }

    @Nonnull
    public Observable<Map<String, String>> getAvailableListsObservable() {
        return availableListsObservable;
    }

    @Nonnull
    public Observable<ShoppingList> getObservableForSingleList(String key) {
        return shoppingListDB.get(key, references.allListsReference(), ShoppingList.class);
    }
}
