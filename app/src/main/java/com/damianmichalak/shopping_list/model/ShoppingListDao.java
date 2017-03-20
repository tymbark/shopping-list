package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;
import com.google.firebase.database.DatabaseReference;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;

@Singleton
public class ShoppingListDao {

    @Nonnull
    private final Observable<ShoppingList> listObservable;

    @Inject
    public ShoppingListDao(@Nonnull @Named("shopping_list") final DatabaseReference reference,
                           @Nonnull final EventsWrapper eventsWrapper) {

        listObservable = RxUtils.createObservableForReference(reference, eventsWrapper, ShoppingList.class)
                .doOnNext(new Action1<ShoppingList>() {
                    @Override
                    public void call(ShoppingList shoppingList) {
                        System.out.println("test");
                    }
                });

    }


    public Observable<ShoppingList> getListObservable() {
        return listObservable;
    }
}
