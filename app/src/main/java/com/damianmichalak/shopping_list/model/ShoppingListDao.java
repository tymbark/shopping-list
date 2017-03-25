package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.Database;
import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ShoppingListDao {

    @Nonnull
    private final Observable<ShoppingList> shoppingListObservable;
    @Nonnull
    private final Observable<Map<String, String>> productsObservable;
    @Nonnull
    private final Database database;

    @Inject
    public ShoppingListDao(@Nonnull final Database database,
                           @Nonnull final EventsWrapper listEventsWrapper,
                           @Nonnull final EventsWrapper productsEventWrapper,
                           @Nonnull final UserDao userDao) {
        this.database = database;

        shoppingListObservable = userDao.getUidObservable()
                .switchMap(o -> RxUtils.createObservableForReference
                        (database.shoppingListReference(), listEventsWrapper, ShoppingList.class))
                .replay(1)
                .refCount();

        productsObservable = userDao.getUidObservable()
                .switchMap(o -> RxUtils.createObservableMapForReference
                        (database.productsReference(), productsEventWrapper, String.class))
                .replay(1)
                .refCount();

    }

    public Observable<Object> addNewItemObservable(final String itemName) {
        return Observable.fromCallable(() -> database.productsReference().push().setValue(itemName));
    }

    public Observable<Object> removeItemByKeyObservable(final String key) {
        return Observable.fromCallable(() -> database.productsReference().child(key).removeValue());
    }

    @Nonnull
    public Observable<ShoppingList> getShoppingListObservable() {
        return shoppingListObservable;
    }

    @Nonnull
    public Observable<Map<String, String>> getProductsObservable() {
        return productsObservable;
    }
}
