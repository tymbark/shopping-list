package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

@Singleton
public class ShoppingListDao {

    @Nonnull
    private final Observable<ShoppingList> shoppingListObservable;
    @Nonnull
    private final Observable<List<String>> productsObservable;

    @Nonnull
    private final DatabaseReference listReference;
    @Nonnull
    private final DatabaseReference productsReference;

    @Inject
    public ShoppingListDao(@Nonnull @Named("shopping_list") final DatabaseReference listReference,
                           @Nonnull @Named("shopping_list/products") final DatabaseReference productsReference,
                           @Nonnull final EventsWrapper listEventsWrapper,
                           @Nonnull final EventsWrapper productsEventWrapper) {
        this.listReference = listReference;
        this.productsReference = productsReference;

        shoppingListObservable = RxUtils.createObservableForReference(listReference, listEventsWrapper, ShoppingList.class);

        productsObservable = RxUtils.createObservableListForReference(productsReference, productsEventWrapper, String.class);

    }

    public Observable<Object> addNewItemObservable(final String itemName) {
        return Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return productsReference.push().setValue(itemName);
            }
        });
    }

    public Observable<Object> removeItemByKeyObservable(final String itemName) {
        return Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return productsReference.child(itemName).removeValue();

//                return shoppingListObservable
//                        .flatMap(new Func1<ShoppingList, Observable<?>>() {
//                            @Override
//                            public Observable<?> call(ShoppingList shoppingList) {
//                                final Map<String, String> products = shoppingList.getProducts();
//                                if (products != null)
//                                    for (String key : products.keySet()) {
//                                        if (products.get(key).equals(itemName)) {
//                                            productsReference.child(key).removeValue();
//                                            return null;
//                                        }
//                                    }
//
//                                return null;
//                            }
//                        });
            }
        });
    }

    @Nonnull
    public Observable<ShoppingList> getShoppingListObservable() {
        return shoppingListObservable;
    }

    @Nonnull
    public Observable<List<String>> getProductsObservable() {
        return productsObservable;
    }
}
