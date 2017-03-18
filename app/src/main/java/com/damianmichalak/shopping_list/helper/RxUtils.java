package com.damianmichalak.shopping_list.helper;

import com.damianmichalak.shopping_list.model.ShoppingList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import javax.annotation.Nonnull;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;


public class RxUtils {

    @Nonnull
    public static Observable<ShoppingList> createObservableForReference(@Nonnull final DatabaseReference reference, @Nonnull final EventsWrapper eventsWrapper) {
        return Observable.fromEmitter(new Action1<AsyncEmitter<ShoppingList>>() {
            @Override
            public void call(final AsyncEmitter<ShoppingList> asyncEmitter) {

                eventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
                        asyncEmitter.onNext(shoppingList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        asyncEmitter.onError(new Throwable(databaseError.getMessage()));
                    }
                });
                reference.addValueEventListener(eventsWrapper.getFirebaseListener());
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        reference.removeEventListener(eventsWrapper.getFirebaseListener());

                    }
                });
    }

}
