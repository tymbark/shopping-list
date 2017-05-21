package com.damianmichalak.shopping_list.helper;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import rx.AsyncEmitter;
import rx.Observable;
import rx.functions.Action1;

public class Database<T> {

    private final EventsWrapper eventsWrapper;

    @Inject
    public Database(@Nonnull final EventsWrapper eventsWrapper) {
        this.eventsWrapper = eventsWrapper;
    }

    public Observable<Boolean> put(T obj, DatabaseReference reference) {
        return Observable.fromEmitter(
                emitter -> reference
                        .push()
                        .setValue(obj)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onNext(true);
                            } else {
                                emitter.onNext(false);
                            }
                        }),
                AsyncEmitter.BackpressureMode.LATEST);
    }

    public Observable<Boolean> remove(String key, DatabaseReference reference) {
        return Observable.fromEmitter(
                emitter -> reference
                        .child(key)
                        .removeValue()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                emitter.onNext(true);
                            } else {
                                emitter.onNext(false);
                            }
                        }),
                AsyncEmitter.BackpressureMode.LATEST);
    }

    public Observable<T> get(String key, String path) {
        return Observable.never();
    }

    public Observable<Map<String, T>> products(DatabaseReference reference) {
        return Observable.fromEmitter((Action1<AsyncEmitter<Map<String, T>>>) emitter -> {
            eventsWrapper.setEventsListener(new EventsWrapper.EventsListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final LinkedHashMap<String, T> output = new LinkedHashMap<>();
                    for (DataSnapshot next : dataSnapshot.getChildren()) {
                        final T object = next.getValue(new GenericTypeIndicator<T>() {
                        });
                        output.put(next.getKey(), object);
                    }
                    emitter.onNext(output);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(new Throwable(databaseError.getMessage()));
                }
            });
            reference.addValueEventListener(eventsWrapper.getFirebaseListener());
        }, AsyncEmitter.BackpressureMode.LATEST)
                .doOnUnsubscribe(() -> reference.removeEventListener(eventsWrapper.getFirebaseListener()));
    }

}
