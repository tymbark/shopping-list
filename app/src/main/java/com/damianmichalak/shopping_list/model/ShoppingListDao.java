package com.damianmichalak.shopping_list.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

@Singleton
public class ShoppingListDao {

    @Nonnull
    private final Observable<ShoppingList> listObservable;

    @Inject
    public ShoppingListDao(final DatabaseReference reference) {

        final List<ValueEventListener> listeners = new ArrayList<>();

        listObservable = Observable.fromEmitter(new Action1<AsyncEmitter<ShoppingList>>() {
            @Override
            public void call(final AsyncEmitter<ShoppingList> asyncEmitter) {
                final ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final ShoppingList shoppingList = dataSnapshot.getValue(ShoppingList.class);
                        asyncEmitter.onNext(shoppingList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        asyncEmitter.onError(new Throwable(databaseError.getMessage()));
                    }
                };
                listeners.add(valueEventListener);
                reference.addValueEventListener(valueEventListener);
            }
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        for (ValueEventListener listener : listeners) {
                            reference.removeEventListener(listener);
                        }
                        listeners.clear();

                    }
                });

    }

    public Observable<ShoppingList> getListObservable() {
        return listObservable;
    }
}
