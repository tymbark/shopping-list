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
import rx.functions.Action1;

@Singleton
public class ShoppingListDao {

    @Nonnull
    private final Observable<ShoppingList> shoppingListObservable;
    @Nonnull
    private final Observable<Map<String, String>> productsObservable;
    @Nonnull
    private final Database database;
//    @Nonnull
//    private final DatabaseReference listReference;
//    @Nonnull
//    private final DatabaseReference productsReference;

    @Inject
    public ShoppingListDao(@Nonnull final Database database,
                           @Nonnull final EventsWrapper listEventsWrapper,
                           @Nonnull final EventsWrapper productsEventWrapper,
                           @Nonnull final UserDao userDao) {
        this.database = database;

//        listReference = database.shoppingListReference();
//        productsReference = database.productsReference();

        shoppingListObservable = RxUtils.createObservableForReference(database.shoppingListReference(), listEventsWrapper, ShoppingList.class);

        productsObservable = userDao.getUidObservable()
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                })
                .flatMap(o -> RxUtils.createObservableMapForReference
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
