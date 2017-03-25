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
public class ProductsDao {

    @Nonnull
    private final Observable<Map<String, String>> productsObservable;
    @Nonnull
    private final Database database;
    private final Observable<String> currentListKeyObservable;

    @Inject
    public ProductsDao(@Nonnull final Database database,
                       @Nonnull final EventsWrapper productsEventWrapper,
                       @Nonnull final UserDao userDao,
                       @Nonnull final CurrentListDao dao) {
        this.database = database;

        currentListKeyObservable = dao.getCurrentListKeyObservable()
                .filter(uid -> uid != null)
                .replay(1)
                .refCount();

        productsObservable = currentListKeyObservable
                .switchMap(uid -> RxUtils.createObservableMapForReference
                        (database.productsReference(uid), productsEventWrapper, String.class))
                .replay(1)
                .refCount();

    }

    public Observable<Object> addNewItemObservable(final String itemName) {
        return currentListKeyObservable.flatMap(uid -> Observable.fromCallable(
                () -> database.productsReference(uid).push().setValue(itemName)));
    }

    public Observable<Object> removeItemByKeyObservable(final String key) {
        return currentListKeyObservable.flatMap(uid -> Observable.fromCallable(
                () -> database.productsReference(uid).child(key).removeValue()));
    }

    @Nonnull
    public Observable<Map<String, String>> getProductsObservable() {
        return productsObservable;
    }
}
