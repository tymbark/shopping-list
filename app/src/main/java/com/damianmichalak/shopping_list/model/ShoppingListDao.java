package com.damianmichalak.shopping_list.model;

import com.damianmichalak.shopping_list.helper.EventsWrapper;
import com.damianmichalak.shopping_list.helper.RxUtils;
import com.google.firebase.database.DatabaseReference;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class ShoppingListDao {

    @Nonnull
    private final Observable<ShoppingList> listObservable;

    @Inject
    public ShoppingListDao(@Nonnull final DatabaseReference reference,
                           @Nonnull final EventsWrapper eventsWrapper) {

        listObservable = RxUtils.createObservableForReference(reference, eventsWrapper);

    }


    public Observable<ShoppingList> getListObservable() {
        return listObservable;
    }
}
